package br.ufabc.dataplane;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import util.BrazilLocale;
import br.com.padtec.v3.data.ne.ClientInterface;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.ODUk;
import br.com.padtec.v3.data.ne.OTNInterface;
import br.com.padtec.v3.data.ne.OpticalInterface;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.util.Statistic;
import br.ufabc.dataplane.alarms.AlarmGmpls;

public class DataPlaneMonitorThread extends Thread {
	private static final String CLASS = "DataPlaneMonitorThread";
	private DataPlane dataPlane;
	public static final Locale BRAZIL = new Locale("pt", "BR");
	private FileOutputStream outFile;
	private PrintWriter writer;
	/*Hash map with event.code and alarmGmpls*/
	private Map<Integer, AlarmGmpls> listLastAlarmGmpls;
	/*Hash map with alarmGmpls*/
	private LinkedList<AlarmGmpls> listAlarmsGmpls;
	private int timeReactFromAlarm = 0;
	private int timeWait = 0;
	private ArrayList<Double> connectionOk;
	private ArrayList<Double> connectionFail;
	private int numberOfSamples;
	private int intervalBetweenSamples;
	private int timesLOF = 0;

	public DataPlaneMonitorThread(DataPlane dataPlane){

		super(CLASS);

		try{
			if(dataPlane == null){
				throw new DataPlaneException("Plano de dados não iniciado");
			}
		} catch (DataPlaneException d){
			d.printStackTrace();
		}
		listLastAlarmGmpls = new HashMap<Integer, AlarmGmpls>();
		listAlarmsGmpls = new LinkedList<AlarmGmpls>();
		connectionOk = new ArrayList<Double>();
		connectionFail = new ArrayList<Double>();
		this.dataPlane = dataPlane;
		this.timeReactFromAlarm = dataPlane.getConf().getDataPlaneTimeToWait();
		this.numberOfSamples = dataPlane.getConf().getNumberOfPowerSamples();
		this.intervalBetweenSamples = dataPlane.getConf().getIntervalBetweenSamples();
		try {
			Calendar c =GregorianCalendar.getInstance(BRAZIL);
			String filename = "Alarmes"+"-"+ c.get(Calendar.YEAR)+"."+
			(c.get(Calendar.MONTH) + 1) +"."+c.get(Calendar.DAY_OF_MONTH)+
			"-"+c.get(Calendar.HOUR_OF_DAY)+"."+c.get(Calendar.MINUTE)+".txt";
			outFile = new FileOutputStream(filename);
			writer = new PrintWriter(outFile);


		} catch (Exception e) {

			e.printStackTrace();
		}

	}


