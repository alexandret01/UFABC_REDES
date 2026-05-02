package br.com.padtec.v3.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import br.com.padtec.v3.data.impl.FEC_Impl;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.FanG8;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.PBAmp;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.util.log.Log;

public class NEPerformanceData {
	private static final int[] TYPE_B1_ = { 288, 289, 290, 291 };

	private static final int[] TYPE_PIN = { 12, 7, 14, 16, 18, 20, 22, 24 };

	private static final int[] TYPE_POUT = { 13, 8, 15, 17, 19, 21, 23, 25 };

	private static final int[] TYPE_TEMP = { 45, 46 };

	public static Map<Integer, Number> getPerformanceData(NE ne)  {
		TreeMap<Integer, Number> result = new TreeMap<Integer, Number>();
		try {
			if ((ne instanceof Transponder) && (!(ne instanceof TrpOTNTerminal))) {
				Transponder t = (Transponder)ne;
				result.put(1, t.getPin());
				result.put(2, t.getPout());
			}
			if (ne instanceof Amplifier) {
				Amplifier a = (Amplifier)ne;
				if (a instanceof PBAmp) {
					PBAmp pa = (PBAmp)a;
					result.put(1, pa.getPin());
				}

			}
			if (ne instanceof TrpOTNTerminal) {
				TrpOTNTerminal otn = (TrpOTNTerminal)ne;
				if (otn.getFEC().getFecType().compareTo(FEC.FEC_Type.ENHANCED_FEC) == 0)
					result.put(Integer.valueOf(297), (Number)((FEC_Impl)
							otn.getFEC()).clone());
				else {
					result.put(54, (Number)((FEC_Impl)otn.getFEC()).clone());
				}

				double aux_pin_rede = 0.0D;
				double aux_pout_rede = 0.0D;
				double aux_pin_client = 0.0D;
				double aux_pout_client = 0.0D;

				TrpOTNTerminal trpT = (TrpOTNTerminal)ne;
				aux_pin_rede = trpT.getOpticalWDMInterface().getPin();
				aux_pout_rede = trpT.getOpticalWDMInterface().getPout();
				aux_pin_client = trpT.getOpticalClientInterface().getPin();
				aux_pout_client = trpT.getOpticalClientInterface().getPout();
				result.put(59, aux_pin_rede);
				result.put(60, aux_pout_rede);
				result.put(61, aux_pin_client);
				result.put(62, aux_pout_client);
				BigInteger bip8_odu = otn.getODUk().getBip8();
				result.put(5, bip8_odu);
				BigInteger bip8_otu = otn.getOTUk().getBip8();
				result.put(11, bip8_otu);
				Double bei_odu = otn.getODUk().getBEIRate();
				result.put(39, bei_odu);
				Double bei_otu = otn.getOTUk().getBEIRate();
				result.put(40, bei_otu);
			}

			if (Functions.compareVersions(ne.getVersion(), "1.15") >= 0)      {
				if (ne instanceof TrpOTNTerminal) {
					TrpOTNTerminal trpS = (TrpOTNTerminal)ne;
					result.put(299, trpS.getOpticalWDMInterface().getModuleTemperature());
					result.put(300, trpS.getOpticalClientInterface().getModuleTemperature());
				}
			}

			if (ne instanceof FanG8) {
				FanG8 fanG8 = (FanG8)ne;
				if (fanG8.haveTemperatureSensors()) {
					result.put(50, fanG8.getTemperatureSensor(0).getTemperature());
					result.put(51, fanG8.getTemperatureSensor(1).getTemperature());
				}
			}
		} catch (RuntimeException e) {
			log("Fail in getPerformanceData for " + ne, e);
		}
		return result;
	}

