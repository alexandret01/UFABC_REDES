package org.onosproject.opticallab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor HTTP mínimo usando java.net.ServerSocket (porta 9191).
 *
 * Usa apenas java.io / java.net — pacotes sempre exportados pelo bundle do
 * sistema OSGi em qualquer JVM. Não depende de com.sun.*, PAX-Web, Jersey,
 * HttpService ou qualquer infraestrutura do Karaf/ONOS.
 *
 * Endpoints:
 *   GET /status         — snapshot atual (JSON)
 *   GET /history        — histórico (JSON, aceita ?limit=N)
 *   GET /dataset.csv    — dataset completo (CSV download)
 *   GET /info           — info do app (JSON)
 *   GET /ui             — dashboard HTML
 *   GET /               — redirect → /ui
 */
public class OpticalLabHttpServer {

    private static final Logger log = LoggerFactory.getLogger(OpticalLabHttpServer.class);
    static final int PORT = 9191;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private volatile boolean  running = false;
    private ServerSocket      serverSocket;
    private Thread            acceptThread;
    private ExecutorService   workers;

    // ── ciclo de vida ─────────────────────────────────────────────────────────

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        workers      = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "opticallab-http-worker");
            t.setDaemon(true);
            return t;
        });
        running = true;
        acceptThread = new Thread(this::acceptLoop, "opticallab-http-accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
        log.info("Optical Lab HTTP server na porta {}", PORT);
    }

    public void stop() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
        if (workers != null) workers.shutdownNow();
    }

    // ── accept loop ───────────────────────────────────────────────────────────

    private void acceptLoop() {
        while (running && !serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                workers.submit(() -> handleClient(client));
            } catch (IOException e) {
                if (running) log.warn("HTTP accept error: {}", e.getMessage());
            }
        }
    }

    // ── request handler ───────────────────────────────────────────────────────

    private void handleClient(Socket client) {
        try {
            client.setSoTimeout(5_000);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "UTF-8"));

            // Linha de requisição: "GET /path?query HTTP/1.1"
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) { client.close(); return; }

            // Descartar cabeçalhos HTTP
            String header;
            while ((header = in.readLine()) != null && !header.isEmpty()) { /* skip */ }

            // Parsing
            String[] tokens   = requestLine.split(" ");
            String   fullPath = tokens.length > 1 ? tokens[1] : "/";
            String   path     = fullPath.contains("?") ? fullPath.split("\\?")[0] : fullPath;
            String   query    = fullPath.contains("?") ? fullPath.split("\\?")[1] : "";

            // Roteamento
            Response resp = route(path, query);

            // Escrever resposta HTTP
            OutputStream os = client.getOutputStream();
            StringBuilder header2 = new StringBuilder();
            header2.append("HTTP/1.1 ").append(resp.status).append(" ").append(statusText(resp.status)).append("\r\n");
            header2.append("Content-Type: ").append(resp.contentType).append("\r\n");
            header2.append("Access-Control-Allow-Origin: *\r\n");
            if (resp.location   != null) header2.append("Location: ").append(resp.location).append("\r\n");
            if (resp.disposition!= null) header2.append("Content-Disposition: ").append(resp.disposition).append("\r\n");
            header2.append("Connection: close\r\n");
            header2.append("Content-Length: ").append(resp.body.length).append("\r\n");
            header2.append("\r\n");
            os.write(header2.toString().getBytes("UTF-8"));
            os.write(resp.body);
            os.flush();

        } catch (Exception e) {
            log.debug("HTTP client error: {}", e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    // ── roteamento ────────────────────────────────────────────────────────────

    private Response route(String path, String query) {
        switch (path) {
            case "/status":      return json(200, statusJson());
            case "/history":     return json(200, historyJson(query));
            case "/dataset.csv": return csv(csvData());
            case "/info":        return json(200, infoJson());
            case "/ui":          return html(OpticalLabWebResource.buildDashboardHtml());
            case "/":            return redirect("/ui");
            default:             return text(404, "Not found");
        }
    }

    // ── serialização de dados ─────────────────────────────────────────────────

    private String statusJson() {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null || app.getStore().getLatest() == null) return "{}";
        try { return MAPPER.writeValueAsString(app.getStore().getLatest().toMap()); }
        catch (Exception e) { return "{\"error\":\"" + esc(e.getMessage()) + "\"}"; }
    }

    private String historyJson(String query) {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null) return "[]";
        Integer limit = null;
        if (query != null && query.contains("limit=")) {
            try { limit = Integer.parseInt(query.replaceAll(".*limit=(\\d+).*", "$1")); }
            catch (Exception ignored) {}
        }
        List<DataPoint> history = app.getStore().getHistory();
        if (limit != null && limit > 0 && limit < history.size()) {
            history = history.subList(history.size() - limit, history.size());
        }
        try {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < history.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(MAPPER.writeValueAsString(history.get(i).toMap()));
            }
            return sb.append("]").toString();
        } catch (Exception e) { return "[]"; }
    }

    private String csvData() {
        OpticalLabApp app = OpticalLabApp.getInstance();
        return app == null ? "" : app.getStore().toCsvString();
    }

    private String infoJson() {
        OpticalLabApp app = OpticalLabApp.getInstance();
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"app\":\"br.ufabc.opticallab\",\"version\":\"1.0.0\"");
        sb.append(",\"port\":").append(PORT);
        sb.append(",\"active\":").append(app != null);
        if (app != null) {
            sb.append(",\"collectCount\":").append(app.getCollectCount());
            sb.append(",\"intervalSeconds\":").append(app.getIntervalSeconds());
            sb.append(",\"historySize\":").append(app.getStore().size());
        }
        return sb.append("}").toString();
    }

    // ── Response builders ─────────────────────────────────────────────────────

    private static Response json(int status, String body) {
        return new Response(status, "application/json; charset=UTF-8",
                null, null, bytes(body));
    }
    private static Response html(String body) {
        return new Response(200, "text/html; charset=UTF-8", null, null, bytes(body));
    }
    private static Response csv(String body) {
        return new Response(200, "text/csv", null,
                "attachment; filename=\"opticallab-dataset.csv\"", bytes(body));
    }
    private static Response text(int status, String body) {
        return new Response(status, "text/plain; charset=UTF-8", null, null, bytes(body));
    }
    private static Response redirect(String location) {
        return new Response(302, "text/plain", location, null, new byte[0]);
    }

    private static byte[] bytes(String s) {
        try { return s.getBytes("UTF-8"); } catch (Exception e) { return s.getBytes(); }
    }
    private static String esc(String s) { return s == null ? "" : s.replace("\"", "'"); }
    private static String statusText(int code) {
        switch (code) {
            case 200: return "OK";
            case 204: return "No Content";
            case 302: return "Found";
            case 404: return "Not Found";
            case 500: return "Internal Server Error";
            default:  return "Unknown";
        }
    }

    // ── Response record ───────────────────────────────────────────────────────

    private static class Response {
        final int    status;
        final String contentType;
        final String location;
        final String disposition;
        final byte[] body;

        Response(int status, String contentType, String location,
                 String disposition, byte[] body) {
            this.status      = status;
            this.contentType = contentType;
            this.location    = location;
            this.disposition = disposition;
            this.body        = body;
        }
    }
}
