package br.com.padtec.v3.server.protocols.ppm2v2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.Amplifier_Impl;
import br.com.padtec.v3.data.impl.Fan_Impl;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.PBAmp_Impl;
import br.com.padtec.v3.data.impl.SPVL4_Impl;
import br.com.padtec.v3.data.impl.SupSPVJ_Impl;
import br.com.padtec.v3.data.impl.T100D_GT_Impl;
import br.com.padtec.v3.data.impl.Transponder_Impl;
import br.com.padtec.v3.data.impl.TrpGBEthD_Impl;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.Fan;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Rateable;
import br.com.padtec.v3.data.ne.SupSPVJ;
import br.com.padtec.v3.data.ne.TransponderOTN;
import br.com.padtec.v3.data.ne.TrpBiDWDMRate;
import br.com.padtec.v3.data.ne.TrpBidirecional;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.server.AlarmFactory;
import br.com.padtec.v3.util.CustomResourceBundle;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.Msg;
import br.com.padtec.v3.util.PartNumber;
import br.com.padtec.v3.util.math.MathUtils;

public final class AlarmFactoryPPM2v2 extends AlarmFactory {
	public static Alarm getAlarm(PPM2v2 packet, NE_Impl ne)
	{
		if ((packet.getCommand() == 4) || (packet.getCommand() == 5))  {
			boolean isNew;
			byte p = packet.getParameter();
			int parameter = (p < 0) ? p + 256 : p;
			//      if (ne instanceof SHK_Impl) {
			//        if (parameter < 128) {
			//          return getSHKAlarm((SHK_Impl)ne, parameter - 1, 1);
			//        }
			//        return getSHKAlarm((SHK_Impl)ne, parameter - 129, 0);
			//      }

			if (parameter < 128) {
				isNew = true;
			} else {
				isNew = false;
				parameter -= 128;
			}

			if (ne instanceof SupSPVJ) {
				if (parameter == 1)
					isNew = !(isNew);
			}
			//      else if ((ne instanceof OpticalProtection_Impl) && ((
			//        (parameter == 1) || (parameter == 2) || (parameter == 4) || 
			//        (parameter == 5)))) {
			//        isNew = !(isNew);
			//      }

			int idAlarm = getAlarmID(ne, parameter);

			if (idAlarm != 15) {
				return createAlarm(ne, isNew, idAlarm);
			}
			return null;
		}

		return null;
	}

	public static Alarm createCommandAlarm(NE_Impl ne, String commandName, int contact)
	{
		Alarm a = createAlarm(ne, true, 56);
		/* if (ne instanceof SHK_Impl) {
      SHK_Impl shk = (SHK_Impl)ne;
      SHKConfig c = shk.getConfig(contact - 1);
      if (c != null) {
        a.setAlarmName(c.getName() + " (" + contact + ") " + 
          Msg.getString("AlarmFactoryPPM2v2.received"));
        a.setDescription(Msg.getString("AlarmFactoryPPM2v2.descr.0") + 
          contact + " " + Msg.getString("AlarmFactoryPPM2v2.descr.1"));
      }
      a.setContact(Integer.valueOf(contact - 1));
    }
    else*/ if (commandName.length() != 0) {
    	a.setAlarmName(commandName + " " + 
    			Msg.getString("AlarmFactoryPPM2v2.received"));
    	a.setDescription(Msg.getString("AlarmFactoryPPM2v2.descr.2"));
    }

    return a;
	}

	public static Alarm createCommandNotSentAlarm(NE_Impl ne, String commandName, int contact)
	{
		Alarm a = createAlarm(ne, true, 1302);
		/*if (ne instanceof SHK_Impl) {
      SHK_Impl shk = (SHK_Impl)ne;
      SHKConfig c = shk.getConfig(contact - 1);
      if (c != null) {
        a.setAlarmName(c.getName() + " (" + contact + ") " + 
          Msg.getString("AlarmFactoryPPM2v2.not_sent"));
        a.setDescription(Msg.getString("AlarmFactoryPPM2v2.descr.3") + 
          contact + " " + Msg.getString("AlarmFactoryPPM2v2.descr.4"));
      }
      a.setContact(Integer.valueOf(contact - 1));
    }*/
		return a;
	}

	/*private static Alarm getSHKAlarm(SHK_Impl shk, int contactNumber, int status)  {
    SHKConfig config = shk.getConfig(contactNumber);
    if ((config != null) && (config.isAlarmOnOne() != null) && 
      (!(config.isMono().booleanValue()))) {
      AlarmConfig alarmConfig = config.getAlarmType();
      if (alarmConfig != null) {
        Alarm a = new Alarm(shk.getSerial(), alarmConfig.getId().intValue(), 
          new Integer(contactNumber));
        a.setNeName(shk.getName());
        applyAlarmConfig(a, alarmConfig);
        if (((config.isAlarmOnOne().booleanValue()) && (status == 0)) || (
          (!(config.isAlarmOnOne().booleanValue())) && (status == 1))) {
          a.setEndDate(System.currentTimeMillis());
        }
        return a;
      }
    }
    return null;
  }*/

