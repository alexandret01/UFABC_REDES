package br.ufabc.controlplane;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.savarese.rocksaw.net.RawSocket;

import util.ByteOperation;
import br.ufabc.controlplane.conf.Configuracao;
import br.ufabc.controlplane.metropad.TransponderCommands;
import br.ufabc.controlplane.net.NetworkAddressOperation;
import br.ufabc.controlplane.net.NetworkAddressOperation.TypeIP;
import br.ufabc.controlplane.rsvp.error.EnumError;
import br.ufabc.controlplane.rsvp.error.RSVPObjectValidator;
import br.ufabc.controlplane.rsvp.factory.NotifyMessageFactory;
import br.ufabc.controlplane.rsvp.factory.PathErrMessageFactory;
import br.ufabc.controlplane.rsvp.factory.PathMessageFactory;
import br.ufabc.controlplane.rsvp.factory.ResvConfMessageFactory;
import br.ufabc.controlplane.rsvp.factory.ResvErrMessageFactory;
import br.ufabc.controlplane.rsvp.factory.ResvMessageFactory;
import br.ufabc.controlplane.rsvp.factory.TearMessageFactory;
import br.ufabc.controlplane.rsvp.state.PathState;
import br.ufabc.controlplane.rsvp.state.PathStateSender;
import br.ufabc.controlplane.rsvp.state.ResvState;
import br.ufabc.controlplane.rsvp.state.State;
import br.ufabc.controlplane.testes.ExecuteTests;
import br.ufabc.controlplane.util.log.LogTXT;
import br.ufabc.dataplane.DataPlane;
import br.ufabc.dataplane.DataPlaneEvent;
import br.ufabc.dataplane.DataPlaneMonitorThread;
import br.ufabc.dataplane.alarms.AlarmGmpls;
import br.ufabc.dataplane.alarms.RsvpNotifyIndication;
import event.Event;
import event.EventListener;
import gmpls.common.PCAP;
import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.Association;
import gmpls.signaling.object.ErrorSpec;
import gmpls.signaling.object.ExplicitRoute;
import gmpls.signaling.object.FlowSpec;
import gmpls.signaling.object.Label;
import gmpls.signaling.object.LabelSet;
import gmpls.signaling.object.NotifyRequest;
import gmpls.signaling.object.Protection;
import gmpls.signaling.object.RSVPHop;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.RecordRoute;
import gmpls.signaling.object.ResvConfirm;
import gmpls.signaling.object.SenderTemplate;
import gmpls.signaling.object.Session;
import gmpls.signaling.object.TimeValues;
import gmpls.signaling.object.error.IPv4ErrorSpec;
import gmpls.signaling.object.error.IPv6ErrorSpec;
import gmpls.signaling.object.explicitroute.ExplicitRouteSubObject;
import gmpls.signaling.object.explicitroute.IPv4ExplicitRoute;
import gmpls.signaling.object.explicitroute.IPv6ExplicitRoute;
import gmpls.signaling.object.label.RSVPLabel;
import gmpls.signaling.object.label.SuggestedLabel;
import gmpls.signaling.object.label.UpstreamLabel;
import gmpls.signaling.object.labelrequest.GeneralizedLabelRequest;
import gmpls.signaling.object.recordroute.IPv4RecordRoute;
import gmpls.signaling.object.sendertemplate.LSPTunnelIPv4SenderTemplate;
import gmpls.signaling.object.session.LSPTunnelIPv4Session;
import gmpls.signaling.object.session.LSPTunnelIPv6Session;

/**
 * This class provide the functions of control about GMPLS RSVP protocol
 * */
public class ControlPlane implements EventListener {

	private String name;
	private static int IDLSPCOUNT = 0;
	private static ControlPlane controller;

	private PCAP pcap;
	private HashMap<Integer, PathState> pathStatesTable;
	private HashMap<Integer, ResvState> resvStatesTable;
	private RawSocket socket;
	private InetAddress localAddress;
	private Vector<Integer> supportedLabels;
	private RSVPObjectValidator validator;
	private HashMap<Integer, SuggestedLabel> labelsSuggested;
	private HashMap<Integer, InetAddress> outgoingLabelToOutgoingInterfaceTable;
	private HashMap<Integer, InetAddress> incomingLabelToIncomingInterfaceTable;
	
	/* Table of supported channel in transponder. */
	private Map<String, Double> transponderTable = TransponderCommands.getChannelList(); 
	private Logger logger;
	private FileHandler fh;
	private DataPlane dataPlane;
	/**Guarda o idLSP do lightPath e o tempo configurado para a enviar a mensagem RESV*/
	private HashMap<Integer, Long> timerToSendResv;
	protected static LogTXT txt;
	private boolean runTest;
	private Thread executeTests;
	public static long START_TIME = System.currentTimeMillis();
	public static boolean SENT_NOTIFY = false;
	private DataPlaneMonitorThread monitor;
	public static int delayToRefresh = 20000;
	private int numberOfChannels = 1;
	public Configuracao conf;
	/*usedLabels<label, idLSP>*/
	private HashMap<Integer, Integer> usedLabels;


	/**seconds to receive a update message*/
	public int timeout;

