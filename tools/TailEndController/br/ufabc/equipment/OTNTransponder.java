package br.ufabc.equipment;

import java.math.BigInteger;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.impl.FEC_Impl;
import br.com.padtec.v3.data.impl.ODUk_Impl;
import br.com.padtec.v3.data.impl.OpticalInterface_Impl;
import br.com.padtec.v3.data.ne.FEC.FEC_Type;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.util.log.Log;

public class OTNTransponder extends Transponders {
	/** The OTN terminal. */
	protected TrpOTNTerminal terminal; 
	/** Optical interface. */
	protected OpticalInterface_Impl oiWDM;
	/** ODU-k layer. */
	protected ODUk_Impl oduk;
	/** FEC implementation. */
	protected FEC_Impl fec;
	/** Client interface. */
	protected OpticalInterface_Impl client;
	/** Logger. */
	private static Logger log = Log.getInstance();
	
	/**
	 * Creates a new OTNTransponder object.
	 * @param sup The supervisor.
	 * @param transp The transponder.
	 */
	public OTNTransponder(Supervisor sup, Transponder transp)  {
		super(sup,transp);
		//Get OTN terminal
		terminal = (TrpOTNTerminal) transponder;
		//Get optical interface
		oiWDM = (OpticalInterface_Impl) terminal.getOpticalWDMInterface();
		//Get ODUk payload
		oduk = (ODUk_Impl) terminal.getODUk();
		//GET FEC
		fec = (FEC_Impl) terminal.getFEC();// FEC Código
		//Get client interface
		client = (OpticalInterface_Impl) terminal.getOpticalClientInterface();
	}
	
	/**
	 * Reset error rate counters, including FEC, at the transponder.
	 */
	public void resetCounters() {
		log.info("Reseting counters of: "+transponder.getName());
		Command cmd = TransponderCommands.getCommandResetErrorRateCounters(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());
	}
	
	/**
	 * Returns the WDM channel.
	 * @return The WDM channel.
	 */
	public String getChannel() {
		return oiWDM.getChannel();
	}