	/*private static List<Alarm> generateSHKInitialAlarms(byte[] data, SHK_Impl shk)  {
    Alarm a;
    List alarms = new ArrayList();

    for (int contador = 0; contador < 40; ++contador) {
      a = getSHKAlarm(shk, contador, 
        data[(contador / 8)] >> contador % 8 & 0x1);
      if (a != null) {
        alarms.add(a);
      }
    }
    for (contador = 0; contador < shk.MAXCOMMAND; ++contador) {
      a = getSHKAlarm(shk, contador + 40, data[5] >> contador & 0x1);
      if (a != null)
        alarms.add(a);
    }
    return alarms;
  }*/

	public static List<Alarm> generateInitialAlarms(ColetorPPM2v2 coletor, NE_Impl ne, byte data)
	{
		int idAlarm;
		boolean isNew;
		List<Alarm> alarms = new ArrayList<Alarm>();
		if (ne instanceof Transponder_Impl) {
			if ((!(ne instanceof TrpBidirecional)) && (!(ne instanceof TrpOTNTerminal)) ){ /*
    		  && (!(ne instanceof TrpPreOTN)) && (!(ne instanceof TrpOTNRegenerador))  && 
    		  (!(ne instanceof TrpTrS25_Impl))) {*/

				isNew = (data & 0x1) == 1;
				idAlarm = 1;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				isNew = (data & 0x2) == 2;
				idAlarm = 3;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				isNew = (data & 0x4) == 4;
				idAlarm = 2;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				isNew = (data & 0x8) == 8;
				idAlarm = 4;
				alarms.add(createAlarm(ne, isNew, idAlarm));
				if (ne instanceof Rateable)
				{
					isNew = (data & 0x40) == 64;
					idAlarm = 7;
					alarms.add(createAlarm(ne, isNew, idAlarm));
				}
			}

			if (/*(ne instanceof TrpOTNRegenerador) || */(ne instanceof TrpOTNTerminal))   {
				isNew = (data & 0x2) == 2;
				idAlarm = 3;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				isNew = (data & 0x40) == 64;
				idAlarm = 9;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				isNew = (data & 0x80) == 128;
				idAlarm = 10;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				/* if (ne instanceof TrpOTNRegenerador)  {
          isNew = (data & 0x4) == 4;
          idAlarm = 1621;
          alarms.add(createAlarm(ne, isNew, idAlarm));

          isNew = (data & 0x1) == 1;
          idAlarm = 1403;
          alarms.add(createAlarm(ne, isNew, idAlarm));

          isNew = (data & 0x8) == 8;
          idAlarm = 1405;
          alarms.add(createAlarm(ne, isNew, idAlarm));

          isNew = (data & 0x10) == 16;
          idAlarm = 1404;
          alarms.add(createAlarm(ne, isNew, idAlarm));

          isNew = (data & 0x20) == 32;
          idAlarm = 1406;
          alarms.add(createAlarm(ne, isNew, idAlarm));
        }
        else
        {*/
				isNew = (data & 0x4) == 4;
				idAlarm = 8;
				alarms.add(createAlarm(ne, isNew, idAlarm));
				//        }

				if (ne instanceof TrpOTNTerminal) {
					isNew = (data & 0x1) == 1;
					idAlarm = 1401;
					alarms.add(createAlarm(ne, isNew, idAlarm));

					isNew = (data & 0x8) == 8;
					idAlarm = 1402;
					alarms.add(createAlarm(ne, isNew, idAlarm));

					isNew = (data & 0x10) == 16;
					idAlarm = 5;
					alarms.add(createAlarm(ne, isNew, idAlarm));

					isNew = (data & 0x20) == 32;
					idAlarm = 6;
					alarms.add(createAlarm(ne, isNew, idAlarm));
				}
			}

			if ((ne instanceof TrpBidirecional) /*|| (ne instanceof TrpPreOTN) */)  {
				isNew = (data & 0x1) == 1;
				idAlarm = 1401;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				isNew = (data & 0x2) == 2;
				idAlarm = 3;
				alarms.add(createAlarm(ne, isNew, idAlarm));
				/*if (ne instanceof BasicOTN) {
          isNew = (data & 0x4) == 4;
          idAlarm = 8;
          alarms.add(createAlarm(ne, isNew, idAlarm));
        } else*/ 
				if (ne instanceof TrpGBEthD_Impl) {
					isNew = (data & 0x4) == 4;
					idAlarm = 2;
					alarms.add(createAlarm(ne, isNew, idAlarm));
				}

				isNew = (data & 0x8) == 8;
				idAlarm = 1402;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				isNew = (data & 0x10) == 16;
				idAlarm = 5;
				alarms.add(createAlarm(ne, isNew, idAlarm));

				isNew = (data & 0x20) == 32;
				idAlarm = 6;
				alarms.add(createAlarm(ne, isNew, idAlarm));
				/*if (ne instanceof BasicOTN) {
          isNew = (data & 0x40) == 64;
          idAlarm = 9;
          alarms.add(createAlarm(ne, isNew, idAlarm));
        }*/
				if (ne instanceof Rateable)
				{
					isNew = (data & 0x40) == 64;
					idAlarm = 7;
					alarms.add(createAlarm(ne, isNew, idAlarm));
				}
				if (!(ne instanceof TrpGBEthD_Impl))
				{
					isNew = (data & 0x80) == 128;
					idAlarm = 10;
					alarms.add(createAlarm(ne, isNew, idAlarm));
				}
				if ((ne instanceof TrpBiDWDMRate) && 
						(coletor != null)) {
					SupSPVJ sup = coletor.getSiteSpvj(ne.getSupAddress());
					String spvjVerTaxa2 = Functions.getProperty("spvjVerTaxa2", 
							null);
					if ((spvjVerTaxa2 != null) && 
							(sup != null) && 
							(Functions.compareVersions(sup.getVersion(), spvjVerTaxa2) >= 0)) {
						isNew = false;
						isNew = (data & 0x80) == 128;
						idAlarm = 1430;
						alarms.add(createAlarm(ne, isNew, idAlarm));
					}
				}

			}

		} /*else if (ne instanceof RateMeter)  {
      isNew = (data & 0x1) == 1;
      idAlarm = 1;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x40) == 64;
      idAlarm = 7;
      alarms.add(createAlarm(ne, isNew, idAlarm));
    }*/ else if (ne instanceof Amplifier_Impl) {
    	//      if (!(ne instanceof RAmp_Impl))
    	//      {
    	isNew = (data & 0x4) == 4;
    	idAlarm = 21;
    	alarms.add(createAlarm(ne, isNew, idAlarm));
    	//      }

    	isNew = (data & 0x1) == 1;
    	idAlarm = 23;
    	alarms.add(createAlarm(ne, isNew, idAlarm));

    	isNew = (data & 0x2) == 2;
    	idAlarm = 22;
    	alarms.add(createAlarm(ne, isNew, idAlarm));

    	isNew = (data & 0x20) == 32;
    	idAlarm = 26;
    	alarms.add(createAlarm(ne, isNew, idAlarm));

    	isNew = (data & 0x40) == 64;
    	idAlarm = 27;
    	alarms.add(createAlarm(ne, isNew, idAlarm));

    	isNew = (data & 0x80) == 128;
    	idAlarm = 25;
    	alarms.add(createAlarm(ne, isNew, idAlarm));
    	/* } else if (ne instanceof OpticalProtection_Impl)  {
      isNew = (data & 0x1) != 1;
      idAlarm = 36;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x2) != 2;
      idAlarm = 37;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x8) != 8;
      idAlarm = 34;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x10) != 16;
      idAlarm = 35;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x4) == 4;
      idAlarm = 38;
      alarms.add(createAlarm(ne, isNew, idAlarm));
    }
    else if (ne instanceof MediaConverter_Impl)
    {
      isNew = (data & 0x1) == 1;
      idAlarm = 60;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x2) == 2;
      idAlarm = 61;
      alarms.add(createAlarm(ne, isNew, idAlarm));
    } else if (ne instanceof OpticalSwitch8x1_Impl)
    {
      isNew = (data & 0x1) == 1;
      idAlarm = 63;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x2) == 2;
      idAlarm = 65;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x4) == 4;
      idAlarm = 62;
      alarms.add(createAlarm(ne, isNew, idAlarm));

      isNew = (data & 0x8) == 8;
      idAlarm = 64;
      alarms.add(createAlarm(ne, isNew, idAlarm));*/
    } else if (ne instanceof Fan_Impl) {
    	if (data != 0)
    	{
    		isNew = (data & 0x1) != 1;
    		idAlarm = 1003;
    		alarms.add(createAlarm(ne, isNew, idAlarm));

    		isNew = (data & 0x2) != 2;
    		idAlarm = 1002;
    		alarms.add(createAlarm(ne, isNew, idAlarm));

    		isNew = (data & 0x4) != 4;
    		idAlarm = 1001;
    		alarms.add(createAlarm(ne, isNew, idAlarm));

    		isNew = (data & 0x8) == 8;
    		idAlarm = 1000;
    		alarms.add(createAlarm(ne, isNew, idAlarm));
    	}
    	/*} else if (ne instanceof PowerSupply_Impl) {
      isNew = (data & 0x1) == 1;
      idAlarm = 1010;
      alarms.add(createAlarm(ne, isNew, idAlarm));
      isNew = (data & 0x2) == 2;
      idAlarm = 1011;
      alarms.add(createAlarm(ne, isNew, idAlarm));
      isNew = (data & 0x4) == 4;
      idAlarm = 1011;
      alarms.add(createAlarm(ne, isNew, idAlarm));
    } else if (ne instanceof AmplifierPowerSupply_Impl) {
      isNew = (data & 0x1) == 1;
      idAlarm = 1020;
      alarms.add(createAlarm(ne, isNew, idAlarm));
      isNew = (data & 0x2) == 2;
      idAlarm = 1021;
      alarms.add(createAlarm(ne, isNew, idAlarm));*/
    }
		return alarms;
	}

