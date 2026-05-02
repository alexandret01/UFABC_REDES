package br.ufabc.dataplane;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.Amplifier_Impl;
import br.com.padtec.v3.data.impl.FEC_Impl;
import br.com.padtec.v3.data.impl.ODUk_Impl;
import br.com.padtec.v3.data.impl.OpticalInterface_Impl;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.ODUk;
import br.com.padtec.v3.data.ne.OpticalInterface;
import br.com.padtec.v3.data.ne.PBAmp;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.conf.Configuracao;
import br.ufabc.controlplane.metropad.AmplifierCommands;
import br.ufabc.controlplane.metropad.Servidor;
import br.ufabc.controlplane.metropad.TransponderCommands;


/**
 * This class represents the Data Plane into a Light Path
 * The data plane is controlled by a GMPLS Control Plane
 *  
 * */
public class DataPlane { 
	private static final String CLASS = "DataPlane";
	private int increaseGainValue = 1; 
	int idLSP = -1;
	Servidor server;
	private TrpOTNTerminal transponder;
	private OpticalInterface_Impl oiWDM;
	private ODUk_Impl oduk;
	private FEC_Impl fec;
	private PBAmp amplifierIn;
	private PBAmp amplifierOut;
	private double gainOut;
	private double gainIn;
	private DataPlaneHistory history;
	private List<SerialNumber> lstSerials;
	private boolean downstream;
	private boolean hasAmplifierIn = false;
	private boolean hasAmplifierOut = false;
	private boolean transmiting = false;
	private String name;
	private Map<Integer, Alarm> alarmListWarning;
	private Map<Integer, Alarm> alarmListMinor;
	private Map<Integer, Alarm> alarmListMajor;
	private Map<Integer, Alarm> alarmListCritical;
	private boolean startedMonitor = false;
	private List<TrpOTNTerminal> listOtherTransponders;
	private Configuracao conf;
//	private boolean blocked;
	
	//	private boolean waitForNotifyResponse = false;
	//	private Thread waitForResponse;
	/**
	 * Inicia um objeto DataPlane com os parâmetros
	 * @param idLSP É o identificador do LSP estabelecido pelo plano de controle GMPLS
	 * @param transponder O Transponder que será monitorado
	 * @param downstream indica se o peer é a atua como transmissor (downstream) ou como receptor (upstream)
	 * @param name é o nome do peer
	 * */
	public DataPlane(TrpOTNTerminal transponder, Configuracao conf){
		this.conf = conf;
		this.name = conf.getNomeLocal();
		this.downstream = conf.isDownstream();
		lstSerials = new Vector<SerialNumber>();
		lstSerials.add(transponder.getSerial());
		server = Servidor.getInstance();
		//		this.idLSP = idLSP;
		this.transponder = transponder;
		this.oiWDM = (OpticalInterface_Impl)transponder.getOpticalWDMInterface();
		this.oduk = (ODUk_Impl)transponder.getODUk();
		this.fec = (FEC_Impl)transponder.getFEC();
		this.history = new DataPlaneHistory();
		this.alarmListWarning = new ConcurrentHashMap<Integer, Alarm>();
		this.alarmListMinor = new ConcurrentHashMap<Integer, Alarm>();
		this.alarmListMajor = new ConcurrentHashMap<Integer, Alarm>();
		this.alarmListCritical = new ConcurrentHashMap<Integer, Alarm>();

	}

	/**
	 * Inicia um objeto DataPlane com os parâmetros
	 * @param idLSP É o identificador do LSP estabelecido pelo plano de controle GMPLS
	 * @param transponder O Transponder que será monitorado
	 * @param amplifierIn Amplificador de entrada (Pré-amplificador)
	 * @param amplifierOut Amplificador de saída (Booster)
	 * @param downstream indica se o peer é a atua como transmissor (downstream) ou como receptor (upstream)
	 * @param name é o nome do peer
	 * @param gainValue valor inteiro para configurar o aumento ou decréscimo do ganho 
	 * */
	public DataPlane(TrpOTNTerminal transponder, Configuracao conf , PBAmp amplifierIn, PBAmp amplifierOut) {
		this(transponder, conf);
		lstSerials.add(amplifierIn.getSerial());
		lstSerials.add(amplifierOut.getSerial());
		this.amplifierIn = amplifierIn;
		this.gainIn = amplifierIn.getAGCGain();
		this.amplifierOut = amplifierOut;
		this.gainOut = amplifierOut.getAGCGain();
		this.history = new DataPlaneHistory();
		hasAmplifierIn = true;
		hasAmplifierOut = true;
		this.increaseGainValue = conf.getIncrementGainValue();
	}
	
