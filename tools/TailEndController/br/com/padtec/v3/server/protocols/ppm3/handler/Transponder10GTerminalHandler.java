package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.FEC_Impl;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.T100D_GT_Impl;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.ODP;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.data.ne.TrpSintonizavel;
import br.com.padtec.v3.data.ne.MultiRate.Rate;
import br.com.padtec.v3.data.ne.ODP.PathState;
import br.com.padtec.v3.server.AlarmFactory;
import br.com.padtec.v3.server.protocols.codegenerator.Generator;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Factory;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Response;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Trap;
import br.com.padtec.v3.server.protocols.ppm3.packet.TLV;
import br.com.padtec.v3.server.protocols.ppm3.packet.TimeTLV;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.log.Log;

public class Transponder10GTerminalHandler implements HandlerInterface<PPM3> {
	private Logger log = Log.getInstance();

	private ResponseController response = new ResponseController();

	public Transponder10GTerminalHandler()	{
		for (TypeGet type : TypeGet.values())
			this.response.registerCode(Integer.valueOf(type.getCode()));
	}

	public boolean canHandle(NE_Impl ne)	{
		return ne instanceof T100D_GT_Impl;
	}

	public List<PPM3> getUpdatePacketList(NE_Impl ne) {
		List<PPM3> result = new LinkedList<PPM3>();

		PPM3 sendPacket = PPM3Factory.getGetPPM3(ne.getSupAddress(), ne.getSerial());
		result.add(sendPacket);

		if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_ODU_TTI.getCode()),	ne.getSerial()))) {
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_ODU_TTI.getCode());
		}
		if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_OTU_TTI.getCode()),	ne.getSerial()))) {
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_OTU_TTI.getCode());
		}
		if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_PT.getCode()), ne.getSerial()))) {
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_PT.getCode());
		}
		if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_ODU_TTI_REF_TX.getCode()), ne.getSerial()))) {
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_ODU_TTI_REF_TX.getCode());
		}
		if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_ODU_TTI_REF_RX.getCode()),	ne.getSerial()))) {
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_ODU_TTI_REF_RX.getCode());
		}
		if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_OTU_TTI_REF_TX.getCode()),ne.getSerial()))) {
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_OTU_TTI_REF_TX.getCode());
		}
		if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_OTU_TTI_REF_RX.getCode()), ne.getSerial()))) {
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_OTU_TTI_REF_RX.getCode());
		}
		if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_RATE.getCode()), ne.getSerial()))) {
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_RATE.getCode());
		}

		ODP odp = null;
		if (ne instanceof TrpOTNTerminal) {
			odp = ((TrpOTNTerminal)ne).getODP();
		}

		if (odp != null) {
			if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_ODP_PATH_STATE.getCode()), 
					ne.getSerial()))) {
				HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_ODP_PATH_STATE.getCode());
			}
			if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_ODP_WAITTORESTORE_TIME.getCode()), 
					ne.getSerial()))) {
				HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_ODP_WAITTORESTORE_TIME.getCode());
			}
			if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_ODP_HOLDOFF_TIME.getCode()), 
					ne.getSerial()))) {
				HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_ODP_HOLDOFF_TIME.getCode());
			}
			if (!(this.response.hasArrived(Integer.valueOf(TypeGet.TRT_FLAGGET_ODP_NEIGHBOURBOARD_ID.getCode()), 
					ne.getSerial()))) {
				HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_ODP_NEIGHBOURBOARD_ID.getCode());
			}
		}
		
		sendPacket = PPM3Factory.getGetPPM3(ne.getSupAddress(), ne.getSerial());
		result.add(sendPacket);

		HandlerHelper.addTlv(sendPacket, TypeGet.TRT_FLAGGET_STAT.getCode());
		if (Functions.compareVersions(ne.getVersion(), "1.15") >= 0)
			HandlerHelper.addTlv(sendPacket, TypeGet.TRT_GET_TABLE_CLIENT.getCode());
		else {
			this.response.registerArrival(Integer.valueOf(TypeGet.TRT_GET_TABLE_CLIENT.getCode()), 
					ne.getSerial());
		}

		HandlerHelper.addTlv(sendPacket, TypeGet.TRT_GET_TABLE.getCode());
		HandlerHelper.addTlv(sendPacket, TypeGet.TRT_GET_POWER_OTN.getCode());
		HandlerHelper.addTlv(sendPacket, TypeGet.TRT_GET_POWER_CLIENT.getCode());

		HandlerHelper.addTlv(sendPacket, TypeGet.TRT_GET_OTN.getCode());

		return result;
	}

	public boolean onReceiveResponse(NE_Impl ne, PPM3 pacote, Collection<Notification> alarmList)	{
		if (pacote.getPayload() instanceof PPM3Response) {
			T100D_GT_Impl tTerm = (T100D_GT_Impl)ne;
			PPM3Response response = (PPM3Response)pacote.getPayload();

			for (int i = 0; i < response.getTLVCount(); ++i) {
				TLV tlv = response.getTLV(i);
				settTerm(tTerm, tlv.getTypeAsInt(), tlv.getValue(), alarmList);
			}
			HandlerHelper.setAlarmTimestamp(alarmList, response.getTimestamp());
			return true;
		}
		return false;
	}

	public boolean onReceiveTrap(NE_Impl ne, PPM3 pacote, List<PPM3> packetToSend, List<Notification> event, boolean history) {
		if (pacote.getPayload() instanceof PPM3Trap) {
			PPM3Trap trapPacket = (PPM3Trap)pacote.getPayload();
			T100D_GT_Impl tTerm = (T100D_GT_Impl)ne;
			List<Notification> alarms = new ArrayList<Notification>();
			Iterator<TimeTLV> localIterator = trapPacket.getEvents().iterator();
			while (true) { 
				TimeTLV e = (TimeTLV)localIterator.next();
				alarms.clear();
				onReceiveTrap(tTerm, e, packetToSend, alarms);
				HandlerHelper.setAlarmTimestamp(alarms, e.getTimestamp());
				event.addAll(alarms);

				if (!(localIterator.hasNext())) {
					return true; 
				} 
			}
		}
		return false;
	}

	private void onReceiveTrap(T100D_GT_Impl tTerm, TimeTLV e, List<PPM3> packetToSend, List<Notification> event)	{

		boolean isStart = (e.getTypeAsInt() & 0x8000) != 32768;

		byte[] data = e.getValue();

		int trapId = e.getTypeAsInt() & 0x7FFF;
		if ((trapId != 0) &&(trapId != 128)){

			TypeTrap trap = TypeTrap.getType(trapId);
			if (trap != null){

				switch (trap){
				case TRT_TRAP_ENC_AIS_OFF:
					tTerm.setEncAIS(!(isStart));
					event.add( AlarmFactory.createGenericAlarm(tTerm, null,	Alarm.TYPE_TRP_ENCAISOFF, null, isStart, null));
					break;
				case TRT_TRAP_FAIL:
					tTerm.setFail(isStart);
					event.add( 	AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_FAIL, null, tTerm.getOpticalWDMInterface_Impl().isFail(), null));
					break;
				case TRT_TRAP_FAIL2:
					tTerm.setFail2(isStart);
					 
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_FAIL2, null, tTerm.getOpticalClientInterface_Impl().isFail(), null));
					break;
				case TRT_TRAP_LASEROFF:
					tTerm.setLaserOff(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_LASEROFF1, null, tTerm.getOpticalWDMInterface_Impl().isLaserOff(), null));
					break;
				case TRT_TRAP_LASEROFF2:
					tTerm.setLaserOff2(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_LASEROFF2, null, tTerm.getOpticalClientInterface_Impl().isLaserOff(), null));
					break;
				case TRT_TRAP_LOF:
					tTerm.setLof(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_LOF, null, tTerm.getOTN_WDMInterface().isLof(), null));
					break;
				case TRT_TRAP_LOF2:
					tTerm.setLof2(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_LOF2, null, tTerm.getClientInterface().isLof(), null));
					break;
				case TRT_TRAP_LOM:
					tTerm.setLom(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_TRP_LOM, null, tTerm.getOTN_WDMInterface().isLom(), null));
					break;
				case TRT_TRAP_LOS:
					tTerm.setLos(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_LOS, null, tTerm.isLos(), null));
					break;
				case TRT_TRAP_LOS2:
					tTerm.setLos2(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_LOS2, null, tTerm.getOpticalClientInterface_Impl().isLos(), null));
					break;
				case TRT_TRAP_LOSSYNC:
					tTerm.setLosSync(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_TRP_LOS_SYNC, null, tTerm.getOTN_WDMInterface().isLosSync(), null));
					break;
				case TRT_TRAP_LOSSYNC2:
					tTerm.setLosSync2(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_TRP_LOS2_SYNC, null, tTerm.getClientInterface().isLosSync(), null));
					break;

				case TRT_TRAP_ODU_AIS:
					tTerm.setAis_ODUk(isStart);
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_TRP_ODU_AIS, null, tTerm.getODUk_Impl().isAis(), null));
					break;
				case TRT_TRAP_ODU_BDI:
					tTerm.setBdi_ODUk(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_TRP_ODU_BDI, null, tTerm.getODUk().isBdi(), null));
					break;
				case TRT_TRAP_ODU_TIM:
					tTerm.setTim_ODUk(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_TRP_ODU_TIM, null, tTerm.getODUk().isTim(), null));
					break;
				case TRT_TRAP_OTU_TIM:
					tTerm.setTim_OTUk(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_TRP_OTU_TIM, null, tTerm.getOTUk().isTim(), null));
					break;
				case TRT_TRAP_OTU_BDI:
					tTerm.setBdi_OTUk(isStart);
					event.add( AlarmFactory.createGenericAlarm(tTerm, null, Alarm.TYPE_TRP_OTU_BDI, null, tTerm.getOTUk().isBdi(), null));
					break;
				case TRT_TRAP_FCS_LINK_DOWN:
					
					/* Used in T100D_GT_Rate_Impl and T100D_GT_ETH_Impl transponders
					 * You can consult this class in padtec source code.
					 * */
					break;
				case TRT_TRAP_MAC_RECEIVE_LOCAL_FAULT:
					
					/* Used in T100D_GT_Rate_Impl and T100D_GT_ETH_Impl transponders
					 * You can consult this class in padtec source code.
					 * */

					break;
				case TRT_TRAP_MAC_RECEIVE_REMOTE_FAULT:
					
					/* Used in T100D_GT_Rate_Impl and T100D_GT_ETH_Impl transponders
					 * You can consult this class in padtec source code.
					 * */

					break;
				case TRT_TRAP_RS_TIM:
					/* Used in T100D_GT_Rate_Impl and T100D_GT_SDH_Impl transponders.
					 * The both implement TrpOTNTerminalSDH
					 * You can consult this class in padtec source code.
					 * */
					
					break;
				case TRT_TRAP_ENC_RS_TIM:
					/* Used in T100D_GT_Rate_Impl and T100D_GT_SDH_Impl transponders.
					 * The both implement TrpOTNTerminalSDH
					 * You can consult this class in padtec source code.
					 * */

					break;
				case TRT_TRAP_J0_MODE_FINISHED:
					/* Used in T100D_GT_Rate_Impl and T100D_GT_SDH_Impl transponders.
					 * The both implement TrpOTNTerminalSDH
					 * You can consult this class in padtec source code.
					 * */
					break;
				case TRT_TRAP_CLIENT_SD:
					event.add( AlarmFactory.createAlarm(tTerm, isStart, 1538));
					break;
				case TRT_TRAP_CLIENT_SF:
					event.add(AlarmFactory.createAlarm(tTerm, isStart,1539));
					break;
				case TRT_TRAP_PLM:
					event.add( AlarmFactory.createAlarm(tTerm, isStart, 99));
					break;
				case TRT_TRAP_ODP_WAITTORESTORE:
					if (tTerm.getODP_Impl() != null){
						tTerm.getODP_Impl().setWaitToRestoreEnabled(new Boolean(isStart));
						event.add( AlarmFactory.createGenericAlarm(tTerm, null,	1636, null, isStart, null));
					}

					break;

				case TRT_TRAP_ODP_PATHTYPE: 
					if(tTerm.getODP_Impl() != null)
					{
						tTerm.getODP_Impl().setWorkingPath(new Boolean(!isStart));
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1635, null, true, null));
					}
					break;

				case TRT_TRAP_ODP_DISABLED: 
					if(tTerm.getODP_Impl() != null)
					{
						tTerm.getODP_Impl().setOdpEnabled(new Boolean(!isStart));
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1637, null, isStart, null));
					}
					break;

				case TRT_TRAP_ODP_CONFIG_ERROR: 
					if(tTerm.getODP_Impl() != null)
					{
						tTerm.getODP_Impl().setConfigError(new Boolean(isStart));
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1630, null, isStart, null));
					}
					break;

				case TRT_TRAP_ODP_CABLE_FAIL: 
					if(tTerm.getODP_Impl() != null)
					{
						tTerm.getODP_Impl().setCableFail(new Boolean(isStart));
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1631, null, isStart, null));
					}
					break;

				case TRT_TRAP_ODP_COMM_LOS: 
					if(tTerm.getODP_Impl() != null)
					{
						tTerm.getODP_Impl().setCommLOS(new Boolean(isStart));
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1632, null, isStart, null));
					}
					break;

				case TRT_TRAP_ODP_NEIGHBOUR_CABLE_FAIL: 
					if(tTerm.getODP_Impl() != null)
					{
						tTerm.getODP_Impl().setNeighbourCableFail(new Boolean(isStart));
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1638, null, isStart, null));
					}
					break;

				case TRT_TRAP_ODP_NEIGHBOUR_COMM_LOS: 
					if(tTerm.getODP_Impl() != null)
					{
						tTerm.getODP_Impl().setNeighbourCommLOS(new Boolean(isStart));
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1639, null, isStart, null));
					}
					break;

				case TRT_TRAP_ODP_SIGNALDEGRADE: 
					if(tTerm.getODP_Impl() != null)
						tTerm.getODP_Impl().setSignalDegradeEnabled(new Boolean(isStart));
					break;

				case TRT_TRAP_ODP_LASER_OFF_ODP: 
					if(tTerm.getODP_Impl() != null)
					{
						tTerm.getODP_Impl().setLaserOffODP(new Boolean(isStart));
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1640, null, isStart, null));
					}
					break;

				case TRT_TRAP_ODP_MANUAL_LASER_ON: 
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1642, null, isStart, null));
					break;

				case TRT_TRAP_LASER_SHUTDOWN_OTN: 
					tTerm.getOpticalWDMInterface_Impl().setLaserShutdown(isStart);
					break;

				case TRT_TRAP_LASER_SHUTDOWN_CLIENT: 
					tTerm.getOpticalClientInterface_Impl().setLaserShutdown(isStart);
					break;

				case TRT_TRAP_TEMP_OTN_ALARM: 
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1644, null, isStart, null));
					break;

				case TRT_TRAP_ODU_TTI_REF_RX:
					if(data != null)
					{
						if(HandlerHelper.stringChanged(tTerm.getODUk_Impl().getSapiRefRx(), Generator.getString(data, 0, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1645, null, isStart, null));
						if(HandlerHelper.stringChanged(tTerm.getODUk_Impl().getDapiRefRx(), Generator.getString(data, 15, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1647, null, isStart, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_ODU_TTI_REF_RX.getCode(), data, event);
					}
					break;

				case TRT_TRAP_OTU_TTI_REF_RX: 
					if(data != null)
					{
						if(HandlerHelper.stringChanged(tTerm.getOTUk_Impl().getSapiRefRx(), Generator.getString(data, 0, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1649, null, isStart, null));
						if(HandlerHelper.stringChanged(tTerm.getOTUk_Impl().getDapiRefRx(), Generator.getString(data, 15, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1653, null, isStart, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_OTU_TTI_REF_RX.getCode(), data, event);
					}
					break;

				case TRT_TRAP_ODU_TTI_REF_TX: 
					if(data != null)
					{
						if(HandlerHelper.stringChanged(tTerm.getODUk_Impl().getSapiRefTx(), Generator.getString(data, 0, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1646, null, isStart, null));
						if(HandlerHelper.stringChanged(tTerm.getODUk_Impl().getDapiRefTx(), Generator.getString(data, 15, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1648, null, isStart, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_ODU_TTI_REF_TX.getCode(), data, event);
					}
					break;

				case TRT_TRAP_OTU_TTI_REF_TX: 
					if(data != null)
					{
						if(HandlerHelper.stringChanged(tTerm.getOTUk_Impl().getSapiRefTx(), Generator.getString(data, 0, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1650, null, isStart, null));
						if(HandlerHelper.stringChanged(tTerm.getOTUk_Impl().getDapiRefTx(), Generator.getString(data, 15, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1654, null, isStart, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_OTU_TTI_REF_TX.getCode(), data, event);
					}
					break;

				case TRT_TRAP_PT: 
					if(data != null)
					{
						if(HandlerHelper.byteChanged(tTerm.getPT(), Generator.getByte(data, 0)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 74, null, isStart, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_PT.getCode(), data, event);
					}
					break;

				case TRT_TRAP_STAT: // '$'
					if(data != null)
						settTerm(tTerm, TypeGet.TRT_FLAGGET_STAT.getCode(), data, event);
					break;

				case TRT_TRAP_J0: 
					
					/* Used in T100D_GT_Rate_Impl and T100D_GT_SDH_Impl transponders.
					 * The both implement TrpOTNTerminalSDH
					 * You can consult this class in padtec source code.
					 * */
					break;

				case TRT_TRAP_J0_RX: 
					/* Used in T100D_GT_Rate_Impl and T100D_GT_SDH_Impl transponders.
					 * The both implement TrpOTNTerminalSDH
					 * You can consult this class in padtec source code.
					 * */
					break;

				case TRT_TRAP_J0_TX: 
					/* Used in T100D_GT_Rate_Impl and T100D_GT_SDH_Impl transponders.
					 * The both implement TrpOTNTerminalSDH
					 * You can consult this class in padtec source code.
					 * */
					break;

				case TRT_TRAP_ODU_TTI:
					if(data != null)  {
						if(HandlerHelper.stringChanged(tTerm.getODUk_Impl().getSAPI(), tTerm.getODUk_Impl().getDAPI(), Generator.getString(data, 0, 15), Generator.getString(data, 15, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 72, null, isStart, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_ODU_TTI.getCode(), data, event);
					}
					break;

				case TRT_TRAP_OTU_TTI: 
					if(data != null)  {
						if(HandlerHelper.stringChanged(tTerm.getOTUk_Impl().getSAPI(), tTerm.getOTUk_Impl().getDAPI(), Generator.getString(data, 0, 15), Generator.getString(data, 15, 15)))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 73, null, isStart, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_OTU_TTI.getCode(), data, event);
					}
					break;


				case TRT_TRAP_RATE: 
					if(data != null)  {
						Rate newRate = Generator.getRate(data, 0);
						if(tTerm.getRate() != Rate.NONE && HandlerHelper.objectChanged(tTerm.getRate(), newRate))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1531, null, true, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_RATE.getCode(), data, event);
					}
					break;

				case TRT_TRAP_MAX_FRAME_SIZE:
					/* Used in T100D_GT_Rate_Impl and T100D_GT_ETH_Impl transponders
					 * You can consult this class in padtec source code.
					 * */
					break;

				case TRT_FLAG_CLIENT_SD: 
					if(data != null)
						settTerm(tTerm, TypeGet.TRT_FLAGGET_RS_SD.getCode(), data, event);
					break;

				case TRT_FLAG_REBOOT: 
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1623, null, true, null));
					prepareFullUpdate(tTerm);
					break;

				case TRT_TRAP_ODP_WAITTORESTORE_TIME: 
					if(data != null && tTerm.getODP() != null)
						settTerm(tTerm, TypeGet.TRT_FLAGGET_ODP_WAITTORESTORE_TIME.getCode(), data, event);
					break;

				case TRT_TRAP_ODP_HOLDOFF_TIME: 
					if(data != null && tTerm.getODP() != null)
						settTerm(tTerm, TypeGet.TRT_FLAGGET_ODP_HOLDOFF_TIME.getCode(), data, event);
					break;

				case TRT_TRAP_ODP_NEIGHBOURBOARD_ID:
					if(data != null && tTerm.getODP() != null)	{
						byte part[] = {
								data[5], data[4]
						};
						byte seq[] = {
								data[7], data[6]
						};
						SerialNumber serial = new SerialNumber(Functions.b2i(part, 0, 2), Functions.b2i(seq, 0, 2));
						if(HandlerHelper.objectChanged(tTerm.getODP().getNeighbourBoard(), serial))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1633, null, isStart, null));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_ODP_NEIGHBOURBOARD_ID.getCode(), data, event);
					}
					break;

				case TRT_TRAP_ODP_PATH_STATE:
					if(data != null && tTerm.getODP() != null)
					{
						PathState pathState = PathState.getType(Generator.getInt(data, 1, 1));
						if(HandlerHelper.objectChanged(tTerm.getODP().getPathState(), pathState))
							event.add(AlarmFactory.createGenericAlarm(tTerm, null, 1634, null, isStart, (new StringBuilder("DC")).append(pathState.getStateName()).toString()));
						settTerm(tTerm, TypeGet.TRT_FLAGGET_ODP_PATH_STATE.getCode(), data, event);
					}
					break;

				default:
					break;
				}
			} else {
				this.log.warning( "TrapId [" + Functions.getHexa(Functions.l2b(trapId, 2)) + "] Not Found From " + 
						tTerm.getSerial().toShortString() );
			}

		}
	}

	public boolean prepareFullUpdate(NE_Impl ne) {
		this.response.clearArrival(ne.getSerial());
		return true;
	}

	private void settTerm(T100D_GT_Impl tTerm, int codigo, byte data[], Collection<Notification> event)	{
		TypeGet type = TypeGet.getType(codigo);
		if(type != null) {
			if(data != null){
				response.registerArrival(Integer.valueOf(type.getCode()), tTerm.getSerial());
				switch(type) {
				default:
					break;

				case TRT_GET_OTN: 
					/* Used in T100D_GT_Rate_Impl and T100D_GT_ETH_Impl transponders
					 * You can consult this class in padtec source code.
					 * */
					BigInteger framesOTN = Generator.getBigInteger(data, tTerm.getODUk_Impl().getFramesOTN(), 4, 6, tTerm);
					tTerm.getODUk_Impl().setFramesOTN(framesOTN);
					tTerm.getOTUk_Impl().setFramesOTN(framesOTN);
					FEC fec = tTerm.getFEC();
					if(fec instanceof FEC_Impl)
					{
						FEC_Impl rsFec = (FEC_Impl)fec;
						rsFec.setFramesOTN(framesOTN);
						rsFec.setFixedBits(Generator.getBigInteger(data, rsFec.getFixedBits(), 16, 4, tTerm));
						rsFec.setErroredBlocks(Generator.getBigInteger(data, rsFec.getErroredBlocks(), 20, 4, tTerm));
						rsFec.setFecRxCorrEnabled(Generator.getBit(data, 41, 2));
						rsFec.setFecRxStatsEnabled(Generator.getBit(data, 41, 1));
						rsFec.setFecTxCorrEnabled(Generator.getBit(data, 41, 0));
					}
					tTerm.setBip8_ODUk(Generator.getBigInteger(data, tTerm.getBip8_ODUk(), 24, 4, tTerm));
					tTerm.setBei_ODUk(Generator.getBigInteger(data, tTerm.getBei_ODUk(), 28, 4, tTerm));
					tTerm.setBip8_OTUk(Generator.getBigInteger(data, tTerm.getBip8_OTUk(), 32, 4, tTerm));
					tTerm.setBei_OTUk(Generator.getBigInteger(data, tTerm.getBei_OTUk(), 36, 4, tTerm));
					tTerm.setAutoLaserOff2(Generator.getBit(data, 41, 7));
					tTerm.setAutoLaserOff(Generator.getBit(data, 41, 6));
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, 94, null, tTerm.getODUk_Impl().getBei().compareTo(BigInteger.ZERO) > 0, null));
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, 75, null, tTerm.getODUk_Impl().getBip8().compareTo(BigInteger.ZERO) > 0, null));
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, 95, null, tTerm.getOTUk_Impl().getBei().compareTo(BigInteger.ZERO) > 0, null));
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, 81, null, tTerm.getOTUk_Impl().getBip8().compareTo(BigInteger.ZERO) > 0, null));
					event.add(AlarmFactory.createGenericAlarm(tTerm, null, 97, null, fec.getErroredBlocks().compareTo(BigInteger.ZERO) > 0, null));
					break;


				case TRT_GET_POWER_CLIENT: // '\003'
					tTerm.setPin2(Generator.getPot10G(data, 0, 1, 4, 4, 6, 4, true));
					tTerm.setPout2(Generator.getPot10G(data, 0, 1, 4, 4, 6, 4, false));
					break;

				case TRT_GET_POWER_OTN: // '\002'
					tTerm.setPin(Generator.getPot10G(data, 0, 1, 4, 4, 6, 4, true));
					tTerm.setPout(Generator.getPot10G(data, 0, 1, 4, 4, 6, 4, false));
					break;

				case TRT_GET_TABLE: 
					if(!(tTerm instanceof TrpSintonizavel))	{
						tTerm.getOpticalWDMInterface_Impl().setChannel(Generator.getChannelDWDM(data, 0));
						tTerm.getOpticalWDMInterface_Impl().setLambdaNominal(Generator.getLambda(data, 0));
					} else 	{
						if(tTerm.getOpticalWDMInterface_Impl().getLambdaNominal() != 0.0 && tTerm.getOpticalWDMInterface_Impl().getLambdaNominal() != Generator.getLambdaSintonizavel(data, 6))
							event.add(AlarmFactory.createAlarm(tTerm, true, Alarm.TYPE_TRP_NEW_CHANNEL1));
						tTerm.getOpticalWDMInterface_Impl().setLambdaNominal(Generator.getLambdaSintonizavel(data, 6));
						tTerm.getOpticalWDMInterface_Impl().setChannel(Generator.getChannelDWDMSintonizavel(data, 6));
					}
					tTerm.getOpticalWDMInterface_Impl().getLaserTemperature().setData(tTerm.getOpticalWDMInterface().getChannel());
					
					/* Bloco comentado para teste pois não está setando o valor correto da temperatura do laser
					 * 
					 * if(Functions.compareVersions(tTerm.getVersion(), "3.0") >= 0) {
						double temperature = Generator.getDoubleSigned(data, 11, 3, 1000);
						if(!Functions.testBit(data[11], 7))
							tTerm.getOpticalWDMInterface_Impl().getLaserTemperature().setTemperature(new Double(temperature));
					}*/
					if(Functions.compareVersions(tTerm.getVersion(), "1.15") >= 0)
						tTerm.getOpticalWDMInterface_Impl().setModuleTemperature(Generator.getLong(data, 8, 3) / 1000L);
					break;

				case TRT_GET_TABLE_CLIENT: 
					tTerm.setChannel2(Generator.getChannel(data, 0));
					if(Functions.compareVersions(tTerm.getVersion(), "1.15") >= 0)
						tTerm.getOpticalClientInterface_Impl().setModuleTemperature(Generator.getLong(data, 8, 3) / 1000L);
					break;

				case TRT_FLAGGET_J0: 
					/* Used in T100D_GT_Rate_Impl and T100D_GT_SDH_Impl transponders.
					 * The both implement TrpOTNTerminalSDH
					 * You can consult this class in padtec source code.
					 * */
					log.warning("J0 data not handled by handler Transponder10GTerminalHandler: " + tTerm.getSerial().toShortString());

					break;



				case TRT_FLAGGET_J0_RX: 
					/* Used in T100D_GT_Rate_Impl and T100D_GT_SDH_Impl transponders.
					 * The both implement TrpOTNTerminalSDH
					 * You can consult this class in padtec source code.
					 * */
					log.warning("J0 RX data not handled by handler Transponder10GTerminalHandler: " + tTerm.getSerial().toShortString());
					//					}
					break;

				case TRT_FLAGGET_J0_TX: // '\023'
					//					if(tTerm instanceof TrpOTNTerminalSDH) {
					//						SDH_Impl sdh_impl = null;
					//						if(tTerm instanceof T100D_GT_SDH_Impl)
					//							sdh_impl = ((T100D_GT_SDH_Impl)tTerm).getSDHClientInterface_Impl();
					//						else
					//							if(tTerm instanceof T100D_GT_Rate_Impl)
					//								sdh_impl = ((T100D_GT_Rate_Impl)tTerm).getSDHClientInterface_Impl();
					//						if(sdh_impl != null)
					//						{
					//							String newJ0_TX = Generator.getString(data, 0, 15);
					//							sdh_impl.setJ0_TX(newJ0_TX);
					//						}
					//					} else
					log.warning("J0 TX data not handled by handler Transponder10GTerminalHandler: " +tTerm.getSerial().toShortString());

					break;

				case TRT_FLAGGET_ODU_TTI: // '\007'
					tTerm.getODUk_Impl().setSapi(Generator.getString(data, 0, 15));
					tTerm.getODUk_Impl().setDapi(Generator.getString(data, 15, 15));
					break;

				case TRT_FLAGGET_OTU_TTI: // '\b'
					tTerm.getOTUk_Impl().setSapi(Generator.getString(data, 0, 15));
					tTerm.getOTUk_Impl().setDapi(Generator.getString(data, 15, 15));
					break;

				case TRT_FLAGGET_PT: // '\t'
					tTerm.setPT(Generator.getByte(data, 0));
					break;

				case TRT_FLAGGET_STAT: // '\n'
					if(HandlerHelper.intChanged(tTerm.getStat_ODUk(), Generator.getInt(data, 0)))
						event.add(AlarmFactory.createGenericAlarm(tTerm, null, 80, null, true, null));
					tTerm.setStat_ODUk(Generator.getInt(data, 0));
					break;

				case TRT_FLAGGET_ODU_TTI_REF_TX: // '\013'
					tTerm.getODUk_Impl().setSapiRefTx(Generator.getString(data, 0, 15));
					tTerm.getODUk_Impl().setDapiRefTx(Generator.getString(data, 15, 15));
					break;

				case TRT_FLAGGET_ODU_TTI_REF_RX: // '\f'
					tTerm.getODUk_Impl().setSapiRefRx(Generator.getString(data, 0, 15));
					tTerm.getODUk_Impl().setDapiRefRx(Generator.getString(data, 15, 15));
					break;

				case TRT_FLAGGET_OTU_TTI_REF_TX: // '\r'
					tTerm.getOTUk_Impl().setSapiRefTx(Generator.getString(data, 0, 15));
					tTerm.getOTUk_Impl().setDapiRefTx(Generator.getString(data, 15, 15));
					break;

				case TRT_FLAGGET_OTU_TTI_REF_RX: // '\016'
					tTerm.getOTUk_Impl().setSapiRefRx(Generator.getString(data, 0, 15));
					tTerm.getOTUk_Impl().setDapiRefRx(Generator.getString(data, 15, 15));
					break;


				case TRT_GET_FCS_COUNTERS: 
					
					/* Used in T100D_GT_Rate_Impl and T100D_GT_ETH_Impl transponders
					 * You can consult this class in padtec source code.
					 * */
					break;

				case TRT_FLAGGET_RATE:
					Rate newRate = Generator.getRate(data, 0);
					tTerm.setRate(newRate);
					/* Used in T100D_GT_Rate_Impl transponder
					 * You can consult this class in padtec source code.
					 * */
					break;

				case TRT_FLAGGET_MAX_FRAME_SIZE: 
					/* Used in T100D_GT_Rate_Impl and T100D_GT_ETH_Impl transponders
					 * You can consult this class in padtec source code.
					 * */
					break;


				case TRT_FLAGGET_RS_SD: 
					log.warning("RS SD data not handled by handler Transponder10GTerminalHandler: " + tTerm.getSerial().toShortString());

					break;

				case TRT_FLAGGET_ODP_WAITTORESTORE_TIME: 
					if(data != null && tTerm.getODP_Impl() != null)
						tTerm.getODP_Impl().setWaitToRestoreTime(new Integer(Generator.getInt(data, 0, 2)));
					break;

				case TRT_FLAGGET_ODP_HOLDOFF_TIME: 
					if(data != null && tTerm.getODP_Impl() != null)
						tTerm.getODP_Impl().setHoldOffTime(new Integer(Generator.getInt(data, 0, 2)));
					break;

				case TRT_FLAGGET_ODP_NEIGHBOURBOARD_ID: 
					if(data != null && tTerm.getODP_Impl() != null)	{
						byte part[] = {
								data[5], data[4]
						};
						byte seq[] = {
								data[7], data[6]
						};
						SerialNumber serial = new SerialNumber(Functions.b2i(part, 0, 2), Functions.b2i(seq, 0, 2));
						tTerm.getODP_Impl().setNeighbourBoard(serial);
					}
					break;

				case TRT_FLAGGET_ODP_PATH_STATE: 
					if(data != null && tTerm.getODP_Impl() != null)
						tTerm.getODP_Impl().setPathState(br.com.padtec.v3.data.ne.ODP.PathState.getType(Generator.getInt(data, 1, 1)));
					break;
				}
			} else	{
				log.warning("Response [" + codigo + "] From " + tTerm.getSerial().toShortString() + " has TLV with null Value ");
			}
		} else {
			log.warning("Response ["+codigo+"] Not Found From "+tTerm.getSerial().toShortString());
		}
	}




	public boolean isFullUpdated(SerialNumber serial) {
		return this.response.hasAllArrivals(serial);
	}

	public static enum TypeGet	{
	
		TRT_GET_TABLE_CLIENT(116),
		TRT_GET_POWER_OTN(35),
		TRT_GET_POWER_CLIENT(67),
		TRT_GET_OTN(192),
		TRT_GET_TABLE(21504),
		TRT_FLAGGET_J0(163),
		TRT_FLAGGET_ODU_TTI(162),
		TRT_FLAGGET_OTU_TTI(193),
		TRT_FLAGGET_PT(164),
		TRT_FLAGGET_STAT(194),
		TRT_FLAGGET_ODU_TTI_REF_TX(181),
		TRT_FLAGGET_ODU_TTI_REF_RX(182),
		TRT_FLAGGET_OTU_TTI_REF_TX(190),
		TRT_FLAGGET_OTU_TTI_REF_RX(191),
		TRT_GET_FCS_COUNTERS(242),
		TRT_FLAGGET_RATE(243),
		TRT_FLAGGET_MAX_FRAME_SIZE(245),
		TRT_FLAGGET_J0_RX(247),
		TRT_FLAGGET_J0_TX(249),
		TRT_FLAGGET_RS_SD(251),
		TRT_FLAGGET_ODP_WAITTORESTORE_TIME(153),
		TRT_FLAGGET_ODP_HOLDOFF_TIME(155),
		TRT_FLAGGET_ODP_NEIGHBOURBOARD_ID(156),
		TRT_FLAGGET_ODP_PATH_STATE(157);
		private final int code;

		private TypeGet(int code){
			this.code = code;
		}

		public int getCode()
		{
			return this.code;
		}

		public static TypeGet getType(int decode) {
			for (TypeGet type : values()) {
				if (type.getCode() == decode) {
					return type;
				}
			}
			return null;
		}
	}

	public static enum TypeTrap {
	
		TRT_TRAP_LOS(1),
		TRT_TRAP_LOF(2),
		TRT_TRAP_FAIL(3),
		TRT_TRAP_LASEROFF(4),
		TRT_TRAP_LOS2(5),
		TRT_TRAP_LOF2(7),
		TRT_TRAP_FAIL2(21),
		TRT_TRAP_LASEROFF2(6),
		TRT_TRAP_ODU_TIM(8),
		TRT_TRAP_ODU_BDI(9),
		TRT_TRAP_ODU_AIS(10),
		TRT_TRAP_LOSSYNC(11),
		TRT_TRAP_LOSSYNC2(12),
		TRT_TRAP_OTU_TIM(17),
		TRT_TRAP_OTU_BDI(18),
		TRT_TRAP_LOM(19),
		TRT_TRAP_ENC_AIS_OFF(22),
		TRT_TRAP_ODP_SIGNALDEGRADE(256),
		TRT_TRAP_ODP_WAITTORESTORE(257),
		TRT_TRAP_ODP_PATHTYPE(258),
		TRT_TRAP_ODP_DISABLED(259),
		TRT_TRAP_ODP_NEIGHBOUR_CABLE_FAIL(260),
		TRT_TRAP_ODP_NEIGHBOUR_COMM_LOS(261),
		TRT_TRAP_ODP_CONFIG_ERROR(262),
		TRT_TRAP_ODP_CABLE_FAIL(263),
		TRT_TRAP_ODP_COMM_LOS(264),
		TRT_TRAP_ODP_LASER_OFF_ODP(265),
		TRT_TRAP_ODP_MANUAL_LASER_ON(266),
		TRT_TRAP_LASER_SHUTDOWN_CLIENT(304),
		TRT_TRAP_LASER_SHUTDOWN_OTN(305),
		TRT_TRAP_TEMP_OTN_ALARM(254),
		TRT_TRAP_J0(13),
		TRT_TRAP_ODU_TTI(14),
		TRT_TRAP_OTU_TTI(15),
		TRT_TRAP_PT(16),
		TRT_TRAP_STAT(20),
		TRT_TRAP_ODU_TTI_REF_TX(23),
		TRT_TRAP_ODU_TTI_REF_RX(24),
		TRT_TRAP_OTU_TTI_REF_TX(25),
		TRT_TRAP_OTU_TTI_REF_RX(26),
		TRT_TRAP_PLM(69),
		TRT_TRAP_J0_RX(104),
		TRT_TRAP_J0_TX(105),
		TRT_TRAP_FCS_LINK_DOWN(112),
		TRT_TRAP_MAC_RECEIVE_LOCAL_FAULT(113),
		TRT_TRAP_MAC_RECEIVE_REMOTE_FAULT(114),
		TRT_TRAP_RS_TIM(115),
		TRT_TRAP_CLIENT_SD(116),
		TRT_TRAP_CLIENT_SF(117),
		TRT_TRAP_ENC_RS_TIM(118),
		TRT_TRAP_J0_MODE_FINISHED(119),
		TRT_TRAP_RATE(243),
		TRT_TRAP_MAX_FRAME_SIZE(245),
		TRT_FLAG_CLIENT_SD(251),
		TRT_FLAG_REBOOT(4095),
		TRT_TRAP_ODP_WAITTORESTORE_TIME(512),
		TRT_TRAP_ODP_HOLDOFF_TIME(513),
		TRT_TRAP_ODP_NEIGHBOURBOARD_ID(514),
		TRT_TRAP_ODP_PATH_STATE(515);

		private final int code;

		private TypeTrap(int code){
			this.code = code;
		}
		public int getCode() {
			return this.code;
		}

		public static TypeTrap getType(int code) {
			for (TypeTrap type : values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null;
		}
	}
}