	private static int getAlarmID(NE ne, int trap) {
		/*if (ne instanceof Muxponder) {
      if (trap == 1) return 1;
      if (trap == 2) return 8;
      if (trap == 3) return 3;
      if (trap == 4) return 4;
      if (trap == 8) return 66;
      if (trap == 9) return 67;
      if (trap == 10) return 68;
      if (trap == 11) return 69;
      if (trap == 14) return 72;
      if (trap == 15) return 73;
      if (trap == 16) return 74;
      if (trap == 17) return 77;
      if (trap == 18) return 78;
      if (trap == 19) return 79;

      if (trap == 22) return 82;
      if (trap == 23) return 83;
      if (trap == 24) return 84;
      if (trap == 25) return 85;
      if (trap == 26) return 86;

      if (trap == 35) return 1032;
      if (trap == 36) return 1033;
      if (trap == 37) return 1050;
      if (trap == 38) return 1051;
      if (trap == 39) return 1052;
      if (trap == 40) return 1053;
      if (trap == 41) return 1054;
      if (trap == 42) return 1055;
      if (trap == 43) return 1056;
      if (trap == 44) return 1057;
      if (trap == 45) return 1058;
      if (trap == 46) return 1059;
      if (trap == 47) return 1060;
      if (trap == 48) return 1061;
      if (trap == 49) return 1062;
      if (trap == 50) return 1063;
      if (trap == 51) return 1064;
      if (trap == 52) return 1065;
      if (trap == 53) return 1066;
      if (trap == 54) return 1067;
      if (trap == 55) return 1068;
      if (trap == 56) return 1069;

      if (trap == 61) return 1074;
      if (trap == 62) return 1075;
      if (trap == 63) return 1076;
      if (trap == 64) return 1077;
      if (trap == 65) return 1078;
      if (trap == 66) return 1079;
      if (trap == 67) return 1080;
      if (trap == 68) return 1081;
      if (trap == 69) return 99;
    }*/

		if (ne instanceof Transponder_Impl)   {
			if ((!(ne instanceof TrpBidirecional)) && (!(ne instanceof TrpOTNTerminal)) ){
				//    		  (!(ne instanceof TrpPreOTN)) && (!(ne instanceof TrpOTNRegenerador)) && 
				//         &&  (!(ne instanceof TrpTrS25_Impl)))   {
				if (trap == 1) return 1;

				if (trap == 2) return 2;
				if (trap == 3) return 3;
				if (trap == 4) return 4;

				if (trap != 7) return 7;
			}

			if ((ne instanceof TrpBidirecional) || (ne instanceof TrpOTNTerminal)) {
				/*|| (ne instanceof TrpOTNRegenerador) || (ne instanceof TrpPreOTN) {*/
				/* if (ne instanceof TrpOTNRegenerador)      {
          if (trap == 1) return 1403;
          if (trap == 4) return 1405;
          if (trap == 5) return 1404;
          if (trap == 6) return 1406;
          if (trap != 19) break label650; return 1622;
        }*/

				if (trap == 19) return 79;

				if ((ne instanceof TrpOTNTerminal) || (ne instanceof TrpBidirecional) || 
						(ne instanceof TrpOTNTerminal) /*|| (ne instanceof TrpPreOTN) */ ){
					if (trap == 1) return 1401;
					if (trap == 4) return 1402;
					if (trap == 5) return 5;
					if (trap == 6) return 6;
				}

				if (ne instanceof TrpGBEthD_Impl)
				{
					if (trap != 2) return 2;
				}

				if (trap == 2) return 8;

				if (trap == 3) return 3;
				if (!(ne instanceof TrpBiDWDMRate))    {
					if (trap == 7) return 9;
					if (trap == 21) return 10;
				}

				if (trap == 8) return 66;
				if (trap == 9) return 67;
				if (trap == 10) return 68;
				if (trap == 11) return 69;
				if (trap == 12) return 70;

				if (trap == 14) return 72;
				if (trap == 15) return 73;
				if (trap == 16) return 74;
				if (trap == 17) return 77;
				if (trap == 18) return 78;

				if (trap == 22) return 82;
				if (trap == 23) return 83;
				if (trap == 24) return 84;
				if (trap == 25) return 85;
				if (trap == 26) return 86;
				if (trap == 27) return 87;
				if (trap == 28) return 88;
				if (trap == 29) return 89;
				if (trap == 30) return 90;
				if (trap == 31) return 91;
				if (trap == 32) return 92;
			}
		}

		/*if (ne instanceof RateMeter) {
      if (trap == 1) label955: return 1;
      if (trap == 7) return 7;
    }*/

		if (ne instanceof Amplifier) {
			if (/*(!(ne instanceof RAmp_Impl)) &&*/ 
					(trap == 1)) return 21;

			if (trap == 2) return 22;
			if (trap == 3) return 23;
			if (trap == 4) return 24;
			if (trap == 5) return 27;
			if (trap == 6) return 26;
			if (trap == 7) return 25;
		}

		/*if (ne instanceof MediaConverter) {
			if (trap == 1) return 60;
			if (trap == 2) return 61;
		}*/

		/*if (ne instanceof OpticalProtection) {
			if (trap == 1) return 36;
			if (trap == 2) return 37;
			if (trap == 3) return 38;
			if (trap == 4) return 34;
			if (trap == 5) return 35;
		}*/

		/*if (ne instanceof OpticalSwitch8x1) {
			if (trap == 1) return 62;
			if (trap == 2) return 63;
			if (trap == 3) return 64;
			if (trap == 4) return 65;
		}*/

		if (ne instanceof Fan) {
			if (trap == 1) return 1000;
			if (trap == 2) return 1001;
			if (trap == 3) return 1002;
			if (trap == 4) return 1003;
		}

		/*if (ne instanceof PowerSupply) {
			if (trap == 1) return 1010;
			if (trap == 2) return 1011;
			if (trap == 3) return 1011;
		}*/

		/*if (ne instanceof AmplifierPowerSupply) {
			if (trap == 1) return 1020;
			if (trap == 2) return 1021;
		}*/

		return -1;
	}