	public DataPlane(TrpOTNTerminal transponder,  Configuracao conf, PBAmp amplifierIn, PBAmp amplifierOut,  List<TrpOTNTerminal> outrosTransponders) {
		this(transponder, conf , amplifierIn, amplifierOut);
		listOtherTransponders = outrosTransponders;
	}

	
	/**
	 * Retorna a lista com os números seriais dos elementos de rede cadastrados
	 * */
	public List<SerialNumber> getSerialNumberList(){
		return this.lstSerials;
	}

	/**
	 * Verifica se a lista de NEs contém um determinado elemento de rede
	 * @param serial Número de série do elemento de rede
	 * */
	public boolean contains(SerialNumber serial){
		return lstSerials.contains(serial);
	}

	/**
	 * Retorna o identificador do LSP
	 * */
	public int getIdLSP() {
		return idLSP;
	}

	/**
	 * Retorna o Pré-amplificador
	 * */
	public PBAmp getAmplifierIn() {
		return amplifierIn;
	}

	/**
	 * Retorna o amplificador de saída (booster)
	 * */
	public PBAmp getAmplifierOut() {
		return amplifierOut;
	}

	/**
	 * Configura manualmente um valor de ganho no amplificador
	 * @param amplifier Amplificador de referencia
	 * @param gain valor de ganho a ser configurado
	 * */
	public boolean setGain(PBAmp amplifier, double gain){
		boolean result = false;
		
		if (amplifier!= null){
			if (amplifier.equals(amplifierIn)){
				if (this.amplifierIn.getAGCGain() == gain)
					return false;
				if ( gain < history.getAcceptableMaxGainIn() && gain > history.getAcceptableMinGainIn() ){
					log(CLASS, Level.INFO, "Alterando ganho da NE"+ amplifier.getSerial().toShortString() +
							"DE:" + gainIn + " PARA: " + gain + " em: "+ 
							(System.currentTimeMillis()-ControlPlane.START_TIME)/1000);
					this.gainIn = gain;
					Command cmd = AmplifierCommands.getCommandSetAGCGain(amplifier.getSerial(), gain);
					server.doCommand(cmd, amplifier.getSupAddress());
					result = true;
				} else {
					log(CLASS, Level.INFO, "Não foi possível alterar ganho da NE"+ amplifier.getSerial().toShortString() +
							"DE:" + gainIn + " PARA: " + gain + ", \nGANHO CONFIGURADO FORA DO RANGE!");
				}
			}
			
			if (amplifier.equals(amplifierOut)){
				if (this.amplifierOut.getAGCGain() == gain)
					return false;
				if ( gain < history.getAcceptableMaxGainOut() && gain > history.getAcceptableMinGainOut() ){
					log(CLASS, Level.INFO, "Alterando ganho da NE"+ amplifier.getSerial().toShortString() +
							"DE:" + gainOut + " PARA: " + gain);
					this.gainOut = gain;
					Command cmd = AmplifierCommands.getCommandSetAGCGain(amplifier.getSerial(), gain);
					server.doCommand(cmd, amplifier.getSupAddress());
					result = true;
				} else {
					log(CLASS, Level.INFO, "Não foi possível alterar ganho da NE"+ amplifier.getSerial().toShortString() +
							"DE:" + gainOut + " PARA: " + gain + ", \nGANHO CONFIGURADO FORA DO RANGE!");
				}
			}
			
		}
		
		return result;
	}

	/**
	 * Retorna o transponder
	 * */
	public TrpOTNTerminal getTransponder() {
		return transponder;
	}

