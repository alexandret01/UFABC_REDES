package org.onosproject.opticallab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Servidor HTTP embutido do Optical Lab Monitor (porta 9191).
 *
 * Usa apenas java.net + com.sun.net.httpserver (JDK padrão) — sem dependências
 * externas, sem OSGi HTTP Service, sem pax-web. Garante que os endpoints
 * funcionem independentemente de como o ONOS gerencia WABs.
 *
 * Endpoints:
 *   GET /status        — snapshot atual (JSON)
 *   GET /history       — histórico (JSON array, ?limit=N)
 *   GET /dataset.csv   — dataset completo (CSV download)
 *   GET /info          — info do app (JSON)
 *   GET /ui            — dashboard HTML
 *   GET /              — redirect → /ui
 */
public class OpticalLabHttpServer {

    private static final Logger log = LoggerFactory.getLogger(OpticalLabHttpServer.class);
    static final int PORT = 9191;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private HttpServer server;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 10);
        server.createContext("/status",      this::handleStatus);
        server.createContext("/history",     this::handleHistory);
        server.createContext("/dataset.csv", this::handleDataset);
        server.createContext("/info",        this::handleInfo);
        server.createContext("/ui",          this::handleUI);
        server.createContext("/",            this::handleRoot);
        server.setExecutor(null);
        server.start();
        log.info("Optical Lab HTTP server iniciado na porta {}", PORT);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            log.info("Optical Lab HTTP server parado");
        }
    }

    // ── handlers ──────────────────────────────────────────────────────────────

    private void handleStatus(HttpExchange ex) throws IOException {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null || app.getStore().getLatest() == null) {
            sendJson(ex, 204, "{}");
            return;
        }
        try {
            sendJson(ex, 200, MAPPER.writeValueAsString(app.getStore().getLatest().toMap()));
        } catch (Exception e) {
            sendJson(ex, 500, "{\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleHistory(HttpExchange ex) throws IOException {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null) { sendJson(ex, 503, "[]"); return; }

        Integer limit = null;
        String q = ex.getRequestURI().getQuery();
        if (q != null && q.contains("limit=")) {
            try { limit = Integer.parseInt(q.replaceAll(".*limit=(\\d+).*", "$1")); }
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
            sb.append("]");
            sendJson(ex, 200, sb.toString());
        } catch (Exception e) {
            sendJson(ex, 500, "{\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleDataset(HttpExchange ex) throws IOException {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null) { sendText(ex, 503, "text/plain", "App not initialized"); return; }
        String csv = app.getStore().toCsvString();
        ex.getResponseHeaders().add("Content-Disposition",
                "attachment; filename=\"opticallab-dataset.csv\"");
        sendText(ex, 200, "text/csv", csv);
    }

    private void handleInfo(HttpExchange ex) throws IOException {
        OpticalLabApp app = OpticalLabApp.getInstance();
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"app\":\"br.ufabc.opticallab\",\"version\":\"1.0.0\"");
        sb.append(",\"active\":").append(app != null);
        if (app != null) {
            sb.append(",\"collectCount\":").append(app.getCollectCount());
            sb.append(",\"intervalSeconds\":").append(app.getIntervalSeconds());
            sb.append(",\"historySize\":").append(app.getStore().size());
        }
        sb.append("}");
        sendJson(ex, 200, sb.toString());
    }

    private void handleUI(HttpExchange ex) throws IOException {
        sendText(ex, 200, "text/html; charset=UTF-8",
                OpticalLabWebResource.buildDashboardHtml());
    }

    private void handleRoot(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if ("/".equals(path) || path.isEmpty()) {
            ex.getResponseHeaders().add("Location", "/ui");
            ex.sendResponseHeaders(302, -1);
            ex.close();
        } else {
            sendText(ex, 404, "text/plain", "Not found");
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void sendJson(HttpExchange ex, int code, String json) throws IOException {
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        byte[] b = json.getBytes("UTF-8");
        ex.sendResponseHeaders(code, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    private void sendText(HttpExchange ex, int code, String ct, String body) throws IOException {
        ex.getResponseHeaders().add("Content-Type", ct);
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        byte[] b = body.getBytes("UTF-8");
        ex.sendResponseHeaders(code, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\"", "'");
    }
}
