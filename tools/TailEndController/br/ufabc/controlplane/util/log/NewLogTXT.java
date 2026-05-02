package br.ufabc.controlplane.util.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

import org.xml.sax.SAXException;

import br.com.padtec.v3.data.impl.OpticalInterface_Impl;
import br.com.padtec.v3.data.impl.T100D_GT_Impl;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.ClientInterface;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.ODUk;
import br.com.padtec.v3.data.ne.OTNInterface;
import br.com.padtec.v3.data.ne.PBAmp;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.conf.Configuracao;
import br.ufabc.controlplane.conf.ExcecaoConfiguracao;
import br.ufabc.dataplane.DataPlane;
import br.ufabc.equipment.Amplifiers;
import br.ufabc.equipment.OTNTransponder;
import br.ufabc.equipment.OXC;
import br.ufabc.polatis.PolatisOXC;

public class NewLogTXT {

	//public static final Locale BRAZIL = new Locale("pt", "BR");
	//private FileOutputStream outFile;
	private PrintWriter writer;
	//private Configuracao conf;
	private FileWriter logFile;
	Vector<NE> monitored = new Vector<NE>();
	
	TrpOTNTerminal otn;
	
	DataPlane dataPlane;
	ControlPlane controller;
	Date now=new Date();
	private static NewLogTXT txt;
	Long nowHeader = System.currentTimeMillis();
	public static final Locale BRAZIL = new Locale("pt", "BR");
	private String name;
	// Grava no arquivo de log
	public void logRecord(String entry) {
		try {
			logFile = new FileWriter(new File(name), true);
			logFile.write("\t" + entry);
			logFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void logRecord(Double entry) {
		try {
			logFile = new FileWriter(new File(name), true);
			logFile.write("\t" + entry);
			logFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logRecord(BigInteger entry) {
		try {
			logFile = new FileWriter(new File(name), true);
			logFile.write("\t" + entry);
			logFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Gera o cabeçalho no arquivo de log
	public void headerRecorder() {
		name = controller.getConf().getNomeExperimento();
		logRecord("\n");
		
		
		//Grava dados do Transponder
				logRecord("Iniciando em "+System.currentTimeMillis());
				logRecord(otn.getName()+ "-POUT");
				logRecord("PIN");
				logRecord("IS_UP");
				logRecord("IS_LOS");
				logRecord("BIP8");
				logRecord("BIP8 RATE");
				logRecord("BEI");
				logRecord("BEI RATE");
				logRecord("StatDesc");
				logRecord("FEC fixed bits");
				logRecord("FEC fixed bits rate");
				
				
		logRecord("\n");
		
	}
	// Obtêm leituras do elemento a ser monitorado
	public void getInf() {
		Calendar date = GregorianCalendar.getInstance(BRAZIL);
		String now = date.get(Calendar.HOUR_OF_DAY)+":"+date.get(Calendar.MINUTE)+":"+date.get(Calendar.SECOND);
		
		logRecord(String.valueOf(System.currentTimeMillis()));
		NumberFormat df1 = new DecimalFormat("0.00", new DecimalFormatSymbols(BRAZIL));
		NumberFormat df2 = new DecimalFormat("0.000E0", new DecimalFormatSymbols(BRAZIL));
		NumberFormat df3 = new DecimalFormat("0.000", new DecimalFormatSymbols(BRAZIL));

				logRecord(String.valueOf(otn.getPout()));
				logRecord(String.valueOf(otn.getPin()));
				logRecord(String.valueOf(otn.isUp()));
				logRecord(String.valueOf(otn.isLos()));
				logRecord(String.valueOf(otn.getOpticalWDMInterface().getLambdaReal()));
				logRecord(df1.format(otn.getODUk().getBip8()));
				logRecord(df2.format(otn.getODUk().getBIP8Rate()));
				logRecord(df1.format(otn.getODUk().getBei()));
				logRecord(df2.format(otn.getODUk().getBEIRate()));
				logRecord(otn.getODUk().getStatDesc());
				logRecord(String.valueOf(df2.format(otn.getFEC().getFixedBits())));
				logRecord(String.valueOf(df2.format(otn.getFEC().getFixedBitsRate())));
				
			System.out.println(now+"Transponder "+ otn.getName()+"Pout: "+otn.getPout()+"Pin: "+otn.getPin()+" add data "+otn.getODUk().getBei()+" br "+otn.getODUk().getBEIRate()+" b8 "+otn.getODUk().getBip8()+" b8r "+otn.getODUk().getBIP8Rate());
				
//				oxc.setActivedEgressPorts();
//				oxc.setActivedIgressPorts();
		
				
//				for(int i=1; i<=8; i++){
//					//int port=Integer.valueOf(i);
//					logRecord(String.valueOf(polatis.getConnectPort(i)));
//					logRecord(String.valueOf(polatis.getMonitorAlarmInPort(i)));
//					logRecord(String.valueOf(polatis.getPortDesiredCondition(i)));
//					logRecord(String.valueOf(polatis.getPortDesiredState(i)));
//					logRecord(String.valueOf(polatis.getProtectingPort(i)));
//				}
//				for(int i=9; i<=16;i++){
//					//int port=Integer.valueOf(i);
//					logRecord(String.valueOf(polatis.getConnectPort(i)));
//					logRecord(String.valueOf(polatis.getMeasurePower(i)));
//					logRecord(String.valueOf(polatis.getVOAMode(i)));
//					logRecord(String.valueOf(polatis.getMonitorAlarmInPort(i)));
//					logRecord(String.valueOf(polatis.getPortDesiredCondition(i)));
//					logRecord(String.valueOf(polatis.getPortDesiredState(i)));
//					logRecord(String.valueOf(polatis.getAttenuationLevel(i)));
//					logRecord(String.valueOf(polatis.getOPMType(i)));
//					logRecord(String.valueOf(polatis.getOPMWavelength(i)));
//					
//				}

				logRecord("\n");
			}
		

	
	
	
	private NewLogTXT(DataPlane dataPlane, ControlPlane controller) {
		this.dataPlane = dataPlane;
		this.controller = controller;
		
		otn = dataPlane.getTransponder();
		
		//monitored.add(oxc);
		
//		try {
//			
//			String arq_conf = "./configuracao.xml";
//			try {
//				conf = Configuracao.carregarConfiguracao(arq_conf);
//			} catch (ExcecaoConfiguracao e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (SAXException e) {
//				e.printStackTrace();
//			}
//			
//			Calendar c =GregorianCalendar.getInstance(BRAZIL);
//			//"-"+c.get(Calendar.HOUR_OF_DAY)+"."+c.get(Calendar.MINUTE)
//			//String filename = "experimento1"+"-"+c.get(Calendar.DAY_OF_MONTH)+"."+c.getDisplayName(Calendar.MONTH, Calendar.SHORT,BRAZIL)+
//			//"-"+c.get(Calendar.HOUR_OF_DAY)+"."+c.get(Calendar.MINUTE)+".txt";
//			String filename = conf.getNomeExperimento()+"-"+ c.get(Calendar.YEAR)+"."+
//			(c.get(Calendar.MONTH)+1)+"."+c.get(Calendar.DAY_OF_MONTH)+
//			"-"+c.get(Calendar.HOUR_OF_DAY)+"."+c.get(Calendar.MINUTE)+".txt";
//			System.out.print("Iniciando Arquivo de Experimento: ");
//			System.out.println(filename);
//			outFile = new FileOutputStream(filename);
//
//			writer = new PrintWriter(outFile);
//			writer.write("Tempo\tData\tHora\tSerial\tPin\tPout\tCanal\tLambda\tLOS\tLOF\tFAIL\tLaserOFF" +
//					"\tPin\tPout\tLOS\tLOF\tFAIL\tLaserOFF" +
//			"\tBIP-8\tBIP-8_Taxa\tBEI\tBEI_Taxa\tStatus\tFEC_Taxa_Bits_Corrigido\tFEC_RX_enable\tFEC_TX_enable\tGanhoIn\tGanhoRealIn\t" +
//			"GanhoOut\tGanhoRealOut\tAmpInPin\tAmpInPout\tAmpOutPin\tAmpOutPout\n");
//			
//			
//		} catch (FileNotFoundException e) {
//
//			e.printStackTrace();
//		}

	}

	public static NewLogTXT getInstance(DataPlane dataPlane, ControlPlane controller){
		if (txt == null){
			txt = new NewLogTXT(dataPlane,controller);
			return txt;
		}
		return txt;
	}

	public void flush(){
		writer.flush();
	}

	public void close(){
		writer.close();
	}

	public void write(String s){
		writer.write(s);
	}

	
	public synchronized void writeDataPlane(){
					
				
		getInf();
		
		
		
		
	}

//	public static void main(String[] args){
//		double pin = 14.4;
//		double pout = 15.9;
//		double fec = Double.parseDouble("1.471E-6");
//
//
//		NumberFormat df1 = new DecimalFormat("#.00", new DecimalFormatSymbols(BRAZIL));
//		NumberFormat df2 = new DecimalFormat("0.000E0", new DecimalFormatSymbols(BRAZIL));
//		NumberFormat df3 = new DecimalFormat("0.000", new DecimalFormatSymbols(BRAZIL));
//		System.out.println(df3.format(System.currentTimeMillis()));
//		Calendar c = GregorianCalendar.getInstance();
//		Object[] arg = {c, df1.format(pin), df2.format(fec)};
//		//		LogTXT txt = new LogTXT();
//		//\t%1$T\t%2$.2f\t%3$.2f
//		String format = String.format("%1$te/%1$tm/%1$tY %1$tT, %2$sdbm, %3$s", arg);
//		String format2 = "|%1$-10s|%2$-10s|%3$-20s|\n";
//		Formatter f = new Formatter();
//		//		System.out.println(f.format(format, arg));
//		System.out.println(format);
//		for (int i = 1 ; i < 23 ; i++){
//			System.out.print("\\t%"+i+"$s");
//		}
//	}

}