	public ControlPlane() {
		try {
			this.localAddress = NetworkAddressOperation.getLocalAddress(TypeIP.IPv4);
			//			this.localAddress = InetAddress.getLocalHost();
			this.validator = new RSVPObjectValidator();
			this.logger = Logger.getLogger("GMPLSLog");
			this.fh = new FileHandler("GMPLSFile.log", false);
			// This block configure the logger with handler and formatter
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
			this.fh.setFormatter(new SimpleFormatter());
			this.pathStatesTable = new HashMap<Integer, PathState>();
			this.resvStatesTable = new HashMap<Integer, ResvState>();
			this.supportedLabels = new Vector<Integer>();
			this.outgoingLabelToOutgoingInterfaceTable = new HashMap<Integer, InetAddress>();
			this.incomingLabelToIncomingInterfaceTable = new HashMap<Integer, InetAddress>();
			this.timerToSendResv = new HashMap<Integer, Long>();
			usedLabels = new HashMap<Integer, Integer>();
			loadSupportedLabels();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all channels supported by transponder 10G sintonizable
	 * But, only channels of Band C
	 * */
	private void loadSupportedLabels(){
		for (String s : transponderTable.keySet()){
			if(s.charAt(0) == 'C'){
				supportedLabels.add(Integer.parseInt(s.substring(1, 3)));
			}
		}
	}




	/** 
	 * Gets the IP packet that arrived  
	 * */
	public void getNotification(Event event) {
		//		System.out.println(event.toString());

		try {

			byte[] data = event.getData();
			RSVPPacket packet = new RSVPPacket(data.length);
			packet.setData(data);
			long now = System.currentTimeMillis()-START_TIME;
			switch ( packet.getMessageType() ) {
			case RSVPPacket.TYPE_PATH:

				this.logger.log(Level.INFO,"RSVP Message Type: PATH, from: " + packet.getSourceAsInetAddress().getHostAddress()+ ", in "+ now/1000 + " s" ) ;
				//				System.out.println("Mensagem PATH recebida!");
				handlingPathMessage( packet) ;
				break;

			case RSVPPacket.TYPE_RESV:
				this.logger.log(Level.INFO,"RSVP Message Type: RESV, from: " + packet.getSourceAsInetAddress().getHostAddress() + ", in "+ now/1000 + " s" );
				//				System.out.println("Mensagem RESV recebida");
				handlingResvMessage( packet );
				break;
			case RSVPPacket.TYPE_PATH_ERR:
				this.logger.log(Level.INFO,"RSVP Message Type: PATH ERROR, from: " + packet.getSourceAsInetAddress().getHostAddress()+ ", in "+ now/1000 + " s" );
				handlingPathErrMessage( packet );
				break;
			case RSVPPacket.TYPE_PATH_TEAR:
				this.logger.log(Level.INFO,"RSVP Message Type: PATH TEAR, from: " + packet.getSourceAsInetAddress().getHostAddress()+ ", in "+ now/1000 + " s" );
				handlingTearMessage( packet );
				break;
			case RSVPPacket.TYPE_RESV_ERR:
				this.logger.log(Level.INFO,"RSVP Message Type: RESV ERROR, from: " + packet.getSourceAsInetAddress().getHostAddress()+ ", in "+ now/1000 + " s" );
				//				handlingResvErrMessage (packet );
				handlingPathErrMessage( packet );
				break;
			case RSVPPacket.TYPE_RESV_CONF:
				this.logger.log(Level.INFO,"RSVP Message Type: RESV CONFIRM, from: " + packet.getSourceAsInetAddress().getHostAddress()+ ", in "+ now/1000 + " s" );
				handlingResvConfMessage( packet );
				break;
			case RSVPPacket.TYPE_RESV_TEAR:
				this.logger.log(Level.INFO,"RSVP Message Type: RESV TEAR, from: " + packet.getSourceAsInetAddress().getHostAddress()+ ", in "+ now/1000 + " s" );
				handlingTearMessage( packet );
				break;
			case RSVPPacket.TYPE_RESV_TEAR_CONFIRM:
				this.logger.log(Level.INFO,"RSVP Message Type: RESV TEAR CONFIR, from: " + packet.getSourceAsInetAddress().getHostAddress()+ ", in "+ now/1000 + " s" );
				handlingResvTearConfirmMessage( packet );
				break;
			case RSVPPacket.TYPE_NOTIFY:
				this.logger.log(Level.INFO,"RSVP Message Type: NOTIFY, from: " + packet.getSourceAsInetAddress().getHostAddress()+ ", in "+ now/1000 + " s" );
				handlingNotifyMessage( packet );
				break;
			}



			pcap.write(packet.getData(), event.getTime());
		} catch (Exception e) {

			logSevere(e.getMessage(), e);
		}
	}



	private void handlingTearMessage(RSVPPacket packet) throws Exception{
		int tunnelID = -1;
		Vector<RSVPObject> vector = RecoveryObject.getRsvpObjects(packet);
		Session session = (Session)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.SESSION_CLASS);

		if (session instanceof LSPTunnelIPv4Session){
			tunnelID = ((LSPTunnelIPv4Session)session).getTunnelID();
		} else if (session instanceof LSPTunnelIPv6Session){
			tunnelID = ((LSPTunnelIPv6Session)session).getTunnelID();
		}
		if (tunnelID > -1) {
			if (dataPlane.getIdLSP() == tunnelID) {
				if (dataPlane.isTransmiting()) {
					removeStates(tunnelID);
					//dataPlane.stopTransmission();
				} else {
					removeStates(tunnelID);
				}
				logInfo("Estados Removidos e transmissão encerrada para o LSP: " + tunnelID);
			} else {
				logInfo("O plano de dados não está conectado ao LSP ID = " + tunnelID);
			}
		}
		

	}

	public void removeStates(int tunnelID){
		if (pathStatesTable.containsKey(tunnelID)) {
			PathState pathState = getPathState(tunnelID); 
			if (pathState != null){
				removePathState(tunnelID);
			}
		}
		if (resvStatesTable.containsKey(tunnelID)){
			ResvState resvState = getResvState(tunnelID); 
			if (resvState != null){
//				resvState.cancelWaitResvConf();
				removeResvState(tunnelID);
			}
		}
		removeUsedLabel(tunnelID);
		
	}

	public void removeUsedLabel(int idLSP) {
		int label=-1;
		if (usedLabels.containsValue(idLSP)){
			for (Iterator<Entry<Integer,Integer>> it = usedLabels.entrySet().iterator() ; 
				it.hasNext();) {
				Entry<Integer, Integer> entryMap = it.next();
				int key = entryMap.getKey();
				int value = entryMap.getValue();
				if (value == idLSP){
					label = key;
					break;
				}
			}
				
		}
		if (label != -1)
			usedLabels.remove(label);
	}
	
	public void removeLSP(int idLSP) throws Exception {
		long now = (System.currentTimeMillis() - START_TIME)/1000;
		PathState ps = getPathState(idLSP);
		ResvState resvState = getResvState(idLSP);
		RSVPPacket tear = null;
		if (dataPlane.isTransmiting()){
			if (!dataPlane.isDownstream()){
				if (resvState != null) {
					tear = TearMessageFactory.create(resvState, dataPlane.isDownstream());
//					sendTear(resvState, tear);
				} else {
					throw new ControlPlaneException("IDLSP = " + idLSP + ". " + " Não foi possível remover o LSP. Resv State não encontrado");
				}
			} else {
				if (ps != null){
					tear = TearMessageFactory.create(ps, dataPlane.isDownstream());
//					sendTear(ps, tear);
				 }else {
					 throw new ControlPlaneException("IDLSP = " + idLSP + ". " + " Não foi possível remover o LSP. Path State não encontrado");
				}
			}
			
			if (tear != null) {
				if (tear.getMessageType() == RSVPPacket.TYPE_PATH_TEAR) {
					logInfo("enviando pacote PATH TEAR, de: " + 
						tear.getSourceAsInetAddress().getHostAddress() + ", para: " + 
						tear.getDestinationAsInetAddress().getHostAddress() +", em " + now);
				} else {
					logInfo("enviando pacote Resv TEAR, de: " + 
							tear.getSourceAsInetAddress().getHostAddress() + ", para: " + 
							tear.getDestinationAsInetAddress().getHostAddress() +", em " + now);
					
				}
				send(tear);
			}
			
			dataPlane.stopTransmission();
		} else {
			
		}
		
		removeStates(idLSP);
	}
	
	
	/**
	 * Checks if the received label is used by other LSP
	 * @param idLSP the LSP's id that was received together the label
	 * @label the label received
	 * */
	public boolean isUsedLabel(int idLSP, int label){
		if (usedLabels.containsKey(label)){
			if (usedLabels.get(label) != idLSP){
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles the RSVP Packet with type RESV
	 * @packet the packet to handle
	 * */
	public void handlingResvMessage(RSVPPacket packet) throws Exception{

		int tunnelID = 0;
		int extendedTunnelID = 0;
		boolean isNew = false;
		InetAddress extendedTunnelIDAddress = null;
		PathState pathState = null;
		Vector<RSVPObject> vector = RecoveryObject.getRsvpObjects(packet);
		Session session = (Session)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.SESSION_CLASS);
		InetAddress destino = null;
		int destination = 0;
		if (session instanceof LSPTunnelIPv4Session){
			destination = ((LSPTunnelIPv4Session)session).getEndPointAddress();
			tunnelID = ((LSPTunnelIPv4Session)session).getTunnelID();
			extendedTunnelID = ((LSPTunnelIPv4Session)session).getExtendedTunnelID();
			byte[] extendedByte = new byte[4];
			extendedByte = ByteOperation.intToByteArray(extendedTunnelID);
			extendedTunnelIDAddress =  InetAddress.getByAddress(extendedByte);
			pathState = getPathState(tunnelID);
			destino = InetAddress.getByAddress(ByteOperation.intToByteArray(destination));
		} else if (session instanceof LSPTunnelIPv6Session){
			tunnelID = ((LSPTunnelIPv6Session)session).getTunnelID();
			extendedTunnelIDAddress = InetAddress.getByAddress(((LSPTunnelIPv6Session)session).getExtendedTunnelID());
			pathState = getPathState(tunnelID);
		}

		if (pathState == null){
			throw new ControlPlaneException("Não foi possível estabelecer a reserva. Path State Não encontrado");
			
		}


		if(extendedTunnelIDAddress.isSiteLocalAddress()){
			if (pathState != null){
				if (pathState instanceof PathStateSender){
					logInfo("IDLSP = " + tunnelID + ", " + "Resv chegou à fonte");
					if (makeReservation(tunnelID, packet)){
						ResvState resvState = getResvState(tunnelID);
						resvState.setDestination(destination);
						resvState.setSource(extendedTunnelID);
						((PathStateSender)pathState).setReceivedResv(true); //ativa o envido de mensagens path de atualização 
						pathState.setActived(true); // permite que o monitoramento deste estado pelas trheads de timeout
						dataPlane.setIdLSP(tunnelID);
						if (!monitor.isAlive())
							monitor.start();
						// habilita o plano de dados para enviar mensagens notify
						if (runTest){
							executeTests();
							runTest = false;
						}
						RSVPPacket resvConf = ResvConfMessageFactory.create(resvState);
						send(resvConf);
					}
				} else {
					logInfo("Este Path State não iniciou esta conexão | IDLSP = " + tunnelID);
					
				}
			} else {
				logInfo("IDLSP = " + tunnelID + ", " + "Path State não encontrado");
			}

		} else {
			//Makes the reservation and create the resv state
			makeReservation(tunnelID, packet);
			//Updates the objects before back-forwarding the RESV message
			updateAndForward(pathState, packet);

		}
	}




	/**
	 * Handles the RSVP Packet with type RESV CONF
	 * @packet the packet to handle
	 * */
	public void handlingResvConfMessage(RSVPPacket packet) throws Exception{

		int tunnelID = 0;
		int extendedTunnelID = 0;
		InetAddress extendedTunnelIDAddress = null;

		Vector<RSVPObject> vector = RecoveryObject.getRsvpObjects(packet);
		Session session = (Session)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.SESSION_CLASS);

		if (session instanceof LSPTunnelIPv4Session){
			tunnelID = ((LSPTunnelIPv4Session)session).getTunnelID();
			extendedTunnelID = ((LSPTunnelIPv4Session)session).getExtendedTunnelID();
			byte[] extendedByte = new byte[4];
			extendedByte = ByteOperation.intToByteArray(extendedTunnelID);
			extendedTunnelIDAddress =  InetAddress.getByAddress(extendedByte);

		} else if (session instanceof LSPTunnelIPv6Session){
			tunnelID = ((LSPTunnelIPv6Session)session).getTunnelID();
			extendedTunnelIDAddress = InetAddress.getByAddress(((LSPTunnelIPv6Session)session).getExtendedTunnelID());

		}

		ResvState resvState = getResvState(tunnelID);
		PathState pathState = getPathState(tunnelID);
		if (resvState == null){
			throw new ControlPlaneException("Erro ao recuperar ResvState ao receber mensagem ResvConf");
		}
		if (pathState == null){
			throw new ControlPlaneException("Erro ao recuperar Path ao receber mensagem ResvConf");
		}	

		if(extendedTunnelIDAddress.isSiteLocalAddress()){
			boolean isNew = !dataPlane.isTransmiting();
			if (!makeReservation(isNew, tunnelID, pathState.getUpstreamLabel())){
				pathState.setActived(false);
				Exception e = new ControlPlaneException("Reserva de Upstream Label não pode ser efetivada, ");
				logSevere(e.getMessage(), e );
			}

			resvState.setReceivedResvConf(true);// habilita as threads que atualizam os este estado
			resvState.setActived(true);// habilita as threads que atualizam os este estado
			dataPlane.setIdLSP(tunnelID); // habilita o plano de dados para enviar mensagens notify
			if (isNew){
				monitor.start();  
				RSVPLabel label = (RSVPLabel) RecoveryObject.getRsvpObjectByClass(packet, RSVPObject.RSVP_LABEL_CLASS);
				resvState.setLabel(label);
			}
			

		} else {

			//Updates the objects before back-forwarding the RESV message
			updateAndForward(resvState, packet);
		}
	}

	/**
	 * Handling a RSVP Packet of type Notify
	 * @param packet The RSVP Packet to handle
	 */
	public void handlingNotifyMessage(RSVPPacket packet) throws Exception{
		System.out.println("Entrei em handlingNotifyMessage");
		int tunnelID = 0;
		State state = null;
		Vector<RSVPObject> vector = RecoveryObject.getRsvpObjects(packet);
		LSPTunnelIPv4Session session = (LSPTunnelIPv4Session)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.SESSION_CLASS);
		boolean downstream = true;
		FlowSpec flowSpec =(FlowSpec)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.FLOWSPEC_CLASS);
		SenderTemplate senderT = (SenderTemplate)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.SENDER_TEMPLATE_CLASS);