	/**
	 * Configura um transponder
	 * @param transponder
	 */
	public void setTransponder(TrpOTNTerminal transponder) {
		this.transponder = transponder;
	}
	/****
	 * Retorna a interface ótica WDM do transponder
	 * @return OpticalInterface
	 */
	public OpticalInterface getOiWDM() {
		return oiWDM;
	}
	/**
	 * Configura uma interface ótica de saída
	 * @param oiWDM Interface ótica de saída
	 */
	public void setOiWDM(OpticalInterface_Impl oiWDM) {
		this.oiWDM = oiWDM;
	}

	/**
	 * retorna o ODUk (optical data unit)
	 * */
	public ODUk getOduk() {
		return oduk;
	}


	/**
	 * Configura um ODUk
	 * @param oduk
	 */
	public void setOduk(ODUk_Impl oduk) {
		this.oduk = oduk;
	}

	/**
	 * Retorna a FEC (Foward Error Corrector)
	 * @return
	 */
	public FEC getFec() {
		return fec;
	}

	/**
	 * Configura um objeto FEC (Forward Error Corrector)
	 * @param fec
	 */
	public void setFec(FEC_Impl fec) {
		this.fec = fec;
	}

	/**
	 * Retorna o canal WDM configurado
	 * */
	public String getChannel() {
		return oiWDM.getChannel();
	}

	/**
	 * Configura um novo canal WDM
	 * @param channel Canal WDM
	 * */
	public void setChannel(String channel) {
		this.oiWDM.setChannel(channel);
	}

	/**
	 * Retorna o comprimento de onda configurado
	 * @return
	 */
	public double getLambda() {
		return oiWDM.getLambdaNominal();
	}

	/**
	 * Configura um novo comprimento de onda
	 * @param lambda Comprimento de onda
	 */
	public void setLambda(double lambda) {
		this.oiWDM.setLambdaNominal(lambda);
	}

	/**
	 * Retorna uma String com o canal da Banda C a partir de um inteiro
	 * */
	public String getChannelCBand(int valueLabel){
		//		Servidor server = Servidor.getInstance();
		//		Command cmd = null;
		StringBuilder channel = new StringBuilder();
		channel.append("C");
		int value = valueLabel;
		DecimalFormat format = new DecimalFormat("00");
		channel.append(format.format(value));

		return channel.toString();
		
	}

	/**
	 * Liga o laser WDM
	 * */
	public void turnOnLaserWdm(){
		ArrayList<Command> commands = new ArrayList<Command>();
		if (oiWDM.isLaserOff()){
			Command cmd = TransponderCommands.getCommandTurnOnLaserWDM(transponder.getSerial());
			commands.add(cmd);

		}
		Command cmd2 = TransponderCommands.getCommandResetErrorRateCounters(transponder.getSerial());
		commands.add(cmd2);
		Servidor.getInstance().doCommand(commands, transponder.getSupAddress());
	}
	/**
	 * Desliga o laser WDM
	 */
	public void turnOFFLaserWdm(){
		ArrayList<Command> commands = new ArrayList<Command>();
		if (!oiWDM.isLaserOff()){
			Command cmd = TransponderCommands.getCommandTurnOffLaserWDM(transponder.getSerial());
			commands.add(cmd);

		}
		Servidor.getInstance().doCommand(commands, transponder.getSupAddress());
	}


	/**
	 * Increases the gain value in Booster Amplifier
	 * */
	public boolean increaseGainOut() {
		double oldGain = gainOut;
		boolean result = false;
		if (hasAmplifierOut){
			ArrayList<Command> commands = new ArrayList<Command>();
			if ( this.gainOut < history.getAcceptableMaxGainOut() ){
				if (gainOut == history.getAcceptableMaxGainOut() -2 || gainOut == history.getAcceptableMaxGainOut() - 1){
					gainOut++;
				} else {
					this.gainOut+=increaseGainValue;
				}
				((Amplifier_Impl)amplifierOut).setAGCGain(gainOut);
				Command cmd = AmplifierCommands.getCommandSetAGCGain(amplifierOut.getSerial(), gainOut);
				commands.add(cmd);
				result = true;
				log(CLASS, Level.INFO, "Aumentando ganho do booster, DE:" + oldGain + " PARA: " + gainOut + ", em: " +
						(System.currentTimeMillis()-ControlPlane.START_TIME)/1000); 
			}

			Servidor.getInstance().doCommand(commands, amplifierOut.getSupAddress());
			reserCountersFec();
		}
		
		return result;
	}