	/**
	 * Set up WDM channel. NOT WORKING - PPMv3 packet with no parameters.
	 * @param channel The new WDM channel.
	 * */
	public void setChannel(String channel) {
		log.info("Setting channel of: "+transponder.getName()+" to "+channel);
		try {
			Command cmd = TransponderCommands.configureLambdaWDM(transponder.getSerial(),channel);
			//System.out.println("Command: "+cmd.toString());
			supervisor.doCommand(cmd, transponder.getSupAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the wavelength frequency (in nm).
	 * @return The wavelength frequency (in nm).
	 */
	public double getLambda() {
		return oiWDM.getLambdaNominal();
	}

	/**
	 * Returns the input power at the WDM channel.
	 * @return The input power at the WDM channel.
	 */
	public double getInputPowerWDM() {
		return oiWDM.getPin();
	}
	
	/**
	 * Returns the output power at the WDM channel.
	 * @return The output power at the WDM channel.
	 */
	public double getOutputPowerWDM() {
		return oiWDM.getPout();
	}
	
	/**
	 * Returns true if the WDM channel is in LOS state. False, otherwise.
	 * @return True if the WDM channel is in LOS state. False, otherwise.
	 */
	public boolean isLOS() {
		return oiWDM.isLos();
	}
	
	/**
	 * Returns true if the WDM channel is in LOF state. False, otherwise.
	 * @return True if the WDM channel is in LOF state. False, otherwise.
	 */
	public boolean isLOF() {
		return terminal.getOTN_WDMInterface().isLof();
	}
	
	/**
	 * Returns true if the WDM laser is OFF. False, otherwise.
	 * @return True if the WDM laser is OFF. False, otherwise.
	 */
	public boolean isOff() {
		return oiWDM.isLaserOff();
	}
	
	/**
	 * Return the BIP-8 information.
	 * @return The BIP-8 information..
	 */
	public BigInteger getBIP8() {
		return oduk.getBip8();
	}
	
	/**
	 * Return the BIP-8 rate information.
	 * @return The BIP-8 rate information..
	 */	
	public double getBIP8Rate() {
		return oduk.getBIP8Rate();
	}
	
	/**
	 * Return the BEI information.
	 * @return The BEI information..
	 */
	public BigInteger getBEI() {
		return oduk.getBei();
	}

	/**
	 * Return the BEI rate information.
	 * @return The BEI rate information..
	 */
	public double getBEIRate() {
		return oduk.getBEIRate();
	}
	
	/**
	 * Returns the number of OTN frames so far.
	 * @return the number of OTN frames so far.
	 */
	public BigInteger getFramesOTN() {
		return oduk.getFramesOTN();
	}
	
	/**
	 * Returns true, if backward defect indication (BDI) is ON. False, otherwise.
	 * @return True, if backward defect indication (BDI) is ON. False, otherwise.
	 */
	public boolean isBDI() {
		return oduk.isBdi();
	}
	
	/**
	 * Returns true if FEC is enable at the reception.
	 * @return True if FEC is enable at the reception.
	 */
	public boolean isFECReceptionEnabled() {
		return fec.isFecRxCorrEnabled();
	}
	
	/**
	 * Returns true if FEC is enable at the transmission.
	 * @return True if FEC is enable at the transmission.
	 */
	public boolean isFECTransmissionEnabled() {
		return fec.isFecTxCorrEnabled();
	}
	
	/**
	 * Returns the name of the FEC mechanism.
	 * @return The name of the FEC mechanism.
	 */
	public String getFECName() {
		return fec.getFECName();
	}
	
	/**
	 * Returns the FEC Type. 
	 * @return The FEC Type.
	 */
	public FEC_Type getFECType() {
		return fec.getFecType();
	}
	
	/**
	 * Returns the number of FEC errors.
	 * @return The number of FEC errors.
	 */
	public long getFECErrors() {
		return fec.longValue();
	}
	
	/**
	 * Returns the rate for fixed bits.
	 * @return The rate for fixed bits
	 */
	public Double getFECRate() {
		return fec.getFixedBitsRate();
	}
	
	/**
	 * Returns the number of blocks with error.
	 * @return The number of blocks with error.
	 */
	public BigInteger getErroredBlocks() {
		return fec.getErroredBlocks();
	}
	
	/**
	 * Returns the output power at the client interface.
	 * @return The output power at the client interface.
	 */
	public double getOutputPowerClient() {
		return client.getPout();
	}
	
	/**
	 * Returns the input power at the client interface.
	 * @return The input power at the client interface.
	 */
	public double getInputPowerClient() {
		return client.getPin();
	}

	/**
	 * Return the wavelength frequency at the client.
	 * @return The wavelength frequency at the client.
	 */	
	public double getClientLambda() {
		return client.getLambdaNominal();
	}

	/**
	 * Returns true if the Client interface is in LOS state. False, otherwise.
	 * @return True if the Client interface is in LOS state. False, otherwise.
	 */
	public boolean isClientLOS() {
		return client.isLos();
	}
	
	/**
	 * Returns true if the Client interface is in LOF state. False, otherwise.
	 * @return True if the Client interface is in LOF state. False, otherwise.
	 */
	public boolean isClientLOF() {
		return terminal.getClientInterface().isLof();
	}
	
	/**
	 * Returns true if the Client laser is OFF. False, otherwise.
	 * @return True if the Client laser is OFF. False, otherwise.
	 */
	public boolean isClientOff() {
		return client.isLaserOff();
	}

		
	/**
	 * Turns on WDM laser.
	 */
	public void turnOnWDMLaser() {
		log.info("Turning on WDM laser of: "+transponder.getName());
		Command cmd = TransponderCommands.getCommandTurnOnLaserWDM(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());
	}
	
	/**
	 * Turns off WDM laser.
	 */
	public void turnOffWDMLaser() {
		log.info("Turning off WDM laser of: "+transponder.getName());
		Command cmd = TransponderCommands.getCommandTurnOffLaserWDM(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());
	}

	/**
	 * Turns on Client laser.
	 */
	public void turnOnClientLaser() {
		log.info("Turning on client laser of: "+transponder.getName());
		Command cmd = TransponderCommands.getCommandTurnOnLaserClient(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());
	}
	
	/**
	 * Turns off Client laser.
	 */
	public void turnOffClientLaser() {
		log.info("Turning off client laser of: "+transponder.getName());
		Command cmd = TransponderCommands.getCommandTurnOffLaserClient(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());
	}
	
	/**
	 * Enables FEC at the reception.
	 */
	public void enableFECReception() {
		log.info("Enabling FEC at reception of: "+transponder.getName());		
		Command cmd = TransponderCommands.enableFecReceptor(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());
	}
	
	/**
	 * Enables FEC at the transmission.
	 */
	public void enableFECTransmission() {
		log.info("Enabling FEC at transmission of: "+transponder.getName());		
		Command cmd = TransponderCommands.enableFecTransmissor(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());
	}
	
	/**
	 * Disables FEC at the reception.
	 */
	public void disableFECReception() {
		log.info("Disabling FEC at reception of: "+transponder.getName());		
		Command cmd = TransponderCommands.disableFecReceptor(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());		
	}
	
	/**
	 * Disables FEC at the transmission.
	 */
	public void disableFECTransmission() {
		log.info("Disabling FEC at transmission of: "+transponder.getName());		
		Command cmd = TransponderCommands.disableFecTransmissor(transponder.getSerial());
		supervisor.doCommand(cmd, transponder.getSupAddress());
	}
}