		if (senderT != null) {
			downstream = false;
			/*Bad Notify Message*/
			if (flowSpec != null)
				return;
		} else if (flowSpec == null){
			/*Bad Notify Message*/
			return;
		}


		tunnelID = session.getTunnelID();
		InetAddress node = null;
		if (downstream) {
			int endPointAddressInt = ((LSPTunnelIPv4Session)session).getEndPointAddress();
			byte[] endPointAddressByte = new byte[4];
			endPointAddressByte = ByteOperation.intToByteArray(endPointAddressInt);
			node =  InetAddress.getByAddress(endPointAddressByte);
			state = getResvState(tunnelID);
		} else { 
			int extendedTunnelID = ((LSPTunnelIPv4Session)session).getExtendedTunnelID();
			byte[] extendedByte = new byte[4];
			extendedByte = ByteOperation.intToByteArray(extendedTunnelID);
			node =  InetAddress.getByAddress(extendedByte);
			state = getPathState(tunnelID);
		}
		
		if (state == null){
			return;
		}


		ErrorSpec error = (ErrorSpec)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.ERROR_SPEC_CLASS);
		int code = error.getErrorCode();
		int value = error.getErrorValue();

		RsvpNotifyIndication indication = null; 
		if(node.isSiteLocalAddress()){
			long now = System.currentTimeMillis();
			log(Level.INFO, "ControlPlane, recebendo notify message ("+ 
					ErrorSpec.NOTIFY_ERROR.getName(value) +"), em: " + (now - START_TIME) + "ms ");

			if (code == ErrorSpec.ERROR_CODE.NOTIFY_ERROR.value()){
				if (value == ErrorSpec.NOTIFY_ERROR.OTN_BELOW_MINIMUM_OTICAL.value()){
					if (!dataPlane.getOiWDM().isLaserOff()){
						log(Level.SEVERE, "Removendo LSP ("+ tunnelID +"), em: " + (now - START_TIME) + "ms ");
//						removeLSP(tunnelID);
					} else {
						log(Level.SEVERE, "Religando Laser WDM em: " + (now - START_TIME) + "ms ");
						dataPlane.turnOnLaserWdm();
					}
					
				} else if (value == ErrorSpec.NOTIFY_ERROR.OTN_ABOVE_MAXIMUM_OPTICAL.value()){
					if (dataPlane.hasAmplifierOut()){
						if(!dataPlane.decreaseGainOut()){
							log(Level.SEVERE, "Removendo LSP ("+ tunnelID +"), em: " + (now - START_TIME) + "ms ");
							removeLSP(tunnelID);
						}
					}

				} else if (value == ErrorSpec.NOTIFY_ERROR.OTN_BELOW_MINIMUM_ELETRICAL.value()){
					if (dataPlane.hasAmplifierOut()){
						if (!dataPlane.increaseGainOut()){
							removeLSP(tunnelID);
						}
					}
				}
				//começo do código proposto DENER
				else if(value == ErrorSpec.NOTIFY_ERROR.LSP_LOCALLY_FAILED.value()){
					
					//Cria objetos de proteção e associação que irão armazenar os estados atuais para tratamento
					Protection currentlyProtectState = (Protection)RecoveryObject.getRsvpObjectByClass(packet, RSVPObject.PROTECTION_CLASS);
					Association currentlyAssocState = (Association)RecoveryObject.getRsvpObjectByClass(packet, RSVPObject.ASSOCIATION_CLASS);
					log(Level.SEVERE, "Falha local de LSP "+ "("+ tunnelID +"), em: " + (now - START_TIME) + "ms ");
					
					//Checa se é o head end
					if(getLocalAddressAsInt() == currentlyAssocState.getIntAssociationSource()){
					
						//Obtem o estado do caminho descrito na mensagem NOTIFY
						PathState pathState = getPathState(tunnelID);
						
						//Cria vetor com informações para criar a recuperação
						Vector<RSVPObject> recoveryInfo = new Vector<RSVPObject>();
						
						//Cria Objetos de proteção e associação para as novas informações de proteção
						Protection newProtectInfo = new Protection(true,true,true,true,2,1);
						Association newAssociationInfo = new Association(0,currentlyAssocState.getAssociationId(),currentlyAssocState.getIntAssociationSource());
						
						recoveryInfo.add(newProtectInfo);
						recoveryInfo.add(newAssociationInfo);
						
						
						
						//Para enviar uma nova mensagem PATH, um novo idLSP se faz necessário
						int idLSP = IDLSPCOUNT++;
												
						//Verifica o Tipo de restauração em LSPFlags no objeto atual e toma a ação de acordo com o tipo de proteção
						switch(currentlyProtectState.getLSPFlags()){
						
						case Protection.FULL_REROUTING:
							System.out.println("Restauração FULL REROUTING acionada, enviando nova mensagem PATH");
							//Cria uma nova mensagem path com as informações de recuperação desejadas
							RSVPPacket recoveryPacket = PathMessageFactory.create(currentlyAssocState.getIntAssociationSource(), pathState.getDestination(), idLSP, true, recoveryInfo);
							send(recoveryPacket);
							break;
							
						case Protection.UNPROTECTED_LSP:
							
							break;
							
						case Protection.REROUTING_WITHOUT_EXTRA_TRAFFIC:
							
							break;
							
						case Protection.SHARED_PROTECTION_WITH_EXTRA_TRAFFIC:
							
							break;
						
						case Protection.DEDICATED_BIDIRECTIONAL_PROTECTION:
							
							break;
							
						case Protection.DEDICATED_UNIDIRECTIONAL_PROTECTION:
							
							break;
						
							
						}
					//Checar se iremos remover o caminho ou aguardar para reversão					
						//removeLSP(tunnelID);
					} 
				}// fim
			}

		} else {

			//Updates the objects before back-forwarding the RESV message
			updateAndForward(state, packet);
		}
	}

	/**
	 * Gets and analyze the recovery information in a RSVP Notify Message and start
	 * the recovery mechanism 
	 * @throws Exception 
	 */
	



	/**
	 * Installs the reservation on peer and save the state this reservation
	 * @param tunnelID is the identification of the LSP
	 * @param resv The RSVP Resv Message that content the parameters of the reservation 
	 * */
	public boolean makeReservation( int tunnelID, RSVPPacket resv) throws Exception{ 
		ErrorSpec err = null;
		RSVPPacket resvErr = null;
		boolean isNew = false;
		ResvState resvState = this.getResvState(tunnelID);
		if(resvState == null){
			resvState = new ResvState(resv, dataPlane);
			this.setResvState(tunnelID, resvState);
			isNew = true;
		}
		if (isNew){
			resvState.setIdLSP(tunnelID);
			resvState.setSource(resv.getSourceAsWord());
			resvState.setDestination(resv.getDestinationAsWord());
			resvState.setPacket(resv);
			RSVPLabel label = (RSVPLabel) RecoveryObject.getRsvpObjectByClass(resv, RSVPObject.RSVP_LABEL_CLASS);
			if (label == null){
				err = validator.checkIntegrity(label);
				resvErr = ResvErrMessageFactory.create(resvState, err);
				send(resvErr);
				sendPathError(getPathState(tunnelID), err);
				removePathState(tunnelID);
				removeResvState(tunnelID);
				//			removeResvState(tunnelID);
				log(Level.WARNING, "Mensagem Resv Não contém um objeto RSVP Label" +
						". Reserva cancelada!");
				send(resvErr);
			
				return false;
			} else {
				
				int code = ErrorSpec.ERROR_CODE.ROUTING_PROBLEM.value();
				int value = 0;
				if (isUsedLabel( tunnelID, label.getIntLabel() )){
					value = ErrorSpec.ROUTING_PROBLEM.MPLS_LABEL_ALLOCATION_FAILURE.value();
					err = RSVPObjectValidator.getIPv4ErrorSpec( 0, code, value );
					resvErr = ResvErrMessageFactory.create(resvState, err);
					send(resvErr);
					sendPathError(getPathState(tunnelID), err);
					removeStates(tunnelID);
					log(Level.WARNING, "Label: "+ label.getIntLabel() + "já está sendo usado por outro LSP: "
							+ "enviando ResvERR e PathERR");

					return false;
				}
				
				resvState.setLabel(label, isNew);
				usedLabels.put(label.getIntLabel(), resvState.getIdLSP());
			}

			
		} else {
			resvState.updateState(resv);
		}
		return true;



	}


	/**
	 * Sets up a upstream label in Path State. 
	 * @param idLSP The identification of LSP Tunnel
	 * @param label the label to set
	 * */
	public boolean makeReservation(boolean isNew, int idLSP, UpstreamLabel label) throws Exception{
		PathState ps = getPathState(idLSP);
		if (isNew){
			int code = ErrorSpec.ERROR_CODE.ROUTING_PROBLEM.value();
			int value = 0;
			if (isUsedLabel( idLSP, label.getIntLabel() )){
				value = ErrorSpec.ROUTING_PROBLEM.MPLS_LABEL_ALLOCATION_FAILURE.value();
				ErrorSpec err = RSVPObjectValidator.getIPv4ErrorSpec( 0, code, value );
				sendPathError(ps, err);
				removePathState(idLSP);

				log(Level.WARNING, "Label: "+ label.getIntLabel() + "já está sendo usado por outro LSP: ");
				return false;
			}
				if ( label.getClassNum() == RSVPObject.UPSTREAM_LABEL_CLASS ) {
					
					return ps.setUpstreamLabel(label, isNew);
				}
			
		} 


		return true;


	}



	/**
	 * Handles a RSVP Path Message 
	 * @param packet The RSVP Path Message
	 * */
	public void handlingPathMessage(RSVPPacket packet) throws Exception{
		boolean isNew = false;
		
		Vector<RSVPObject> vector = RecoveryObject.getRsvpObjects(packet);
		//Gets the Session Object
		Session session = ( Session ) RecoveryObject.getRsvpObjectByClass( vector, RSVPObject.SESSION_CLASS );
		InetAddress destAddress = null;
		int destination = 0;
		int tunnelID = 0;
		int extendedTunnelID = 0;
		InetAddress extendedTunnelIDAddress = null;
	
			
		

		PathState pathState = null;
		RSVPPacket pathErr = null;
		//handles the session object
		if ( session instanceof LSPTunnelIPv4Session ){
			byte[] ipv4 = new byte[4];
			destination = ( ( LSPTunnelIPv4Session ) session ).getEndPointAddress();
			ipv4 = ByteOperation.intToByteArray(destination);
			destAddress = InetAddress.getByAddress(ipv4);
			
			tunnelID = ( ( LSPTunnelIPv4Session ) session ).getTunnelID();
			extendedTunnelID = ( ( LSPTunnelIPv4Session ) session ).getExtendedTunnelID();
			byte[] extendedByte = new byte[4];
			extendedByte = ByteOperation.intToByteArray( extendedTunnelID );
			extendedTunnelIDAddress =  InetAddress.getByAddress( extendedByte );
			pathState = getPathState(tunnelID);
			
			
		} else if ( session instanceof LSPTunnelIPv6Session ) {
			byte[] ipv6 = ( ( LSPTunnelIPv6Session )session ).getEndPointAddress();
			destAddress = InetAddress.getByAddress(ipv6);
			System.out.println( "imprime destination ipv6: " + destAddress.getHostAddress() );
			tunnelID = ( ( LSPTunnelIPv6Session )session ).getTunnelID();
			extendedTunnelIDAddress = InetAddress.getByAddress( ( ( LSPTunnelIPv6Session )session ).getExtendedTunnelID() );
			pathState = getPathState( tunnelID );
		}

		
		if(pathState == null){
			isNew = true;
			if (isNew)
				logInfo("iniciando PathState, IDLSP = " + tunnelID);
			else 
				logInfo("atualizando PathState, IDLSP = " + tunnelID);
			/**Proposta de código novo Dener INICIO   */
			//Gets the Protection Object
			
			Protection protection_info = (Protection)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.PROTECTION_CLASS);
			if (protection_info.getLinkFlags()==Protection.FULL_REROUTING){
				System.out.println("RECEBI A MENSAGEM DE PROTEÇÃO FULL REROUTING");
				
			}
			
			//Gets the Association Object
			Association association_info = (Association)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.ASSOCIATION_CLASS);
			System.out.println("****** ASSOCIATION"+association_info.toString()+association_info.getAssociationId()+ " e"+association_info.getAssociationType()+"e"+ association_info.getIntAssociationSource()  );
			/**Proposta de código novo Dener FIM   */
			pathState = new PathState(packet,dataPlane);
			pathState.setSource(extendedTunnelID);
			pathState.setDestination(destination);
			pathState.setProtection(protection_info);
			pathState.setAssociation(association_info);
			this.setPathState(tunnelID, pathState);
		} else {
			pathState.updateState(packet);
		}

		//Gets the Label Request
		GeneralizedLabelRequest labelRequest = (GeneralizedLabelRequest)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.LABEL_REQUEST_CLASS);
		//Checks the Label Request
		ErrorSpec err = this.validator.checkIntegrity(labelRequest, destination);

		if ( err != null) {
			System.out.println("Erro em labelRequest");
			removePathState(tunnelID);
			sendPathError(pathState, err);
			return;
		}
		if (isNew)
			logInfo("IDLSP = " + tunnelID + ", " + "Label Request OK!"); 


		//Sets LabelRequest Object into PathState
		pathState.setLabelRequest(labelRequest);

		//Gets the NotifyRequest Object
		NotifyRequest notifyRequest = (NotifyRequest) RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.NOTIFY_REQUEST_CLASS);
		pathState.setListener(ByteOperation.byteArrayToInt(notifyRequest.getAddress()));
		if (isNew)
			logInfo("IDLSP = " + tunnelID + ", " + "Notify Request OK!");
		//Gets the SenderTemplate 
		SenderTemplate senderTemplate= (SenderTemplate)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.SENDER_TEMPLATE_CLASS);
		int source_SenderTemplate = 0;
		int lspID_SenderTemplate = 0;
		InetAddress sourceIAddress = null;

		if (senderTemplate instanceof LSPTunnelIPv4SenderTemplate){
			source_SenderTemplate = ((LSPTunnelIPv4SenderTemplate)senderTemplate).getSourceAddress();
			sourceIAddress = InetAddress.getByAddress(ByteOperation.intToByteArray(source_SenderTemplate));
			if(source_SenderTemplate != extendedTunnelID){
				//TODO pathERR sender template diferente no senderTemplate do Session
			}

			lspID_SenderTemplate = ((LSPTunnelIPv4SenderTemplate)senderTemplate).getLSPID();

			if ( lspID_SenderTemplate != tunnelID ) {
				//TODO Reroute this LSP
			}

		} else {
			// Invalid Sender Template
			err = new IPv4ErrorSpec(getLocalAddressAsInt(), 0, ErrorSpec.ERROR_CODE.ROUTING_PROBLEM.value(), 
					ErrorSpec.ROUTING_PROBLEM.MPLS_LABEL_ALLOCATION_FAILURE.value());
			log(Level.WARNING, "Sender Template inválido");
			removePathState(tunnelID);
			sendPathError(pathState, err);
			return;
		}
		if (isNew)
			logInfo("IDLSP = " + tunnelID + ", " + "Sender Template OK!");
		
		
		//Gets the Suggested Label if present
		SuggestedLabel suggestedLabel = (SuggestedLabel) RecoveryObject.getRsvpObjectByClass( vector, RSVPObject.SUGGESTED_LABEL_CLASS);
		if ( suggestedLabel != null ) {
			ErrorSpec error = validator.checkIntegrity(suggestedLabel);
			if ( error != null) {
				log(Level.WARNING, "Suggedted Label inválido. Este object será ignorado e retirado da mensagem Path");
				vector.remove(suggestedLabel);
			}
			if (isNew){
				logInfo("IDLSP = " + tunnelID + ", " + "Suggested Label OK!");
				labelsSuggested.put(tunnelID, suggestedLabel);
			}
		}
		//Gets the Upstream Label if present
		UpstreamLabel upstreamLabel = (UpstreamLabel) RecoveryObject.getRsvpObjectByClass( vector, RSVPObject.UPSTREAM_LABEL_CLASS);
		if ( upstreamLabel != null ) {
			ErrorSpec error = validator.checkIntegrity(upstreamLabel);
			if ( error != null) {
				System.out.println("Erro em UpstreamLabel");
				removePathState(tunnelID);
				sendPathError(pathState, error);
				return;
			}
			if (isNew)
				logInfo("IDLSP = " + tunnelID + ", " + "Upstream Label OK!");
		}
		pathState.setUpstreamLabel(upstreamLabel);
		//Gets the LabelSet 
		LabelSet labelSet = (LabelSet) RecoveryObject.getRsvpObjectByClass( vector, RSVPObject.LABEL_SET_CLASS);

		if (labelSet != null) {
			ErrorSpec error = validator.checkIntegrity(labelSet);
			if ( error != null) {
				System.out.println("Erro em LabelSet");
				removePathState(tunnelID);
				sendPathError(pathState, error);
				return;
			}
			labelSet = updateLabelSet(labelSet);
			pathState.setLabelSet(labelSet);
			if (isNew)
				logInfo("IDLSP = " + tunnelID + ", " + "Label Set OK!");
		}

		TimeValues timeValues = (TimeValues) RecoveryObject.getRsvpObjectByClass(packet, RSVPObject.TIME_VALUES_CLASS);
		int timeout = 0;
		if (timeValues != null);
		timeout= timeValues.getRefreshPeriod();
		//Verifies if the Path Message arrived in the destination
		if(destAddress.isSiteLocalAddress()){
			pathState.setTimeout(timeout);
			logInfo("IDLSP = " + tunnelID + ", " + "Path Chegou ao Destino!");
			if(sourceIAddress == null){
				throw new Exception("Endereço da fonte não pode ser nulo!");	
			}
			RSVPPacket resv  = null;
			if (isNew){
				resv = ResvMessageFactory.create(pathState);
				
				Label label = (Label) RecoveryObject.getRsvpObjectByClass(resv, RSVPObject.RSVP_LABEL_CLASS);
				if (label != null){
					err = validator.checkIntegrity(label);
					if (err != null){
						log(Level.WARNING, "Erro ao validar Label no momento de fazer a reserva");
						pathErr = PathErrMessageFactory.create(pathState, err);
						send(pathErr);
						pathState.setActived(false);
						removePathState(tunnelID);
						return;
					}

				}

				ResvConfirm resvConf = (ResvConfirm) RecoveryObject.getRsvpObjectByClass(resv, RSVPObject.RESV_CONFIRM_CLASS); 
				if (resvConf == null){ //se não é necessária confirmação
					if (!makeReservation(isNew, tunnelID, upstreamLabel)){
						Exception e = new ControlPlaneException("Reserva de Upstream Label não pode ser efetivada");
						logSevere(e.getMessage(), e );
						
					}
					        ;
				} else {
					createResvState(tunnelID, resv);
					pathState.setUpstreamLabel(upstreamLabel);
				}

			} else {
				
				ResvState resvState = getResvState(tunnelID);
				
				if (resvState == null){
					throw new ControlPlaneException("Erro ao recuperar ResvState ao receber mensagem Path de atualização");
				}
				resvState.setTimeout(timeout);
				resv = resvState.getPacket();
			}
			//Edits the Resv Message 

			//			Sets up the Label Switched Path into the peer
			//			makeReservation(tunnelID, resv);
			//Sends the packet
			this.send(resv);

			//
		} else {
			//Does the update of objects before forward the packet
			updateAndForward(pathState, packet);

		}
	}

	/**
	 * Creates a new Resvervation State to a LSP
	 * @param idLSP The identifier of LSP
	 * @param resv the RSVP packet with type RESV
	 * */
	public void createResvState(int idLSP, RSVPPacket resv) throws ControlPlaneException{
		PathState ps = getPathState(idLSP);
		ResvState resvState = new ResvState(resv, dataPlane);
		resvState.setIdLSP(idLSP);
		resvState.setSource(ps.getSource());
		resvState.setDestination(ps.getDestination());
		resvState.setPacket(resv);
		setResvState(idLSP, resvState);
		resvState.setTimeout(timeout);
	}
	
	/**
	 * Updates the Label Set's entries
	 * @param labelSet A Label Set Object
	 * */
	public LabelSet updateLabelSet(LabelSet labelSet){
		ArrayList<Integer> arrayChannels = new ArrayList<Integer>();
		byte[] subchannels = labelSet.getSubChannels();

		int action = labelSet.getAction();
		for (int i = 0 ; i < subchannels.length ; i += 4){
			byte[] channel= new byte[4];
			System.arraycopy(subchannels, i , channel, 0, 4);
			int c = ByteOperation.byteArrayToInt(channel);;
			arrayChannels.add(c);
		}
		switch (action){
		//includes channels
		case 0:
			for (int i : getSupportedLabels()){
				if (!arrayChannels.contains(i)){
					arrayChannels.add(i);
				}
			}
			break;
			//excludes channels
		case 1:
			for (int i : arrayChannels){
				if (!getSupportedLabels().contains(i)){
					arrayChannels.remove(i);
				}
			}
			break;
		case 2:
			log(Level.CONFIG, "precisa implementar adição de range em label set");
			//TODO include range
			/*boolean flagError = false;
				for (Iterator<Integer> iterator = channels.iterator(); iterator.hasNext();){
					int start = iterator.next();
					int end = 0;
					if (iterator.hasNext())
						 end = iterator.next();
					else 
						flagError = true;

					if (!flagError){
						for (int i = start ; i <= end ; i++) {
							if (!getSupportedLabels().contains(i)){
								flagError = true;
								break;
							}
						}
					}
					if (flagError == true)
						break;
				}*/
			break;
		case 3: 
			log(Level.CONFIG, "precisa implementar exclusão de range em label set");
			//TODO exclude range
			break;
		}
		byte[] newSubchannels = new byte[arrayChannels.size()*4];
		int j = 0;
		for (int i : arrayChannels){
			byte[] channel = new byte[4];
			channel = ByteOperation.intToByteArray(i);
			System.arraycopy(channel, 0, newSubchannels, j, 4);
			j+=4;
		}

		LabelSet newLabelSet = new LabelSet(action,labelSet.getLabelType(),newSubchannels);
		return newLabelSet;
	}

	/**
	 * Does the update on parameters of packet, and then, forwards it
	 * @param packet The RSVP Packet
	 * */
	private void updateAndForward(State state, RSVPPacket packet) throws Exception{

		int local = ByteOperation.byteArrayToInt(this.localAddress.getAddress());
		int rsvpLength = packet.getRSVPLength();
		int packetLength = packet.getIPHeaderLength();
		int offset = 20+8; //IP and RSVP heathers
		byte[] forwardAddress = new byte[4];

		//run for whole packet
		while (offset < packet.getIPPacketLength()){ 
			RSVPObject object = RecoveryObject.getRsvpObject(packet, offset);

			if ( object.getClassNum() == RSVPObject.RSVP_HOP_CLASS ) {

				RSVPHop hop = new RSVPHop(localAddress.getAddress(),1,RSVPHop.IPV4_TYPE);
				byte[] newData = new byte[packet.getIPHeaderLength()];
				//Copy the RSVP header
				System.arraycopy(packet.getData(), 0, newData, 0, offset);
				//Insert the new sub-object
				System.arraycopy(hop.getData(), 0, newData, offset, hop.getLength());
				//Now, writes the next sub-objects, if applicable.
				System.arraycopy(packet.getData(), offset + hop.getLength(), newData, (offset + hop.getLength()), (packetLength - offset - hop.getLength()));

			} else if ( object.getClassNum() == RSVPObject.RECORD_ROUTE_CLASS) {
				IPv4RecordRoute subRroLocal = new IPv4RecordRoute(local, 0);
				((RecordRoute)object).push(subRroLocal.getData());
				packet.setIPHeaderLength(packetLength+subRroLocal.getLength());
				packet.setRSVPLength(rsvpLength + subRroLocal.getLength());
				byte[] newData = new byte[packet.getIPHeaderLength() + subRroLocal.getLength()];
				//Copy the RSVP header
				System.arraycopy(packet.getData(), 0, newData, 0, offset);
				//Insert the new sub-object
				System.arraycopy(subRroLocal.getData(), 0, newData, offset, subRroLocal.getLength());
				//Now, writes the next sub-objects, if applicable.
				System.arraycopy(packet.getData(), offset, newData, (offset + subRroLocal.getLength()), (packetLength - offset));
				//Updates the size of the object
				newData[offset] = (byte)((object.getLength() >> 8) & 0xff);
				newData[offset + 1] = (byte)(object.getLength() & 0xff);
				//Now set the new data array
				packet.setData(newData);
				//Updates the size of the RSVP Message
				packet.setRSVPLength(rsvpLength + subRroLocal.getLength());
				//Updates the size of the IP Packet
				packet.setIPHeaderLength(packetLength+subRroLocal.getLength());
			} else if ( object.getClassNum() == RSVPObject.EXPLICIT_ROUTE_CLASS ) {

				if (object.getCType() == ExplicitRoute.TYPE_1_EXPLICIT_ROUTE) {

					int offset2 = RSVPObject.OFFSET_CONTENT;

					while (offset2 < object.getLength()){

						byte[] subOTypebyte = new byte[4];
						System.arraycopy(object.getData(), offset2 + ExplicitRouteSubObject.OFFSET_TYPE, subOTypebyte, 0, 1);
						int type = ByteOperation.byteArrayToInt(subOTypebyte);
						// get the the lenght of subobject
						byte[] subObjectLengthByte = new byte[4];
						System.arraycopy(object.getData(), offset2 + ExplicitRouteSubObject.OFFSET_LENGTH, subObjectLengthByte, 0, 1);
						int subObjectLength = ByteOperation.byteArrayToInt(subObjectLengthByte);
						byte[] subObjectData = new byte[subObjectLength];
						System.arraycopy(object.getData(), offset2 , subObjectData, 0, subObjectLength);
						if (type == ExplicitRouteSubObject.IPV4_PREFIX) {
							IPv4ExplicitRoute ipv4ER = new IPv4ExplicitRoute(subObjectData);
							forwardAddress = new byte[4];
							System.arraycopy(ipv4ER,  IPv4ExplicitRoute.OFFSET_ADDRESS, forwardAddress, 0, 4);
						} else if (type == ExplicitRouteSubObject.IPV6_PREFIX) {
							IPv6ExplicitRoute ipv6ER = new IPv6ExplicitRoute(subObjectData);
							forwardAddress = new byte[16];
							System.arraycopy(ipv6ER,  IPv6ExplicitRoute.OFFSET_ADDRESS, forwardAddress, 0, 16);
						} 

						if (forwardAddress.length > 0){
							if( InetAddress.getByAddress(forwardAddress).isSiteLocalAddress() ) {
								offset2 += subObjectLength;

								System.arraycopy(object.getData(), offset2 + ExplicitRouteSubObject.OFFSET_TYPE, subOTypebyte, 0, 1);
								type = ByteOperation.byteArrayToInt(subOTypebyte);
								// get the the lenght of subobject
								System.arraycopy(object.getData(), offset2 + ExplicitRouteSubObject.OFFSET_LENGTH, subObjectLengthByte, 0, 1);
								subObjectLength = ByteOperation.byteArrayToInt(subObjectLengthByte);
								System.arraycopy(object.getData(), offset2 , subObjectData, 0, subObjectLength);
								if (type == ExplicitRouteSubObject.IPV4_PREFIX) {
									IPv4ExplicitRoute ipv4ER = new IPv4ExplicitRoute(subObjectData);
									forwardAddress = new byte[4];
									System.arraycopy(ipv4ER,  IPv4ExplicitRoute.OFFSET_ADDRESS, forwardAddress, 0, 4);
								} else if (type == ExplicitRouteSubObject.IPV6_PREFIX) {
									IPv6ExplicitRoute ipv6ER = new IPv6ExplicitRoute(subObjectData);
									forwardAddress = new byte[16];
									System.arraycopy(ipv6ER,  IPv6ExplicitRoute.OFFSET_ADDRESS, forwardAddress, 0, 16);
								} 
								packet.setDestinationAsWord(ByteOperation.byteArrayToInt(forwardAddress));

							} // End if

						} // End if 
						offset2 += subObjectLength;

					} //End while

				} // End if

			} // End else if
			offset += object.getLength();

		} //End While
		if ( packet.getMessageType() == RSVPPacket.TYPE_RESV || 
				packet.getMessageType() == RSVPPacket.TYPE_PATH_ERR || 
				packet.getMessageType() == RSVPPacket.TYPE_PATH_TEAR ||
				packet.getMessageType() == RSVPPacket.TYPE_RESV_TEAR ||
				packet.getMessageType() == RSVPPacket.TYPE_RESV_ERR ||
				packet.getMessageType() == RSVPPacket.TYPE_RESV_CONF ||
				packet.getMessageType() == RSVPPacket.TYPE_NOTIFY) {
			int dest = ByteOperation.byteArrayToInt(state.getPreviousHop().getNodeAddress());
			packet.setDestinationAsWord(dest);
		}

		this.send(packet);

	}

	public void setPcap(PCAP pcap) {
		this.pcap = pcap;
	}
	/**
	 * Returns a only instance of ControlPlane Class
	 * @return the Control Plane 
	 * */
	public static ControlPlane getInstance() {
		if(controller == null){
			controller = new ControlPlane();
		}
		return controller;
	}

	/**
	 * @param destAddress address IPv4 or IPv6 of destination of LSP
	 * @param idLSP identifier of the LSP
	 * */
	public void sendPath(String destAddress) throws Exception{
		
		
		int idLSP = IDLSPCOUNT++;

		InetAddress target = InetAddress.getByName(destAddress);

		int source = ByteOperation.byteArrayToInt(localAddress.getAddress());
		int destination = ByteOperation.byteArrayToInt(target.getAddress());
		
		//Criação do objeto de proteção dentro do control plane
		Vector<RSVPObject> protectInfo = new Vector<RSVPObject>();
		//Create the PROTECTION_OBJECT by RFC 4872
		Protection protection = new Protection(false,false,false,true,1,4);
		protectInfo.add(protection);
		//Create the ASSOCIATION_OBJECT by RFC 4872
		Association association = new Association(0,idLSP,source);
		protectInfo.add(association);
		
		//
		RSVPPacket packet = PathMessageFactory.create(source, destination, idLSP, true, protectInfo);
		PathStateSender pathState = new PathStateSender(packet, dataPlane);
		pathState.setIdLSP(idLSP);
		pathState.setSource(source);
		pathState.setDestination(destination);
		pathState.setDelaytoRefresh(delayToRefresh);
		pathState.setProtection(protection);
		pathState.setAssociation(association);
		/*adiciona o path state na tabela de estados*/
		setPathState(idLSP, pathState);
		
		long now = (System.currentTimeMillis()-ControlPlane.START_TIME)/1000;
		logInfo(String.format("Enviando Path Messgage | de: %s, para: %s\n", localAddress.getHostAddress(), destAddress));
		//		socket.write(target, packet.getData());
		send(packet);
		/**
		 * Código para teste de criação de mensagem path de proteção, posteriormente irá para handlingNotifyMessage
		 *Descrito em detalhes no método hanglindNotifyMessage
		 */
	/**	PathState state = getPathState(idLSP);
		
		Vector<RSVPObject> protectionInfo = new Vector<RSVPObject>();
		Protection currentlyProtectState = state.getProtection();
		Association currentlyAssocState = state.getAssociation();
		Protection newProtectInfo = new Protection(true,true,false,true,1,4);
		Association newAssociationInfo = new Association(0,currentlyAssocState.getAssociationId(),currentlyAssocState.getIntAssociationSource());
		protectionInfo.add(newProtectInfo);
		protectionInfo.add(newAssociationInfo);
		boolean secondarybit = false;
		Protection teste = (Protection)RecoveryObject.getRsvpObjectByClass(protectionInfo, RSVPObject.PROTECTION_CLASS);
		boolean notificationbit = currentlyProtectState.getNotificationBit();
		idLSP++;
		if (!currentlyProtectState.getSecondaryBit()){
			secondarybit = true;
		}
		
		
		
		if(getLocalAddressAsInt() == currentlyAssocState.getIntAssociationSource()){
			
			if(currentlyProtectState.getLSPFlags()==Protection.FULL_REROUTING){
				System.out.println("Restauração FULL REROUTING acionada, enviando nova mensagem PATH");
				RSVPPacket recoveryPacket = PathMessageFactory.create(currentlyAssocState.getIntAssociationSource(), pathState.getDestination(), idLSP, true, protectionInfo);
				send(recoveryPacket);
				PathStateSender recoverypathState = new PathStateSender(recoveryPacket, dataPlane);
				
				recoverypathState.setIdLSP(idLSP);
				recoverypathState.setSource(newAssociationInfo.getIntAssociationSource());
				recoverypathState.setDestination(pathState.getDestination());
				recoverypathState.setDelaytoRefresh(delayToRefresh);
				
				/*adiciona o path state na tabela de estados
				setPathState(idLSP, recoverypathState);
			
			}
			
		}*/	
		
		
		
	}

	
	
	

	/**
	 * Sends a PathTear Message toward to destination
	 * @param pathState the PathState of LSP
	 * @param tear the RSVP Message to be sent
	 * */