	/**
	 * Decreases the gain value in Booster Amplifier 
	 * */
	public boolean decreaseGainOut(){
		double oldGain = gainOut;
		boolean result = false;
		if (hasAmplifierOut){
			ArrayList<Command> commands = new ArrayList<Command>();
			if ( this.gainOut > history.getAcceptableMinGainOut()){
				if (gainOut == history.getAcceptableMinGainOut() + 2 || gainOut == history.getAcceptableMinGainOut() + 1){
					gainOut--;
				} else {
					this.gainOut-=increaseGainValue;
				}
				((Amplifier_Impl)amplifierOut).setAGCGain(gainOut);
				Command cmd = AmplifierCommands.getCommandSetAGCGain(amplifierOut.getSerial(), gainOut);
				commands.add(cmd);
				result = true;
			} 
			log(CLASS, Level.INFO, "Diminuindo ganho do booster, DE:" + oldGain + " PARA: " + gainOut+ ", em: " +
					(System.currentTimeMillis()-ControlPlane.START_TIME)/1000);
			Servidor.getInstance().doCommand(commands, amplifierOut.getSupAddress());
			reserCountersFec();
		}
		
		return result;
	}

	/**
	 * Increases the gain value in Pre-Amplifier
	 * */
	public boolean increaseGainIn() {
		double oldGain = gainIn;
		boolean result = false;
		if (hasAmplifierIn){
			ArrayList<Command> commands = new ArrayList<Command>();
			if ( this.gainIn < history.getAcceptableMaxGainIn() ){
				System.out.println("aumentando o ganho");
				System.out.println("ganho in atual = " + gainIn);
				if (gainIn == history.getAcceptableMaxGainIn() - 2 || gainIn == history.getAcceptableMaxGainIn() -1 ){
					this.gainIn++;
				}else{ 
					this.gainIn+=increaseGainValue;
				}
				result = true;
				//			((Amplifier_Impl)amplifierIn).setAGCGain(gainIn);
				Command cmd = AmplifierCommands.getCommandSetAGCGain(amplifierIn.getSerial(), gainIn);
				commands.add(cmd);
				log(CLASS, Level.INFO, "Aumentando ganho do pré-amplificador, DE:" + oldGain + " PARA: " + gainOut+ ", em: " +
						(System.currentTimeMillis()-ControlPlane.START_TIME)/1000);
				Servidor.getInstance().doCommand(commands, amplifierIn.getSupAddress());
				reserCountersFec();
			
			}

		}
		return result;
	}

	/**
	 * Decreases the gain value in Pre-Amplifier 
	 * @throws InterruptedException 
	 * */
	public boolean decreaseGainIn() {
		double oldGain = gainIn;
		boolean result = false;
		if (hasAmplifierIn){
			ArrayList<Command> commands = new ArrayList<Command>();
			if ( this.gainIn > history.getAcceptableMinGainIn()){
				if (gainIn == history.getAcceptableMinGainIn() + 2 || gainIn == history.getAcceptableMinGainIn() + 1){
					gainIn--;
				} else {
					this.gainIn-=increaseGainValue;
				}
				result = true;
				
				Command cmd = AmplifierCommands.getCommandSetAGCGain(amplifierIn.getSerial(), gainIn);
				commands.add(cmd);
				log(CLASS, Level.INFO, "Diminuindo ganho do pré-amplificador, DE:" + oldGain + " PARA: " + gainOut+ ", em: " +
						(System.currentTimeMillis()-ControlPlane.START_TIME)/1000);
				Servidor.getInstance().doCommand(commands, amplifierIn.getSupAddress());
				reserCountersFec();
			
			}
			
			

		}
		
		return result;

	}
	
	public boolean checkOtherChannelsAlarms(){
		if (listOtherTransponders != null){
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (Iterator<TrpOTNTerminal> it = listOtherTransponders.iterator(); it.hasNext(); ){
				TrpOTNTerminal t = it.next();
				if (t.getOTN_WDMInterface().isLof()){
					return true;
				} if (t.isLos()){
					return true;
				}
				
			}
		}
		
		return false;
		
	}
	