	public void run(){
		
		try {
			sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true){
			if (dataPlane.getIdLSP() > -1 && dataPlane.isTransmiting()){
				OpticalInterface ladoA = null;
				OpticalInterface ladoB = null;

				NumberFormat format = NumberFormat.getNumberInstance();
				format.setMaximumFractionDigits(2);
				format.setMinimumFractionDigits(2);
				format.setGroupingUsed(false);
				TrpOTNTerminal transponder = dataPlane.getTransponder();
				ladoA = transponder.getOpticalWDMInterface();
				ladoB = transponder.getOpticalClientInterface();

				OTNInterface otn = transponder.getOTN_WDMInterface();
				ClientInterface c = transponder.getClientInterface();

				ODUk odu = transponder.getODUk();
				FEC fec = transponder.getFEC();

				NumberFormat df1 = new DecimalFormat("#.00", new DecimalFormatSymbols(BrazilLocale.BRAZIL));
				NumberFormat df2 = new DecimalFormat("0.000E0", new DecimalFormatSymbols(BrazilLocale.BRAZIL));

				
				AlarmGmpls alarm = null;
				DataPlaneEvent event = null;
				List<AlarmGmpls> listAlarmNow = new ArrayList<AlarmGmpls>();//Alarmes detectados nesta verificação
				AlarmGmpls lastAlarm = null;
				
				if (ladoA != null && (ladoB != null) ) {
				
					if (dataPlane.isTransmiting()){
				
						
						if (ladoA.isLos()){
							System.out.println("GEREI EVENTO DE DISCONEXÃO");
							event = DataPlaneEvent.SIGNAL_FAIL;
							alarm = new AlarmGmpls(event, AlarmGmpls.PRIORITY_CRITICAL, dataPlane.getOiWDM().getPin(),  dataPlane.getOiWDM().getPout());
							listAlarmsGmpls.add(alarm);
							timeWait = timeReactFromAlarm;
							ControlPlane.getInstance().analyzeEventFromDataPlane(event);
						
						} else if (ladoA.getPin() < -24 ){
							event = DataPlaneEvent.POWER_IN_BELOW_LIMITES;
							alarm = new AlarmGmpls(event, AlarmGmpls.PRIORITY_CRITICAL, dataPlane.getOiWDM().getPin(),  dataPlane.getOiWDM().getPout());
							listAlarmsGmpls.add(alarm);
							timeWait = timeReactFromAlarm;
							ControlPlane.getInstance().analyzeEventFromDataPlane(event);
							
						} else if (ladoA.getPin() > -5 ){
							event = DataPlaneEvent.POWER_IN_ABOVE_LIMITES;
							alarm = new AlarmGmpls(event, AlarmGmpls.PRIORITY_CRITICAL, dataPlane.getOiWDM().getPin(),  dataPlane.getOiWDM().getPout());
							listAlarmsGmpls.add(alarm);
							timeWait = timeReactFromAlarm;
							ControlPlane.getInstance().analyzeEventFromDataPlane(event);
						}
//						 else if (ladoA.isLos() ){
//							event = DataPlaneEvent.LOS;
//							alarm = new AlarmGmpls(event, AlarmGmpls.PRIORITY_CRITICAL, dataPlane.getOiWDM().getPin(),  dataPlane.getOiWDM().getPout());
//							listAlarmsGmpls.add(alarm);
//							timeWait = timeReactFromAlarm;
//							ControlPlane.getInstance().analyzeEventFromDataPlane(event);
//
//						} 
						if (otn.isLof()){


								double pinMin = -24; //-24 dBm
								double margin1 = pinMin + 5; //5 dBm
								double margin2 = margin1 + 3; //3 dBm

								connectionFail.clear();
								
								int time = 0;
								while (true){
									if (time < numberOfSamples){
										connectionFail.add(dataPlane.getOiWDM().getPin());
										try {
											sleep(intervalBetweenSamples); //coleta dados a cada 100 ms
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										time++;
									} else {
										break;
									} 

								}

								double media = Statistic.getAverage(connectionFail);
								ControlPlane.getInstance().log(CLASS, Level.INFO, "Média: " + media);
								// Potencia mínima do transponder + margem <= média dos últimas leituras.
								if (pinMin < media && margin1 >= media){
									event = DataPlaneEvent.LOF;
									ControlPlane.getInstance().log(CLASS, Level.WARNING, event + ", potência dentro da primeira margem");
									alarm = new AlarmGmpls(event, AlarmGmpls.PRIORITY_CRITICAL, dataPlane.getOiWDM().getPin(),  dataPlane.getOiWDM().getPout());
									listAlarmsGmpls.add(alarm);
									timeWait = timeReactFromAlarm;
									ControlPlane.getInstance().analyzeEventFromDataPlane(event);
								} else if (margin1 < media && margin2 >= Statistic.getAverage(connectionFail)) {
									event = DataPlaneEvent.LOF;
									ControlPlane.getInstance().log("DataPlaneMonitor", Level.WARNING, event + ", Potência superou a primeira margem, média: " + media);
									timeWait = timeReactFromAlarm;
									alarm = new AlarmGmpls(event, AlarmGmpls.PRIORITY_CRITICAL, dataPlane.getOiWDM().getPin(),  dataPlane.getOiWDM().getPout());
									listAlarmsGmpls.add(alarm);
									ControlPlane.getInstance().analyzeEventFromDataPlane(event);
								} else if (media > margin2){
									event = DataPlaneEvent.DISCONNECT;
									ControlPlane.getInstance().log("DataPlaneMonitor", Level.WARNING, event + ", Potência Fora da margem!. DEVE DESCONECTAR, Média: " + media);
									timeWait = timeReactFromAlarm;
									alarm = new AlarmGmpls(event, AlarmGmpls.PRIORITY_CRITICAL, dataPlane.getOiWDM().getPin(),  dataPlane.getOiWDM().getPout());
									listAlarmsGmpls.add(alarm);
									ControlPlane.getInstance().analyzeEventFromDataPlane(event);
								}

//							} else if (odu.getBIP8Rate() > Thresholds.getBip8MaxRate()){
							} else if (odu.getBip8().compareTo(BigInteger.ZERO) > 0){	

								event = DataPlaneEvent.BIP8_ERROR_RATE;
								alarm = new AlarmGmpls(event, AlarmGmpls.PRIORITY_MINOR, dataPlane.getOiWDM().getPin(),  dataPlane.getOiWDM().getPout());
								ControlPlane.getInstance().log(CLASS, Level.WARNING, "Alta taxa do Bip-8: " + odu.getBIP8Rate() + ", Taxa máxima: " + Thresholds.getBip8MaxRate());
								listAlarmsGmpls.add(alarm);
								ControlPlane.getInstance().analyzeEventFromDataPlane(event);
								timeWait = timeReactFromAlarm;

							}else {
						connectionOk.add(dataPlane.getOiWDM().getPin());
						connectionFail.clear(); 
					}



					}
					
					

				} else {
					System.out.println("Lado A e B são nulos"+ladoA.toString() + "b"+ladoB.toString());
				}
				

				try {
					if (timeWait > 0){
						sleep(timeWait); // monitora de acordo com o tempo especificado
						//					dataPlane.reserCountersFec();
						timeWait = 0;
					} else {
						sleep(1000); // monitora a cada 1 s
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addAlarm(DataPlaneEvent event, AlarmGmpls alarm){
		listAlarmsGmpls.add(alarm);
		listLastAlarmGmpls.put(event.code, alarm);
	}




	public void writeAlarme(AlarmGmpls a){
		if (a != null){
			//			writer.write(PriorityAlarm.getName(a.getPriority()) + ", NAME: " + a.getAlarmName() + ", Desc: " + 
			//				a.getDescription() + ", NE: " + a.getNeName() + ", TIME: " + a.getTimestamp() +"\n");
			writer.write(a.toString());
			writer.flush();

		}

	}


}