//	public void sendTear(State state, RSVPPacket tear) throws Exception{
////		state.getDataPlane().stopTransmission();
//		send(tear);
//	}



	public void setSocket(RawSocket socket) {
		this.socket = socket;
	}

	public RawSocket getSocket() {
		return socket;
	}


	/**
	 * @return the localAddress
	 */
	public InetAddress getLocalAddress() {
		return localAddress;
	}


	/**
	 * @param localAddress the localAddress to set
	 */
	public void setLocalAddress(InetAddress localAddress) {
		this.localAddress = localAddress;
	}

	/**
	 * @return the local addres as a integer number
	 */
	public int getLocalAddressAsInt() {
		int local =  ByteOperation.byteArrayToInt(localAddress.getAddress());
		return local ;
	}



	public synchronized void send(RSVPPacket packet) {

		try {
			socket.write(packet.getDestinationAsInetAddress(), packet.getData());
			pcap.write(packet.getData());
					
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public PathState getPathState(int idLSP){
		return pathStatesTable.get(idLSP);
	}

	public ResvState getResvState(int idLSP){
		return resvStatesTable.get(idLSP);
	}

	public <T extends PathState> void setPathState(int idLSP, T ps){
		pathStatesTable.put(idLSP, ps);
		System.out.println("NOVO LSP " + idLSP);
	}

	public <T extends ResvState> void setResvState(int idLSP, T rs){
		resvStatesTable.put(idLSP, rs);
	}

	public void removePathState(int idLSP){
		long now = (System.currentTimeMillis() - START_TIME)/1000;
		State state = getPathState(idLSP);
		try {
			if (state != null){
				//try {
//					RSVPPacket pathTear = TearMessageFactory.create((PathState)state);
//					sendTear((PathState)state, pathTear);
//				} catch (Exception e) {
					// TODO Auto-generated catch block
//					log(e.getClass().getName(), Level.SEVERE, e.getCause().toString());
//				}
				state.setActived(false);
				pathStatesTable.remove(idLSP);
				state.cancel();
				logInfo("O estado: " + state.getName() + " foi removido com sucesso, LSP: " + idLSP  +" em " + now);
			} else {
				throw new ControlPlaneException("PathState não encontrado | IDLSP: " + idLSP +" em " + now);
			}
		} catch (ControlPlaneException e) {
			logSevere(e.getMessage(), e);
		}



	}

	public void removeResvState(int idLSP){
		long now = (System.currentTimeMillis() - START_TIME)/1000;
		try {
			State state = getResvState(idLSP);

			if (state != null){
//				try {
//					RSVPPacket resvTear = TearMessageFactory.create(state);
//					sendTear(state, resvTear);
//				} catch (Exception e) {
					// TODO Auto-generated catch block
//					log(e.getClass().getName(), Level.SEVERE, e.getMessage());
//				}
				state.setActived(false);
				resvStatesTable.remove(idLSP);
				state.cancel();
				logInfo("O estado: " + state.getName() + " foi removido com sucesso, LSP: " + idLSP+" em " + now);
			} else {
				throw new ControlPlaneException("ResvState não encontrado | IDLSP: " + idLSP+" em " + now);
			}
		} catch (ControlPlaneException e) {
			logSevere(e.getMessage(), e);
		}

	}




	/**
	 * @return the supportedLabels
	 */
	public Vector<Integer> getSupportedLabels() {
		return supportedLabels;
	}




	/**
	 * @param supportedLabels the supportedLabels to set
	 */
	public void setSupportedLabels(Vector<Integer> supportedLabels) {
		this.supportedLabels = supportedLabels;
	}

	/**
	 * Gets the Outgoing Interface
	 * @param label The label to gets the outgoing interface
	 * @return the outgoing interface 
	 * */
	public InetAddress getOutgoingInterface(int label){
		return outgoingLabelToOutgoingInterfaceTable.get(label);
	}

	/**
	 * Gets the Incoming Interface
	 * @param label The label to gets the incoming interface
	 * @return the Incoming interface 
	 * */
	public InetAddress getIncomingInterface(int label){
		return outgoingLabelToOutgoingInterfaceTable.get(label);
	}

	public void sendPathError(PathState pathState, ErrorSpec error) throws Exception {
		RSVPPacket pathErr = PathErrMessageFactory.create(pathState, error);
		updateAndForward(pathState, pathErr);
		//		send(pathErr);
	}



	public <T extends EnumError> void printLogError( ErrorSpec.ERROR_CODE code, int value, T arrayError[], InetAddress node){

		for (T t : arrayError) {
			if ( value == t.value() ) {
				logger.log(Level.WARNING, "Error: " + code + " / "+ t + ", from: " + node.getHostAddress()) ;
			}
		}
	}
	/**
	 * Returns the RESV Message's delay time relative to a LSP
	 * */
	public Long getTimerToSendResv(int idLSP) {
		return timerToSendResv.get(idLSP);
	}

	/**
	 * Sets the RESV Message's delay time relative to a LSP
	 * @param idLSP The LSP ID
	 * @param time The delay
	 * */
	public void setTimerToSendResv(int idLSP, long time) {
		this.timerToSendResv.put(idLSP, time);
	}

	public DataPlane getDataPlane() {
		return dataPlane;
	}

	public void setDataPlane(DataPlane dataPlane) {
		this.dataPlane = dataPlane;
		monitor = new DataPlaneMonitorThread(dataPlane);
		
	}

	public void sendRsvpNotifyMessage(State state,
			RsvpNotifyIndication notifyIndication, List<Integer> notifyListeners){

		for (int i : notifyListeners){
			int flags = 0;
			int code = ErrorSpec.ERROR_CODE.NOTIFY_ERROR.value();
			int value = 0;
			ErrorSpec err = null;
			switch (notifyIndication) {
			case OPTICAL_BELOW_MINIMUM:
				value = ErrorSpec.NOTIFY_ERROR.OTN_BELOW_MINIMUM_OTICAL.value();
				break;
			case OPTICAL_ABOVE_MAXIMUM:
				value = ErrorSpec.NOTIFY_ERROR.OTN_ABOVE_MAXIMUM_OPTICAL.value();
				break;
			case ELETRICAL_BELOW_MINIMUM:
				value = ErrorSpec.NOTIFY_ERROR.OTN_BELOW_MINIMUM_ELETRICAL.value();
				break;
			case LSP_LOCALLY_FAILED:
				value = ErrorSpec.NOTIFY_ERROR.LSP_LOCALLY_FAILED.value();
				
				break;
			}

			if (value != 0){
				err = new IPv4ErrorSpec(getLocalAddressAsInt(), flags, code, value);
				try {
					RSVPPacket notify = NotifyMessageFactory.create(i, state, err);	
					
					send(notify);
				} catch (Exception e) {
					log(Level.WARNING,"Não foi possível criar uma mensagem NOTIFY com o indicativo "+ notifyIndication.getDescription() );
				}


			}
		}

	}

	private void log(Level level , String msg){
		log("ControlPlane", level, msg);
	}

	private void logSevere(String msg, Exception e) {
		logSevere("ControlPlane", msg, e);
	}

	public synchronized void log(String className, Level level , String msg){
		StringBuilder sb = new StringBuilder();
		sb.append(className);
		sb.append(": ");
		sb.append(msg);
		this.logger.log(level, sb.toString());
	}

	public void logInfo(String msg){
		StringBuilder sb = new StringBuilder();
		sb.append("ControlPlane: ");
		sb.append(msg);
		this.logger.log(Level.INFO, sb.toString());
	}

	public void logSevere(String className,String msg, Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append("ControlPlane: ");
		sb.append(msg);
		this.logger.log(Level.SEVERE, sb.toString(), e);
		sb = null;
	}

	private void handlingPathErrMessage(RSVPPacket packet) throws Exception {

		int tunnelID = 0;
		Vector<RSVPObject> vector = RecoveryObject.getRsvpObjects(packet);
		Session session = (Session)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.SESSION_CLASS);

		if (session instanceof LSPTunnelIPv4Session){
			tunnelID = ((LSPTunnelIPv4Session)session).getTunnelID();
		} else if (session instanceof LSPTunnelIPv6Session){
			tunnelID = ((LSPTunnelIPv6Session)session).getTunnelID();
		}


		PathState pathState = getPathState(tunnelID);
		if (pathState != null){
			pathState.setActived(false);
			log(Level.WARNING,"PathState idLSP: " + tunnelID + " está sendo desativado devido ao erro");
		}
		ResvState resvState = getResvState(tunnelID);
		if (resvState != null){
			resvState.setActived(false);
			log(Level.WARNING,"ResvState idLSP: " + tunnelID + " está sendo desativado devido ao erro");
		}

		ErrorSpec error = (ErrorSpec)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.ERROR_SPEC_CLASS);
		int nodeAddress = 0; 
		InetAddress errorNodeAddress = null;
		if (error instanceof IPv4ErrorSpec) {
			nodeAddress = ((IPv4ErrorSpec)error).getErrorNodeAddress();
			byte[] data = new byte[4];
			data = ByteOperation.intToByteArray(nodeAddress);
			errorNodeAddress = InetAddress.getByAddress(data);
		} else if (error instanceof IPv6ErrorSpec)
			errorNodeAddress = InetAddress.getByAddress(((IPv6ErrorSpec)error).getErrorNodeAddress());

		int code = error.getErrorCode();
		int value = error.getErrorValue();
		long now = System.currentTimeMillis();
		for ( ErrorSpec.ERROR_CODE e :  ErrorSpec.ERROR_CODE.values() ){
			if (code == e.value()) {

				switch (e) {
				case ROUTING_PROBLEM:
//					System.out.println("Routing problem value: " + value );
					printLogError(e, value, ErrorSpec.ROUTING_PROBLEM.values(), errorNodeAddress);
					break;
				case ADMISSION_CONTROL_FAILURE:
					printLogError(e, value, ErrorSpec.ADMISSION_CONTROL_FAILURE.values(), errorNodeAddress);
					break;
				case POLICY_CONTROL_FAILURE:
					printLogError(e, value, ErrorSpec.POLICY_CONTROL_FAILURE.values(), errorNodeAddress);
					break;
				case NOTIFY_ERROR:
					
					if(e.value() == ErrorSpec.NOTIFY_ERROR.LSP_LOCALLY_FAILED.value()){
						Protection protection = (Protection)RecoveryObject.getRsvpObjectByClass(packet, RSVPObject.PROTECTION_CLASS);
						Association association = (Association)RecoveryObject.getRsvpObjectByClass(packet, RSVPObject.ASSOCIATION_CLASS);
						log(Level.SEVERE, "Falha local de LSP "+ "("+ tunnelID +"), em: " + (now - START_TIME) + "ms ");
						//Checa se é o head end
						if(getLocalAddressAsInt() == association.getIntAssociationSource()){
							//enviar mensagem notify com o erro para o head end
						}
						
					}
							
					printLogError(e, value, ErrorSpec.NOTIFY_ERROR.values(), errorNodeAddress);
					break;
				case DIFFSERV_ERROR:
					printLogError(e, value, ErrorSpec.DIFFSERV_ERROR.values(), errorNodeAddress);
					break;
				case DIFFSERV_AWARE_TE_ERROR:
					printLogError(e, value, ErrorSpec.DIFFSERV_AWARE_TE_ERROR.values(), errorNodeAddress);
					break;
					
				default:
					break;
				}
			}
		}


	}


	private void handlingResvTearConfirmMessage(RSVPPacket packet ) throws Exception {
		int tunnelID = 0;
		Vector<RSVPObject> vector = RecoveryObject.getRsvpObjects(packet);
		Session session = (Session)RecoveryObject.getRsvpObjectByClass(vector, RSVPObject.SESSION_CLASS);

		if (session instanceof LSPTunnelIPv4Session){
			tunnelID = ((LSPTunnelIPv4Session)session).getTunnelID();
		} else if (session instanceof LSPTunnelIPv6Session){
			tunnelID = ((LSPTunnelIPv6Session)session).getTunnelID();
		}
		
		logInfo("Label-switched path (ID="+tunnelID+") was removed by the remote peer with sucess!");
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the runTest
	 */
	public boolean isRunTest() {
		return runTest;
	}

	/**
	 * @param runTest the runTest to set
	 */
	public void setRunTest(boolean runTest) {
		this.runTest = runTest;
	}

	/**
	 * executa todos as testes dos pacote br.ufabc.controlplane.testes
	 * */
	private void executeTests() {
		executeTests = new ExecuteTests();
		executeTests.start();

	}

	/**Receives a list of alarms gmpls*/
	public void analyzeEventFromDataPlane(List<AlarmGmpls> listAlarm) {
		DataPlaneEvent dpEvent = null;
		State state = null;
		PriorityQueue<AlarmGmpls> pq = new PriorityQueue<AlarmGmpls>();
		if (dataPlane.getIdLSP() > -1) {
			if (dataPlane.isDownstream())
				state = ControlPlane.getInstance().getResvState(dataPlane.getIdLSP());
			else 
				state = ControlPlane.getInstance().getPathState(dataPlane.getIdLSP());
		} 

		RsvpNotifyIndication notifyIndication = null;
		if ( listAlarm.size() > 1 ) {

			pq.addAll(listAlarm);

			AlarmGmpls alarmFirstPriority = pq.poll();
			dpEvent = alarmFirstPriority.getDataPlaneEvent();
			int priority = alarmFirstPriority.getPriority();
			switch (priority){
			case AlarmGmpls.PRIORITY_CRITICAL:
				if (dpEvent == DataPlaneEvent.LOS){
					break;
				} else if (dpEvent == DataPlaneEvent.LOF){
					break;
				}
			}

		}
	}

	/**receives only a event*/
	public void analyzeEventFromDataPlane(DataPlaneEvent dpEvent) {
		//		State state = null;
		//		if (dataPlane.getIdLSP() > -1) {
		//			if (dataPlane.isDownstream())
		//				state = getResvState(dataPlane.getIdLSP());
		//			else 
		//				state = getPathState(dataPlane.getIdLSP());
		//		} 
		System.out.println("entrou analyzeEventFromDataPlane: " + dpEvent);
		State state = null;
		if (dataPlane.getIdLSP() > -1) {
			System.out.println("ID LSP = " + dataPlane.getIdLSP());
			state = getPathState(dataPlane.getIdLSP());
		} 
	
		
		RsvpNotifyIndication notifyIndication = null;

		if (dpEvent != null) {
			log(Level.WARNING, "ERRO NO PLANO DE DADOS: " + dpEvent);
			long now = System.currentTimeMillis() - ControlPlane.START_TIME;
			
			if (!dataPlane.hasAmplifierOut()) {
				switch (dpEvent){
				case DISCONNECT:
					
					try {
						
						removeLSP(dataPlane.getIdLSP());
						SENT_NOTIFY = true;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
					
				
				case LOS:
					if (!state.getNotifyListeners().isEmpty()){
						notifyIndication = RsvpNotifyIndication.OPTICAL_BELOW_MINIMUM;
						SENT_NOTIFY = true;
					} else {
						try {
							log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
									"O LSP será removido devido a " + dpEvent);
							removeLSP(dataPlane.getIdLSP());
						} catch (Exception e) {
							
							logSevere(e.getMessage(), e);
						}
					

					}
					break;
				case POWER_IN_BELOW_LIMITES:
					System.out.println("****ponto 4");
					if (!state.getNotifyListeners().isEmpty()){
						System.out.println("****ponto 5");
						notifyIndication = RsvpNotifyIndication.OPTICAL_BELOW_MINIMUM;
						SENT_NOTIFY = true;
					} else {
						try {
							log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
									"O LSP será removido devido a " + dpEvent);
							removeLSP(dataPlane.getIdLSP());
						} catch (Exception e) {
							
							logSevere(e.getMessage(), e);
						}
					

					}
					break;
				case POWER_IN_ABOVE_LIMITES:
					if (!state.getNotifyListeners().isEmpty()){
						notifyIndication = RsvpNotifyIndication.OPTICAL_ABOVE_MAXIMUM;
						SENT_NOTIFY = true;
					} else {
						try {
							log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
									"O LSP será removido devido a " + dpEvent);
							removeLSP(dataPlane.getIdLSP());
						} catch (Exception e) {
							
							logSevere(e.getMessage(), e);
						}
					

					}
					break;
				case LOF:
					log(Level.WARNING, "Entrou LOF: não tem amplificador");
					if (!state.getNotifyListeners().isEmpty()){
						log(Level.WARNING, "Entrou lista de listeners não está vazia (n = " + state.getNotifyListeners().size() + ")");
						notifyIndication = RsvpNotifyIndication.ELETRICAL_BELOW_MINIMUM;
						SENT_NOTIFY = true;
					} else {
						try {
							log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
									"O LSP será removido devido a " + dpEvent);
							removeLSP(dataPlane.getIdLSP());
						} catch (Exception e) {
							
							logSevere(e.getMessage(), e);
						}
					

					}
					break;
				case BIP8_ERROR_RATE:
					log(Level.WARNING, "Entrou BIP-8: não tem amplificador");
					if (!state.getNotifyListeners().isEmpty()){
						log(Level.WARNING, "Entrou lista de listeners não está vazia (n = " + state.getNotifyListeners().size() + ")");
						notifyIndication = RsvpNotifyIndication.ELETRICAL_BELOW_MINIMUM;
						SENT_NOTIFY = true;
					} else {
						try {
							log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
									"O LSP será removido devido a " + dpEvent);
							removeLSP(dataPlane.getIdLSP());
						} catch (Exception e) {
							
							logSevere(e.getMessage(), e);
						}
					

					}
					break;
				case BEI_REMOTO:
					log(Level.WARNING, dpEvent.toString() + ", NÃO É POSSÍVEL FAZER NADA SOBRE ESTE EVENTO!");
					break;
				
				}

			} else {

				switch (dpEvent){
				
				case DISCONNECT:
					try {
						
						if (!state.getNotifyListeners().isEmpty()){
							notifyIndication = RsvpNotifyIndication.LSP_LOCALLY_FAILED;
						} 
						//removeLSP(dataPlane.getIdLSP());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
					
				
					
				case LOS:
					if (!state.getNotifyListeners().isEmpty()){
						notifyIndication = RsvpNotifyIndication.OPTICAL_BELOW_MINIMUM;
					} else {
						try {
							log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
									"O LSP será removido devido a " + dpEvent);
							removeLSP(dataPlane.getIdLSP());
						} catch (Exception e) {
							
							logSevere(e.getMessage(), e);
						}
					

					}
					break;
				case POWER_IN_BELOW_LIMITES:
					if( !dataPlane.increaseGainIn() ) {
						if (!state.getNotifyListeners().isEmpty()){
							notifyIndication = RsvpNotifyIndication.OPTICAL_BELOW_MINIMUM;
							SENT_NOTIFY = true;
						} else {
							try {
								log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
										"O LSP será removido devido a " + dpEvent);
								removeLSP(dataPlane.getIdLSP());
							} catch (Exception e) {

								logSevere(e.getMessage(), e);
							}


						}
					}
					break;
				case POWER_IN_ABOVE_LIMITES:
					if( !dataPlane.decreaseGainIn() ) {
						if (!state.getNotifyListeners().isEmpty()){
							notifyIndication = RsvpNotifyIndication.OPTICAL_ABOVE_MAXIMUM;
							SENT_NOTIFY = true;
						} else {
							try {
								log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
										"O LSP será removido devido a " + dpEvent);
								removeLSP(dataPlane.getIdLSP());
							} catch (Exception e) {

								logSevere(e.getMessage(), e);
							}


						}
					}
					break;
				case LOF:
					if( !dataPlane.increaseGainIn() ) {
						if (!state.getNotifyListeners().isEmpty()){
							notifyIndication = RsvpNotifyIndication.ELETRICAL_BELOW_MINIMUM;
							
						} else {
							try {
								log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
								"O LSP será removido devido a " + dpEvent);
								removeLSP(dataPlane.getIdLSP());
							} catch (Exception e) {
								
								logSevere(e.getMessage(), e);
							}
						}
					}

					break;
				case BIP8_ERROR_RATE:
					if (!dataPlane.increaseGainIn()){
						log(Level.WARNING, "Entrou BIP-8: tem amplificador");
						if (!state.getNotifyListeners().isEmpty()){
							log(Level.WARNING, "Entrou lista de listeners não está vazia (n = " + state.getNotifyListeners().size() + ")");
							notifyIndication = RsvpNotifyIndication.ELETRICAL_BELOW_MINIMUM;
							SENT_NOTIFY = true;
						} else {
							try {
								log(Level.WARNING, "Não há servidor de notificação para este LSP. " +
										"O LSP será removido devido a " + dpEvent);
								removeLSP(dataPlane.getIdLSP());
							} catch (Exception e) {

								logSevere(e.getMessage(), e);
							}
						}
					}
					break;
				case BEI_REMOTO:
					log(Level.WARNING, dpEvent.toString() + " Tentando aumentar o ganho de saída");
					if (!dataPlane.increaseGainOut()){
						log(Level.WARNING, "Ganho no limite máximo permitido!");
					}
					break;
				}
			} //else não tem amplificador

		} // fim if DPEvent
		
		long now = System.currentTimeMillis() - START_TIME;

		if (notifyIndication != null){
			log( Level.WARNING, "Enviando NotifyMessage: " + dpEvent + " em :" + now/1000 +
			" s\nResetando contadores ..." );
			sendRsvpNotifyMessage(state, notifyIndication,state.getNotifyListeners());
			dataPlane.reserCountersFec();
			
		}	
		

	}

	public void setConfiguration(Configuracao conf) {
		this.conf = conf;
		setName(conf.getNomeLocal());
		setTimeout(conf.getTimeout());
		delayToRefresh = conf.getUpdateLSP();
		setRunTest(conf.isRunTests());
		
	} 

	public Configuracao getConf(){
		return this.conf;
	}

}
