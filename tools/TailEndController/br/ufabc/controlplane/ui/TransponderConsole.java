package br.ufabc.controlplane.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Map;

import util.BrazilLocale;
import br.com.padtec.v3.data.ne.ClientInterface;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.ODUk;
import br.com.padtec.v3.data.ne.OTNInterface;
import br.com.padtec.v3.data.ne.OpticalInterface;
import br.com.padtec.v3.data.ne.PBAmp;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.util.modelparser.TransponderModelParser;


/**
 * This class is used to print the Transponder's information 
 * in console user interface
 * */
public class TransponderConsole extends Console{
	/***/
	private TrpOTNTerminal transponder;
	private PBAmp pre;
	private PBAmp booster;
	private boolean show = true;
	private static int POS_MEDIDAS = 6;
	private static int POS_ALARMS = 1;
	private int totalLineUsed;

	
//	public TransponderConsole(TransponderOTN transponder, PBAmp pre, PBAmp booster) {
//		this.transponder = transponder;
//		this.pre = pre;
//		this.booster = booster;
//	}
	public TransponderConsole() {
		System.out.println("plano de dados:" + dataPlane);
		
		this.transponder = dataPlane.getTransponder();
		if (dataPlane.hasAmplifierIn() && dataPlane.hasAmplifierOut()){
			this.pre = dataPlane.getAmplifierIn();
			this.booster = dataPlane.getAmplifierOut();
		}
	}
	
	public void show() {

		OpticalInterface ladoA = null;
		OpticalInterface ladoB = null;
		
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		format.setGroupingUsed(false);

		ladoA = transponder.getOpticalWDMInterface();
		ladoB = transponder.getOpticalClientInterface();

		OTNInterface otn = transponder.getOTN_WDMInterface();
		ClientInterface c = transponder.getClientInterface();

		ODUk odu = transponder.getODUk();
		FEC fec = transponder.getFEC();
		
		NumberFormat df1 = new DecimalFormat("#.00", new DecimalFormatSymbols(BrazilLocale.BRAZIL));
		NumberFormat df2 = new DecimalFormat("0.000E0", new DecimalFormatSymbols(BrazilLocale.BRAZIL));

		

		if (ladoA != null && (ladoB != null) ) {
			int colA = 14;
			int colB = 30 + colA;
			
			int line = POS_ALARMS;
			
			position(line, colA);
			System.out.printf("Lado WDM");
			position(line, colB);
			System.out.printf("Lado Client");
			++line;
			position(++line, 2);
			System.out.printf("Alarmes");
			
			position(line, colA);
			System.out.print("LOF: " + otn.isLof());
			position(line, colB);
			System.out.print("LOF: " + c.isLof());
			position(++line, colA);
			System.out.print("LOS: " + ladoA.isLos());
			position(line, colB);
			System.out.print("LOS: " + ladoB.isLos());
			position(++line, colA);
			System.out.print("Fail: " + ladoA.isFail());
			position(line, colB);
			System.out.print("Fail: " + ladoB.isFail());
			position(++line, colA );
			System.out.print("Laser off: " + ladoA.isLaserOff());
			position(line, colB );
			System.out.print("Laser off: " + ladoB.isLaserOff());
			
			++line;
			position( ++line, 2 );
			System.out.printf("Medidas:");
			position(line, colA );
			System.out.printf("Pin: %.2f dBm", ladoA.getPin());
			position(line, colB );
			System.out.printf("Pin: %.2f dBm", ladoB.getPin());
			position(++line, colA );
			System.out.printf("Pout: %.2f dBm", ladoA.getPout());
			position(line, colB );
			System.out.printf("Pout: %.2f dBm", ladoB.getPout());
			if (!(Double.isNaN(ladoA.getModuleTemperature()))) {
				position(++line, colA );
				System.out.printf("Temperatura: %.2f °C", ladoA.getModuleTemperature());
			}
			if (!(Double.isNaN(ladoB.getModuleTemperature()))) {
				position(line, colB );
				System.out.printf("Temperatura: %.2f °C ", ladoB.getModuleTemperature());
			}
			if ( ladoA.getLaserTemperature().isEnabled() ){
				position(++line, colA );
				System.out.printf("Temp do Laser: %.2f °C ", ladoA.getLaserTemperature().getTemperature());
			}
			
//			position(++line, colA );
//			System.out.printf("Lambda Real: %.2f", ladoA.getLambdaReal());
			
			position(++line, colA );
			if (ladoA.isDense())
				System.out.printf("Channel: %s (%.2f nm)", ladoA.getChannel(),ladoA.getLambdaNominal());
						
			else {
				System.out.printf("Channel: " + ladoA.getChannel());
			}
			if (dataPlane.hasAmplifierIn() && dataPlane.hasAmplifierOut()){
				position(++line, colA );
				System.out.printf("Ganho IN: %s dB", df1.format(pre.getAGCGain()));
				position(line, colB );
				System.out.printf("Ganho Out: %s dB", df1.format(booster.getAGCGain()));
			}
			position(++line, colA );
			System.out.printf("ODU:");
			position(++line, colA +4);
			System.out.printf("BIP-8:");
			position(++line, colA +8);
			System.out.printf("Total:" + df1.format(odu.getBip8()));
			position(++line, colA +8);
			System.out.printf("Taxa:" + df2.format(odu.getBIP8Rate()));
			position(++line, colA +4);
			System.out.printf("BEI:");
			position(++line, colA +8);
			System.out.printf("Total:" + df1.format(odu.getBei()));
			position(++line, colA +8);
			System.out.printf("Taxa:" + df2.format(odu.getBEIRate()));
			position(++line, colA +4);
			System.out.printf("Status: " + odu.getStatDesc());
			position(++line, colA );
			System.out.printf("FEC:");
			position(++line, colA + 4);
			System.out.printf("Taxa de Bits Corrigidos: " + df2.format(fec.getFixedBitsRate()));
			
			//TranponserOTN tr.getModel 
			Map<String, String> parsed = TransponderModelParser.parse(transponder.getModel());
			String caracteristica = parsed.get("caracteristica");
			if ((caracteristica != null) && (caracteristica.compareTo("T") == 0) ) {
				position(++line, colA );
				System.out.printf("Taxa: 10GbE");
			}
			++line;
			INITIAL_LINE = line;
			totalLineUsed = line;
			POSITION_PROMPT_LINE = line;
			position(POSITION_PROMPT_LINE, INITIAL_COLUMN);
//			txt.writeDataPlane(dataPlane);
		}
		

		
	}
	
	public void close(){
		super.close();
		this.show = false;
		INITIAL_LINE -= totalLineUsed;
		POSITION_PROMPT_LINE = INITIAL_LINE;
		clear();
	}

	
	@Override
	public void run() {

		clear();
	
		while(show){
			
			show();
			//atualiza a cada 5s
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	



}