	/**
	 * Updates the gain values in Pre-Amplifier and Booster Amplifier 
	 * from their objects
	 * */
	public void updateGains(){
		if (hasAmplifierIn && hasAmplifierOut){
			this.gainIn = amplifierIn.getAGCGain();
			this.gainOut = amplifierOut.getAGCGain();
		}
	}
	/**
	 * Starts the transmission and initiates parameters 
	 * @throws DataPlaneException 
	 * */
	public void startTransmission(String channel, boolean isNew) throws DataPlaneException{
		boolean sentCmd = false;
		long now = System.currentTimeMillis() - ControlPlane.START_TIME;
		ArrayList<Command> commands = new ArrayList<Command>();
		if (!getChannel().equals(channel.toString())){
			setChannel(channel.toString());

			ControlPlane.getInstance().log(CLASS, Level.INFO, "Enviando comando modificar canal aos " + now/1000 + " s");
			Command cmd = TransponderCommands.configureLambdaWDM(getTransponder().getSerial(), channel.toString());
			if (cmd != null){
				sentCmd = true;
				commands.add(cmd);
			}

		}
		if (transponder.getOpticalWDMInterface().isLaserOff()){
			ControlPlane.getInstance().log(CLASS, Level.INFO, "Enviando comando ligar Laser WDM aos " + now/1000 + " s");
			Command cmd1 = TransponderCommands.getCommandTurnOnLaserWDM(transponder.getSerial());
			sentCmd = true;
			commands.add(cmd1);
//configurar a interface do oxc
		} 

		server.doCommand(commands, getTransponder().getSupAddress());
		if (isNew){
			if (downstream)
				ControlPlane.getInstance().log(CLASS, Level.INFO, "Iniciando a Transmissão Downstream em: " + (now/1000 ) + " s");
			else 
				ControlPlane.getInstance().log(CLASS, Level.INFO, "Iniciando a Transmissão Upstream em: " + (now/1000 ) + " s");
			setTransmiting(true);
			history.setInitTime(now);
		}
		if (!isNew && sentCmd){
			ControlPlane.getInstance().log(CLASS, Level.INFO, "Transmissão atualizada em : " + (now/1000 ) + " s");
		}


	}

	/**
	 * Reinicia os contadores de erro
	 * */
	public void reserCountersFec(){
		long now = System.currentTimeMillis() - ControlPlane.START_TIME;
		log(CLASS, Level.INFO, "Resetando contadores" + "em :" + now/1000 +" s" );
		Command cmd2 = TransponderCommands.getCommandResetErrorRateCounters(transponder.getSerial());
		server.doCommand(cmd2, transponder.getSupAddress());
	}

	/**
	 * Encerra a transmissão e desliga o laser WDM
	 * */
	public void stopTransmission() {
		idLSP = -1;
		long now = (System.currentTimeMillis() - ControlPlane.START_TIME)/1000;
		ControlPlane.getInstance().log(CLASS, Level.INFO, "Transmissão finalizada em : " + now + " s");
		setTransmiting(false);
		Command cmd = TransponderCommands.getCommandTurnOffLaserWDM(transponder.getSerial());
		server.doCommand(cmd, getTransponder().getSupAddress());
		history.setEndTime(System.currentTimeMillis());	
	}
	
	public Map<Integer, Alarm> getAlarmListCritical() {
		return alarmListCritical;
	}
	
	public void enableFecRX(){
		long now = (System.currentTimeMillis() - ControlPlane.START_TIME)/1000;
		ControlPlane.getInstance().log(CLASS, Level.INFO, "Ligando FEC RX, em : " + now + " s");
		Command cmd = TransponderCommands.enableFecReceptor(transponder.getSerial());
		server.doCommand(cmd, getTransponder().getSupAddress());
	}
	
	public void enableFecTX(){
		long now = (System.currentTimeMillis() - ControlPlane.START_TIME)/1000;
		ControlPlane.getInstance().log(CLASS, Level.INFO, "Ligando FEC TX, em : " + now + " s");
		Command cmd = TransponderCommands.enableFecTransmissor(transponder.getSerial());
		server.doCommand(cmd, getTransponder().getSupAddress());
	}
	
