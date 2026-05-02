package br.ufabc.polatis;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.UdpAddress;

public class PolatisOXC {
	/** The list of Igress ports currently actived */
	private Vector<String> activedIgressPorts = new Vector<String>();
	/** The list of egress ports currently actived */
	private Vector<String> activedEgressPorts = new Vector<String>();
	/** The instance of the SNMP Client to the Polatis. */
	private SNMPClient client;
	/** The instance of the Polatis trap processor. */
	private PolatisTrap agent;
	/** Up time of the system. */
	protected static OID upTime;
	/** System description. */
	protected static OID sysDescr;
	/** Automatic Configure the address and subnet */
	protected static OID autom_Recalc_Net;
	/** SNMP Trap OID.0 */
	protected static OID snmpTrapOID_0;
	/** Cold start. */
	protected static OID coldStart;
	// Polatis System Information Objects
	/** Product code of the switch. */
	protected static OID polatisSysInfoProductCode;
	/** Serial number of the switch. */
	protected static OID polatisSysInfoSerialNumber;
	/** Version number of the firmware. */
	protected static OID polatisSysInfoFirmwareVersion;
	// Polatis System Interface Configuration Objects
	/** The communication protocol running on this interface. */
	protected static OID polatisInterfaceConfigProtocol;
	/** The device parameters for this interface. */
	protected static OID polatisInterfaceConfigDevice;
	/** The status of this interface */
	protected static OID polatisInterfaceConfigStatus;
	// Polatis System Events
	/** A cooling fan is failing. */
	protected static OID polatisSysFanWarning;
	/** A cooling fan has failed. */
	protected static OID polatisSysFanFail;
	/** FPGA programming error. */
	protected static OID polatisSysFpgaError;
	/** Configure file error. */
	protected static OID polatisSysConfigError;
	/** General system error. */
	protected static OID polatisSysGeneralError;
	/** Power supply event. */
	protected static OID polatisSysPsuError;
	/** A cooling fan is failing */
	protected static OID polatisSysFanWarningV2;
	/** The status of fan-failing alarm has been updated. */
	protected static OID polatisSysFanWarningUpdated;
	/** A cooling fan has failed */
	protected static OID polatisSysFanFailV2;
	/** The status of fan-failed alarm has been updated. */
	protected static OID polatisSysFanFailUpdated;
	/** FPGA programming error. */
	protected static OID polatisSysFpgaErrorV2;
	/** The status of an FPGA programming error alarm has been updated. */
	protected static OID polatisSysFpgaErrorUpdated;
	/** Configuration file error. */
	protected static OID polatisSysConfigErrorV2;
	/** The status of a Configured file error alarm has been updated. */
	protected static OID polatisSysConfigErrorUpdated;
	/** General system error. */
	protected static OID polatisSysGeneralErrorV2;
	/** The status of a General system error alarm has been updated. */
	protected static OID polatisSysGeneralErrorUpdated;
	// /** Power supply event*/
	protected static OID polatisSysPsuErrorV2;
	/** The status of a Power supply alarm has been updated. */
	protected static OID polatisSysPsuErrorUpdated;
	/** An expected event did not occur in the switch. */
	protected static OID polatisSysMissingEventError;
	/** The status of a Missing Event alarm has been updated. */
	protected static OID polatisSysMissingEventErrorUpdated;
	// Polatis System Event Objects
	/** A message providing further information about the event. */
	protected static OID polatisSysEventMsg;
	// Polatis OXC Groups
	/** The objects to control OXC ports on the switch. */
	protected static OID polatisOxcPortsGroups;
	/** The notifications sent by this MIB. */
	protected static OID polatisOxcNotificationsGroup;
	/** Objects associated with OXC events. */
	protected static OID polatisOxcEventGroup;
	/** Objects associated with OXC events. */
	protected static OID polatisOxcEventGroupV2;
	/** The notifications sent by this MIB. */
	protected static OID polatisOxcNotificationsGroupV2;
	// Polatis OXC Compliance
	/** All controllers should provide this level of support. */
	protected static OID polatisOxcPortsComplianceV1;
	/** All controllers should provide this level of support. */
	protected static OID polatisOxcPortsComplianceV2;
	/** Implementation including notifications. */
	protected static OID polatisOxcPortsComplianceEvtV1;
	/** Implementation including notifications. */
	protected static OID polatisOxcPortsComplianceEvtV2;
	// Polatis OXC Port Objects
	/**
	 * Size of switch matrix in the form NxM, where N is the number of ingress
	 * and M is the number of egress port.
	 */
	protected static OID polatisOxcSize;
	/** Configure a patch in the switch. */
	protected static OID polatisOxcPortPatch;
	/** Current state of the OXC port. */
	protected static OID polatisOxcPortCurrentState;
	/** Desired State of the OXC Port. */
	protected static OID polatisOxcPortDesiredState;
	/** Polatis OXC Force Updates. */
	protected static OID polatisOxcForceUpdates;
	// Polatis OXC Events
	/** A switch command has completed. */
	protected static OID polatisOxcSwitchComplete;
	/** A port has been enabled or disabled */
	protected static OID polatisOxcPortEnable;
	/** The switch has exceeded its operating temperature range */
	protected static OID polatisOxcTempRange;
	/** An OXC error has occurred */
	protected static OID polatisOxcError;
	/** An OXC port error has occurred */
	protected static OID polatisOxcPortError;
	/** OXC Compensation has resumed. */
	protected static OID polatisOxcCompensationResumed;
	/** A switch command has completed. */
	protected static OID polatisOxcSwitchCompleteV2;
	/** A port has been enabled or disabled. */
	protected static OID polatisOxcPortEnableV2;
	/** The switch has exceeded is operating temperature range. */
	protected static OID polatisOxcTempRangeV2;
	/** The status of a temperature range alarm has been updated. */
	protected static OID polatisOxcTempRangeUpdated;
	/** An OXC error has occurred. */
	protected static OID polatisOxcErrorV2;
	/** The status of an OXC alarm has been updated. */
	protected static OID polatisOxcErrorUpdated;
	/** A OXC port error has occurred. */
	protected static OID polatisOxcPortErrorV2;
	/** The status of an OXC port alarm has been updated. */
	protected static OID polatisOxcPortErrorUpdated;
	// Polatis OXC Event Objects
	/** A message providing further information about the event. */
	protected static OID polatisOxcEventMsg;
	/** The current state of the port. */
	protected static OID polatisOxcEventsPortState;
	/** The ingress ports affected by the event. */
	protected static OID polatisOxcIngressPortList;
	/** Text label for the ingress port. */
	protected static OID polatisOxcIngressPortLabel;
	/** The ingress ports affected by the event. */
	protected static OID polatisOxcEgressPortList;
	/** Text label for the egress port. */
	protected static OID polatisOxcEgressPortLabel;
	/** A list of ports affected by the event. */
	protected static OID polatisOxcPortList;
	/** Text label for the port affected by the event. */
	protected static OID polatisOxcPortLabel;
	// Polatis OPM Groups
	/** The objects to configure OPMs on the switch. */
	protected static OID polatisOpmConfigGroup;
	/** The objects to measure OPM ouput on the switch. */
	protected static OID polatisOpmMeasureGroup;
	/** The notifications sent by this MIB. */
	protected static OID polatisOpmNotificationsGroup;
	/** Objects associated with OPM events. */
	protected static OID polatisOpmEventGroup;
	/** The notifications sent by this MIB V2 */
	protected static OID polatisOpmNotificationsGroupV2;
	/** Objects associated with OPM events V2. */
	protected static OID polatisOpmEventGroupV2;
	/** The notifications sent by this MIB V3 */
	protected static OID polatisOpmNotificationsGroupV3;
	// Polatis OPM Configuration Objects
	/** Wavelength monitoring. */
	protected static OID polatisOpmWavelength;
	/** Power level Offset (dB) */
	protected static OID polatisOpmOffset;
	/** Optical Power measurement Averaging Time (ms). */
	protected static OID polatisOpmAtime;
	/** Indicates whether an OPM is configured as an input or output monitor. */
	protected static OID polatisOpmType;
	/** Description Alarm Edge Options. */
	protected static OID polatisOpmAlarmEdge;
	/** Threshold for loss of service alarm - LOW. */
	protected static OID polatisOpmAlarmLowThresh;
	/** Threshold for loss of service alarm - HIGH. */
	protected static OID polatisOpmAlarmHighThresh;
	/** Configure power monitor alarms. */
	protected static OID polatisOpmAlarmMode;
	// Polatis OPM Measure Objects
	/** Alarm current power measured at OPM */
	protected static OID polatisOpmPower;
	// Polatis OPM Events
	/** The 'Loss of Service' power monitor alarm has triggered */
	protected static OID polatisOpmPowerMonitorAlarm;
	/** The 'Degraded Signal' power monitor alarm has triggered. */
	protected static OID polatisOpmDegradedPowerMonitorAlarm;
	/** A 'Loss of Service' power monitor alarm has triggered. */
	protected static OID polatisOpmPowerMonitorAlarmV2;
	/** The status of a 'Loss of Service' power monitor alarm has been update. */
	protected static OID polatisOpmPowerMonitorAlarmUpdate;
	/** A 'Degraded Signal' power monitor alarm has triggered. */
	protected static OID polatisOpmDegrPowerMonitorAlarm;
	/** The status of a 'Degraded Signal' power monitor alarm has been updated. */
	protected static OID polatisOpmDegrPowerMonitorAlarmUpdate;
	// Polatis OPM Event Objects
	/** The port for switch the power monitor alarm fired. */
	protected static OID polatisOpmAlarmPort;
	/** The text label for the alarmed port */
	protected static OID polatisOpmAlarmPortLabel;
	// Polatis VOA Group
	/** Objects associated with VOA events. */
	protected static OID polatisVoaEventGroup;
	// Polatis VOA Port Objects
	/** Used to specify the desired attenuation level in (dB). */
	protected static OID polatisVoaLevel;
	/** Reference port for attenuation. */
	protected static OID polatisVoaRefport;
	/** The current state of attenuation for the port. */
	protected static OID polatisVoaCurrentState;
	/** The desired state of attenuation for the port. */
	protected static OID polatisVoaDesiredState;
	// Polatis VOA Event
	/** An attenuation command has completed! */
	protected static OID polatisVoaAttenuationComplete;
	/** An attenuation command has completed V2. */
	protected static OID polatisVoaAttenuationCompleteV2;
	/** A list of ports affected by the event. */
	protected static OID polatisVoaPortList;
	/** Text label for the port affected by the event. */
	protected static OID polatisVoaPortLabel;
	// Polatis APS Port Objects
	/** The current state of the port. */
	protected static OID polatisApsPortCurrentState;
	/** The desired state of the port. */
	protected static OID polatisApsPortDesiredState;
	/** The current inhibitions imposed on the port control APS */
	protected static OID polatisApsPortCurrentCond;
	/** The desired inhibitions imposed on the port to control APS. */
	protected static OID polatisApsPortDesiredCond;
	/** Protecting port index. */
	protected static OID polatisApsProtGroupPort;
	/** The priority is assigned automatically, in the order in which ports. */
	protected static OID polatisApsProtGroupPriority;
	/** The status of this row in the table. */
	protected static OID polatisApsProtGroupStatus;
	/** The list of APS trigger ports in the switch. */
	protected static OID polatisApsTriggerTable;
	// Polatis APS Events
	/** A protection switch has occurred. */
	protected static OID polatisApsProtectionSwitch;
	/** A protection switch has occurred V2 */
	protected static OID polatisApsProtectionSwitchV2;
	/** The status of a protection switch alarm has been updated. */
	protected static OID polatisApsProtectionSwitchUpdated;
	/** Objects associated with APS events. */
	protected static OID polatisApsEventGroup;
	/** Objects associated with APS events V2. */
	protected static OID polatisApsEventGroupV2;
	// Polatis APS Event Objects
	/**
	 * The type protection switch event (1 - protection, 2 - reversion, 3 -
	 * other).
	 */
	protected static OID polatisApsProtSwitchType;
	/** The port connected to the working/protection port. */
	protected static OID polatisApsConnectedPort;
	/** The working port. */
	protected static OID polatisApsWorkingPort;
	/** The protection port. */
	protected static OID polatisApsProtectionPort;
	/** The label for the port connect to the working/protecting port. */
	protected static OID polatisApsConnectedPortLabel;
	/** The label for the working port. */
	protected static OID polatisApsWorkingPortLabel;
	/** The label for the protecting port */
	protected static OID polatisApsProtectingPortLabel;
	// Polatis Events Control Objects
	/** A list of events to be generated. */
	protected static OID polatisEventTable;
	/**
	 * A set of parameters that describe an event to be generated when certain
	 * conditions are met.
	 */
	protected static OID polatisEventEntry;
	/** An index that uniquely identifies an entry in the event table. */
	protected static OID polatisEventIndex;
	/** A comment describing this event entry. */
	protected static OID polatisEventDescription;
	/** The type of notification that the probe will make about this event. */
	protected static OID polatisEventType;
	/**
	 * If an SNMP trap is to be sent, it will be sent to the SNMP community
	 * specified by this octet string
	 */
	protected static OID polatisEventCommunity;
	/**
	 * The value of sysUpTime at the time this event entry last generated an
	 * event.
	 */
	protected static OID polatisEventLastTimeSent;

