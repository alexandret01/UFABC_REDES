package br.com.padtec.v3.server;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.Command.Data;
import br.com.padtec.v3.data.ne.FanG8;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Supervisor;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.log.Log;

public final class CommandFactory {
	private static Map<Integer, Command.Data> getCodeTable(NE ne)
	{
		if (ne == null) {
			return null;
		}

		if (ne instanceof Supervisor)
			return Command.supTable;
		if (ne instanceof Transponder) {
			return Command.trpTable;
		}
		if (ne instanceof FanG8)
			return Command.fanG8Table;


		Logger log = Log.getInstance();
		log.warning("Tabela de comandos para " + ne.getClass() + " não encontrada");
		return null;
	}

	public static Command createOriginalCommand(NE ne, int commandCode, byte[] parameters)
	{
		Command result;
		//    SHKConfig config;
		if (commandCode < 0) {
			commandCode = Functions.b2i((byte)commandCode);
		}
		int key = -1;

		//    if (ne instanceof SHK) {
		//      result = new Command(ne.getSerial(), 50, parameters, 
		//        commandCode);
		//      int contact = ShkHelper.getContactFromCommand(commandCode);
		//      SHK shk = (SHK)ne;
		//      config = shk.getConfig()[(contact - 1)];
		//      if (config == null)
		//        result.setName(Integer.toString(contact));
		//      else {
		//        result.setName(config.getName() + " - " + contact);
		//      }
		//      key = 50;
		//    } else {
		Map<Integer,Data> table = getCodeTable(ne);
		if (table != null) {
			for (Entry<Integer,Data> item : table.entrySet()) {
				int itemCode = item.getValue().code;
				if (itemCode < 0) {
					itemCode = Functions.b2i((byte)itemCode);
				}
				if (itemCode == commandCode) {
					key = item.getKey().intValue();
				}

			}

		}

		result = new Command(ne.getSerial(), key, parameters);
		//    }

		if (key == -1)
		{
			result.setName(result.getCommandName() + " " + commandCode);
		}
		return result;
	}
}