	public void disableFecRX(){
		long now = (System.currentTimeMillis() - ControlPlane.START_TIME)/1000;
		ControlPlane.getInstance().log(CLASS, Level.INFO, "Desligando FEC RX, em : " + now + " s");
		Command cmd = TransponderCommands.disableFecReceptor(transponder.getSerial());
		server.doCommand(cmd, getTransponder().getSupAddress());
	}
	
	public void disableFecTX(){
		long now = (System.currentTimeMillis() - ControlPlane.START_TIME)/1000;
		ControlPlane.getInstance().log(CLASS, Level.INFO, "Desligando FEC TX, em : " + now + " s");
		Command cmd = TransponderCommands.disableFecTransmissor(transponder.getSerial());
		server.doCommand(cmd, getTransponder().getSupAddress());
	}

	/**
	 * Analisa os alarmes recebidos do transponder
	 * */
	public void analizeAlarm(Alarm a) {
			
		if (a.getPriority() == 60){
			alarmListCritical.put(a.getAlType(), a);
		} else if (a.getPriority() == 50) {
			alarmListMajor.put(a.getAlType(), a);
		} else if (a.getPriority() == 40) {
			alarmListMinor.put(a.getAlType(), a);
		} else if (a.getPriority() == 30) {
			alarmListWarning.put(a.getAlType(), a);
		}
		
//		if (monitor == null){
//			monitor = new DataPlaneMonitorThread(this);
//		}


	}

	/**
	 * if true indicates that this optical WDM interface is a downstream connection
	 * 
	 * */
	public boolean isDownstream() {
		return downstream;
	}


	public void setDownstream(boolean downstream) {
		this.downstream = downstream;
	}

	class TryTurnOn{
		protected boolean tryTurnOnLaserWdm = false;
		protected long timeToWait = 10000;
		protected long time = 0;
	}

	/**
	 * @return the hasAmplifierIn
	 */
	public boolean hasAmplifierIn() {
		return hasAmplifierIn;
	}


	/**
	 * @param hasAmplifierIn the hasAmplifierIn to set
	 */
	public void setHasAmplifierIn(boolean hasAmplifierIn) {
		this.hasAmplifierIn = hasAmplifierIn;
	}


	/**
	 * @return the hasAmplifierOut
	 */
	public boolean hasAmplifierOut() {
		return hasAmplifierOut;
	}


	/**
	 * @param hasAmplifierOut the hasAmplifierOut to set
	 */
	public void setHasAmplifierOut(boolean hasAmplifierOut) {
		this.hasAmplifierOut = hasAmplifierOut;
	}


	/**
	 * @return the transmiting
	 */
	public boolean isTransmiting() {
		return transmiting;
	}


	/**
	 * @param transmiting the transmiting to set
	 */
	public void setTransmiting(boolean transmiting) {
		this.transmiting = transmiting;
	}

	public void log(String CLASS, Level level, String message){
		ControlPlane.getInstance().log(CLASS, level, message);
	}

	/**
	 * @param idLSP the idLSP to set
	 */
	public void setIdLSP(int idLSP) {
		this.idLSP = idLSP;
	}

	/**
	 * @return the alarmListWarning
	 */
	public Map<Integer, Alarm> getAlarmListWarning() {
		return alarmListWarning;
	}

	/**
	 * @return the alarmListMinor
	 */
	public Map<Integer, Alarm> getAlarmListMinor() {
		return alarmListMinor;
	}

	/**
	 * @return the alarmListMajor
	 */
	public Map<Integer, Alarm> getAlarmListMajor() {
		return alarmListMajor;
	}


	/**
	 * @return the history
	 */
	public DataPlaneHistory getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(DataPlaneHistory history) {
		this.history = history;
	}
	
	public boolean isGainOutIntoLimits(){
		if (gainOut > history.getAcceptableMinGainOut() && gainOut < history.getAcceptableMaxGainOut()) {
			return true;
		}
		
		return false;
	}

	/**
	 * @return the conf
	 */
	public Configuracao getConf() {
		return conf;
	}

	/**
	 * @param conf the conf to set
	 */
	public void setConf(Configuracao conf) {
		this.conf = conf;
	}

	

	/**
	 * @return the blocked
	 *//*
	public boolean isBlocked() {
		return blocked;
	}

	*//**
	 * @param blocked the blocked to set
	 *//*
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
*/

}

