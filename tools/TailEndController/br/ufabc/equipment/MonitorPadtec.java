package br.ufabc.equipment;
/*
 * Created on 10/04/2012.
 */

import java.math.BigInteger;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.impl.FEC_Impl;
import br.com.padtec.v3.data.impl.ODUk_Impl;
import br.com.padtec.v3.data.impl.OpticalInterface_Impl;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;

/**
 * @author Gustavo Sousa Pavani
 * @version 1.0
 */
public class MonitorPadtec extends Thread {
	TrpOTNTerminal transponder;
	Supervisor supervisor;
	//Servidor server;

	public MonitorPadtec(Supervisor supervisor, TrpOTNTerminal transponder) {
		this.transponder = transponder;
		this.supervisor = supervisor;
	}
	
/*	public MonitorPadtec(Servidor server, TrpOTNTerminal transponder) {
		this.transponder = transponder;
		this.server = server;
	}
*/
	public void run() {
		try {
			// Reseta contadores
			//System.out.println("Serial transponder: "+transponder.getSerial());
			//System.out.println("Address transponder:"+transponder.getSupAddress());
			Command cmd2 = TransponderCommands.getCommandResetErrorRateCounters(transponder.getSerial());
			supervisor.doCommand(cmd2, transponder.getSupAddress());
			sleep(10000);
			while (true) {
				if (transponder == null)
					System.out.println("Erro de inicializacao do transponder");
				// Obtem informacoes do transponder
				OpticalInterface_Impl oiWDM = (OpticalInterface_Impl) transponder.getOpticalWDMInterface();
				ODUk_Impl oduk = (ODUk_Impl) transponder.getODUk();
				FEC_Impl fec = (FEC_Impl) transponder.getFEC();// FEC Código
																// corretor de
																// erro
				// Obtem informacoes de bip-8 e potencia
				BigInteger bip8 = oduk.getBip8();
				// if (bip8.compareTo(BigInteger.ZERO) > 0){
				System.out.println("Bip-8: " + oduk.getBip8().toString());
				System.out.println("BEI:" + oduk.getBei().toString());
				System.out.println("Pin: " + oiWDM.getPin() + " dBm");
				System.out.println("Frames OTN: " + oduk.getFramesOTN().toString());
				System.out.println("FEC:"+fec.toString());
				System.out.println(fec.toString());
				// System.out.println("Fec type: "+fec.getFECName());
				// System.out.println("FEC rate: "+fec.doubleValue());
				// System.out.println("Errored blocks: "+fec.getErroredBlocks().toString());
				System.out.println("Pout: " + oiWDM.getPout() + " dBm");
				// Command cmd2 =
				// TransponderCommands.getCommandResetErrorRateCounters(transponder.getSerial());
				// supervisor.doCommand(cmd2, transponder.getSupAddress());
				// }
				sleep(10000); // tempo até iniciar o monitoramento no plano de
								// dados
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
