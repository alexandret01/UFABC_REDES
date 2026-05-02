
package br.ufabc.controlplane.util.log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.xml.sax.SAXException;

import br.com.padtec.v3.data.impl.OpticalInterface_Impl;
import br.com.padtec.v3.data.impl.T100D_GT_Impl;
import br.com.padtec.v3.data.ne.ClientInterface;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.ODUk;
import br.com.padtec.v3.data.ne.OTNInterface;
import br.com.padtec.v3.data.ne.PBAmp;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.conf.Configuracao;
import br.ufabc.controlplane.conf.ExcecaoConfiguracao;
import br.ufabc.dataplane.DataPlane;

public class LogTXT {

	public static final Locale BRAZIL = new Locale("pt", "BR");
	private FileOutputStream outFile;
	private PrintWriter writer;
	private Configuracao conf;

	private static LogTXT txt;

	private LogTXT() {

		try {
			
			String arq_conf = "./configuracao.xml";
			try {
				conf = Configuracao.carregarConfiguracao(arq_conf);
			} catch (ExcecaoConfiguracao e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			
			Calendar c =GregorianCalendar.getInstance(BRAZIL);
			//"-"+c.get(Calendar.HOUR_OF_DAY)+"."+c.get(Calendar.MINUTE)
			//String filename = "experimento1"+"-"+c.get(Calendar.DAY_OF_MONTH)+"."+c.getDisplayName(Calendar.MONTH, Calendar.SHORT,BRAZIL)+
			//"-"+c.get(Calendar.HOUR_OF_DAY)+"."+c.get(Calendar.MINUTE)+".txt";
			String filename = conf.getNomeExperimento()+"-"+ c.get(Calendar.YEAR)+"."+
			(c.get(Calendar.MONTH)+1)+"."+c.get(Calendar.DAY_OF_MONTH)+
			"-"+c.get(Calendar.HOUR_OF_DAY)+"."+c.get(Calendar.MINUTE)+".txt";
			System.out.print("Iniciando Arquivo de Experimento: ");
			System.out.println(filename);
			outFile = new FileOutputStream(filename);

			writer = new PrintWriter(outFile);
			writer.write("Tempo\tData\tHora\tSerial\tPin\tPout\tCanal\tLambda\tLOS\tLOF\tFAIL\tLaserOFF" +
					"\tPin\tPout\tLOS\tLOF\tFAIL\tLaserOFF" +
			"\tBIP-8\tBIP-8_Taxa\tBEI\tBEI_Taxa\tStatus\tFEC_Taxa_Bits_Corrigido\tFEC_RX_enable\tFEC_TX_enable\tGanhoIn\tGanhoRealIn\t" +
			"GanhoOut\tGanhoRealOut\tAmpInPin\tAmpInPout\tAmpOutPin\tAmpOutPout\n");
			

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

	}

	public static LogTXT getInstance(){
		if (txt == null){
			txt = new LogTXT();
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


	public synchronized void writeDataPlane( DataPlane dataPlane ){
		if (dataPlane != null){
			T100D_GT_Impl transponder = (T100D_GT_Impl)dataPlane.getTransponder(); 

			PBAmp pre = dataPlane.getAmplifierIn();
			PBAmp booster = dataPlane.getAmplifierOut();
			NumberFormat df1 = new DecimalFormat("0.00", new DecimalFormatSymbols(BRAZIL));
			NumberFormat df2 = new DecimalFormat("0.000E0", new DecimalFormatSymbols(BRAZIL));
			NumberFormat df3 = new DecimalFormat("0.000", new DecimalFormatSymbols(BRAZIL));
			OpticalInterface_Impl ladoA = transponder.getOpticalWDMInterface_Impl();
			OpticalInterface_Impl ladoB = transponder.getOpticalClientInterface_Impl();
			OTNInterface otn = transponder.getOTN_WDMInterface();
			ClientInterface c = transponder.getClientInterface();
			FEC fec = transponder.getFEC();
			ODUk odu = transponder.getODUk();

			ArrayList<Object> attributes = new ArrayList<Object>();
			attributes.add(GregorianCalendar.getInstance()); 
			double now = (System.currentTimeMillis()-ControlPlane.START_TIME)/1000;
			attributes.add(df3.format(now));
			attributes.add(transponder.getSerial().toShortString());  
			attributes.add(df1.format(ladoA.getPin())); 
			attributes.add(df1.format( Double.isNaN(ladoA.getPout()) ? -50 :  ladoA.getPout() )); 
			attributes.add(ladoA.getChannel()); 
			attributes.add(df1.format(ladoA.getLambdaNominal())); 
			attributes.add(ladoA.isLos()); 
			attributes.add(otn.isLof()); 
			attributes.add(ladoA.isFail()); 
			attributes.add(ladoA.isLaserOff()); 
			attributes.add(df1.format(ladoB.getPin()));  
			attributes.add(df1.format(ladoB.getPout())); 
			attributes.add(ladoB.isLos()); 
			attributes.add(c.isLof()); 
			attributes.add(ladoB.isFail()); 
			attributes.add(ladoB.isLaserOff()); 
			attributes.add(df1.format(odu.getBip8())); 
			attributes.add(df2.format(odu.getBIP8Rate())); 
			attributes.add(df1.format(odu.getBei())); 
			attributes.add(df2.format(odu.getBEIRate())); 
			attributes.add(odu.getStatDesc()); 
			attributes.add(df2.format(fec.getFixedBitsRate()));
			attributes.add(!fec.isFecRxCorrEnabled());
			attributes.add(!fec.isFecTxCorrEnabled());

			if (dataPlane.hasAmplifierIn() && dataPlane.hasAmplifierOut()){
				attributes.add(df1.format(pre.getAGCGain())); 
				attributes.add(df1.format(pre.getPout()-pre.getPin())); 
				attributes.add(df1.format(booster.getAGCGain())); 
				attributes.add(df1.format(booster.getPout()-booster.getPin()));
				attributes.add(df1.format(pre.getPin()));
				attributes.add(df1.format(pre.getPout())); 
				attributes.add(df1.format(booster.getPin()));
				attributes.add(df1.format(booster.getPout()));
			} 
			

			Object[] args = attributes.toArray();

			String format = null;
			if (dataPlane.hasAmplifierIn() && dataPlane.hasAmplifierOut()){
				format = String.format("%2$s\t%1$te/%1$tm/%1$tY\t%1$tT" + 
						"\t%3$s\t%4$s\t%5$s\t%6$s\t%7$s\t%8$s\t%9$s\t%10$s" +
						"\t%11$s\t%12$s\t%13$s\t%14$s\t%15$s\t%16$s\t%17$s" +
						"\t%18$s\t%19$s\t%20$s\t%21$s\t%22$s\t%23$s\t%24$s\t%25$s\t%26$s\t%27$s" +
						"\t%28$s\t%29$s\t%30$s\t%31$s\t%31$s\t%32$s\n", args);
			} else {
				format = String.format("%2$s\t%1$te/%1$tm/%1$tY\t%1$tT" + 
						"\t%3$s\t%4$s\t%5$s\t%6$s\t%7$s\t%8$s\t%9$s\t%10$s" +
						"\t%11$s\t%12$s\t%13$s\t%14$s\t%15$s\t%16$s\t%17$s" +
						"\t%18$s\t%19$s\t%20$s\t%21$s\t%22$s\t%23$s\t%24$s\t%25$s\n", args);
			}

			writer.write(format);
			writer.flush();
		}

	}

	public static void main(String[] args){
		double pin = 14.4;
		double pout = 15.9;
		double fec = Double.parseDouble("1.471E-6");


		NumberFormat df1 = new DecimalFormat("#.00", new DecimalFormatSymbols(BRAZIL));
		NumberFormat df2 = new DecimalFormat("0.000E0", new DecimalFormatSymbols(BRAZIL));
		NumberFormat df3 = new DecimalFormat("0.000", new DecimalFormatSymbols(BRAZIL));
		System.out.println(df3.format(System.currentTimeMillis()));
		Calendar c = GregorianCalendar.getInstance();
		Object[] arg = {c, df1.format(pin), df2.format(fec)};
		//		LogTXT txt = new LogTXT();
		//\t%1$T\t%2$.2f\t%3$.2f
		String format = String.format("%1$te/%1$tm/%1$tY %1$tT, %2$sdbm, %3$s", arg);
		String format2 = "|%1$-10s|%2$-10s|%3$-20s|\n";
		Formatter f = new Formatter();
		//		System.out.println(f.format(format, arg));
		System.out.println(format);
		for (int i = 1 ; i < 23 ; i++){
			System.out.print("\\t%"+i+"$s");
		}
	}

}