	private static byte getAlarmsFromPacket(byte[] packetDataArray, NE_Impl ne)
	throws ArrayIndexOutOfBoundsException	{
		if (ne instanceof Transponder_Impl)
			return packetDataArray[12];
		/*if (ne instanceof RateMeter_Impl)
			return packetDataArray[12];*/
		if (ne instanceof Amplifier_Impl)
			return packetDataArray[4];
		/*if (ne instanceof OpticalProtection_Impl)
			return packetDataArray[0];
		if (ne instanceof MediaConverter)
			return packetDataArray[0];
		if (ne instanceof OpticalSwitch8x1)
			return packetDataArray[1];*/
		if ((ne instanceof Fan & packetDataArray.length > 0))
			return packetDataArray[5];
		/*if (ne instanceof PowerSupply)
			return packetDataArray[5];
		if (ne instanceof AmplifierPowerSupply) {
			return packetDataArray[2];
		}*/
		return 0;
	}

	public static Vector<Alarm> generateTrpOtnInitialAlarms(byte data, NE_Impl ne)	{
		int idAlarm;
		boolean isNew;
		Vector<Alarm> alarms = new Vector<Alarm>();
		/*if (ne instanceof TrpDWDM25Otn_Impl){
			isNew = (data & 0x8) == 8;
			idAlarm = 67;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x20) == 32;
			idAlarm = 69;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x40) == 64;
			idAlarm = 70;
			alarms.add(createAlarm(ne, isNew, idAlarm));
		}*/

		if (ne instanceof T100D_GT_Impl){
			isNew = (data & 0x1) == 1;
			idAlarm = 78;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x2) == 2;
			idAlarm = 67;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x4) == 4;
			idAlarm = 77;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x8) == 8;
			idAlarm = 66;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x10) == 16;
			idAlarm = 68;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x20) == 32;
			idAlarm = 69;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x40) == 64;
			idAlarm = 70;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x80) == 128;
			idAlarm = 79;
			alarms.add(createAlarm(ne, isNew, idAlarm));
		}
		/*if (ne instanceof T100D_GC_Impl){
			isNew = (data & 0x1) == 1;
			idAlarm = 78;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x2) == 2;
			idAlarm = 90;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x4) == 4;
			idAlarm = 77;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x8) == 8;
			idAlarm = 89;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x10) == 16;
			idAlarm = 1622;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x20) == 32;
			idAlarm = 91;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x40) == 64;
			idAlarm = 69;
			alarms.add(createAlarm(ne, isNew, idAlarm));

			isNew = (data & 0x80) == 128;
			idAlarm = 70;
			alarms.add(createAlarm(ne, isNew, idAlarm));
		}*/

		return alarms;
	}

