package org.onosproject.opticallab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
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
            case "/ui":          return html(buildDashboardHtml());
            case "/":            return redirect("/ui");
            default:             return text(404, "Not found");
        }
    }

    // ── serialização de dados ─────────────────────────────────────────────────

    private String statusJson() {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null || app.getStore().getLatest() == null) return "{}";
        return mapToJson(app.getStore().getLatest().toMap());
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
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < history.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(mapToJson(history.get(i).toMap()));
        }
        return sb.append("]").toString();
    }

    /** Serializa Map<String,Object> para JSON sem dependências externas. */
    @SuppressWarnings("unchecked")
    private static String mapToJson(Map<String, Object> map) {
        if (map == null) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(esc(e.getKey())).append("\":");
            Object v = e.getValue();
            if (v == null)                          sb.append("null");
            else if (v instanceof Boolean)          sb.append(v);
            else if (v instanceof Number)           sb.append(v);
            else if (v instanceof List)             sb.append(listToJson((List<?>) v));
            else if (v instanceof Map)              sb.append(mapToJson((Map<String,Object>) v));
            else                                    sb.append("\"").append(esc(v.toString())).append("\"");
        }
        return sb.append("}").toString();
    }

    @SuppressWarnings("unchecked")
    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            Object v = list.get(i);
            if (v == null)        sb.append("null");
            else if (v instanceof int[]) {
                int[] arr = (int[]) v;
                sb.append("{\"ingress\":").append(arr[0]).append(",\"egress\":").append(arr[1]).append("}");
            }
            else if (v instanceof Map)  sb.append(mapToJson((Map<String,Object>) v));
            else if (v instanceof List) sb.append(listToJson((List<?>) v));
            else if (v instanceof Number || v instanceof Boolean) sb.append(v);
            else sb.append("\"").append(esc(v.toString())).append("\"");
        }
        return sb.append("]").toString();
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

    // ── Dashboard HTML ────────────────────────────────────────────────────────

    static String buildDashboardHtml() {
        return "<!DOCTYPE html>\n" +
"<html lang='pt-BR'>\n" +
"<head>\n" +
"<meta charset='UTF-8'>\n" +
"<meta name='viewport' content='width=device-width, initial-scale=1'>\n" +
"<title>Optical Lab Monitor — UFABC</title>\n" +
"<style>\n" +
":root{--bg:#1a1a2e;--panel:#16213e;--border:#0f3460;--accent:#e94560;" +
"--ok:#27ae60;--warn:#f39c12;--err:#e74c3c;--text:#eaf0fb;--muted:#8899aa;}\n" +
"*{box-sizing:border-box;margin:0;padding:0;}\n" +
"body{background:var(--bg);color:var(--text);font-family:monospace,sans-serif;" +
"font-size:14px;min-height:100vh;}\n" +
"header{background:var(--border);padding:12px 20px;display:flex;align-items:center;" +
"justify-content:space-between;border-bottom:2px solid var(--accent);}\n" +
"header h1{font-size:18px;letter-spacing:1px;color:var(--accent);}\n" +
"#status-badge{padding:4px 10px;border-radius:4px;font-size:12px;" +
"background:#333;color:var(--muted);}\n" +
"main{padding:16px;display:grid;grid-template-columns:1fr 1fr;gap:16px;}\n" +
"@media(max-width:900px){main{grid-template-columns:1fr;}}\n" +
".card{background:var(--panel);border:1px solid var(--border);border-radius:6px;" +
"padding:14px;}\n" +
".card h2{font-size:13px;text-transform:uppercase;letter-spacing:1px;" +
"color:var(--muted);margin-bottom:10px;border-bottom:1px solid var(--border);" +
"padding-bottom:6px;}\n" +
"table{width:100%;border-collapse:collapse;font-size:12px;}\n" +
"th{text-align:left;color:var(--muted);padding:4px 6px;border-bottom:" +
"1px solid var(--border);font-weight:normal;}\n" +
"td{padding:4px 6px;border-bottom:1px solid #1e2a3a;vertical-align:top;}\n" +
"tr:last-child td{border-bottom:none;}\n" +
".ok{color:var(--ok);}  .warn{color:var(--warn);}  .err{color:var(--err);}\n" +
".badge{display:inline-block;padding:2px 6px;border-radius:3px;font-size:11px;}\n" +
".badge-ok{background:#1a3a2a;color:var(--ok);}\n" +
".badge-warn{background:#3a2a10;color:var(--warn);}\n" +
".badge-err{background:#3a1a1a;color:var(--err);}\n" +
".kv-grid{display:grid;grid-template-columns:auto 1fr;gap:4px 12px;font-size:12px;}\n" +
".kv-key{color:var(--muted);}\n" +
".kv-val{color:var(--text);}\n" +
"#history-table-wrap{max-height:300px;overflow-y:auto;}\n" +
"#history-table-wrap table td,#history-table-wrap table th{font-size:11px;}\n" +
"footer{padding:10px 20px;color:var(--muted);font-size:11px;text-align:center;" +
"border-top:1px solid var(--border);}\n" +
"button{background:var(--accent);color:#fff;border:none;padding:6px 14px;" +
"border-radius:4px;cursor:pointer;font-size:12px;font-family:monospace;}\n" +
"button:hover{opacity:0.85;}\n" +
"#refresh-bar{background:var(--panel);border-bottom:1px solid var(--border);" +
"padding:8px 20px;display:flex;align-items:center;gap:16px;font-size:12px;color:var(--muted);}\n" +
"progress{height:4px;width:120px;accent-color:var(--accent);}\n" +
".full-width{grid-column:1/-1;}\n" +
"</style>\n" +
"</head>\n" +
"<body>\n" +
"<header>\n" +
"  <h1>Optical Lab Monitor - UFABC</h1>\n" +
"  <span id='status-badge'>Carregando...</span>\n" +
"</header>\n" +
"<div id='refresh-bar'>\n" +
"  <span>Ultima atualizacao: <span id='last-update'>-</span></span>\n" +
"  <progress id='countdown' max='30' value='30'></progress>\n" +
"  <span id='countdown-text'>30s</span>\n" +
"  <button onclick='fetchAll()'>Atualizar agora</button>\n" +
"  <button onclick='downloadCsv()'>Baixar Dataset CSV</button>\n" +
"</div>\n" +
"<main>\n" +
"  <div class='card'>\n" +
"    <h2>Status Geral</h2>\n" +
"    <div class='kv-grid' id='status-grid'>Carregando...</div>\n" +
"  </div>\n" +
"  <div class='card'>\n" +
"    <h2>OXC2 Cross-Connects</h2>\n" +
"    <table id='xconn-table'>\n" +
"      <thead><tr><th>Ingress</th><th>Egress</th><th>Status</th></tr></thead>\n" +
"      <tbody></tbody>\n" +
"    </table>\n" +
"  </div>\n" +
"  <div class='card full-width'>\n" +
"    <h2>Dispositivos Padtec</h2>\n" +
"    <table id='dev-table'>\n" +
"      <thead><tr>\n" +
"        <th>Nome</th><th>Tipo</th><th>Canal</th>\n" +
"        <th>RX WDM (dBm)</th><th>TX WDM (dBm)</th>\n" +
"        <th>RX Client</th><th>TX Client</th>\n" +
"        <th>Gain (dB)</th><th>LOS</th><th>BDI</th><th>FEC Rate</th>\n" +
"        <th>BIP8 Rate</th><th>BEI Rate</th><th>LOF</th>\n" +
"      </tr></thead>\n" +
"      <tbody></tbody>\n" +
"    </table>\n" +
"  </div>\n" +
"  <div class='card full-width'>\n" +
"    <h2>Historico de Coletas <span id='history-count' style='color:var(--muted);font-size:11px'></span></h2>\n" +
"    <div id='history-table-wrap'>\n" +
"      <table id='history-table'>\n" +
"        <thead><tr>\n" +
"          <th>Timestamp</th><th>PAV Flows</th><th>LLDP Links</th><th>XConn Count</th><th>Padtec OK</th><th>Devices</th>\n" +
"        </tr></thead>\n" +
"        <tbody></tbody>\n" +
"      </table>\n" +
"    </div>\n" +
"  </div>\n" +
"</main>\n" +
"<footer>UFABC OpticalLab Monitor v1.0</footer>\n" +
"<script>\n" +
"const API='http://'+window.location.hostname+':9191';\n" +
"async function fetchJson(p){const r=await fetch(API+p);if(!r.ok)throw new Error(r.status);return r.json();}\n" +
"function fmtPow(v){return v?parseFloat(v).toFixed(2)+' dBm':'-';}\n" +
"function fmtLos(v){\n" +
"  if(v===undefined||v===null||v==='')return '-';\n" +
"  const s=String(v).toLowerCase();\n" +
"  const active=s==='true'||s==='sim'||s==='1';\n" +
"  return active?'<span class=\"badge badge-err\">LOS</span>':'<span class=\"badge badge-ok\">OK</span>';}\n" +
"function fmtBool(v){\n" +
"  if(v===undefined||v===null||v==='')return '-';\n" +
"  const s=String(v).toLowerCase();\n" +
"  const active=s==='true'||s==='sim'||s==='1';\n" +
"  return active?'<span class=\"badge badge-err\">SIM</span>':'<span class=\"badge badge-ok\">NAO</span>';}\n" +
"function fmtFec(v){if(!v||v==='-')return '-';const n=parseFloat(v);return isNaN(n)?'-':n.toExponential(2);}\n" +
"function fmtRate(v){if(!v||v==='-')return '-';const n=parseFloat(v);return isNaN(n)?'-':n.toExponential(2);}\n" +
"function updateStatus(dp){\n" +
"  document.getElementById('status-badge').textContent=dp.padtecAvailable?'Padtec ONLINE':'Padtec OFFLINE';\n" +
"  document.getElementById('status-badge').style.background=dp.padtecAvailable?'#1a3a2a':'#3a1a1a';\n" +
"  document.getElementById('status-badge').style.color=dp.padtecAvailable?'#27ae60':'#e74c3c';\n" +
"  document.getElementById('status-grid').innerHTML=`\n" +
"    <span class='kv-key'>Timestamp</span><span class='kv-val'>${dp.timestamp||'-'}</span>\n" +
"    <span class='kv-key'>Padtec</span><span class='kv-val ${dp.padtecAvailable?'ok':'err'}'>${dp.padtecAvailable?'ONLINE':'OFFLINE'}</span>\n" +
"    <span class='kv-key'>PAV Flows</span><span class='kv-val ${dp.pavFlowsAdded>=4?'ok':'warn'}'>${dp.pavFlowsAdded} / 4</span>\n" +
"    <span class='kv-key'>LLDP Links</span><span class='kv-val ${dp.lldpLinks>0?'ok':'warn'}'>${dp.lldpLinks}</span>\n" +
"    <span class='kv-key'>Cross-Connects</span><span class='kv-val ${dp.crossConnects&&dp.crossConnects.length>0?'ok':'err'}'>${dp.crossConnects?dp.crossConnects.length:0} pares</span>\n" +
"    <span class='kv-key'>Devices</span><span class='kv-val'>${dp.devices?dp.devices.length:0}</span>\n" +
"  `;\n" +
"  const xtbody=document.querySelector('#xconn-table tbody');xtbody.innerHTML='';\n" +
"  const EXPECTED=[[1,13],[2,11],[3,10],[5,9],[6,15],[7,14]];\n" +
"  const actual=new Set((dp.crossConnects||[]).map(p=>p.ingress+'-'+p.egress));\n" +
"  for(const [ing,egr] of EXPECTED){\n" +
"    const present=actual.has(ing+'-'+egr);\n" +
"    const tr=document.createElement('tr');\n" +
"    tr.innerHTML=`<td>${ing}</td><td>${egr}</td><td>${present?'<span class=\"badge badge-ok\">OK</span>':'<span class=\"badge badge-err\">AUSENTE</span>'}</td>`;\n" +
"    xtbody.appendChild(tr);\n" +
"  }\n" +
"  const dtbody=document.querySelector('#dev-table tbody');dtbody.innerHTML='';\n" +
"  for(const d of (dp.devices||[])){\n" +
"    const tr=document.createElement('tr');\n" +
"    tr.innerHTML=`<td>${d.neName||'-'}</td><td>${d.type||'-'}</td><td><b>${d.channel||'-'}</b></td>\n" +
"      <td>${fmtPow(d.inputPowerWDM||d.inputPower)}</td><td>${fmtPow(d.outputPowerWDM||d.outputPower)}</td>\n" +
"      <td>${fmtPow(d.inputPowerClient)}</td><td>${fmtPow(d.outputPowerClient)}</td>\n" +
"      <td>${d.gain?parseFloat(d.gain).toFixed(2)+' dB':'-'}</td>\n" +
"      <td>${fmtLos(d.isLOS!==undefined?d.isLOS:d.LOS)}</td><td>${fmtBool(d.isBDI!==undefined?d.isBDI:d.BDI)}</td><td>${fmtFec(d.fecRate)}</td>\n" +
"      <td>${fmtRate(d.bip8Rate!==undefined?d.bip8Rate:d.BIP8Rate)}</td>\n" +
"      <td>${fmtRate(d.beiRate!==undefined?d.beiRate:(d.BEIrate!==undefined?d.BEIrate:d.beirate))}</td>\n" +
"      <td>${fmtBool(d.isLOF!==undefined?d.isLOF:d.LOF)}</td>`;\n" +
"    dtbody.appendChild(tr);\n" +
"  }\n" +
"}\n" +
"function updateHistory(history){\n" +
"  document.getElementById('history-count').textContent='('+history.length+' pontos)';\n" +
"  const tbody=document.querySelector('#history-table tbody');tbody.innerHTML='';\n" +
"  for(const dp of history.slice(-20).reverse()){\n" +
"    const tr=document.createElement('tr');\n" +
"    tr.innerHTML=`<td>${dp.timestamp||'-'}</td>\n" +
"      <td class='${dp.pavFlowsAdded>=4?\"ok\":\"warn\"}'>${dp.pavFlowsAdded}</td>\n" +
"      <td>${dp.lldpLinks}</td><td>${(dp.crossConnects||[]).length}</td>\n" +
"      <td class='${dp.padtecAvailable?\"ok\":\"err\"}'>${dp.padtecAvailable?'OK':'FAIL'}</td>\n" +
"      <td>${(dp.devices||[]).map(d=>d.neName).join(', ')||'-'}</td>`;\n" +
"    tbody.appendChild(tr);\n" +
"  }\n" +
"}\n" +
"async function fetchAll(){\n" +
"  try{\n" +
"    const [dp,history]=await Promise.all([fetchJson('/status'),fetchJson('/history?limit=60')]);\n" +
"    updateStatus(dp);updateHistory(history);\n" +
"    document.getElementById('last-update').textContent=new Date().toLocaleTimeString();\n" +
"  }catch(e){document.getElementById('status-badge').textContent='Erro de conexao';}\n" +
"}\n" +
"function downloadCsv(){window.location.href=API+'/dataset.csv';}\n" +
"let timeLeft=30;\n" +
"function tick(){timeLeft--;document.getElementById('countdown').value=timeLeft;\n" +
"  document.getElementById('countdown-text').textContent=timeLeft+'s';\n" +
"  if(timeLeft<=0){timeLeft=30;fetchAll();}}\n" +
"setInterval(tick,1000);fetchAll();\n" +
"</script>\n" +
"</body>\n" +
"</html>\n";
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