	// Polatis Event Log Objects
	/** A list of events that have been logged. */
	protected static OID polatisLogTable;
	/** A set of data describing an event that has been logged. */
	protected static OID polatisLogEntry;
	/** The event entry that generated this log entry. */
	protected static OID polatisLogEventIndex;
	/**
	 * An index that uniquely identifies an entry in the log table amongst those
	 * generated by the same eventEntries.
	 */
	protected static OID polatisLogIndex;
	/** The value of sysUpTime when this log entry was created. */
	protected static OID polatisLogTime;
	/**
	 * An implementation dependent description of the event that activated this
	 * log entry.
	 */
	protected static OID polatisLogDescription;

	// Polatis Event Trap Objects
	/** A message providing further information about an event. */
	protected static OID polatisEventMsg;
	/** The ID of an alarm. */
	protected static OID polatisAlarmId;
	/** The status of an alarm. */
	protected static OID polatisAlarmStatus;
	/** The user who updated the status of the alarm. */
	protected static OID polatisAlarmUser;
	/** The Sub-Switch for which the event was generated. */
	protected static OID polatisEventSubSwitch;

	// Polatis Event Groups
	/** Objects to control event handling. */
	protected static OID polatisCtrlTableGroup;
	/** Objects to report logs. */
	protected static OID polatisEventLogGroup;
	/** Objects used in notifications by other MIBs. */
	protected static OID polatisEventTrapGroup;

	// Polatis Event Compliance
	/** All controllers should provide this level of support V1. */
	protected static OID polatisEventComplianceV1;
	/** All controllers should provide this level of support V2. */
	protected static OID polatisEventComplianceV2;
	/** All controllers should provide this level of support V3. */
	protected static OID polatisEventComplianceV3;
	protected static OID teste;

	// Power Monitors-Configuration
	public enum AveragingTime {
		Ten(10), Twenty(20), Fifty(50), Hundred(100), TwoHundred(200), FiveHundred(500), Thousand(1000), TwoThousand(2000);
		int time;

		private AveragingTime(int avg) {
			this.time = avg;
		}

		@Override
		public String toString() {
			return new Integer(time).toString();
		}

		public int getTime() {
			return this.time;
		}
	};

	// Power Monitors-Alarm
	public enum AlarmTriggerType {
		/** Alarm is triggered when the power crosses the lower power threshold. */
		LOW,
		/** Alarm is triggered when the power crosses the upper power threshold. */
		HIGH,
		/**
		 * Alarm is triggered when the power crosses either the upper or the
		 * lower power threshold.
		 */
		BOTH
	};

	// Power Monitors-Alarm
	public enum AlarmMonitorPower {
		/** Alarm is disabled when object in the commands. */
		OFF,
		/**
		 * Alarms run in single-shot mode. Once the alarm has triggered it
		 * switches itself off.
		 */
		SINGLE,
		/** The alarm remains armed even after it has triggered */
		CONTINUOUS;
	}

	// Power Monitors-Alarm
	public enum AlarmOPMPower {
		/**
		 * An alarm is triggered when the power crosses the lower power
		 * threshold.
		 */
		Low_Alarm,
		/**
		 * An alarm is triggered when the power crosses the upper power
		 * threshold.
		 */
		High_Alarm,
		/** When the power is within the limits established */
		OK_Alarm;
	}

