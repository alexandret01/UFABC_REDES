/*
 * Created on Aug 20, 2010.
 */
package br.ufabc.controlplane.ui;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Map;

import util.BrazilLocale;
import br.com.padtec.v3.data.impl.T100D_GTSintonizavel_Impl;
import br.com.padtec.v3.data.ne.ClientInterface;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.ODUk;
import br.com.padtec.v3.data.ne.OTNInterface;
import br.com.padtec.v3.data.ne.OpticalInterface;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.util.modelparser.TransponderModelParser;
import br.ufabc.controlplane.conf.Configuracao;
import br.ufabc.controlplane.metropad.Servidor;
import br.ufabc.dataplane.DataPlane;
import java.io.File;
import java.io.IOException;

import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.transport.TransportMappings;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.agent.mo.MOScalar;

public class SNMPAgent extends BaseAgent  {

	DataPlane dataPlane;
	private TrpOTNTerminal transponder;
	Configuracao conf;
	OpticalInterface ladoA = null;
	OpticalInterface ladoB = null;
	MOScalar is_lof_A, is_los_A, is_fail_A, is_laser_off_A, v_pin_A, v_pout_A, v_temp_A, v_temp_laser_A, v_lambda_real, v_channel_A;
	MOScalar is_lof_B, is_los_B, is_fail_B, is_laser_off_B, v_pin_B, v_pout_B, v_temp_B;
	MOScalar gain_pre, gain_booster;
	

	public SNMPAgent(DataPlane dataPlane, Configuracao conf, String address) {
		super(new File("conf.agent"), new File("bootCounter.agent"),
				new CommandProcessor(
						new OctetString(MPv3.createLocalEngineID())));
		this.address = address;		
		this.dataPlane = dataPlane;
		transponder = dataPlane.getTransponder();
		ladoA = transponder.getOpticalWDMInterface();
		ladoB = transponder.getOpticalClientInterface();
		this.conf = conf;
		this.criaTabela();
	}

	//static final OID sysDescr = new OID(".1.3.6.1.2.1.1.1.0");
	static final OID supervisores = new OID(".1.3.6.1.4.1.4846.1.0.0.0.0.0");
	static final OID transponders = new OID(".1.3.6.1.4.1.4846.2.0.0.0.0.0");
	static final OID amplificadores = new OID(".1.3.6.1.4.1.4846.3.0.0.0.0.0");

	//Supervisor 1
	static final OID supervisor_1 = new OID(".1.3.6.1.4.1.4846.1.0.1.0.0.0");
	static final OID nome_11 = new OID(".1.3.6.1.4.1.4846.1.0.1.0.1.0");
	static final OID local_11 = new OID(".1.3.6.1.4.1.4846.1.0.1.0.2.0");
	static final OID ip_11 = new OID(".1.3.6.1.4.1.4846.1.0.1.0.3.0");

	//Supervisor 2
	static final OID supervisor_2 = new OID(".1.3.6.1.4.1.4846.1.0.2.0.0.0");
	static final OID nome_12 = new OID(".1.3.6.1.4.1.4846.1.0.2.0.1.0");
	static final OID local_12 = new OID(".1.3.6.1.4.1.4846.1.0.2.0.2.0");
	static final OID ip_12 = new OID(".1.3.6.1.4.1.4846.1.0.2.0.3.0");

	//Transponder 1
	static final OID transponder_1 = new OID(".1.3.6.1.4.1.4846.2.0.1.0.0.0");
	static final OID nome_21 = new OID(".1.3.6.1.4.1.4846.2.0.1.0.1.0");
	static final OID local_21 = new OID(".1.3.6.1.4.1.4846.2.0.1.0.2.0");
	static final OID ip_21 = new OID(".1.3.6.1.4.1.4846.2.0.1.0.3.0");

	//static final OID lado_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.4.0");
	//static final OID otn = new OID(".1.3.6.1.4.1.4846.2.0.1.0.5.0");