/*	private static List<Alarm> generateMuxponderInitialAlarms(Muxponder_Impl ne, byte[] data)	{
		List alarms = new ArrayList();
		boolean[] sfpRemoved = new boolean[4];
		boolean[] sfpLos = new boolean[4];

		boolean isNew = (data[135] & 0x1) != 1;
		int idAlarm = 1074;
		alarms.add(createAlarm(ne, isNew, idAlarm));
		sfpRemoved[0] = isNew;

		isNew = (data[135] & 0x2) != 2;
		idAlarm = 1076;
		alarms.add(createAlarm(ne, isNew, idAlarm));
		sfpRemoved[1] = isNew;

		isNew = (data[135] & 0x4) != 4;
		idAlarm = 1078;
		alarms.add(createAlarm(ne, isNew, idAlarm));
		sfpRemoved[2] = isNew;

		isNew = (data[135] & 0x8) != 8;
		idAlarm = 1080;
		alarms.add(createAlarm(ne, isNew, idAlarm));
		sfpRemoved[3] = isNew;

		isNew = (data[2] & 0x1) == 1;

		idAlarm = 1050;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(0)));
		sfpLos[0] = isNew;

		isNew = (data[2] & 0x2) == 2;

		idAlarm = 1052;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(0)));

		isNew = (data[2] & 0x4) == 4;

		idAlarm = 1051;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(0)));

		isNew = (data[2] & 0x8) == 8;

		idAlarm = 1053;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(0)));

		isNew = (data[2] & 0x10) == 16;

		idAlarm = 1054;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(1)));
		sfpLos[1] = isNew;

		isNew = (data[2] & 0x20) == 32;

		idAlarm = 1056;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(1)));

		isNew = (data[2] & 0x40) == 64;

		idAlarm = 1055;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(1)));

		isNew = (data[2] & 0x80) == 128;

		idAlarm = 1057;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(1)));

		isNew = (data[3] & 0x1) == 1;

		idAlarm = 1058;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(2)));
		sfpLos[2] = isNew;

		isNew = (data[3] & 0x2) == 2;

		idAlarm = 1060;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(2)));

		isNew = (data[3] & 0x4) == 4;

		idAlarm = 1059;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(2)));

		isNew = (data[3] & 0x8) == 8;

		idAlarm = 1061;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(2)));

		isNew = (data[3] & 0x10) == 16;

		idAlarm = 1062;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(3)));
		sfpLos[3] = isNew;

		isNew = (data[3] & 0x20) == 32;

		idAlarm = 1064;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(3)));

		isNew = (data[3] & 0x40) == 64;

		idAlarm = 1063;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(3)));

		isNew = (data[3] & 0x80) == 128;

		idAlarm = 1065;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(3)));

		isNew = (data[4] & 0x1) == 1;
		idAlarm = 1;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[4] & 0x2) == 2;
		idAlarm = 3;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[4] & 0x4) == 4;

		idAlarm = 8;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[4] & 0x8) == 8;
		idAlarm = 4;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[4] & 0x10) == 16;

		idAlarm = 1066;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(0)));

		isNew = (data[4] & 0x20) == 32;

		idAlarm = 1067;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(1)));

		isNew = (data[4] & 0x40) == 64;

		idAlarm = 1068;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(2)));

		isNew = (data[4] & 0x80) == 128;

		idAlarm = 1069;
		alarms.add(createAlarm(ne, isNew, idAlarm, Integer.valueOf(3)));

		isNew = (data[5] & 0x1) == 1;

		idAlarm = 78;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[5] & 0x2) == 2;

		idAlarm = 67;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[5] & 0x4) == 4;

		idAlarm = 77;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[5] & 0x8) == 8;

		idAlarm = 66;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[5] & 0x10) == 16;

		idAlarm = 68;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[5] & 0x20) == 32;

		idAlarm = 69;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[5] & 0x40) == 64;

		idAlarm = 79;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[5] & 0x80) == 128;

		idAlarm = 99;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data[36] & 0x8) != 8;
		idAlarm = 82;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		if (data.length != 0) {
			System.arraycopy(data, 77, ne.sfpAlarmes, 0, 5);
		}

		return alarms;
	}*/

	public static List<Alarm> generateInitialAlarms(ColetorPPM2v2 coletor, byte[] data, NE_Impl ne)	{
		/*if (ne instanceof Muxponder_Impl)
			return generateMuxponderInitialAlarms((Muxponder_Impl)ne, data);
		if (ne instanceof SHK_Impl) {
			return generateSHKInitialAlarms(data, (SHK_Impl)ne);
		}*/
		return generateInitialAlarms(coletor, ne, getAlarmsFromPacket(data, ne));
	}

	private static void generateCounterAlarms(NE_Impl ne, BigInteger value, int idAlarm, List<Alarm> alarms) {
		boolean isNew = (MathUtils.compare(value, BigInteger.ZERO) > 0) || 
		(MathUtils.compare(value, BigInteger.valueOf(-1L)) < 0);
		alarms.add(createAlarm(ne, isNew, idAlarm));
	}

	public static List<Alarm> generateTrpOtnCounterAlarms(NE_Impl ne, byte parametro){
		List<Alarm> alarms = new Vector<Alarm>();

		/*if ((ne instanceof TrpPreOTN) && (parametro == 5)) {
			generateCounterAlarms(ne, ((TrpPreOTN)ne).getODUk().getBip8(), 
					75, alarms);
			generateCounterAlarms(ne, ((TrpPreOTN)ne).getB1(), 76, 
					alarms);
			generateCounterAlarms(ne, ((FEC)ne).getErroredBlocks(), 
					97, alarms);
		} else*/ if ((ne instanceof TrpOTNTerminal) && (parametro == 12)) {
			generateCounterAlarms(ne, ((TrpOTNTerminal)ne).getODUk().getBip8(), 
					75, alarms);
			generateCounterAlarms(ne, ((TrpOTNTerminal)ne).getOTUk().getBip8(), 
					81, alarms);
			generateCounterAlarms(ne, ((TrpOTNTerminal)ne).getODUk().getBei(), 
					94, alarms);
			generateCounterAlarms(ne, ((TrpOTNTerminal)ne).getOTUk().getBei(), 
					95, alarms);
			generateCounterAlarms(ne, 
					((TrpOTNTerminal)ne).getFEC().getErroredBlocks(), 97, alarms);
			/*if (ne instanceof TrpOTNTerminalSDH)
				generateCounterAlarms(ne, ((TrpOTNTerminalSDH)ne).getSDHClientInterface().getB1(), 76, alarms);*/
		/*} else if ((ne instanceof TrpOTNRegenerador) && (parametro == 17)) {
			generateCounterAlarms(ne, ((TrpOTNRegenerador)ne).getOTUkA().getBip8(), 
					81, alarms);
			generateCounterAlarms(ne, ((TrpOTNRegenerador)ne).getOTUkB().getBip8(), 
					93, alarms);
			generateCounterAlarms(ne, ((TrpOTNRegenerador)ne).getOTUkA().getBei(), 
					95, alarms);
			generateCounterAlarms(ne, ((TrpOTNRegenerador)ne).getOTUkB().getBei(), 
					96, alarms);
			generateCounterAlarms(ne, 
					((TrpOTNRegenerador)ne).getFecA().getErroredBlocks(), 1620, alarms);
			generateCounterAlarms(ne, 
					((TrpOTNRegenerador)ne).getFecB().getErroredBlocks(), 98, alarms);
		} else if ((ne instanceof Muxponder) && (parametro == 0)) {
			generateCounterAlarms(ne, ((Muxponder)ne).getODU().getBip8(), 
					75, alarms);
			generateCounterAlarms(ne, ((Muxponder)ne).getOTU().getBip8(), 
					81, alarms);
			generateCounterAlarms(ne, ((Muxponder)ne).getODU().getBei(), 
					94, alarms);
			generateCounterAlarms(ne, ((Muxponder)ne).getOTU().getBei(), 
					95, alarms);
			if (((Muxponder)ne).getFEC().isFecRxCorrEnabled()) {
				generateCounterAlarms(ne, ((Muxponder)ne).getFEC().getErroredBlocks(), 
						97, alarms);
			}

			generateCounterAlarms(ne, 
					((Muxponder)ne).getSDHClientInterface(0).getB1(), 1082, alarms);
			generateCounterAlarms(ne, 
					((Muxponder)ne).getSDHClientInterface(1).getB1(), 1083, alarms);
			generateCounterAlarms(ne, 
					((Muxponder)ne).getSDHClientInterface(2).getB1(), 1084, alarms);
			generateCounterAlarms(ne, 
					((Muxponder)ne).getSDHClientInterface(3).getB1(), 1085, alarms);*/
		}

		return alarms;
	}

	/*public static List<Alarm> generateSfpAlarms(PPM2v2 pacote, Muxponder_Impl ne)	{
		List<Alarm> alarms = new ArrayList<Alarm>();
		byte[] data = pacote.getDataArray();
		byte[] changedAlarms = new byte[5];

		for (int i = 0; i < 5; ++i) {
			byte[] sfpAlarms = ne.sfpAlarmes;
			changedAlarms[i] = (byte)(data[(77 + i)] ^ sfpAlarms[i]);
			for (int j = 0; j < 8; ++j) {
				if ((changedAlarms[i] & 128 >> j) == 128 >> j) {
					boolean isNew = (data[(77 + i)] & 128 >> j) == 128 >> j;
					alarms.add(
							createAlarm(ne, isNew, 
									1150 + 8 * i + j));
				}
			}
			sfpAlarms[i] = data[(77 + i)];
		}
		return alarms;
	}*/

	public static List<Alarm> generateAlarmsToEnableDisableInTrpOTNTerminal(byte data, NE_Impl ne) {
		List<Alarm> alarms = new ArrayList<Alarm>();

		boolean isNew = (data & 0x1) == 1;
		int idAlarm = 78;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x2) == 2;
		idAlarm = 67;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x4) == 4;
		idAlarm = 77;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x8) == 8;
		idAlarm = 66;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x10) == 16;
		idAlarm = 68;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x20) == 32;
		idAlarm = 69;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x40) == 64;
		idAlarm = 70;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 79;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 82;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 3;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 10;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 1402;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 6;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 1;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 5;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 8;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 9;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		isNew = (data & 0x80) == 128;
		idAlarm = 99;
		alarms.add(createAlarm(ne, isNew, idAlarm));

		return alarms;
	}

	public static void main(String[] args)	{
		List<Notification> notifs;
		Map<String,Map<Integer,List<Notification>>> alarms = new TreeMap<String, Map<Integer,List<Notification>>>();

		ColetorPPM2v2Dummy coletor = new ColetorPPM2v2Dummy();

		for (int part = 1; part < 2000; ++part) {
			try {
				SerialNumber serial = new SerialNumber(part, 1);
				NE_Impl ne = PartNumber.getInstance(serial, false);
				if (ne != null)	{
					String neClass = ne.getClass().getSimpleName();
					if (!(alarms.containsKey(neClass)))	{
						for (int param = 255; param >= 0; --param)
							try	{
								if (((param != 17) && (param != 12)) || (ne instanceof TransponderOTN))	{
									if ((ne instanceof PBAmp_Impl) || (ne instanceof SupSPVJ_Impl)  || 
											(ne instanceof SPVL4_Impl) /*|| (ne instanceof SupSPVJ1U_Impl)
											(ne instanceof Amp1U_Impl) || (ne instanceof Combiner_Impl) || 
											(ne instanceof Muxponder_Impl) || (ne instanceof FOA_Impl) || 
											(ne instanceof RAmp_Impl) */) {
										param = 0;
									}

									byte[] data = new byte[2048];
									notifs = new ArrayList<Notification>();
									ColetorPPM2v2.createAlarms(ne, (byte)param, data, notifs, coletor);
									setAlarms(alarms, neClass, param, notifs); }
						} catch (RuntimeException e) {
							e.printStackTrace(); }
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}

		System.err.flush();

		System.err.println("Class\tExample Model\tPPM2v2 param\tAlarm");

		Iterator<Entry<String,Map<Integer,List<Notification>>>> iterator = alarms.entrySet().iterator();
		
		while (iterator.hasNext()) {
			Entry<String,Map<Integer,List<Notification>>> classData = iterator.next();
			String className = (String)classData.getKey();
			int exampleModelPart = CustomResourceBundle.getInstance().getLastPartByClassName("br.com.padtec.v3.data.impl." + className);
			String exampleModel = CustomResourceBundle.getInstance().getModel(exampleModelPart);

			Iterator<Entry<Integer,List<Notification>>> iterator2 = classData.getValue().entrySet().iterator();

			while (iterator2.hasNext()) {
				Entry<Integer, List<Notification>> paramAlarm = iterator2.next();
				Integer param = (Integer)paramAlarm.getKey();
				List<Notification> alarmList = paramAlarm.getValue();
				ArrayList<String> alarmNames = new ArrayList<String>();
				for (Notification item : alarmList) {
					Alarm alarm = (Alarm)item;
					alarmNames.add(alarm.getAlarmName() + " (" + alarm.getDescription() + ")");
				}
				Collections.sort(alarmNames, String.CASE_INSENSITIVE_ORDER);
				for (String item : alarmNames)
					System.err.println(className + "\t" + exampleModel + "\t" +	
							PPM2v2.getGetName(Byte.valueOf(param.byteValue())) + "\t" + item);
			}
		}
	}

	private static void setAlarms(Map<String, Map<Integer, List<Notification>>> alarmMap, String classe, 
			int param, List<Notification> alarmes) {
		if ((alarmes == null) || (alarmes.isEmpty())) {
			return;
		}
		Map<Integer, List<Notification>> alarmesDaClasse = alarmMap.get(classe);
		if (alarmesDaClasse == null) {
			alarmesDaClasse = new TreeMap<Integer, List<Notification>>();
			alarmMap.put(classe, alarmesDaClasse);
		}
		alarmesDaClasse.put(Integer.valueOf(param), alarmes);
	}

	private static class ColetorPPM2v2Dummy extends ColetorPPM2v2
	{
	}
}