	// Attenuation-Optical Attenuation
	public enum VOAMode {
		/** Disables attenuation for the port. */
		NONE,
		/**
		 * Configures the attenuation of the ports to maintain a desired
		 * absolute power level.
		 */
		ABSOLUTE,
		/** Disables closed-loop updating of the attenuation for the port. */
		MAXIMUM,
		/** Provide maximum attenuation on the port. */
		FIXED,
		/**
		 * A value in the polatisVoaLevel and/or polatisVoaRefport column has
		 * been changed.
		 */
		PENDING;
	}

	// Protection-APS Configuration
	public enum PortDesiredCondition {
		/** Protection switching is enabled. */
		NONE,
		/** Inhibit the change to port protection. */
		ISINHSWPR,
		/** Inhibit the change to port work. */
		INHSWWKG;
	}

	// Connections-Cross-Connects
	public enum ConnectPort {
		/** Port is unplugged. */
		UNPLUGGED,
		/** Connected with the port 1. */
		PORT_1,
		/** Connected with the port 2 */
		PORT_2,
		/** Connected with the port 3. */
		PORT_3,
		/** Connected with the port 4. */
		PORT_4,
		/** Connected with the port 5. */
		PORT_5,
		/** Connected with the port 6. */
		PORT_6,
		/** Connected with the port 7. */
		PORT_7,
		/** Connected with the port 8. */
		PORT_8,
		/** Connected with the port 9. */
		PORT_9,
		/** Connected with the port 10. */
		PORT_10,
		/** Connected with the port 11. */
		PORT_11,
		/** Connected with the port 12. */
		PORT_12,
		/** Connected with the port 13. */
		PORT_13,
		/** Connected with the port 14. */
		PORT_14,
		/** Connected with the port 15. */
		PORT_15,
		/** Connected with the port 16. */
		PORT_16;
	}

	// Monitor In Port Alarm OXC
	public enum MonitorAlarmInPort {
		/** When a port is enabled return 1. */
		ENABLED,
		/** When a port is disabled return 2. */
		DISABLED,
		/** When a port is failed return 3. */
		FAILED;
	}

	// Port Desired State
	public enum PortDesiredState {
		/** When a port is enabled return 1. */
		IS,
		/** When a port is disabled return 2. */
		OOS;
	}

	public PolatisOXC(String ip) {
		// Initialize the client
		client = new SNMPClient("udp:" + ip + "/161");
		// Initialize oids
		init();
	}