	public static PerformanceInfo getPerformanceInfo(int perfType) {
		switch (perfType) { 
		case 1:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 1);
		case 12:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 1);
		case 7:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 2);
		case 14:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 3);
		case 16:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 4);
		case 18:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 5);
		case 20:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 6);
		case 22:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 7);
		case 24:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 8);
		case 41:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", true, 1);
		case 43:
		case 97:
		case 98:
		case 99:
		case 100:
		case 101:
		case 102:
		case 103:
		case 104:
		case 105:
		case 106:
		case 107:
		case 108:
		case 109:
		case 110:
		case 111:
		case 112:
		case 113:
		case 114:
		case 115:
		case 116:
		case 117:
		case 118:
		case 119:
		case 120:
		case 121:
		case 122:
		case 123:
		case 124:
		case 125:
		case 126:
		case 127:
		case 128:
		case 129:
		case 130:
		case 131:
		case 132:
		case 133:
		case 134:
		case 135:
		case 136:
		case 218:
			return new PerformanceInfo("Pin", Msg.getString("JDialogThreshold.0"), "dBm", false, 0);
		case 2:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 1);
		case 13:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 1);
		case 8:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 2);
		case 15:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 3);
		case 17:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 4);
		case 19:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 5);
		case 21:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 6);
		case 23:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 7);
		case 25:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 8);
		case 42:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", true, 1);
		case 44:
		case 137:
		case 138:
		case 139:
		case 140:
		case 141:
		case 142:
		case 143:
		case 144:
		case 145:
		case 146:
		case 147:
		case 148:
		case 149:
		case 150:
		case 151:
		case 152:
		case 153:
		case 154:
		case 155:
		case 156:
		case 157:
		case 158:
		case 159:
		case 160:
		case 161:
		case 162:
		case 163:
		case 164:
		case 165:
		case 166:
		case 167:
		case 168:
		case 169:
		case 170:
		case 171:
		case 172:
		case 173:
		case 174:
		case 175:
		case 176:
		case 177:
		case 178:
		case 179:
		case 180:
		case 181:
		case 182:
		case 183:
		case 184:
		case 185:
		case 186:
		case 187:
		case 188:
		case 189:
		case 190:
		case 191:
		case 192:
		case 193:
		case 194:
		case 195:
		case 196:
		case 197:
		case 198:
		case 199:
		case 200:
		case 201:
		case 202:
		case 203:
		case 204:
		case 205:
		case 206:
		case 207:
		case 208:
		case 209:
		case 210:
		case 211:
		case 212:
		case 213:
		case 214:
		case 215:
		case 216:
		case 217:
			return new PerformanceInfo("Pout", Msg.getString("JDialogThreshold.1"), "dBm", false, 0);
		case 55:
			return new PerformanceInfo("Pin Lado 1", "Potência de Entrada Lado 1", "dBm", true, 1);
		case 56:
			return new PerformanceInfo("Pout Lado 1", "Potência de Saída Lado 1", "dBm", true, 1);
		case 57:
			return new PerformanceInfo("Pin Lado 2", "Potência de Entrada Lado 2", "dBm", true, 2);
		case 58:
			return new PerformanceInfo("Pout Lado 2", "Potência de Saída Lado 2", "dBm", true, 2);
		case 59:
			return new PerformanceInfo("Pin Rede", "Potência de Entrada Lado Rede", "dBm", true, 1);
		case 60:
			return new PerformanceInfo("Pout Rede", "Potência de Saída Lado Rede", "dBm", true, 2);
		case 61:
			return new PerformanceInfo("Pin Cliente", "Potência de Entrada Lado Cliente", "dBm", true, 1);
		case 62:
			return new PerformanceInfo("Pout Cliente", "Potência de Saída Lado Cliente", "dBm", true, 2);
		case 63:
			return new PerformanceInfo("Pot. Bombeio", "Potência de Bombeio", "dBm", false, 0);
		case 3:
			return new PerformanceInfo("Lambda", Msg.getString("JDialogThreshold.2"), "nm", true, 1);
		case 9:
			return new PerformanceInfo("Lambda", Msg.getString("JDialogThreshold.2"), "nm", true, 2);
		case 4:
			return new PerformanceInfo("Taxa", Msg.getString("JDialogThreshold.3"), "bps", true, 1);
		case 5:
			return new PerformanceInfo("BIP8 ODU", "BIP-8 ODU", "", true, 1);
		case 11:
			return new PerformanceInfo("BIP8 OTU", "BIP-8 OTU", "", true, 1);
		case 53:
			return new PerformanceInfo("BIP8 OTU Lado 1", "BIP-8 OTU Lado 1", "", true, 1);
		case 34:
			return new PerformanceInfo("BIP8 OTU Lado 2", "BIP-8 OTU Lado 2", "", true, 2);
		case 287:
			return new PerformanceInfo("B1", "B1", "", true, 1);
		case 288:
			return new PerformanceInfo("B1", "B1", "", true, 1);
		case 289:
			return new PerformanceInfo("B1", "B1", "", true, 2);
		case 290:
			return new PerformanceInfo("B1", "B1", "", true, 3);
		case 291:
			return new PerformanceInfo("B1", "B1", "", true, 4);
		case 10:
			return new PerformanceInfo("Tensão", "Tensão", "V", false, 0);
		case 294:
			return new PerformanceInfo("FEC", "FEC", "", true, 1);
		case 26:
			return new PerformanceInfo("FEC Lado 1", "FEC Lado 1", "", true, 1);
		case 27:
			return new PerformanceInfo("FEC Lado 2", "FEC Lado 2", "", true, 2);
		case 54:
			return new PerformanceInfo("FEC Rede", "FEC Rede", "", true, 1);
		case 298:
			return new PerformanceInfo("EFEC", "EFEC", "", true, 1);
		case 295:
			return new PerformanceInfo("EFEC", "EFEC", "", true, 1);
		case 296:
			return new PerformanceInfo("EFEC Lado 2", "EFEC Lado 2", "", true, 2);
		case 297:
			return new PerformanceInfo("EFEC Rede", "EFEC Rede", "", true, 1);
		case 40:
			return new PerformanceInfo("BEI OTU", "BEI OTU", "", true, 1);
		case 37:
			return new PerformanceInfo("BEI OTU Lado 1", "BEI OTU", "", true, 1);
		case 38:
			return new PerformanceInfo("BEI OTU Lado 2", "BEI OTU", "", true, 2);
		case 39:
			return new PerformanceInfo("BEI ODU", "BEI ODU", "", true, 1);
		case 47:
		case 48:
		case 50:
		case 51:
		case 52:
		case 219:
		case 220:
		case 221:
		case 222:
		case 299:
		case 300:
		case 301:
		case 302:
			return new PerformanceInfo("Temperatura", "Temperatura", "ºC", false, 0);
		case 283:
			return new PerformanceInfo("Tensão Total", "Tensão Total (A+B)", "V", false, 0);
		case 286:
			return new PerformanceInfo("Taxa de ocupação", "Taxa de ocupação", "%",  false, 0);
		case 6:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 35:
		case 36:
		case 45:
		case 46:
		case 49:
		case 64:
		case 65:
		case 66:
		case 67:
		case 68:
		case 69:
		case 70:
		case 71:
		case 72:
		case 73:
		case 74:
		case 75:
		case 76:
		case 77:
		case 78:
		case 79:
		case 80:
		case 81:
		case 82:
		case 83:
		case 84:
		case 85:
		case 86:
		case 87:
		case 88:
		case 89:
		case 90:
		case 91:
		case 92:
		case 93:
		case 94:
		case 95:
		case 96:
		case 223:
		case 224:
		case 225:
		case 226:
		case 227:
		case 228:
		case 229:
		case 230:
		case 231:
		case 232:
		case 233:
		case 234:
		case 235:
		case 236:
		case 237:
		case 238:
		case 239:
		case 240:
		case 241:
		case 242:
		case 243:
		case 244:
		case 245:
		case 246:
		case 247:
		case 248:
		case 249:
		case 250:
		case 251:
		case 252:
		case 253:
		case 254:
		case 255:
		case 256:
		case 257:
		case 258:
		case 259:
		case 260:
		case 261:
		case 262:
		case 263:
		case 264:
		case 265:
		case 266:
		case 267:
		case 268:
		case 269:
		case 270:
		case 271:
		case 272:
		case 273:
		case 274:
		case 275:
		case 276:
		case 277:
		case 278:
		case 279:
		case 280:
		case 281:
		case 282:
		case 284:
		case 285:
		case 292:
		case 293: 
		} 
		return new PerformanceInfo("Desconhecido",  Msg.getString("JDialogThreshold.4"), "", false, 0);
	}

	private static void log(Level level, String msg)  {
		StringBuilder sb = new StringBuilder();
		sb.append("NEPerformanceData: ");
		sb.append(msg);
		Log.getInstance().log(level, sb.toString());
		sb = null;
	}

	private static void log(String msg, Exception e) {
		StringWriter buffer = new StringWriter();
		PrintWriter writer = new PrintWriter(buffer);
		e.printStackTrace(writer);
		log(Level.SEVERE, msg + "\n" + buffer.toString());
		writer = null;
	}

	public static class PerformanceInfo {
		private String name;
		private String unit;
		private String shortName;
		private boolean isAvaliableForSNMP;
		private int port;

		public PerformanceInfo(String shortName, String name, String unit, boolean isAvaliableForSNMP, int port)  {
			this.shortName = shortName;
			this.name = name;
			this.unit = unit;
			this.isAvaliableForSNMP = isAvaliableForSNMP;
			this.port = port;
		}

		public boolean isAvaliableForSNMP() {
			return this.isAvaliableForSNMP;
		}

		public int getPort() {
			return this.port;
		}

		public String getShortName() {
			return this.shortName;
		}

		public String getName() {
			return this.name;
		}

		public String getUnit() {
			return this.unit;
		}
	}
}