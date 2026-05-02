package br.ufabc.controlplane.metropad;

import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.ufabc.equipment.Amplifiers;
import br.ufabc.equipment.OTNTransponder;
import br.ufabc.equipment.Supervisor;
import br.ufabc.equipment.Transponders;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PadtecMonitorJSON3 {
	public static Supervisor sup;
	public static List<NE> monitored;
	
	public static PadtecAgentServer agentePadtec = new PadtecAgentServer();
	
	private static final int THREAD_POOL_SIZE = 10;

	private static final String OUTPUT_DIR = "/tmp/padtec_metrics/";
	
	public static void main(String args[]) {
		
		new Thread(agentePadtec).start();
		System.out.println("Servidor Agente Padtec (Memória RAM) iniciado na porta 10151.");

		monitored = new ArrayList<>();
		
		System.out.println("=== PadtecMonitorJSON ===");
		System.out.println("Iniciando conexão com o supervisor...");
		
		sup = new Supervisor("172.17.36.50", Supervisor.TypeSupervisor.SPVL);
		sup.start();
		
		try {
			System.out.println("Aguardando o supervisor carregar os NEs (20 segundos)...");
			Thread.sleep(20000); 
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Espera interrompida.");
			return;
		}
		
		System.out.println("Coletando elementos de rede...");
		
		List<? extends NE> amplifiers = Amplifiers.getAmplifiers(sup);
		if (!amplifiers.isEmpty()) {
			monitored.addAll(amplifiers);
			System.out.println("Adicionei " + amplifiers.size() + " Amplificadores SPVL para monitoração.");
		} else {
			System.out.println("Não há Amplificadores a serem monitorados em SPVL.");
		}

		List<? extends NE> transponders = Transponders.getTransponders(sup);
		if (!transponders.isEmpty()) {
			monitored.addAll(transponders);
			System.out.println("Adicionei " + transponders.size() + " transponders OTN de SPVL para monitoração.");
		} else {
			System.out.println("Não há Transponders OTN a serem monitorados em SPVL.");
		}
		
		if (!monitored.isEmpty()) {
			System.out.println("\nIniciando monitoramento e exportação JSON de " + monitored.size() + " elementos...");
			exportMetricsToJSON();
		} else {
			System.out.println("Nenhum elemento para monitorar.");
		}
		
		System.out.println("=== Monitoramento concluído ===");
	}
	
	private static void exportMetricsToJSON() {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\n");
		jsonBuilder.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
		jsonBuilder.append("  \"supervisor\": \"172.17.36.50:8886\",\n");
		jsonBuilder.append("  \"devices\": [\n");
		
		boolean first = true;
		for (NE ne : monitored) {
			if (!first) {
				jsonBuilder.append(",\n");
			}
			first = false;
			
			try {
				if (ne instanceof Amplifier) {
					jsonBuilder.append(exportAmplifierJSON((Amplifier) ne));
				} else if (ne instanceof TrpOTNTerminal) {
					jsonBuilder.append(exportOTNTransponderJSON((TrpOTNTerminal) ne));
				} else if (ne instanceof Transponder) {
					jsonBuilder.append(exportTransponderJSON((Transponder) ne));
				}
			} catch (Exception e) {
				System.err.println("Erro ao exportar elemento " + ne.getClass().getSimpleName() + ": " + e.getMessage());
			}
		}
		
		jsonBuilder.append("\n  ]\n");
		jsonBuilder.append("}\n");
		
        enviarParaAgente(jsonBuilder.toString(),timestamp);    	
	}
	
	private static String exportAmplifierJSON(Amplifier amp) {
		Amplifiers amplifier = new Amplifiers(sup, amp);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		StringBuilder json = new StringBuilder();
		json.append("    {\n");
		json.append("      \"type\": \"Amplifier\",\n");
		json.append("      \"name\": \"").append(escapeJSON(amp.getName())).append("\",\n");
		json.append("      \"metrics\": {\n");
		json.append("        \"powerInput\": ").append(amplifier.getPowerInput()).append(",\n");
		json.append("        \"powerOutput\": ").append(amplifier.getPowerOutput()).append(",\n");
		json.append("        \"gain\": ").append(amplifier.getGain()).append(",\n");
		json.append("        \"isAGC\": ").append(amplifier.isAGC()).append(",\n");
		json.append("        \"isLOS\": ").append(amplifier.isLOS()).append("\n");
		json.append("      }\n");
		json.append("    }");
		
		return json.toString();
	}
	
	private static String exportOTNTransponderJSON(TrpOTNTerminal otnTrp) {
		OTNTransponder transponder = new OTNTransponder(sup, otnTrp);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		StringBuilder json = new StringBuilder();
		json.append("    {\n");
		json.append("      \"type\": \"OTNTransponder\",\n");
		json.append("      \"name\": \"").append(escapeJSON(otnTrp.getName())).append("\",\n");
		json.append("      \"metrics\": {\n");
		json.append("        \"channel\": \"").append(escapeJSON(transponder.getChannel())).append("\"\n");
		json.append("      }\n");
		json.append("    }");
		
		return json.toString();
	}
	
	private static String exportTransponderJSON(Transponder transp) {
		Transponders transponder = new Transponders(sup, transp);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		StringBuilder json = new StringBuilder();
		json.append("    {\n");
		json.append("      \"type\": \"Transponder\",\n");
		json.append("      \"name\": \"").append(escapeJSON(transp.getName())).append("\",\n");
		json.append("      \"metrics\": {\n");
		json.append("        \"inputPower\": ").append(transponder.getInputPower()).append(",\n");
		json.append("        \"outputPower\": ").append(transponder.getOutputPower()).append(",\n");
		json.append("        \"channel\": \"").append(escapeJSON(transponder.getChannel())).append("\",\n");
		json.append("        \"lambda\": ").append(transponder.getLambda()).append(",\n");
		json.append("        \"isLOS\": ").append(transponder.isLOS()).append("\n");
		json.append("      }\n");
		json.append("    }");
		
		return json.toString();
	}
	
	private static void enviarParaAgente(String jsonBuilder, String timestamp) {
		try {
			agentePadtec.updateMetrics(jsonBuilder);
			
			System.out.println("[" + timestamp + "] Novas métricas injetadas na porta 10151 para o ONOS.");
			
		} catch (Exception e) {
			System.err.println("Erro ao transferir dados para o Agente em memória: " + e.getMessage());
		}
	}
	
	private static String escapeJSON(String str) {
		if (str == null) return "";
		return str.replace("\\", "\\\\")
		          .replace("\"", "\\\"")
		          .replace("\n", "\\n")
		          .replace("\r", "\\r")
		          .replace("\t", "\\t");
	}
}