	//Alarmes
	//WDM (1550 nm)
	static final OID lof_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.4.0");
	static final OID los_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.5.0");
	static final OID fail_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.6.0");
	static final OID laserOff_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.7.0");
	static final OID pIn_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.8.0");
	static final OID pOut_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.9.0");
	static final OID temp_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.10.0");
	static final OID tempLaser_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.11.0");
	static final OID lambdaReal_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.12.0");
	static final OID channel_A = new OID(".1.3.6.1.4.1.4846.2.0.1.0.13.0");
	//Client (1310 nm)
	static final OID lof_B = new OID(".1.3.6.1.4.1.4846.2.0.1.0.14.0");
	static final OID los_B = new OID(".1.3.6.1.4.1.4846.2.0.1.0.15.0");
	static final OID fail_B = new OID(".1.3.6.1.4.1.4846.2.0.1.0.16.0");
	static final OID laserOff_B = new OID(".1.3.6.1.4.1.4846.2.0.1.0.17.0");
	static final OID pIn_B = new OID(".1.3.6.1.4.1.4846.2.0.1.0.18.0");
	static final OID pOut_B = new OID(".1.3.6.1.4.1.4846.2.0.1.0.19.0");
	static final OID temp_B = new OID(".1.3.6.1.4.1.4846.2.0.1.0.20.0");


	//Transponder 2
	static final OID transponder_2 = new OID(".1.3.6.1.4.1.4846.2.0.2.0.0.0");
	static final OID nome_22 = new OID(".1.3.6.1.4.1.4846.2.0.2.0.1.0");
	static final OID local_22 = new OID(".1.3.6.1.4.1.4846.2.0.2.0.2.0");
	static final OID ip_22 = new OID(".1.3.6.1.4.1.4846.2.0.2.0.3.0");
	//static final OID lado_B = new OID(".1.3.6.1.4.1.4846.2.0.2.0.4.0");
	//static final OID c = new OID(".1.3.6.1.4.1.4846.2.0.2.0.5.0");


	//Amplificador 1
	static final OID amplificador_1 = new OID(".1.3.6.1.4.1.4846.3.0.1.0.0.0");
	static final OID nome_31 = new OID(".1.3.6.1.4.1.4846.3.0.1.0.1.0");
	static final OID local_31 = new OID(".1.3.6.1.4.1.4846.3.0.1.0.2.0");
	static final OID ip_31 = new OID(".1.3.6.1.4.1.4846.3.0.1.0.3.0");
	static final OID ganho_31 = new OID(".1.3.6.1.4.1.4846.3.0.1.0.4.0");

	//Amplificador 2
	static final OID amplificador_2 = new OID(".1.3.6.1.4.1.4846.3.0.2.0.0.0");
	static final OID nome_32 = new OID(".1.3.6.1.4.1.4846.3.0.2.0.1.0");
	static final OID local_32 = new OID(".1.3.6.1.4.1.4846.3.0.2.0.2.0");
	static final OID ip_32 = new OID(".1.3.6.1.4.1.4846.3.0.2.0.3.0");
	static final OID ganho_32 = new OID(".1.3.6.1.4.1.4846.3.0.2.0.4.0");


	// not needed but very useful of course
	static {
		LogFactory.setLogFactory(new Log4jLogFactory());
	}

	private String address;

	/*
public Agent(String address) throws IOException {

// These files does not exist and are not used but has to be specified
// Read snmp4j docs for more info
super(new File("conf.agent"), new File("bootCounter.agent"),
new CommandProcessor(
  new OctetString(MPv3.createLocalEngineID())));
this.address = address;
}

	 */
	/**
	 * We let clients of this agent register the MO they
	 * need so this method does nothing
	 */
	@Override
	protected void registerManagedObjects() {
	}

