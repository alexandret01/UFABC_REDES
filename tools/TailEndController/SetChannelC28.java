import java.util.Vector;
import java.util.logging.Logger;

import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.ufabc.equipment.OTNTransponder;
import br.ufabc.equipment.Supervisor;

/**
 * SetChannelC28 — Configura o canal C28 (1554.94 nm) em transponders Padtec T100DCT.
 *
 * Uso (no servidor optinet):
 *   cd /home/sdn/onos27/drivers/padtec/git/UFABC_REDES/tools/TailEndController
 *   /usr/lib/jvm/jdk-18.0.2.1/bin/javac -cp "./lib/*:." SetChannelC28.java
 *   /usr/lib/jvm/jdk-18.0.2.1/bin/java -Djava.library.path=./lib/ -cp "./lib/*:." SetChannelC28
 *
 * Argumentos opcionais:
 *   SetChannelC28 [nome_transponder] [canal]
 *   Ex: SetChannelC28 "T100DCT-4GTT2L#27" C28
 *       SetChannelC28 (sem args = configura todos os OTNTransponders para C28)
 */
public class SetChannelC28 {

    public static void main(String[] args) throws Exception {
        String targetName   = (args.length > 0) ? args[0] : null;
        String targetChannel = (args.length > 1) ? args[1] : "C28";

        System.out.println("=".repeat(60));
        System.out.println("  SetChannelC28 — Configuração de canal Padtec");
        System.out.println("  Supervisor: 172.17.36.50");
        System.out.printf ("  Canal alvo: %s%n", targetChannel);
        if (targetName != null)
            System.out.printf("  Transponder alvo: %s%n", targetName);
        else
            System.out.println("  Alvo: todos os OTNTransponders");
        System.out.println("=".repeat(60));

        // 1. Conecta ao supervisor
        System.out.println("\n[1] Conectando ao supervisor Padtec...");
        Supervisor sup = new Supervisor("172.17.36.50", Supervisor.TypeSupervisor.SPVL);

        System.out.println("  Aguardando o supervisor carregar os NEs (20 segundos)...");
        Thread.sleep(20000);

        Vector<NE> nes = sup.getNEs();
        if (nes == null || nes.isEmpty()) {
            System.out.println("  ERRO: nenhum NE carregado. Verifique conexão com 172.17.36.50.");
            System.exit(1);
        }
        System.out.printf("  %d NE(s) carregado(s).%n", nes.size());

        // 2. Lista e configura transponders
        System.out.println("\n[2] OTNTransponders encontrados:");
        int changed = 0, errors = 0;

        for (NE ne : nes) {
            if (!(ne instanceof TrpOTNTerminal)) continue;

            TrpOTNTerminal trp = (TrpOTNTerminal) ne;
            OTNTransponder otn = new OTNTransponder(sup, trp);

            String name    = otn.getName();
            String current = otn.getChannel();
            System.out.printf("  Transponder: %-30s  Canal atual: %s%n", name, current);

            // Filtro por nome (se especificado)
            if (targetName != null && !name.contains(targetName)) {
                System.out.printf("    -> Pulando (não é o alvo '%s')%n", targetName);
                continue;
            }

            if (targetChannel.equals(current)) {
                System.out.printf("    -> Já está em %s. Nada a fazer.%n", targetChannel);
                continue;
            }

            // Tenta configurar
            System.out.printf("    -> Configurando para %s...%n", targetChannel);
            try {
                otn.setChannel(targetChannel);
                Thread.sleep(3000); // aguarda o equipamento processar

                // Verifica se mudou
                String newChannel = otn.getChannel();
                if (targetChannel.equals(newChannel)) {
                    System.out.printf("    ✓ Canal configurado com sucesso: %s%n", newChannel);
                    changed++;
                } else {
                    System.out.printf("    ✗ Canal após comando: %s (esperado: %s)%n", newChannel, targetChannel);
                    System.out.println("      Nota: setChannel() é marcado como 'NOT WORKING' no código.");
                    System.out.println("      O equipamento pode ter rejeitado o comando PPMv3.");
                    errors++;
                }
            } catch (Exception e) {
                System.out.printf("    ✗ Erro ao configurar: %s%n", e.getMessage());
                e.printStackTrace();
                errors++;
            }
        }

        // 3. Resultado
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("  Resultado: %d alterado(s), %d erro(s)%n", changed, errors);

        if (changed == 0 && errors == 0) {
            System.out.println("  Nenhuma alteração necessária (canais já corretos).");
        } else if (changed == 0 && errors > 0) {
            System.out.println("  FALHA: o comando PPMv3 de canal pode não ser suportado.");
            System.out.println("  Alternativas:");
            System.out.println("    1. Acesso físico ao painel do T100DCT#27");
            System.out.println("    2. SSH: bash tools/ssh_padtec.sh");
            System.out.println("    3. Interface web (se disponível): http://172.17.36.50");
        }
        System.out.println("=".repeat(60));

        System.exit(changed > 0 ? 0 : (errors > 0 ? 2 : 0));
    }
}
