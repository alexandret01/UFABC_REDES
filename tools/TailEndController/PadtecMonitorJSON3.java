import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.ufabc.controlplane.metropad.Servidor;
import br.ufabc.controlplane.metropad.Servidor.TypeSupervisor;
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

/**
 * Aplicação Java 18 que monitora o transponder Padtec SPVL4 e exporta
 * as métricas em formato JSON para consumo pelo aplicativo ONOS.
 */
public class PadtecMonitorJSON3 {
	public static Supervisor sup;
	public static List<NE> monitored;
	
    // 1. INSTÂNCIA DO SERVIDOR TCP EM MEMÓRIA
	public static PadtecAgentServer agentePadtec = new PadtecAgentServer();
	
    // Define um pool de threads para processamento paralelo
	private static final int THREAD_POOL_SIZE = 10; // Ajuste conforme a necessidade

	// Diretório de saída para os arquivos JSON
	private static final String OUTPUT_DIR = "/tmp/padtec_metrics/";
	
	public static void main(String args[]) {
		
        // 2. INICIALIZAÇÃO DA THREAD DO AGENTE LOGO AO ABRIR O PROGRAMA
		new Thread(agentePadtec).start();
		System.out.println("Servidor Agente Padtec (Memória RAM) iniciado na porta 10151.");

		monitored = new ArrayList<>();
		
		System.out.println("=== PadtecMonitorJSON ===");
		System.out.println("Iniciando conexão com o supervisor...");
		
		// 1. Inicialização do Supervisor
		sup = new Supervisor("172.17.36.50", Supervisor.TypeSupervisor.SPVL);
		sup.start();
		
		// 2. Aguardar o carregamento dos NEs
		try {
			System.out.println("Aguardando o supervisor carregar os NEs (20 segundos)...");
			Thread.sleep(20000); 
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Espera interrompida.");
			return;
		}
		
		// 3. Descoberta e Coleta de Elementos
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
		
		// 4. Monitoramento e Exportação JSON
		if (!monitored.isEmpty()) {
			System.out.println("\nIniciando monitoramento e exportação JSON de " + monitored.size() + " elementos...");
			exportMetricsToJSON();
		} else {
			System.out.println("Nenhum elemento para monitorar.");
		}
		
		System.out.println("=== Monitoramento concluído ===");
	}
	
	/**
	 * Exporta as métricas de todos os elementos monitorados para arquivos JSON.
	 */
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
		
		// Salvar o JSON em arquivo
		//saveJSONToFile(jsonBuilder.toString(), timestamp);
        enviarParaAgente(jsonBuilder.toString(),timestamp);    	
	}
	
	/**
	 * Exporta as métricas de um Amplifier em formato JSON.
	 */
	private static String exportAmplifierJSON(Amplifier amp) {
		Amplifiers amplifier = new Amplifiers(sup, amp);
		
		try {
			Thread.sleep(500); // Pequeno delay para evitar sobrecarga
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
	
	/**
	 * Exporta as métricas de um OTN Transponder em formato JSON.
	 */
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
	
	/**
	 * Exporta as métricas de um Transponder genérico em formato JSON.
	 */
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
	
	/**
	 * Salva o JSON em um arquivo.
	
	private static void saveJSONToFile(String json, String timestamp) {
		String filename = OUTPUT_DIR + "padtec_metrics_" + timestamp + ".json";
		
		try {
			// Criar o diretório se não existir
			new java.io.File(OUTPUT_DIR).mkdirs();
			
			FileWriter writer = new FileWriter(filename);
			writer.write(json);
			writer.close();
			
			System.out.println("Métricas exportadas para: " + filename);
			
			// Também criar um link simbólico para o arquivo mais recente
			String latestFile = OUTPUT_DIR + "padtec_metrics_latest.json";
			FileWriter latestWriter = new FileWriter(latestFile);
			latestWriter.write(json);
			latestWriter.close();
			
			System.out.println("Arquivo mais recente: " + latestFile);
			
		} catch (IOException e) {
			System.err.println("Erro ao salvar arquivo JSON: " + e.getMessage());
		}
	}**/
	
    // 4. O MÉTODO DE GRAVAÇÃO FOI SUBSTITUÍDO PELO MÉTODO ABAIXO
	/**
	 * Envia a String do JSON diretamente para a memória do Agente Servidor,
	 * substituindo a gravação em disco rígido.
	 */
	private static void enviarParaAgente(String jsonBuilder, String timestamp) {
		try {
			// Injeta a string construída diretamente na memória volátil do Agente
			agentePadtec.updateMetrics(jsonBuilder);
			
			// Imprime no console do Linux apenas para auditoria visual
			System.out.println("[" + timestamp + "] Novas métricas injetadas na porta 10151 para o ONOS.");
			
		} catch (Exception e) {
			System.err.println("Erro ao transferir dados para o Agente em memória: " + e.getMessage());
		}
	}
	
	// ... (Mantenha o restante das suas funções de utilidade no final do arquivo) ...
  
	/**
	 * Escapa caracteres especiais para JSON.
	 */
	private static String escapeJSON(String str) {
		if (str == null) return "";
		return str.replace("\\", "\\\\")
		          .replace("\"", "\\\"")
		          .replace("\n", "\\n")
		          .replace("\r", "\\r")
		          .replace("\t", "\\t");
	}
}