	/**
	 * Clients can register the MO they need
	 */
	public void registerManagedObject(ManagedObject mo) {
		try {
			server.register(mo, null);
		} catch (DuplicateRegistrationException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void unregisterManagedObject(MOGroup moGroup) {
		moGroup.unregisterMOs(server, getContext(moGroup));
	}

	/*
	 * Empty implementation
	 */
	@Override
	protected void addNotificationTargets(SnmpTargetMIB targetMIB,
			SnmpNotificationMIB notificationMIB) {
	}

	/**
	 * Minimal View based Access Control
	 *
	 * http://www.faqs.org/rfcs/rfc2575.html
	 */
	@Override
	protected void addViews(VacmMIB vacm) {

		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(
		"cpublic"), new OctetString("v1v2group"),
		StorageType.nonVolatile);

		vacm.addAccess(new OctetString("v1v2group"), new OctetString("public"),
				SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV,
				MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
				new OctetString("fullWriteView"), new OctetString(
				"fullNotifyView"), StorageType.nonVolatile);

		vacm.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"),
				new OctetString(), VacmMIB.vacmViewIncluded,
				StorageType.nonVolatile);
	}

	/** 
	 * User based Security Model, only applicable to
	 * SNMP v.3
	 *
	 */
	protected void addUsmUser(USM usm) {
	}

	protected void initTransportMappings() throws IOException {
		transportMappings = new TransportMapping[1];
		Address addr = GenericAddress.parse(address);
		TransportMapping tm = TransportMappings.getInstance()
		.createTransportMapping(addr);
		transportMappings[0] = tm;
	}

	/**
	 * Start method invokes some initialization methods needed to
	 * start the agent
	 * @throws IOException
	 */
	public void start() throws IOException {

		init();
		// This method reads some old config from a file and causes
		// unexpected behavior.
		// loadConfig(ImportModes.REPLACE_CREATE);
		addShutdownHook();
		getServer().addContext(new OctetString("public"));
		finishInit();
		run();
		sendColdStartNotification();
	}


	protected void unregisterManagedObjects() {
		// here we should unregister those objects previously registered...
	}

	/**
	 * The table of community strings configured in the SNMP
	 * engine's Local Configuration Datastore (LCD).
	 *
	 * We only configure one, "public".
	 */
	protected void addCommunities(SnmpCommunityMIB communityMIB) {
		Variable[] com2sec = new Variable[] {
				new OctetString("public"), // community name
				new OctetString("cpublic"), // security name
				getAgent().getContextEngineID(), // local engine ID
				new OctetString("public"), // default context name
				new OctetString(), // transport tag
				new Integer32(StorageType.nonVolatile), // storage type
				new Integer32(RowStatus.active) // row status
		};
		MOTableRow row = communityMIB.getSnmpCommunityEntry().createRow(
				new OctetString("public2public").toSubIndex(true), com2sec);
		communityMIB.getSnmpCommunityEntry().addRow(row);
	}

