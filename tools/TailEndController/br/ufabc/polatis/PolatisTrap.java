package br.ufabc.polatis;

import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

public class PolatisTrap implements SNMPTrap {

	public void processPDU(PDU pdu) {	
		//System.out.println("Trap Type = " + pdu.getType());
		//System.out.println("Variable Bindings = " + pdu.getVariableBindings());
		Vector<VariableBinding> bindings = pdu.getVariableBindings();
		String uptime = null;
		OID trap_oid0 = null;
		for (VariableBinding bind : bindings) {
			OID oid = bind.getOid();
			Variable variable = bind.getVariable();
			if (oid.equals(PolatisOXC.upTime)) {
				uptime = variable.toString();
			} else if (oid.equals(PolatisOXC.snmpTrapOID_0)) {
				trap_oid0 = new OID(variable.toString());
			}
		}
		System.out.println("Trap: " + trap_oid0.toString());

		/* Polatis System Events *///OK
		if (trap_oid0.equals(PolatisOXC.coldStart)) { //Cold start
			System.out.println("Cold start!");						
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFanWarning)){ //  A cooling fan is failing.
			System.out.println("Fan in fail! ");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFanFail)){ // A cooling fan has failed.
			System.out.println("A cooling fan has failed! ");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFpgaError)){ // FPGA programming error.
			System.out.println("FPGA programming error");					   
		} else if (trap_oid0.equals(PolatisOXC.polatisSysConfigError)){ // Configure file error.
			System.out.println("Configure file error");						   
		} else if (trap_oid0.equals(PolatisOXC.polatisSysGeneralError)){ // General system error.
			System.out.println("General system error");				   
		} else if (trap_oid0.equals(PolatisOXC.polatisSysPsuError)){ // Power supply event.
			System.out.println("Power supply event");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFanWarningV2)){ // A cooling fan is failing.
			System.out.println("A cooling fan is failing");	
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFanWarningUpdated)){ // The status of fan-failed alarm has been updated.
			System.out.println("Update alarm fan");	   
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFanFailV2)){ // A cooling fan has failed.
			System.out.println("A cooling fan has failed");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFanFailUpdated)){ // The status of an FPGA programming error alarm has been updated.
			System.out.println("Update alarm error FPGA.");	
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFpgaErrorV2)){ // FPGA programming error.
			System.out.println("FPGA programming error.");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysFpgaErrorUpdated)){ // The status of an FPGA programming error alarm has been updated.
			System.out.println("Update alarm error configure FPGA programming.");	
		} else if (trap_oid0.equals(PolatisOXC.polatisSysConfigErrorV2)){ // Configuration file error.
			System.out.println(" Configuration file error.");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysConfigErrorUpdated)){ // The status of a configuration file error alarm has been updated.
			System.out.println(" Configuration file error alarm has been update.");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysGeneralErrorV2)){ // General system error.
			System.out.println(" General system error.");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysGeneralErrorUpdated)){ // The status of a General system error alarm has been updated.
			System.out.println("Update alarm general system error.");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysPsuErrorV2)){ // Power supply event.
			System.out.println("Power supply event.");
		} else if (trap_oid0.equals(PolatisOXC.polatisSysPsuErrorUpdated)){ // The status of a Power supply alarm has been updated.
			System.out.println("Update alarm power sypply event.");	          
		} else if (trap_oid0.equals(PolatisOXC.polatisSysMissingEventError)){ // An expected event did not occur in the switch.
			System.out.println("An expected event did not occur in the switch.");	          
		} else if (trap_oid0.equals(PolatisOXC.polatisSysMissingEventErrorUpdated)){ // The status of a Missing Event alarm has been updated.
			System.out.println("The Missing Event alarm has been updated.");	 
		} else if (trap_oid0.equals(PolatisOXC.polatisSysEventMsg)){ // A message providing further information about the event.
			System.out.println(" More information about the event. ");
			
		/* polatisOxcEventsGroups */
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortsGroups)){ // The objects to control OXC ports on the switch.
			System.out.println(" The objects to control OXC ports on the switch. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcNotificationsGroup)){ // The notifications sent by this MIB.
			System.out.println(" The notifications sent by this MIB.  ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcEventGroup)){ // Objects associated with OXC events.
			System.out.println(" Objects associated with OXC events. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcEventGroupV2)){ // Objects associated with OXC events V2.
			System.out.println(" Objects associated with OXC events V2 ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcNotificationsGroupV2)){ // The notifications sent by this MIB V2.
			System.out.println(" The notifications sent by this MIB V2 ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortsComplianceV1)){ // All controllers should provide this level of support.
			System.out.println(" Level of support provided by the controllers. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortsComplianceV2)){ // All controllers should provide this level of support V2.
			System.out.println(" Level of support provided by the controllers V2. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortsComplianceEvtV1)){ // Implementation including notifications.
			System.out.println(" Implementation including notifications. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortsComplianceEvtV2)){ // Implementation including notifications V2.
			System.out.println(" Implementation including notifications V2. ");		
			
		/* polatisOxcPortEvents */
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortPatch)){ // Port patch
			System.out.println("Port patch!");
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC.polatisOxcPortCurrentState)) { // Port current status
					int i = variable.toInt();
					if (i == 1)
						System.out.print("Port enabled -");
					if (i == 2)
						System.out.print("Port disabled -");
					else  
						System.out.print("Port in fail"); 
				}
			}
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC.polatisOxcPortDesiredState)) { // Port desired state
					int i = variable.toInt();
					if (i == 1)
						System.out.print("Port desired state enable  -");
					else if (i == 2)
						System.out.print("Port desired state disable -");
				}
			}
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC.polatisOxcForceUpdates)) {
					int i = variable.toInt();
					if (i == 1)
						System.out.print("True -");
					else if (i == 2)
						System.out.print("False -");
				}
			}
		/** polatisOpmConfigObjs */
					
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmType)) { // Indicates whether an OPM is configured as an input or output monitor
			System.out.println("OPM type!");
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC.polatisOpmAlarmEdge)) {
					int i = variable.toInt();
					if (i == 1)
						System.out.print("Low -");// When 'low' the alarm will fire if the measured power drops below the low threshold.							                         
					if (i == 2)
						System.out.print("Higt -");// When 'high' the alarm will fire if the measured power rises below the high threshold.
					else  
						System.out.print("Both -");// When 'both' the alarm will fire if the power crosses either the low or high thresholds.
				}
			}
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmAlarmLowThresh)) { // Low-power threshold for triggering an alarm.
			System.out.println("The wavelength of the light!");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmAlarmHighThresh)) { // High-power threshold for triggering an alarm.
			System.out.println("The wavelength of the light!");
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC. polatisOpmAlarmMode )) {
					int i = variable.toInt();
					if (i == 1)
						System.out.print("Off -");// When "off" the alarm is disabled.
					if (i == 2)
						System.out.print("Single -");//  when "Single" the alarm is enable port single. 
					else  
						System.out.print("Continuous -");// When "continuous" the alarm fires it remains active and will fire again the next time the power crosses the configured threshold. .
				}
			}
		/** polatisOxcEvents *///OK
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcSwitchComplete)) { //Switch complete.
			System.out.println("Switch command completed.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortEnable)){ // A Port has been enabled or disabled.
			System.out.println("Port enable or disabled!");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcTempRange)){  // The switch has exceeded its operating temperature range.
			System.out.println("Exceeded temperature range!");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcError)){ //An OXC error has occurred.
			System.out.println("An OXC error has occurred!");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortError)){ // A OXC port error has occurred.
			System.out.println("A OXC port error has occurred!");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcCompensationResumed)){ // OXC Compensation has resumed.
			System.out.println("OXC Compensation has resumed.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcSwitchCompleteV2)){ // A switch command has completed.
			System.out.println("A switch command has completed.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortEnableV2)){ // A port has been enabled or disabled.
			System.out.println("A port has been enabled or disabled.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcTempRangeV2)){ // The switch has exceeded is operating temperature range.
			System.out.println("The switch has exceeded is operating temperature range.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcTempRangeUpdated)){ // The status of a temperature range alarm has been updated.
			System.out.println(" Update a temperature alarm.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcErrorV2)){ // An OXC error has occurred.
			System.out.println(" An OXC error has occurred.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcErrorUpdated)){ // The status of an OXC alarm has been updated.
			System.out.println(" Updated a alarm OXC.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortErrorV2)){ // A OXC port error has occurred.
			System.out.println(" A OXC port error has occurred.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortErrorUpdated)){ // The status of an OXC port alarm has been updated.
			System.out.println(" The status of an OXC port alarm has been updated.");	
			
		/* PolatisOxcEventObjects*///OK
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcEventMsg)){ // A message providing further information about the event.
			System.out.println(" Message about the event. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcEventsPortState)){ // The current state of the port.
			System.out.println(" The current state of the port. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcIngressPortList)){ // The ingress ports affected by the event.
			System.out.println(" The ingress ports affected by the event. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcIngressPortLabel)){ // Text label for the ingress port.
			System.out.println(" Text label for the ingress port. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcEgressPortList)){ // The ingress ports affected by the event.
			System.out.println(" The ingress ports affected by the event. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcEgressPortLabel)){ // Text label for the egress port.
			System.out.println("  Text label for the egress port.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortList)){ // A list of ports affected by the event.
			System.out.println(" A list of ports affected by the event. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOxcPortLabel)){ // Text label for the port affected by the event.
			System.out.println(" Text label for the port affected by the event. ");
		
			
		/* polatisOpmEvents *///OK
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmPowerMonitorAlarm)){ // The 'Loss of Service' power monitor alarm has triggered.
			System.out.println("A 'Loss of Service' power monitor alarm has triggered!");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmDegradedPowerMonitorAlarm)){ // The 'Degraded Signal' power monitor alarm has triggered
			System.out.println("The 'Degraded Signal' power monitor alarm has triggered!");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmPowerMonitorAlarmV2)){ // A 'Loss of Service' power monitor alarm has triggered.
			System.out.println("A 'Loss of Service' power monitor alarm has triggered.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmPowerMonitorAlarmUpdate)){ // The status of a 'Loss of Service' power monitor alarm has been update.
			System.out.println("The status of a 'Loss of Service' power monitor alarm has been update.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmDegrPowerMonitorAlarm)){ // A 'Degraded Signal' power monitor alarm has triggered.
			System.out.println("A 'Degraded Signal' power monitor alarm has triggered.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmDegrPowerMonitorAlarmUpdate)){ // The status of a 'Degraded Signal' power monitor alarm has been updated.
			System.out.println("The status of a 'Degraded Signal' power monitor alarm has been updated.");
			
		/*PolatisOpmEventObjects*/
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmAlarmPort)){ // The port for switch the power monitor alarm fired.
			System.out.println("The port for switch the power monitor alarm fired.");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmAlarmPortLabel)){ // The text label for the alarmed port.
			System.out.println("The text label for the alarmed port.");	
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmNotificationsGroup)){ // The notifications sent by this MIB.
			System.out.println("The notifications sent by this MIB. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmEventGroup)){ //  Objects associated with OPM events.
			System.out.println("Objects associated with OPM events.  ");	
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmNotificationsGroupV2)){ // The notifications sent by this MIB V2.
			System.out.println("The notifications sent by this MIB V2. ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmEventGroupV2)){ //  Objects associated with OPM events V2.
			System.out.println("Objects associated with OPM events V2.  ");
		} else if (trap_oid0.equals(PolatisOXC.polatisOpmNotificationsGroupV3)){ // The notifications sent by this MIB V3.
			System.out.println("The notifications sent by this MIB V3. ");	
		
		/* polatisApsEvents */
		} else if (trap_oid0.equals(PolatisOXC.polatisApsProtGroupStatus)){ // The status of this row in the table.
			System.out.println("The status of  row.!");
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC. polatisApsPortCurrentState )) {//The current state of the port.
					int i = variable.toInt();
					if (i == 1)
						System.out.print("is -");// When 'is' the port is in service.
					if (i == 2)
						System.out.print("oosma -");//  when 'oosma' indicates that the door was manually removed from service. 
					else  
						System.out.print("oosau -");// When "oosau" indicates that the door was automatically taken out of service.
				}
			}
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC.polatisApsPortDesiredState)) {// The desired state of the port.
					int i = variable.toInt();
					if (i == 1)
						System.out.print("is -");//
					else if (i == 2)
						System.out.print("oos -");//
				}
			}
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC. polatisApsPortCurrentCond )) {// The current inhibitions imposed on the port control APS.
					int i = variable.toInt();
					if (i == 1)
						System.out.print("none -");// 
					if (i == 2)
						System.out.print("inhswpr -");// 
					else  
						System.out.print("inhswwkg -");//
				}
			}
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC. polatisApsPortDesiredCond )) {// The desired inhibitions imposed on the port to control APS.
					int i = variable.toInt();
					if (i == 1)
						System.out.print("none -");// 
					if (i == 2)
						System.out.print("inhswpr -");// 
					else  
						System.out.print("inhswwkg -");//
				}
			}
		} else if (trap_oid0.equals(PolatisOXC.polatisApsProtectionSwitch)) { //Protection switch.
			System.out.println("A protection switch event has occured!");
		} else if (trap_oid0.equals(PolatisOXC.polatisApsProtectionSwitchV2)) { //A protection switch has occurred V2.
			System.out.println("A protection switch has occurred V2.");
		} else if (trap_oid0.equals(PolatisOXC.polatisApsProtectionSwitchUpdated)) { //The status of a protection switch alarm has been updated.
			System.out.println("The status of a protection switch alarm has been updated.");
			for (VariableBinding bind : bindings) {
				OID oid = bind.getOid();
				Variable variable = bind.getVariable();
				if (oid.equals(PolatisOXC.polatisApsProtSwitchType)) {
					int i = variable.toInt();
					if (i == 1)
						System.out.print("Protection -");
					else if (i == 2)
						System.out.print("Reversion -");
				} else if (trap_oid0.equals(PolatisOXC.polatisApsEventGroup)) { // Objects associated with APS events.
					System.out.println("Objects associated with APS events.");
				} else if (trap_oid0.equals(PolatisOXC.polatisApsEventGroupV2)) { // Objects associated with APS events V2.
					System.out.println("Objects associated with APS events V2.");
				} else if (oid.equals(PolatisOXC.polatisApsConnectedPort)) {
					System.out.print(" Connected port: " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisApsWorkingPort)) {
					System.out.print(", Working port: " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisApsProtectionPort)) {
					System.out.println(", Protection port: " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisApsConnectedPortLabel)) {
					System.out.println(",Connected port label : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisApsWorkingPortLabel)) {
					System.out.println(",Working port label : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisApsProtectingPortLabel)) {
					System.out.println(",Protected port label : " + variable.toInt());
					
				/** polatisVoaGroup */
				} else if (trap_oid0.equals(PolatisOXC.polatisVoaEventGroup)) { // Objects associated with VOA events.
					System.out.println("Objects associated with VOA events.");
				} else if (oid.equals(PolatisOXC.polatisVoaPortList)) { // A list of ports affected by the event.
					System.out.println(",List of ports affected : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisVoaPortLabel)) { // Text label for the port affected by the event.
					System.out.println(",Text label for the port affected : " + variable.toInt());
				} else if (trap_oid0.equals(PolatisOXC.polatisVoaAttenuationComplete)) { // Attenuation complete. 
					System.out.println("VOA attenuation completed!");
				} else if (trap_oid0.equals(PolatisOXC.polatisVoaAttenuationCompleteV2)) { // Attenuation complete V2.
					System.out.println("VOA attenuation completed V2!");	
									
				/** PolatisEventGroups */
				} else if (oid.equals(PolatisOXC.polatisCtrlTableGroup)) { // Objects to control event handling.
					System.out.println("Objects to control event handling. " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventLogGroup)) { // Objects to report logs.
					System.out.println("Objects to report logs. " + variable.toInt());
				} else if (trap_oid0.equals(PolatisOXC.polatisEventTrapGroup)) { // Objects used in notifications by other MIBs.
					System.out.println("Objects used in notifications by other MIBs.");
					
				/** PolatisEventCompliance */
				} else if (oid.equals(PolatisOXC.polatisEventComplianceV1)) { // All controllers should provide this level of support V1.
					System.out.println("All controllers should provide this level of support V1. " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventComplianceV2)) { // All controllers should provide this level of support V2.
					System.out.println("All controllers should provide this level of support V2. " + variable.toInt());
				} else if (trap_oid0.equals(PolatisOXC.polatisEventComplianceV3)) { // All controllers should provide this level of support V3.
					System.out.println("All controllers should provide this level of support V3.");
				
				/** polatisEventCtrlObjs */
				} else if (oid.equals(PolatisOXC.polatisEventTable)) { // A list of events to be generated.
					System.out.println("A list of events to be generated. " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventEntry)) { // A set of parameters that describe an event to be generated when certain conditions are met.
					System.out.println("A set of parameters that describe an event. " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventIndex)) { // An index that uniquely identifies an entry in the event table.
					System.out.println(",Index an entry in event table : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventDescription)) { // A comment describing this event entry.
					System.out.println(",Descriction event entry : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventType)) { // The type of notification that the probe will make about this event.
					System.out.println(",Type of notification : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventCommunity)) { // If an SNMP trap is to be sent, it will be sent to the SNMP community specified by this octet string.
					System.out.println(",SNMP trap is to be sent : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventLastTimeSent)) { // The value of sysUpTime at the time this event entry last generated an event.
					System.out.println(",The value of sysUpTime : " + variable.toInt());
					
				/** polatisEventLogObjs */	
				} else if (oid.equals(PolatisOXC.polatisLogTable)) { // A list of events that have been logged.
					System.out.println(" A list of events that have been logged. " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisLogEntry)) { // A set of data describing an event that has been logged.
					System.out.println("A set of data describing an event that has been logged. " + variable.toInt());	
				} else if (oid.equals(PolatisOXC.polatisLogEventIndex)) { // The event entry that generated this log entry.
					System.out.println(",Log entry : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisLogIndex)) { // An index that uniquely identifies an entry in the log table.
					System.out.println(",Index entry in the log table : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisLogTime)) { // The value of sysUpTime when this log entry was created.
					System.out.println(",Log entry was created : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisLogDescription)) { // An implementation dependent description of the event that activated this log entry.
					System.out.println(",Description of the event. : " + variable.toInt());
					
				/** polatisEventTrapObjects*/
				} else if (oid.equals(PolatisOXC.polatisEventMsg)) { // A message providing further information about an event.
					System.out.println(",A message an event. : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisAlarmId)) { // The ID of an alarm.
					System.out.println(",The ID of an alarm : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisAlarmStatus)) { // The status of an alarm.
					System.out.println(",The status of an alarm : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisAlarmUser)) { // The user who updated the status of the alarm.
					System.out.println(",The user who updated the status : " + variable.toInt());
				} else if (oid.equals(PolatisOXC.polatisEventSubSwitch)) { // The Sub-Switch for which the event was generated.
					System.out.println(",The sub-switch : " + variable.toInt());
				}
			}
		}
	}
}