	/** Initialize all OIDs related to the Polatis OXC. */
	protected void init() {
		// Initialize the OIDs
		upTime = new OID(".1.3.6.1.2.1.1.3.0");
		sysDescr = new OID(".1.3.6.1.2.1.1.1.0");
		snmpTrapOID_0 = new OID("1.3.6.1.6.3.1.1.4.1.0");
		coldStart = new OID("1.3.6.1.6.3.1.1.5.1");
		autom_Recalc_Net = new OID(".1.3.6.1.4.1.26592.2.1.2.3.1.1.1.6");
		// Polatis System Information Objects
		polatisSysInfoProductCode = new OID(".1.3.6.1.4.1.26592.2.1.2.2.1.0");
		polatisSysInfoSerialNumber = new OID(".1.3.6.1.4.1.26592.2.1.2.2.2.0");
		polatisSysInfoFirmwareVersion = new OID(".1.3.6.1.4.1.26592.2.1.2.2.3.0");
		// Polatis System Interface Configuration Objects

		polatisInterfaceConfigProtocol = new OID(".1.3.6.1.4.1.26592.2.1.2.3.2.1.1.2");
		polatisInterfaceConfigDevice = new OID(".1.3.6.1.4.1.26592.2.1.2.3.2.1.1.3");
		polatisInterfaceConfigStatus = new OID(".1.3.6.1.4.1.26592.2.1.2.3.2.1.1.4");
		// Polatis System Events
		polatisSysFanWarning = new OID(".1.3.6.1.4.1.26592.2.1.3.0.1");
		polatisSysFanFail = new OID(".1.3.6.1.4.1.26592.2.1.3.0.2");
		polatisSysFpgaError = new OID(".1.3.6.1.4.1.26592.2.1.3.0.3");
		polatisSysConfigError = new OID(".1.3.6.1.4.1.26592.2.1.3.0.4");
		polatisSysGeneralError = new OID(".1.3.6.1.4.1.26592.2.1.3.0.5");
		polatisSysPsuError = new OID(".1.3.6.1.4.1.26592.2.1.3.0.6");
		polatisSysFanWarningV2 = new OID(".1.3.6.1.4.1.26592.2.1.3.0.7");
		polatisSysFanWarningUpdated = new OID(".1.3.6.1.4.1.26592.2.1.3.0.8");
		polatisSysFanFailV2 = new OID(".1.3.6.1.4.1.26592.2.1.3.0.9");
		polatisSysFanFailUpdated = new OID(".1.3.6.1.4.1.26592.2.1.3.0.10");
		polatisSysFpgaErrorV2 = new OID(".1.3.6.1.4.1.26592.2.1.3.0.11");
		polatisSysFpgaErrorUpdated = new OID(".1.3.6.1.4.1.26592.2.1.3.0.12");
		polatisSysConfigErrorV2 = new OID(".1.3.6.1.4.1.26592.2.1.3.0.13");
		polatisSysConfigErrorUpdated = new OID(".1.3.6.1.4.1.26592.2.1.3.0.14");
		polatisSysGeneralErrorV2 = new OID(".1.3.6.1.4.1.26592.2.1.3.0.15");
		polatisSysGeneralErrorUpdated = new OID(".1.3.6.1.4.1.26592.2.1.3.0.16");
		polatisSysPsuErrorV2 = new OID(".1.3.6.1.4.1.26592.2.1.3.0.17");
		polatisSysPsuErrorUpdated = new OID(".1.3.6.1.4.1.26592.2.1.3.0.18");
		polatisSysMissingEventError = new OID(".1.3.6.1.4.1.26592.2.1.3.0.19");
		polatisSysMissingEventErrorUpdated = new OID(".1.3.6.1.4.1.26592.2.1.3.0.20");
		polatisSysEventMsg = new OID(".1.3.6.1.4.1.26592.2.1.3.1.1");
		// polatis OXC Groups
		polatisOxcPortsGroups = new OID(".1.3.6.1.4.1.26592.2.2.1.1.1");
		polatisOxcNotificationsGroup = new OID(".1.3.6.1.4.1.26592.2.2.1.1.2");
		polatisOxcEventGroup = new OID(".1.3.6.1.4.1.26592.2.2.1.1.3");
		polatisOxcEventGroupV2 = new OID(".1.3.6.1.4.1.26592.2.2.1.1.4");
		polatisOxcNotificationsGroupV2 = new OID(".1.3.6.1.4.1.26592.2.2.1.1.5");
		polatisOxcPortsComplianceV1 = new OID(".1.3.6.1.4.1.26592.2.2.1.2.1");
		polatisOxcPortsComplianceV2 = new OID(".1.3.6.1.4.1.26592.2.2.1.2.2");
		polatisOxcPortsComplianceEvtV1 = new OID(".1.3.6.1.4.1.26592.2.2.1.2.3");
		polatisOxcPortsComplianceEvtV2 = new OID(".1.3.6.1.4.1.26592.2.2.1.2.4");
		// Polatis OXC port objects
		polatisOxcSize = new OID(".1.3.6.1.4.1.26592.2.2.2.1.1.0");
		polatisOxcPortPatch = new OID(".1.3.6.1.4.1.26592.2.2.2.1.2.1.2");
		polatisOxcPortCurrentState = new OID(".1.3.6.1.4.1.26592.2.2.2.1.2.1.3");
		polatisOxcPortDesiredState = new OID(".1.3.6.1.4.1.26592.2.2.2.1.2.1.4");
		polatisOxcForceUpdates = new OID(".1.3.6.1.4.1.26592.2.2.2.1.3");
		polatisOxcSwitchComplete = new OID(".1.3.6.1.4.1.26592.2.2.3.0.1");
		polatisOxcPortEnable = new OID(".1.3.6.1.4.1.26592.2.2.3.0.2");
		polatisOxcTempRange = new OID(".1.3.6.1.4.1.26592.2.2.3.0.3");
		polatisOxcError = new OID(".1.3.6.1.4.1.26592.2.2.3.0.4");
		polatisOxcPortError = new OID(".1.3.6.1.4.1.26592.2.2.3.0.5");
		polatisOxcCompensationResumed = new OID(".1.3.6.1.4.1.26592.2.2.3.0.6");
		polatisOxcSwitchCompleteV2 = new OID(".1.3.6.1.4.1.26592.2.2.3.0.7");
		polatisOxcPortEnableV2 = new OID(".1.3.6.1.4.1.26592.2.2.3.0.8");
		polatisOxcTempRangeV2 = new OID(".1.3.6.1.4.1.26592.2.2.3.0.9");
		polatisOxcTempRangeUpdated = new OID(".1.3.6.1.4.1.26592.2.2.3.0.10");
		polatisOxcErrorV2 = new OID(".1.3.6.1.4.1.26592.2.2.3.0.11");
		polatisOxcErrorUpdated = new OID(".1.3.6.1.4.1.26592.2.2.3.0.12");
		polatisOxcPortErrorV2 = new OID(".1.3.6.1.4.1.26592.2.2.3.0.13");
		polatisOxcPortErrorUpdated = new OID(".1.3.6.1.4.1.26592.2.2.3.0.14");
		// Polatis OXC Event Objects
		polatisOxcEventMsg = new OID(".1.3.6.1.4.1.26592.2.2.3.1.1");
		polatisOxcEventsPortState = new OID(".1.3.6.1.4.1.26592.2.2.3.1.2");
		polatisOxcIngressPortList = new OID(".1.3.6.1.4.1.26592.2.2.3.1.3");
		polatisOxcIngressPortLabel = new OID(".1.3.6.1.4.1.26592.2.2.3.1.4");
		polatisOxcEgressPortList = new OID(".1.3.6.1.4.1.26592.2.2.3.1.5");
		polatisOxcEgressPortLabel = new OID(".1.3.6.1.4.1.26592.2.2.3.1.6");
		polatisOxcPortList = new OID(".1.3.6.1.4.1.26592.2.2.3.1.7");
		polatisOxcPortLabel = new OID(".1.3.6.1.4.1.26592.2.2.3.1.8");
		// Polatis OPM Groups
		polatisOpmConfigGroup = new OID(".1.3.6.1.4.1.26592.2.3.1.1.1");
		polatisOpmMeasureGroup = new OID(".1.3.6.1.4.1.26592.2.3.1.1.2");
		polatisOpmNotificationsGroup = new OID(".1.3.6.1.4.1.26592.2.3.1.1.3");
		polatisOpmEventGroup = new OID(".1.3.6.1.4.1.26592.2.3.1.1.4");
		polatisOpmNotificationsGroupV2 = new OID(".1.3.6.1.4.1.26592.2.3.1.1.5");
		polatisOpmEventGroupV2 = new OID(".1.3.6.1.4.1.26592.2.3.1.1.6");
		polatisOpmNotificationsGroupV3 = new OID(".1.3.6.1.4.1.26592.2.3.1.1.7");
		// Potalis OPM configuration objects
		polatisOpmWavelength = new OID(".1.3.6.1.4.1.26592.2.3.2.1.1.1.1");
		polatisOpmOffset = new OID(".1.3.6.1.4.1.26592.2.3.2.1.1.1.2");
		polatisOpmAtime = new OID(".1.3.6.1.4.1.26592.2.3.2.1.1.1.3");
		polatisOpmType = new OID(".1.3.6.1.4.1.26592.2.3.2.1.1.1.4");
		polatisOpmAlarmEdge = new OID(".1.3.6.1.4.1.26592.2.3.2.1.2.1.1");
		polatisOpmAlarmLowThresh = new OID(".1.3.6.1.4.1.26592.2.3.2.1.2.1.2");
		polatisOpmAlarmHighThresh = new OID(".1.3.6.1.4.1.26592.2.3.2.1.2.1.3");
		polatisOpmAlarmMode = new OID(".1.3.6.1.4.1.26592.2.3.2.1.2.1.4");
		polatisOpmPower = new OID(".1.3.6.1.4.1.26592.2.3.2.2.2.1.1");
		// Polatis OPM Events
		polatisOpmPowerMonitorAlarm = new OID(".1.3.6.1.4.1.26592.2.3.3.0.1");
		polatisOpmDegradedPowerMonitorAlarm = new OID(".1.3.6.1.4.1.26592.2.3.3.0.2");
		polatisOpmPowerMonitorAlarmV2 = new OID(".1.3.6.1.4.1.26592.2.3.3.0.3");
		polatisOpmPowerMonitorAlarmUpdate = new OID(".1.3.6.1.4.1.26592.2.3.3.0.4");
		polatisOpmDegrPowerMonitorAlarm = new OID(".1.3.6.1.4.1.26592.2.3.3.0.5");
		polatisOpmDegrPowerMonitorAlarmUpdate = new OID(".1.3.6.1.4.1.26592.2.3.3.0.6");
		// Polatis OPM Event Objects
		polatisOpmAlarmPort = new OID(".1.3.6.1.4.1.26592.2.3.3.1.1");
		polatisOpmAlarmPortLabel = new OID(".1.3.6.1.4.1.26592.2.3.3.1.2");
		// Polatis VOA Groups
		polatisVoaEventGroup = new OID(".1.3.6.1.4.1.26592.2.4.1.1.4");
		// Polatis VOA Port Objects
		polatisVoaLevel = new OID(".1.3.6.1.4.1.26592.2.4.2.1.1.1.1");
		polatisVoaRefport = new OID(".1.3.6.1.4.1.26592.2.4.2.1.1.1.2");
		polatisVoaCurrentState = new OID(".1.3.6.1.4.1.26592.2.4.2.1.1.1.3");
		polatisVoaDesiredState = new OID(".1.3.6.1.4.1.26592.2.4.2.1.1.1.4");
		// Polatis VOA Events
		polatisVoaAttenuationComplete = new OID(".1.3.6.1.4.1.26592.2.4.3.0.1");
		polatisVoaAttenuationCompleteV2 = new OID(".1.3.6.1.4.1.26592.2.4.3.0.2");
		polatisVoaPortList = new OID(".1.3.6.1.4.1.26592.2.4.3.1.1");
		polatisVoaPortLabel = new OID(".1.3.6.1.4.1.26592.2.4.3.1.2");
		// Polatis Aps Groups
		polatisApsEventGroup = new OID(".1.3.6.1.4.1.26592.2.5.1.1.4");
		polatisApsEventGroupV2 = new OID(".1.3.6.1.4.1.26592.2.5.1.1.5");
		// Polatis Aps Port Objects
		polatisApsPortCurrentState = new OID(".1.3.6.1.4.1.26592.2.5.2.1.1.1.1");
		polatisApsPortDesiredState = new OID(".1.3.6.1.4.1.26592.2.5.2.1.1.1.2");
		polatisApsPortCurrentCond = new OID(".1.3.6.1.4.1.26592.2.5.2.1.1.1.3");
		polatisApsPortDesiredCond = new OID(".1.3.6.1.4.1.26592.2.5.2.1.1.1.4");
		polatisApsProtGroupPort = new OID(".1.3.6.1.4.1.26592.2.5.2.1.2.1.1");
		polatisApsProtGroupPriority = new OID(".1.3.6.1.4.1.26592.2.5.2.1.2.1.2");
		polatisApsProtGroupStatus = new OID(".1.3.6.1.4.1.26592.2.5.2.1.2.1.3");
		polatisApsTriggerTable = new OID(".1.3.6.1.4.1.26592.2.5.2.1.3.1.2");
		// Polatis APS Events
		polatisApsProtectionSwitch = new OID(".1.3.6.1.4.1.26592.2.5.3.0.1");
		polatisApsProtectionSwitchV2 = new OID(".1.3.6.1.4.1.26592.2.5.3.0.2");
		polatisApsProtectionSwitchUpdated = new OID(".1.3.6.1.4.1.26592.2.5.3.0.3");
		polatisApsProtSwitchType = new OID(".1.3.6.1.4.1.26592.2.5.3.1.1");
		polatisApsConnectedPort = new OID(".1.3.6.1.4.1.26592.2.5.3.1.2");
		polatisApsWorkingPort = new OID(".1.3.6.1.4.1.26592.2.5.3.1.3");
		polatisApsProtectionPort = new OID(".1.3.6.1.4.1.26592.2.5.3.1.4");
		polatisApsConnectedPortLabel = new OID(".1.3.6.1.4.1.26592.2.5.3.1.5");
		polatisApsWorkingPortLabel = new OID(".1.3.6.1.4.1.26592.2.5.3.1.6");
		polatisApsProtectingPortLabel = new OID(".1.3.6.1.4.1.26592.2.5.3.1.7");
		// Polatis Event Groups
		polatisCtrlTableGroup = new OID(".1.3.6.1.4.1.26592.2.6.1.1.1");
		polatisEventLogGroup = new OID(".1.3.6.1.4.1.26592.2.6.1.1.2");
		polatisEventTrapGroup = new OID(".1.3.6.1.4.1.26592.2.6.1.1.3");
		// Polatis Event Compliance
		polatisEventComplianceV1 = new OID(".1.3.6.1.4.1.26592.2.6.1.2.1");
		polatisEventComplianceV2 = new OID(".1.3.6.1.4.1.26592.2.6.1.2.2");
		polatisEventComplianceV3 = new OID(".1.3.6.1.4.1.26592.2.6.1.2.3");
		// Polatis Event Control Objects
		polatisEventTable = new OID(".1.3.6.1.4.1.26592.2.6.2.1.1");
		polatisEventEntry = new OID(".1.3.6.1.4.1.26592.2.6.2.1.1.1");
		polatisEventIndex = new OID(".1.3.6.1.4.1.26592.2.6.2.1.1.1.1");
		polatisEventDescription = new OID(".1.3.6.1.4.1.26592.2.6.2.1.1.1.2");
		polatisEventType = new OID(".1.3.6.1.4.1.26592.2.6.2.1.1.1.3");
		polatisEventCommunity = new OID(".1.3.6.1.4.1.26592.2.6.2.1.1.1.4");
		polatisEventLastTimeSent = new OID(".1.3.6.1.4.1.26592.2.6.2.1.1.1.5");
		// Polatis Event Log Objects
		polatisLogTable = new OID(".1.3.6.1.4.1.26592.2.6.2.2.1");
		polatisLogEntry = new OID(".1.3.6.1.4.1.26592.2.6.2.2.1.1");
		polatisLogEventIndex = new OID(".1.3.6.1.4.1.26592.2.6.2.2.1.1.1");
		polatisLogIndex = new OID(".1.3.6.1.4.1.26592.2.6.2.2.1.1.2");
		polatisLogTime = new OID(".1.3.6.1.4.1.26592.2.6.2.2.1.1.3");
		polatisLogDescription = new OID(".1.3.6.1.4.1.26592.2.6.2.2.1.1.4");
		// Polatis Event Trap Objects
		polatisEventMsg = new OID(".1.3.6.1.4.1.26592.2.6.2.3.1");
		polatisAlarmId = new OID(".1.3.6.1.4.1.26592.2.6.2.3.2");
		polatisAlarmStatus = new OID(".1.3.6.1.4.1.26592.2.6.2.3.3");
		polatisAlarmUser = new OID(".1.3.6.1.4.1.26592.2.6.2.3.4");
		polatisEventSubSwitch = new OID(".1.3.6.1.4.1.26592.2.6.2.3.5");
		// Jut to OID Tests
		teste = new OID(".1.3.6.1.4.1.26592.2.5.2.1.2.1.1");

	}

