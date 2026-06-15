package org.onosproject.opticallab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.osgi.service.component.annotations.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * JAX-RS REST API + Dashboard HTML do Optical Lab Monitor.
 *
 * Registrado como serviço OSGi (@Component) para ser descoberto pelo
 * BundleContextUtils do onos-rest, que serve todos os recursos @Path encontrados
 * no registro OSGi sob o contexto /onos/v1/.
 *
 * Endpoints (base: /onos/v1/opticallab):
 *   GET /onos/v1/opticallab/status       — snapshot atual (JSON)
 *   GET /onos/v1/opticallab/history      — série histórica (JSON array)
 *   GET /onos/v1/opticallab/dataset.csv  — dataset completo (CSV download)
 *   GET /onos/v1/opticallab/ui           — dashboard HTML
 *   GET /onos/v1/opticallab/info         — info do app (JSON)
 */
@Component(immediate = true, service = OpticalLabWebResource.class)
@Path("opticallab")
public class OpticalLabWebResource {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ── REST API ──────────────────────────────────────────────────────────────

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null) {
            return errorJson("App não inicializado", 503);
        }
        DataPoint dp = app.getStore().getLatest();
        if (dp == null) {
            return errorJson("Nenhuma coleta realizada ainda", 204);
        }
        try {
            String json = MAPPER.writeValueAsString(dp.toMap());
            return Response.ok(json, MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            return errorJson("Erro ao serializar: " + e.getMessage(), 500);
        }
    }

    @GET
    @Path("history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory(@QueryParam("limit") Integer limit) {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null) {
            return errorJson("App não inicializado", 503);
        }
        List<DataPoint> history = app.getStore().getHistory();
        if (limit != null && limit > 0 && limit < history.size()) {
            history = history.subList(history.size() - limit, history.size());
        }
        try {
            ArrayNode arr = MAPPER.createArrayNode();
            for (DataPoint dp : history) {
                arr.addPOJO(dp.toMap());
            }
            return Response.ok(MAPPER.writeValueAsString(arr), MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            return errorJson("Erro ao serializar histórico: " + e.getMessage(), 500);
        }
    }

    @GET
    @Path("dataset.csv")
    @Produces("text/csv")
    public Response getDataset() {
        OpticalLabApp app = OpticalLabApp.getInstance();
        if (app == null) {
            return errorJson("App não inicializado", 503);
        }
        String csv = app.getStore().toCsvString();
        return Response.ok(csv)
                .header("Content-Disposition",
                        "attachment; filename=\"opticallab-dataset.csv\"")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo() {
        OpticalLabApp app = OpticalLabApp.getInstance();
        try {
            ObjectNode n = MAPPER.createObjectNode();
            n.put("app", "br.ufabc.opticallab");
            n.put("version", "1.0.0");
            n.put("active", app != null);
            if (app != null) {
                n.put("collectCount", app.getCollectCount());
                n.put("intervalSeconds", app.getIntervalSeconds());
                n.put("historySize", app.getStore().size());
            }
            return Response.ok(MAPPER.writeValueAsString(n), MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            return errorJson(e.getMessage(), 500);
        }
    }

    // ── HTML Dashboard ────────────────────────────────────────────────────────

    @GET
    @Path("ui")
    @Produces(MediaType.TEXT_HTML)
    public Response getDashboard() {
        return Response.ok(buildDashboardHtml(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getRoot() {
        return Response.temporaryRedirect(
                java.net.URI.create("/onos/v1/opticallab/ui")).build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Response errorJson(String msg, int status) {
        String body = "{\"error\":\"" + msg.replace("\"", "'") + "\"}";
        return Response.status(status).entity(body)
                .type(MediaType.APPLICATION_JSON).build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HTML Dashboard — chamado também pelo OpticalLabHttpServer
    // ─────────────────────────────────────────────────────────────────────────

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
"  <h1>⬡ Optical Lab Monitor — UFABC</h1>\n" +
"  <span id='status-badge'>Carregando...</span>\n" +
"</header>\n" +
"<div id='refresh-bar'>\n" +
"  <span>Última atualização: <span id='last-update'>—</span></span>\n" +
"  <progress id='countdown' max='30' value='30'></progress>\n" +
"  <span id='countdown-text'>30s</span>\n" +
"  <button onclick='fetchAll()'>↺ Atualizar agora</button>\n" +
"  <button onclick='downloadCsv()'>⬇ Baixar Dataset CSV</button>\n" +
"</div>\n" +
"<main>\n" +

// Card: Status Geral
"  <div class='card'>\n" +
"    <h2>Status Geral</h2>\n" +
"    <div class='kv-grid' id='status-grid'>Carregando...</div>\n" +
"  </div>\n" +

// Card: OXC2 Cross-Connects
"  <div class='card'>\n" +
"    <h2>OXC2 Cross-Connects</h2>\n" +
"    <table id='xconn-table'>\n" +
"      <thead><tr><th>Ingress</th><th>Egress</th><th>Status</th></tr></thead>\n" +
"      <tbody></tbody>\n" +
"    </table>\n" +
"  </div>\n" +

// Card: Transponders / Amplificadores
"  <div class='card full-width'>\n" +
"    <h2>Dispositivos Padtec</h2>\n" +
"    <table id='dev-table'>\n" +
"      <thead><tr>\n" +
"        <th>Nome</th><th>Tipo</th><th>Canal</th>\n" +
"        <th>RX WDM (dBm)</th><th>TX WDM (dBm)</th>\n" +
"        <th>RX Client</th><th>TX Client</th>\n" +
"        <th>Gain (dB)</th><th>LOS</th><th>BDI</th><th>FEC Rate</th>\n" +
"      </tr></thead>\n" +
"      <tbody></tbody>\n" +
"    </table>\n" +
"  </div>\n" +

// Card: Histórico (últimos 20)
"  <div class='card full-width'>\n" +
"    <h2>Histórico de Coletas <span id='history-count' style='color:var(--muted);font-size:11px'></span></h2>\n" +
"    <div id='history-table-wrap'>\n" +
"      <table id='history-table'>\n" +
"        <thead><tr>\n" +
"          <th>Timestamp</th><th>PAV Flows</th><th>LLDP Links</th>" +
"<th>XConn Count</th><th>Padtec OK</th><th>Devices</th>\n" +
"        </tr></thead>\n" +
"        <tbody></tbody>\n" +
"      </table>\n" +
"    </div>\n" +
"  </div>\n" +

"</main>\n" +
"<footer>UFABC OpticalLab Monitor v1.0 — dados coletados a cada 60s do agente Padtec (TCP:10151) e OXC2 REST (172.17.36.22:8008)</footer>\n" +

"<script>\n" +
"const API = 'http://' + window.location.hostname + ':" + OpticalLabHttpServer.PORT + "';\n" +

"async function fetchJson(path){\n" +
"  const r = await fetch(API+path);\n" +
"  if(!r.ok) throw new Error(r.status);\n" +
"  return r.json();\n" +
"}\n" +

"function fmtPow(v){ return v ? parseFloat(v).toFixed(2)+' dBm' : '—'; }\n" +
"function fmtBool(v){ if(v===undefined||v===null||v==='') return '—';\n" +
"  const b = String(v).toLowerCase()==='true';\n" +
"  return b ? '<span class=\"badge badge-err\">SIM</span>' : '<span class=\"badge badge-ok\">NÃO</span>'; }\n" +
"function fmtLos(v){\n" +
"  if(v===undefined||v===null||v==='') return '—';\n" +
"  const b = String(v).toLowerCase()==='true';\n" +
"  return b ? '<span class=\"badge badge-err\">LOS</span>' : '<span class=\"badge badge-ok\">OK</span>'; }\n" +
"function fmtFec(v){ return v ? parseFloat(v).toExponential(2) : '—'; }\n" +

"function updateStatus(dp){\n" +
"  document.getElementById('status-badge').textContent =\n" +
"    dp.padtecAvailable ? '✓ Padtec ONLINE' : '✗ Padtec OFFLINE';\n" +
"  document.getElementById('status-badge').style.background =\n" +
"    dp.padtecAvailable ? '#1a3a2a' : '#3a1a1a';\n" +
"  document.getElementById('status-badge').style.color =\n" +
"    dp.padtecAvailable ? '#27ae60' : '#e74c3c';\n" +

"  const xok = dp.crossConnects && dp.crossConnects.length > 0;\n" +
"  const grid = document.getElementById('status-grid');\n" +
"  grid.innerHTML = `\n" +
"    <span class='kv-key'>Timestamp</span><span class='kv-val'>${dp.timestamp||'—'}</span>\n" +
"    <span class='kv-key'>Padtec</span><span class='kv-val ${dp.padtecAvailable?'ok':'err'}'>${dp.padtecAvailable?'✓ ONLINE':'✗ OFFLINE'}</span>\n" +
"    <span class='kv-key'>PAV Flows (ADDED)</span><span class='kv-val ${dp.pavFlowsAdded>=4?'ok':'warn'}'>${dp.pavFlowsAdded} / 4 esperados</span>\n" +
"    <span class='kv-key'>LLDP Links</span><span class='kv-val ${dp.lldpLinks>0?'ok':'warn'}'>${dp.lldpLinks}</span>\n" +
"    <span class='kv-key'>Cross-Connects</span><span class='kv-val ${xok?'ok':'err'}'>${dp.crossConnects?dp.crossConnects.length:0} pares</span>\n" +
"    <span class='kv-key'>Devices coletados</span><span class='kv-val'>${dp.devices?dp.devices.length:0}</span>\n" +
"  `;\n" +

"  // Cross-connects table\n" +
"  const xtbody = document.querySelector('#xconn-table tbody');\n" +
"  xtbody.innerHTML = '';\n" +
"  const EXPECTED = [[1,13],[2,11],[3,10],[5,9],[6,15],[7,14]];\n" +
"  const actual = new Set((dp.crossConnects||[]).map(p=>p.ingress+'-'+p.egress));\n" +
"  for(const [ing,egr] of EXPECTED){\n" +
"    const present = actual.has(ing+'-'+egr);\n" +
"    const tr = document.createElement('tr');\n" +
"    tr.innerHTML = `<td>${ing}</td><td>${egr}</td>` +\n" +
"      `<td>${present?'<span class=\"badge badge-ok\">✓ OK</span>':'<span class=\"badge badge-err\">✗ AUSENTE</span>'}</td>`;\n" +
"    xtbody.appendChild(tr);\n" +
"  }\n" +

"  // Devices table\n" +
"  const dtbody = document.querySelector('#dev-table tbody');\n" +
"  dtbody.innerHTML = '';\n" +
"  for(const d of (dp.devices||[])){\n" +
"    const tr = document.createElement('tr');\n" +
"    tr.innerHTML = `\n" +
"      <td>${d.neName||'—'}</td>\n" +
"      <td>${d.type||'—'}</td>\n" +
"      <td><b>${d.channel||'—'}</b></td>\n" +
"      <td>${fmtPow(d.inputPowerWDM||d.inputPower)}</td>\n" +
"      <td>${fmtPow(d.outputPowerWDM||d.outputPower)}</td>\n" +
"      <td>${fmtPow(d.inputPowerClient)}</td>\n" +
"      <td>${fmtPow(d.outputPowerClient)}</td>\n" +
"      <td>${d.gain?parseFloat(d.gain).toFixed(2)+' dB':'—'}</td>\n" +
"      <td>${fmtLos(d.isLOS)}</td>\n" +
"      <td>${fmtBool(d.isBDI)}</td>\n" +
"      <td>${fmtFec(d.fecRate)}</td>\n" +
"    `;\n" +
"    dtbody.appendChild(tr);\n" +
"  }\n" +
"}\n" +

"function updateHistory(history){\n" +
"  document.getElementById('history-count').textContent =\n" +
"    '(' + history.length + ' pontos)';\n" +
"  const tbody = document.querySelector('#history-table tbody');\n" +
"  tbody.innerHTML = '';\n" +
"  const last20 = history.slice(-20).reverse();\n" +
"  for(const dp of last20){\n" +
"    const tr = document.createElement('tr');\n" +
"    tr.innerHTML = `\n" +
"      <td>${dp.timestamp||'—'}</td>\n" +
"      <td class='${dp.pavFlowsAdded>=4?\"ok\":\"warn\"}'>${dp.pavFlowsAdded}</td>\n" +
"      <td>${dp.lldpLinks}</td>\n" +
"      <td>${(dp.crossConnects||[]).length}</td>\n" +
"      <td class='${dp.padtecAvailable?\"ok\":\"err\"}'>${dp.padtecAvailable?'✓':'✗'}</td>\n" +
"      <td>${(dp.devices||[]).map(d=>d.neName).join(', ')||'—'}</td>\n" +
"    `;\n" +
"    tbody.appendChild(tr);\n" +
"  }\n" +
"}\n" +

"async function fetchAll(){\n" +
"  try{\n" +
"    const [dp, history] = await Promise.all([\n" +
"      fetchJson('/status'),\n" +
"      fetchJson('/history?limit=60')\n" +
"    ]);\n" +
"    updateStatus(dp);\n" +
"    updateHistory(history);\n" +
"    document.getElementById('last-update').textContent = new Date().toLocaleTimeString();\n" +
"  }catch(e){\n" +
"    console.error('Fetch error:', e);\n" +
"    document.getElementById('status-badge').textContent = '✗ Erro de conexão';\n" +
"  }\n" +
"}\n" +

"function downloadCsv(){\n" +
"  window.location.href = API + '/dataset.csv';\n" +
"}\n" +

"// Countdown & auto-refresh\n" +
"let timeLeft = 30;\n" +
"function tick(){\n" +
"  timeLeft--;\n" +
"  document.getElementById('countdown').value = timeLeft;\n" +
"  document.getElementById('countdown-text').textContent = timeLeft + 's';\n" +
"  if(timeLeft <= 0){ timeLeft=30; fetchAll(); }\n" +
"}\n" +
"setInterval(tick, 1000);\n" +
"fetchAll();\n" +
"</script>\n" +
"</body>\n" +
"</html>\n";
    }
}