	/*
public static void main(String[] args) throws IOException, InterruptedException {
Agent agent = new Agent("172.17.36.249/161");
agent.start();
            agent.criaTabela();
while(true) {
System.out.println("Agent running...");
Thread.sleep(5000);
}
}
	 */
	public void criaTabela() {
		// Since BaseAgent registers some mibs by default we need to unregister
		// one before we register our own sysDescr. Normally you would
		// override that method and register the mibs that you need
		//this.unregisterManagedObject(this.getSnmpv2MIB());

		OTNInterface otn = transponder.getOTN_WDMInterface();
		ClientInterface c = transponder.getClientInterface();

		System.out.println("*** Comecando a registrar a tabela!!! ***");

		// Register a system description, use one from you product environment
		// to test with
	//	this.registerManagedObject(MOScalarFactory.createReadOnly(sysDescr,"MySystemDescr"));;
		this.registerManagedObject(MOScalarFactory.createReadOnly(supervisores,"Supervisor"));
		this.registerManagedObject(MOScalarFactory.createReadOnly(transponders,"Transponder"));
		this.registerManagedObject(MOScalarFactory.createReadOnly(amplificadores,"Amplificador"));


		//Supervisor 1
		this.registerManagedObject(MOScalarFactory.createReadOnly(supervisor_1,"Supervisor 1"));
		this.registerManagedObject(MOScalarFactory.createReadOnly(nome_11,"SPVL"));
		this.registerManagedObject(MOScalarFactory.createReadOnly(local_11,"L104"));
		this.registerManagedObject(MOScalarFactory.createReadOnly(ip_11,conf.getIpSPVL()));

//		//Supervisor 2
//		this.registerManagedObject(MOScalarFactory.createReadOnly(supervisor_2,"Supervisor 2"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(nome_12,"SPVJ"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(local_12,"NTI"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(ip_12,conf.getIpSPVJ()));

		//Transponder 1 - 10GE
		this.registerManagedObject(MOScalarFactory.createReadOnly(transponder_1,"Transponder 1"));
		this.registerManagedObject(MOScalarFactory.createReadOnly(nome_21,"G.709-10G"));
		this.registerManagedObject(MOScalarFactory.createReadOnly(local_21,"L104"));
		this.registerManagedObject(MOScalarFactory.createReadOnly(ip_21,conf.getIpSPVL()));
		//WDM (1550 nm)		
		is_lof_A = MOScalarFactory.createReadOnly(lof_A,Boolean.toString(otn.isLof()));
		this.registerManagedObject(is_lof_A);
		is_los_A = MOScalarFactory.createReadOnly(los_A,Boolean.toString(ladoA.isLos()));
		this.registerManagedObject(is_los_A);
		is_fail_A = MOScalarFactory.createReadOnly(fail_A,Boolean.toString(ladoA.isFail()));
		this.registerManagedObject(is_fail_A);
		is_laser_off_A = MOScalarFactory.createReadOnly(laserOff_A,Boolean.toString(ladoA.isLaserOff()));
		this.registerManagedObject(is_laser_off_A);
		v_pin_A = MOScalarFactory.createReadOnly(pIn_A,Double.toString(ladoA.getPin()));
		this.registerManagedObject(v_pin_A);
		v_pout_A = MOScalarFactory.createReadOnly(pOut_A,Double.toString(ladoA.getPout()));
		this.registerManagedObject(v_pout_A);
		v_temp_A = MOScalarFactory.createReadOnly(temp_A,Double.toString(ladoA.getModuleTemperature()));
		this.registerManagedObject(v_temp_A);
		v_temp_laser_A = MOScalarFactory.createReadOnly(tempLaser_A,Double.toString(ladoA.getLaserTemperature().getTemperature()));
		this.registerManagedObject(v_temp_laser_A);
		v_lambda_real = MOScalarFactory.createReadOnly(lambdaReal_A,Double.toString(ladoA.getLambdaReal()));
		this.registerManagedObject(v_lambda_real);
		v_channel_A = MOScalarFactory.createReadOnly(channel_A,ladoA.getChannel());
		this.registerManagedObject(v_channel_A);
		//Client (1310 nm)		
		is_lof_B = MOScalarFactory.createReadOnly(lof_B,Boolean.toString(otn.isLof()));
		this.registerManagedObject(is_lof_B);
		is_los_B = MOScalarFactory.createReadOnly(los_B,Boolean.toString(ladoB.isLos()));
		this.registerManagedObject(is_los_B);
		is_fail_B = MOScalarFactory.createReadOnly(fail_B,Boolean.toString(ladoB.isFail()));
		this.registerManagedObject(is_fail_B);
		is_laser_off_B = MOScalarFactory.createReadOnly(laserOff_B,Boolean.toString(ladoB.isLaserOff()));
		this.registerManagedObject(is_laser_off_B);
		v_pin_B = MOScalarFactory.createReadOnly(pIn_B,Double.toString(ladoB.getPin()));
		this.registerManagedObject(v_pin_B);
		v_pout_B = MOScalarFactory.createReadOnly(pOut_B,Double.toString(ladoB.getPout()));
		this.registerManagedObject(v_pout_B);
		v_temp_B = MOScalarFactory.createReadOnly(temp_B,Double.toString(ladoB.getModuleTemperature()));
		this.registerManagedObject(v_temp_B);

//		//Transponder 2 - 1GE
//		this.registerManagedObject(MOScalarFactory.createReadOnly(transponder_2,"Transponder 2"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(nome_22,"1GE"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(local_22,"NTI"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(ip_22,conf.getIpSPVJ()));

//		//Amplificador 1
//		this.registerManagedObject(MOScalarFactory.createReadOnly(amplificador_1,"Amplificador 1"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(nome_31,"Pre"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(local_31,"NTI"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(ip_31,conf.getIpSPVJ()));
//		gain_pre = MOScalarFactory.createReadOnly(ganho_31,Double.toString(dataPlane.getAmplifierIn().getAGCGain()));
//		this.registerManagedObject(gain_pre);//in dB
//
//		//Amplificador 2
//		this.registerManagedObject(MOScalarFactory.createReadOnly(amplificador_2,"Amplificador 2"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(nome_32,"Booster"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(local_32,"NTI"));
//		this.registerManagedObject(MOScalarFactory.createReadOnly(ip_32,conf.getIpSPVJ()));
//		gain_booster = MOScalarFactory.createReadOnly(ganho_32,Double.toString(dataPlane.getAmplifierOut().getAGCGain()));
//		this.registerManagedObject(gain_booster);//in dB
		
		System.out.println("*** Tabela criada!!! ***");
	}