	//OXC Information
	/** Returns the model of the OXC */
	public String getModel() {
		try {
			String value1 = client.getAsString(new OID(polatisSysInfoProductCode));
			return value1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Returns the serial number of the OXC */
	public String getSerial() {
		try {
			String value1 = client.getAsString(new OID(polatisSysInfoSerialNumber));
			return value1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** just for OIDs tests */
	public String testarleituras() {
		try {
			String value1 = client.getAsString(new OID(teste));
			return value1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Connections-Port Status
	
	/**
	 * Returns the size of the switch matrix, in the form NxM, where N is the
	 * number of ingress and M is the number of egress ports.
	 * 
	 * @return The size of the switch matrix.
	 */

	
	public String getOXCSize() {
		try {
			String value1 = client.getAsString(new OID(polatisOxcSize));
			return value1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

		/** Returns the protecting port of a specific working port */
	public String getProtectingPort(int port) {
		try {
			int i;
			for (i = 0; i <= getIgressPorts(); i++) {
				if (client.getAsString(new OID(polatisApsProtGroupPort).append(port + "." + i)).equals(String.valueOf(i))) {
					return String.valueOf(i);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/** Returns the number of igress ports from the OXC */
	public int getIgressPorts() {
		return Integer.valueOf(String.valueOf(getOXCSize()).split("x")[0]);

	}

	/** Returns the number of egress ports from the OXC */
	public int getEgressPorts() {
		return Integer.valueOf(String.valueOf(getOXCSize()).split("x")[1]);

	}

	/** Set the actived Igress Ports */
	public void setActivedIgressPorts() {
		int i = getIgressPorts();
		for (int y = 0; y < i; y++) {

			if (getStateOXCPort(i - y)) {
				activedIgressPorts.add(String.valueOf(i - y));
			}

		}

	}

	/** Set the actived Egress Ports */
	public void setActivedEgressPorts() {
		int i = getEgressPorts() + getIgressPorts();
		for (int y = 0; y < getEgressPorts(); y++) {

			if (getStateOXCPort(i - y)) {
				activedEgressPorts.add(String.valueOf(i - y));

			}

		}

	}

	/** Returns a vector with the actived Igress Ports */
	public Vector<String> getActivedIgressPorts() {
		return activedIgressPorts;
	}

	/** Returns a vector with the actived Egress Ports */
	public Vector<String> getActivedEgressPorts() {
		return activedEgressPorts;
	}

	/**
	 * Returns the current state of the OXC Port.
	 * 
	 * @param port
	 *            The port number (1-16).
	 * @return True, if enabled. False, if it is disabled.
	 */
	public boolean getStateOXCPort(int port) {
		try {
			String value = client.getAsString((new OID(polatisOxcPortCurrentState)).append(port));
			// System.out.println("State of OXC Input port #"+port+": "+value);
			if (value.equals("1"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Connections-Port Status
	/**
	 * Sets the state of the OXC Input Port.
	 * 
	 * @param port
	 *            The port number (1-16).
	 * @param enable
	 *            True, to enable the port. False, otherwise.
	 * @return True, if enable. False, if it is disable.
	 */
	public String setStateOXCInputPort(int port, boolean enable) {
		try {
			int value;
			if (enable)
				value = 1;
			else
				value = 2; // disable
			String resp = client.setAsString((new OID(polatisOxcPortDesiredState)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Configure - Automatic address
	/**
	 * Returns true, when set to true (its default value) the switch
	 * automatically recalculates the broadcast address and subnet. Returns
	 * false, when the switch does not attempt to recalculate these addresses.
	 * 
	 * @param OID
	 *            .1.3.6.1.4.1.26592.2.1.2.3.1.1.1.6
	 * @return True, if enable. False, if it is disable.
	 */
	public boolean getAutomRecalcNet(int port) {
		try {
			String value = client.getAsString((new OID(autom_Recalc_Net)).append(port));

			if (value.equals("1"))
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String setAutomRecalcNet(int port, boolean status) {
		try {
			int value;
			if (status)
				value = 1;
			else
				value = 2; // false
			String resp = client.setAsString((new OID(autom_Recalc_Net)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns true, when set to true (its default value) the switch
	 * automatically recalculates the broadcast address and subnet. Returns
	 * false, when the switch does not attempt to recalculate these addresses.
	 * 
	 * @param OID
	 *            .1.3.6.1.4.1.26592.2.1.2.3.1.1.1.6
	 * @return True, if enable. False, if it is disable.
	 */
	public boolean getInterConfigStatus(int port) {
		try {
			String value = client.getAsString((new OID(polatisInterfaceConfigStatus)).append(port));

			if (value.equals("1"))
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String setInterConfigStatus(int port, boolean status) {
		try {
			int value;
			if (status)
				value = 1;// enable
			else
				value = 2; // disable
			String resp = client.setAsString((new OID(polatisInterfaceConfigStatus)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Configuration
	/**
	 * Returns the parameter averaging time.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return The parameter average time in ms (10,20,50,100,200,500,1000 or
	 *         2000).if a problem has occurred return 00.
	 */
	public AveragingTime getAveragingTime(int port) {
		try {
			String value = client.getAsString((new OID(polatisOpmAtime)).append(port));
			switch (Integer.parseInt(value)) {
			case 1:
				return AveragingTime.Ten;
			case 2:
				return AveragingTime.Twenty;
			case 3:
				return AveragingTime.Fifty;
			case 4:
				return AveragingTime.Hundred;
			case 5:
				return AveragingTime.TwoHundred;
			case 6:
				return AveragingTime.FiveHundred;
			case 7:
				return AveragingTime.Thousand;
			case 8:
				return AveragingTime.TwoThousand;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Configuration
	public String setAveragingTime(int port, AveragingTime time) {
		try {
			int value = 0;
			switch (time) {
			case Ten:
				value = 1;
				break; // Don't forget to put a break at the end
			case Twenty:
				value = 2;
				break;
			case Fifty:
				value = 3;
				break;
			case Hundred:
				value = 4;
				break;
			case TwoHundred:
				value = 5;
				break;
			case FiveHundred:
				value = 6;
				break;
			case Thousand:
				value = 7;
				break;
			case TwoThousand:
				value = 8;
				break;
			}
			String resp = client.setAsString((new OID(polatisOpmAtime)).append(port), new UnsignedInteger32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getpolatisOxcIngressPortLabel() {
		return String.valueOf(polatisOxcIngressPortLabel.get(2));
	}

	// Connections-Cross-Connects
	/**
	 * Returns connection map.
	 * 
	 * @param port
	 *            The port number (1-16).
	 * @return If port 1 is connected to port 9; return PORT_9.
	 * @return If not connected to any port; return UNPLUGGER.
	 */
	public ConnectPort getConnectPort(int port) {
		try {
			String value = client.getAsString((new OID(polatisOxcPortPatch)).append(port));
			switch (Integer.parseInt(value)) {
			case 0:
				return ConnectPort.UNPLUGGED;
			case 1:
				return ConnectPort.PORT_1;
			case 2:
				return ConnectPort.PORT_2;
			case 3:
				return ConnectPort.PORT_3;
			case 4:
				return ConnectPort.PORT_4;
			case 5:
				return ConnectPort.PORT_5;
			case 6:
				return ConnectPort.PORT_6;
			case 7:
				return ConnectPort.PORT_7;
			case 8:
				return ConnectPort.PORT_8;
			case 9:
				return ConnectPort.PORT_9;
			case 10:
				return ConnectPort.PORT_10;
			case 11:
				return ConnectPort.PORT_11;
			case 12:
				return ConnectPort.PORT_12;
			case 13:
				return ConnectPort.PORT_13;
			case 14:
				return ConnectPort.PORT_14;
			case 15:
				return ConnectPort.PORT_15;
			case 16:
				return ConnectPort.PORT_16;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Connections-Cross-Connects
	/**
	 * Creating in OXC connection.
	 * 
	 * @param port
	 *            The port number (1-16).
	 * @return e.g If port 1 is connected to port 9; return PORT_9.
	 * @return If not connected to any port; return UNPLUGGED.
	 */
	public String setConnectPort(int port, ConnectPort connect) {
		try {
			int value = 0;
			switch (connect) {
			case PORT_1:
				value = 1;
				break; // Don't forget to put a break at the end
			case PORT_2:
				value = 2;
				break;
			case PORT_3:
				value = 3;
				break;
			case PORT_4:
				value = 4;
				break;
			case PORT_5:
				value = 5;
				break;
			case PORT_6:
				value = 6;
				break;
			case PORT_7:
				value = 7;
				break;
			case PORT_8:
				value = 8;
				break;
			case PORT_9:
				value = 9;
				break;
			case PORT_10:
				value = 10;
				break;
			case PORT_11:
				value = 11;
				break;
			case PORT_12:
				value = 12;
				break;
			case PORT_13:
				value = 13;
				break;
			case PORT_14:
				value = 14;
				break;
			case PORT_15:
				value = 15;
				break;
			case PORT_16:
				value = 16;
				break;
			case UNPLUGGED:
			}
			String resp = client.setAsString((new OID(polatisOxcPortPatch)).append(port), new Gauge32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Power Levels
	/**
	 * Returns the optical power measurement.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return Power (in dBm).
	 */
	public double getMeasurePower(int port) {
		try {
			String value = client.getAsString((new OID(polatisOpmPower)).append(port));
			return Double.parseDouble(value) / 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.NEGATIVE_INFINITY;
	}

	// Attenuation-Optical Attenuation
	/**
	 * Returns the desired attenuation level in (dBm).
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return Power (in dBm).
	 */
	public double getAttenuationLevel(int port) {
		try {
			String value = client.getAsString((new OID(polatisVoaLevel)).append(port));
			return Double.parseDouble(value) / 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.NEGATIVE_INFINITY;
	}

	public String getOPMType(int port) {
		try {
			String value = client.getAsString((new OID(polatisOpmType)).append(port));
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	// Attenuation-Optical Attenuation
	/**
	 * Returns the desired attenuation level in (dBm).
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return Power (in dBm).
	 */
	public String setAttenuationLevel(int port, double att) {
		try {
			int value = (int) Math.round(att * 1000.0);
			String resp = client.setAsString((new OID(polatisVoaLevel)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Alarm
	/**
	 * Returns the alarm threshold - High.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return Power (in dBm).
	 */
	public double getAlarmThresholdHigh(int port) {
		try {
			String value = client.getAsString((new OID(polatisOpmAlarmHighThresh)).append(port));
			return Double.parseDouble(value) / 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.NEGATIVE_INFINITY;
	}

	// Power Monitors-Alarm
	public String setAlarmThresholdHigh(int port, double ath) {
		try {
			int value = (int) Math.round(ath * 1000.0);
			String resp = client.setAsString((new OID(polatisOpmAlarmHighThresh)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Alarm
	/**
	 * Returns the alarm threshold - low.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return Power (in dBm).
	 */
	public double getAlarmThresholdLow(int port) {
		try {
			String value = client.getAsString((new OID(polatisOpmAlarmLowThresh)).append(port));
			return Double.parseDouble(value) / 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.NEGATIVE_INFINITY;
	}

	// Power Monitors-Alarm
	public String setAlarmThresholdLow(int port, double atl) {
		try {
			int value = (int) Math.round(atl * 1000.0);
			String resp = client.setAsString((new OID(polatisOpmAlarmLowThresh)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Alarm
	/**
	 * Returns the alarm edge option.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return The mode (Low, High or Both). Null, if a problem has occurred.
	 */
	public AlarmTriggerType getAlarmEdgeOption(int port) {
		try {
			String value = client.getAsString((new OID(polatisOpmAlarmEdge)).append(port));
			switch (Integer.parseInt(value)) {
			case 1:
				return AlarmTriggerType.LOW;
			case 2:
				return AlarmTriggerType.HIGH;
			case 3:
				return AlarmTriggerType.BOTH;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Alarm
	public String setAlarmEdgeOption(int port, AlarmTriggerType mode) {
		try {
			int value = 0;
			switch (mode) {
			case LOW:
				value = 1;
				break; // Don't forget to put a break at the end
			case HIGH:
				value = 2;
				break;
			case BOTH:
				value = 3;
				break;
			}
			String resp = client.setAsString((new OID(polatisOpmAlarmEdge)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Alarm
	/**
	 * Returns the alarm Mode.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return The mode (Off, Single or Continuous). Null, if a problem has
	 *         occurred.
	 */
	public AlarmMonitorPower getAlarmModeOption(int port) {
		try {
			String value = client.getAsString((new OID(polatisOpmAlarmMode)).append(port));
			switch (Integer.parseInt(value)) {
			case 1:
				return AlarmMonitorPower.OFF;
			case 2:
				return AlarmMonitorPower.SINGLE;
			case 3:
				return AlarmMonitorPower.CONTINUOUS;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Alarm
	/**
	 * Se
	 */
	public String setAlarmModeOption(int port, AlarmMonitorPower mode) {
		try {
			int value = 0;
			switch (mode) {
			case OFF:
				value = 1;
				break; // Don't forget to put a break at the end
			case SINGLE:
				value = 2;
				break;
			case CONTINUOUS:
				value = 3;
				break;
			}
			String resp = client.setAsString((new OID(polatisOpmAlarmMode)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the alarm type at the specified port within the specified
	 * threshold.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @param high_threshold
	 *            The threshold for a High_Alarm (in dBm) - Saturation.
	 * @param soft_threshold
	 *            The threshold for a Low_Alarm (in dBm) - Low detection or LOL.
	 *            *
	 * @return The type (High_Alarm, Low_Alarm, OK_Alarm). "No Alarm", if a
	 *         problem has occurred.
	 */

	public String getAlarmOPMPower(int port, double high_threshold, double low_threshold) {
		String alarm;
		try {
			double value = Double.parseDouble(client.getAsString((new OID(polatisOpmPower)).append(port)));
			if (value >= high_threshold * 1000) {
				alarm = "High_Alarm";
			} else if (value <= low_threshold * 1000) {
				alarm = "Low_Alarm";
			} else {
				alarm = "OK Alarm";
			}
			return alarm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		alarm = "Not Alarm";
		return alarm;
	}

	/**
	 * Set the wavelength of the light, used for calibrating OPM power
	 * measurements.
	 * 
	 * @param port
	 *            The output port (9-16)
	 * @param wavelength
	 *            The wavelength, in nm;
	 * @return The string response.
	 */
	public String setOPMWavelength(int port, double wavelength) {
		try {
			int value = (int) Math.round(wavelength * 1000.0);
			String resp = client.setAsString((new OID(polatisOpmWavelength)).append(port), new UnsignedInteger32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Double getOPMWavelength(int port) {
		try {

			Double resp = Double.valueOf(client.getAsString((new OID(polatisOpmWavelength)).append(port))) / 1000;
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the VOA Mode at the specified node.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return The mode (None, Absolute, Maximum or Fixed). Null, if a problem
	 *         has occurred.
	 */
	public VOAMode getVOAMode(int port) {
		try {
			String value = client.getAsString((new OID(polatisVoaDesiredState)).append(port));
			if (value != null && !value.equals("noSuchInstance")) {
				switch (Integer.parseInt(value)) {
				case 1:
					return VOAMode.NONE;
				case 2:
					return VOAMode.ABSOLUTE;
				case 5:
					return VOAMode.MAXIMUM;
				case 6:
					return VOAMode.FIXED;
				case 7:
					return VOAMode.PENDING;
				}
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Set the VOA mode in the specified port.
	 * 
	 * @param port
	 *            The index of the port (9-16).
	 * @param mode
	 *            The mode, as in VOAMode.
	 * @return The reply from the switch.
	 */
	public String setVOAMode(int port, VOAMode mode) {
		try {
			int value = 0;
			switch (mode) {
			case NONE:
				value = 1;
				break; // Don't forget to put a break at the end
			case ABSOLUTE:
				value = 2;
				break;
			case MAXIMUM:
				value = 5;
				break;
			case FIXED:
				value = 6;
				break;
			case PENDING:
				value = 7;
				break;
			}
			String resp = client.setAsString((new OID(polatisVoaDesiredState)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Protection-APS Configuration
	/**
	 * Set Port Desired Condition.
	 * 
	 * @param port
	 *            The port number (1-16).
	 * @return NONE, no condition set (switch-to-working and
	 *         switch-to-protection are allowed for the port. ISINHSWPR, if the
	 *         switching to the protection is inhibited. INHSWWKG, if switching
	 *         to the working port is inhibited. protection switch is disabled.
	 */
	public PortDesiredCondition getPortDesiredCondition(int port) {
		try {
			String value = client.getAsString((new OID(polatisApsPortDesiredCond)).append(port));
			if (value != null && !value.equals("noSuchInstance")) {
				switch (Integer.parseInt(value)) {
				case 1:
					return PortDesiredCondition.NONE;
				case 2:
					return PortDesiredCondition.ISINHSWPR;
				case 3:
					return PortDesiredCondition.INHSWWKG;
				}
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Protection-APS Configuration
	/**
	 * 
	 */
	public String setPortDesiredCondition(int port, PortDesiredCondition condition) {
		try {
			int value = 0;
			switch (condition) {
			case NONE:
				value = 1;
				break; // Don't forget to put a break at the end
			case ISINHSWPR:
				value = 2;
				break;
			case INHSWWKG:
				value = 3;
				break;
			}
			String resp = client.setAsString((new OID(polatisApsPortDesiredCond)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Power Monitors-Configuration
	/**
	 * Returns Power level Offset (dB).
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return Power (in dBm).
	 * */
	public double getPowerLevelOffset(int port) {
		try {
			String value = client.getAsString((new OID(polatisOpmOffset)).append(port));
			return Double.parseDouble(value) / 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.NEGATIVE_INFINITY;
	}

	// Power Monitors-Configuration
	public String setPowerLevelOffset(int port, double plo) {
		try {
			int value = (int) Math.round(plo * 1000.0);
			String resp = client.setAsString((new OID(polatisOpmOffset)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Monitor Alarm In Port OXC
	/**
	 * Returns the alarm Mode.
	 * 
	 * @param port
	 *            The port number (9-16).
	 * @return The mode (Off, Single or Continuous). Null, if a problem has
	 *         occurred.
	 */
	public MonitorAlarmInPort getMonitorAlarmInPort(int port) {
		try {
			String value = client.getAsString((new OID(polatisOxcPortCurrentState)).append(port));
			switch (Integer.parseInt(value)) {
			case 1:
				return MonitorAlarmInPort.ENABLED;
			case 2:
				return MonitorAlarmInPort.DISABLED;
			case 3:
				return MonitorAlarmInPort.FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Port Desired State
	/**
	 * Returns the port desired state.
	 * 
	 * @param port
	 *            The port number (1-16).
	 * @return The desired state.
	 */
	public PortDesiredState getPortDesiredState(int port) {
		try {
			String value = client.getAsString((new OID(polatisApsPortDesiredState)).append(port));
			if (value != null && !value.equals("noSuchInstance")) {
				switch (Integer.parseInt(value)) {
				case 1:
					return PortDesiredState.IS;
				case 2:
					return PortDesiredState.OOS;
				}
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the port desired state.
	 * 
	 * @param port
	 *            The port number (1-16). Setting the desired state of a working
	 *            port to "IS" will force a reversion back to the working path,
	 *            unless "INHSWWKG" is set. The state can only be set for
	 *            working an protection ports.
	 * @param "IS" in service or "OOS" to port out service.
	 * @return True, if the port is in service. False, if the port out service.
	 */
	public String setPortDesiredState(int port, PortDesiredState desired) {
		try {
			int value = 0;
			switch (desired) {
			case IS:
				value = 1;
				break;
			case OOS:
				value = 2;
				break;
			}
			String resp = client.setAsString((new OID(polatisApsPortDesiredState)).append(port), new Integer32(value));
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Create a protection group.
	 * 
	 * @param working
	 *            The working port.
	 * @param protection
	 *            The protection port.
	 * @param trigger
	 *            The trigger port (9-16).
	 * @param create
	 *            True, to create and go. False, it will cause the destruction
	 *            of the group.
	 * @return "Success" if the operation was completed successfully.
	 */
	public String setProtectionGroup(int working, int protection, int trigger, boolean create) {
		try {
			int value;
			if (create)
				value = 4; // create & go
			else
				value = 6; // destroy
			OID aux1 = (new OID(polatisApsProtGroupStatus)).append(working);
			aux1.append(protection);
			String resp1 = client.setAsString(aux1, new Integer32(value));
			OID aux2 = (new OID(polatisApsTriggerTable)).append(working);
			aux2.append(trigger);
			String resp2 = client.setAsString(aux2, new Integer32(value));
			resp1.concat("\n");
			resp1.concat(resp2);
			return resp1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String resetProtection(int working, int protection) {
		String resp1 = setPortDesiredState(working, PortDesiredState.IS);
		String resp2 = setPortDesiredCondition(working, PortDesiredCondition.ISINHSWPR);
		String resp3 = setPortDesiredCondition(working, PortDesiredCondition.NONE);
		String resp4 = setPortDesiredCondition(protection, PortDesiredCondition.NONE);
		resp1.concat(" \n ");
		resp1.concat(resp2);
		resp1.concat(" \n ");
		resp1.concat(resp3);
		resp1.concat(" \n ");
		return resp1.concat(resp4);
	}

	public void startTrapListening(String address) {
		PolatisTrap agent = new PolatisTrap();
		SNMPTrapMonitor trap = new SNMPTrapMonitor(agent);
		try {
			trap.listen(new UdpAddress(address));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void main(String args[]) {
		// init();
		try {
			System.out.println();
			System.out.println("Starting connection at " + (new Date()).toString());
			// Enable ports 1, 2, 8, 9, 10 and 11. (Port Status)

			// System.out.println("Desired state: "+setPortDesiredState
			// (9,PortDesiredState.IS));
			setAlarmModeOption(10, AlarmMonitorPower.CONTINUOUS);
			setAlarmEdgeOption(10, AlarmTriggerType.LOW);
			setAlarmThresholdLow(10, -60.0);
			System.out.println(resetProtection(9, 11));
			resetProtection(1, 2);
			/*
			 * // Connect ingress 8 to egress 9 (TX_UFABC -> RX_UNICAMP) //
			 * (Cross-connects - Connection Map) setConnectPort(8,
			 * ConnectPort.PORT_9); // Connect ingress 1 to egress 10
			 * (TX_UNICAMP -> RX_UFABC) setConnectPort(1, ConnectPort.PORT_10);
			 * //Set VOA to disable setVOAMode(9,VOAMode.NONE);
			 * setVOAMode(10,VOAMode.NONE); setVOAMode(11,VOAMode.NONE);
			 * //Configure OPM //Set OPM Wavelength to C28 (1540.54 nm)
			 * setOPMWavelength(9, 1540.5); setOPMWavelength(10, 1540.5);
			 * setOPMWavelength(11, 1540.5); //Set OPM offset to zero
			 * setPowerLevelOffset(9,0.0); setPowerLevelOffset(10,0.0);
			 * setPowerLevelOffset(11,0.0); //Set OPM averaging time - 50 ms
			 * setAveragingTime(9,AveragingTime.Fifty);
			 * setAveragingTime(10,AveragingTime.Fifty);
			 * setAveragingTime(11,AveragingTime.Fifty); //Set OPM alarms - port
			 * 10 setAlarmModeOption(10,AlarmMonitorPower.CONTINUOUS);
			 * setAlarmEdgeOption(10,AlarmTriggerType.LOW);
			 * setAlarmThresholdLow(10,-40.0); // Set protection for working 1
			 * in port 2 - trigger at 10 (APS // Configuration - Protection
			 * groups) setProtectionGroup(1,2,10,true); // Set protection for
			 * working 9 in port 11 - trigger at 10
			 * setProtectionGroup(9,11,10,true); System.out.println(
			 * "Setting connection: source: 1, working: 9 and protection:10");
			 * // String statusPort = setConnectPort (1, ConnectPort.PORT_9); /*
			 * System.out.println("Connected to: "+ statusPort); //Set the
			 * protection String monitor = setMonitorAlarmInPort (1,
			 * ConnectPort.PORT_9, ConnectPort.PORT_10,
			 * MonitorAlarmInPort.ENABLED); System.out.println(); String desc =
			 * client.getAsString(sysDescr); String wave =
			 * client.getAsString(polatisOpmWavelength); String uptime =
			 * client.getAsString(upTime); String if_in = client.getAsString(new
			 * OID(".1.3.6.1.2.1.2.2.1.10.1")); String if_type =
			 * client.getAsString(new OID(".1.3.6.1.2.1.2.2.1.3.1"));
			 * 
			 * System.out.println("Description: "+desc);
			 * System.out.println("Wavelength: "+wave);
			 * System.out.println("UpTime: "+uptime);
			 * System.out.println("IfType: "+if_type);
			 * System.out.println("IfInOctets: "+if_in+" bytes.");
			 * System.out.println("Measure: "+getMeasurePower(9)+ " dBm");
			 * //System.out.println("Automatic: " +setAutomRecalcNet (2,
			 * false)); //System.out.println("Automatic: " +setAutomRecalcNet
			 * (1, true)); System.out.println("Automatic: " +getAutomRecalcNet
			 * (1));
			 * 
			 * 
			 * //Return the configuration alarm
			 * System.out.println("Alarm Threshold Low: "
			 * +setAlarmThresholdLow(9,-50.0));
			 * System.out.println("Alarm Threshold Low: "
			 * +getAlarmThresholdLow(9)+ " dBm");
			 * System.out.println("Alarm Threshold High: "
			 * +setAlarmThresholdHigh(9,15.0));
			 * System.out.println("Alarm Threshold High: "
			 * +getAlarmThresholdHigh(9)+ " dBm");
			 * 
			 * 
			 * System.out.println("Attenuation Level: "+setAttenuationLevel(10,8.0
			 * ));
			 * System.out.println("Attenuation Level: "+getAttenuationLevel(10)+
			 * "dBm");
			 * 
			 * //Power Monitors-Alarm System.out.println("Alarm Edge: "
			 * +setAlarmEdgeOption(9,AlarmTriggerType.LOW));
			 * //System.out.println("Alarm Edge: "
			 * +setAlarmEdgeOption(9,AlarmTriggerType.HIGH));
			 * //System.out.println("Alarm Edge: "
			 * +setAlarmEdgeOption(9,AlarmTriggerType.BOTH));
			 * System.out.println("Alarm Edge:"+getAlarmEdgeOption(9));
			 * //System.out.println("Alarm Mode: "
			 * +setAlarmModeOption(9,AlarmMonitorPower.OFF));
			 * //System.out.println("Alarm Mode: "
			 * +setAlarmModeOption(9,AlarmMonitorPower.SINGLE));
			 * System.out.println("Alarm Mode: "
			 * +setAlarmModeOption(9,AlarmMonitorPower.CONTINUOUS));
			 * System.out.println ("Alarm Mode: "+getAlarmModeOption(9));
			 * System.out.println ("Alarm OPM Power: "+
			 * getAlarmOPMPower(10,-30,-45));
			 * 
			 * //MonitorAlarmInPort receiver the state port 9 MonitorAlarmInPort
			 * statusPort9 = getMonitorAlarmInPort (9); System.out.println
			 * ("Alarm InPort 9: "+ statusPort9);
			 * //System.out.println("connecting the  port " + 2 + " a port " +
			 * 9); setMonitorAlarmInPort (2, ConnectPort.PORT_9,
			 * ConnectPort.PORT_10, statusPort9);//If the port 9 "disable" or
			 * "fail" connect port 10. When normality then return to work
			 * port"9". System.out.println ("Alarm InPort 9: "+
			 * getMonitorAlarmInPort(9));
			 * 
			 * //System.out.println("status: PASSEI AQUI "+
			 * setMonitorAlarmInPort(9,MonitorAlarmInPort.FAILED)); //End
			 * 
			 * //Attenuation-Optical Attenuation
			 * //System.out.println("VOA Mode: " +setVOAMode(13,VOAMode.NONE));
			 * //System.out.println("VOA Mode: "
			 * +setVOAMode(13,VOAMode.ABSOLUTE));
			 * System.out.println("VOA Mode: " +setVOAMode(14,VOAMode.MAXIMUM));
			 * //System.out.println("Status: "+setVOAMode(13,VOAMode.FIXED));
			 * System.out.println("VOA Mode: " + getVOAMode(14));
			 * System.out.println
			 * ("Status: "+setPortStatus(6,PortStatus.ISNONE));
			 * System.out.println
			 * ("Status: "+setPortStatus(9,PortStatus.ISINHSWPR));
			 * System.out.println("Port Status(6): " + getPortStatus(9));
			 * 
			 * //Power Monitors-Configuration
			 * System.out.println("Averaging Time: "
			 * +setAveragingTime(9,AveragingTime.FiveHundred));
			 * //System.out.println("Averaging Time: "
			 * +setAveragingTime(9,AveragingTime.Thousand)); System.out.println
			 * ("Averaging Time: "+getAveragingTime(9)+" ms");
			 * System.out.println("teste de conexão");
			 * 
			 * //System.out.println("Status: "+ setConnectPort (1,
			 * ConnectPort.PORT_9)); //System.out.println("Status: "+
			 * setConnectPort (5, ConnectPort.PORT_11));
			 * System.out.println("Status: "+ setConnectPort (8,
			 * ConnectPort.PORT_16));
			 * //System.out.println("Connect Port: "+getConnectPort(9));
			 * 
			 * // State of the OXC Port. True enabled and false disabled.
			 * System.out.println("Status: "+setStateOXCInputPort(9, false));
			 * System.out.println("Status: "+setStateOXCInputPort(2, true));
			 * System.out.println("Status: "+setStateOXCInputPort(3, false));
			 * System.out.println("Status OXC:"+getStateOXCInputPort(2));
			 * System.out.println("Offset: "+setPowerLevelOffset(9,15.0));
			 * System.out.println("Offset: "+getPowerLevelOffset(9)+ " dBm");
			 * System.out.println("Interface Status: "+getInterConfigStatus(1));
			 * System.out.println("Interface Status: "+setInterConfigStatus(1,
			 * false));
			 * 
			 * /* Traps.
			 */
			startTrapListening("172.17.36.238/162");

			while (true) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