	public void run() {
		//Muito importante - chamar o run() da classe BaseAgent!!!!
		super.run();
		//Agora codigo de atualização dos meus dados
		while(true){

			try {
				//Supervisores
				//System.out.println("NTI "+conf.getIpSPVJ());
				//System.out.println("L104 "+conf.getIpSPVL());				
				//Amplifiers
				//System.out.println("Pre: "+dataPlane.getAmplifierIn().getAGCGain()+" dB");
				//System.out.println("Booster: "+dataPlane.getAmplifierOut().getAGCGain()+ "dB");
				//Transponder OTN
				//				OpticalInterface ladoA = null;
				//				OpticalInterface ladoB = null;

				NumberFormat format = NumberFormat.getNumberInstance();
				format.setMaximumFractionDigits(2);
				format.setMinimumFractionDigits(2);
				format.setGroupingUsed(false);


				OTNInterface otn = transponder.getOTN_WDMInterface();
				ClientInterface c = transponder.getClientInterface();

				ODUk odu = transponder.getODUk();
				FEC fec = transponder.getFEC();

				//Update the values
				is_lof_A.setValue(MOScalarFactory.getVariable(Boolean.toString(otn.isLof())));
				is_los_A.setValue(MOScalarFactory.getVariable(Boolean.toString(ladoA.isLos())));
				is_fail_A.setValue(MOScalarFactory.getVariable(Boolean.toString(ladoA.isFail())));
				is_laser_off_A.setValue(MOScalarFactory.getVariable(Boolean.toString(ladoA.isLaserOff())));
				v_pin_A.setValue(MOScalarFactory.getVariable(Double.toString(ladoA.getPin())));
				v_pout_A.setValue(MOScalarFactory.getVariable(Double.toString(ladoA.getPout())));
				v_temp_A.setValue(MOScalarFactory.getVariable(Double.toString(ladoA.getModuleTemperature())));
				v_temp_laser_A.setValue(MOScalarFactory.getVariable(Double.toString(ladoA.getLaserTemperature().getTemperature())));
				v_lambda_real.setValue(MOScalarFactory.getVariable(Double.toString(ladoA.getLambdaReal())));
				v_channel_A.setValue(MOScalarFactory.getVariable(ladoA.getChannel()));
				//Client (1310 nm)		
				is_lof_B.setValue(MOScalarFactory.getVariable(Boolean.toString(otn.isLof())));
				is_los_B.setValue(MOScalarFactory.getVariable(Boolean.toString(ladoB.isLos())));
				is_fail_B.setValue(MOScalarFactory.getVariable(Boolean.toString(ladoB.isFail())));
				is_laser_off_B.setValue(MOScalarFactory.getVariable(Boolean.toString(ladoB.isLaserOff())));
				v_pin_B.setValue(MOScalarFactory.getVariable(Double.toString(ladoB.getPin())));
				v_pout_B.setValue(MOScalarFactory.getVariable(Double.toString(ladoB.getPout())));
				//gain_pre.setValue(MOScalarFactory.getVariable(Double.toString(dataPlane.getAmplifierIn().getAGCGain())));
				//gain_booster.setValue(MOScalarFactory.getVariable(Double.toString(dataPlane.getAmplifierOut().getAGCGain())));

				
				NumberFormat df1 = new DecimalFormat("#.00", new DecimalFormatSymbols(BrazilLocale.BRAZIL));
				NumberFormat df2 = new DecimalFormat("0.000E0", new DecimalFormatSymbols(BrazilLocale.BRAZIL));

				if (ladoA != null && (ladoB != null) ) {
//					System.out.printf("Alarmes");	
//
//					System.out.print("LOF A: " + otn.isLof()+"\n");
//					System.out.print("LOF B: " + c.isLof()+"\n");
//					System.out.print("LOS A: " + ladoA.isLos()+"\n");
//					System.out.print("LOS B: " + ladoB.isLos()+"\n");
//					System.out.print("Fail A : " + ladoA.isFail()+"\n");
//					System.out.print("Fail B: " + ladoB.isFail()+"\n");
//					System.out.print("Laser off A: " + ladoA.isLaserOff()+"\n");
//					System.out.print("Laser off B: " + ladoB.isLaserOff()+"\n");
//
//					System.out.printf("Medidas:");
//					System.out.println();
//					System.out.printf("Pin A: %.2f dBm", ladoA.getPin());
//					System.out.println();
//					System.out.printf("Pin B: %.2f dBm", ladoB.getPin());
//					System.out.println();
//					System.out.printf("Pout A: %.2f dBm", ladoA.getPout());
//					System.out.println();
//					System.out.printf("Pout B: %.2f dBm", ladoB.getPout());
//					System.out.println();
//					if (!(Double.isNaN(ladoA.getModuleTemperature()))) {
//						System.out.printf("Temperatura A: %.2f °C", ladoA.getModuleTemperature());
//					}
//					System.out.println();
//					if (!(Double.isNaN(ladoB.getModuleTemperature()))) {
//						System.out.printf("Temperatura B: %.2f °C ", ladoB.getModuleTemperature());
//					}
//					System.out.println();
//					if ( ladoA.getLaserTemperature().isEnabled() ){
//						System.out.printf("Temp do Laser A: %.2f °C ", ladoA.getLaserTemperature().getTemperature());
//					}
//					System.out.println();
//
//					System.out.printf("Lambda Real A: %.2f", ladoA.getLambdaReal());
//					System.out.println();
//
//					if (ladoA.isDense())
//						System.out.printf("Channel A: %s (%.2f nm)", ladoA.getChannel(),ladoA.getLambdaNominal());
//
//					else {
//						System.out.printf("Channel A: " + ladoA.getChannel());
//					}
//					System.out.println();
//
//					System.out.printf("ODU: ");
//					System.out.println();
//					System.out.printf("BIP-8:");
//					System.out.println();
//					System.out.printf("Total:" + df1.format(odu.getBip8()));
//					System.out.println();
//					System.out.printf("Taxa:" + df2.format(odu.getBIP8Rate()));
//					System.out.println();
//					System.out.printf("BEI:");
//					System.out.println();
//					System.out.printf("Total:" + df1.format(odu.getBei()));
//					System.out.println();
//					System.out.printf("Taxa:" + df2.format(odu.getBEIRate()));
//					System.out.println();
//					System.out.printf("Status: " + odu.getStatDesc());
//					System.out.println();
//					System.out.printf("FEC:");
//					System.out.printf("Taxa de Bits Corrigidos: " + df2.format(fec.getFixedBitsRate()));
//					System.out.println();

					//Transponder OTN tr.getModel 
					Map<String, String> parsed = TransponderModelParser.parse(transponder.getModel());
					String caracteristica = parsed.get("caracteristica");
					if ((caracteristica != null) && (caracteristica.compareTo("T") == 0) ) {
					//	System.out.printf("Taxa: 10GbE");
					//	System.out.println();
						//					txt.writeDataPlane(dataPlane);
					}
				}


				//atualiza a cada 30s
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}
}
