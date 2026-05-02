package br.ufabc.polatis;

/** @author Claudecir
 *
 */

	
	import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;


	/**
	 * @param args
	 */
	
		
		public class SNMPClient {

			private String address;

			private Snmp snmp;
			
			public SNMPClient(String address) {
				super();
				this.address = address;
				try {
					start();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			// Since snmp4j relies on asynch req/resp we need a listener
			// for responses which should be closed
			/**
			 * Stop and close listener.
			 */
			public void stop() throws IOException {
				snmp.close();
			}

			/**
			 * Start a new SNMP session and start listening.
			 * @throws IOException
			 */
			private void start() throws IOException {
				//Start a new SNMP session
				TransportMapping transport = new DefaultUdpTransportMapping();
				snmp = new Snmp(transport);
				// Do not forget this line - start listening!
				transport.listen();
			}
			
			/**
			 * Returns the response from a SNMP agent of a single OID as a String.
			 * @param oid A single OID from the agent.
			 * @return The response from a SNMP agent of a single OID as a String.
			 * @throws IOException
			 */
			public String getAsString(OID oid) throws IOException {
				ResponseEvent event = this.get(new OID[]{oid});
				return event.getResponse().get(0).getVariable().toString();
			}
			
			
			public void getAsString(OID oids,ResponseListener listener) {
				try {
					snmp.send(getPDU(new OID[]{oids}), getPublicTarget(),null, listener);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			/**
			 * Get the PDU.
			 * @param oids
			 * @return
			 */
			private PDU getPDU(OID oids[]) {
				PDU pdu = new PDU();
				for (OID oid : oids) {
					pdu.add(new VariableBinding(oid));
				}
			 	   
				pdu.setType(PDU.GET);
				return pdu;
			}
			
			/**
			 * Returns the response from a list of OIDs.
			 * @param oids The list of OIDs.
			 * @return The response from a list of OIDs.
			 * @throws IOException
			 */
			public ResponseEvent get(OID oids[]) throws IOException {
			   ResponseEvent event = snmp.send(getPDU(oids), getPublicTarget(), null);
			   if(event != null) {
				   return event;
			   }
			   throw new RuntimeException("GET timed out");	  
			}
			
			/**
			 * Returns a Target, which contains information about where the data
			 * should be fetched and how.
			 * @return A Target.
			 */
			private Target getPublicTarget() {
				Address targetAddress = GenericAddress.parse(address);
				CommunityTarget target = new CommunityTarget();
				target.setCommunity(new OctetString("public"));
				target.setAddress(targetAddress);
				target.setRetries(2);
				target.setTimeout(1500);
				target.setVersion(SnmpConstants.version2c);
				return target;
			}

			/**
			 * Returns a Target, which contains information about where the data
			 * should be fetched and how.
			 * @return A Target.
			 */
			private Target getPrivateTarget() {
				Address targetAddress = GenericAddress.parse(address);
				CommunityTarget target = new CommunityTarget();
				target.setCommunity(new OctetString("private"));
				target.setAddress(targetAddress);
				target.setRetries(2);
				target.setTimeout(1500);
				target.setVersion(SnmpConstants.version2c);
				return target;
			}

			/**
			 * 
			 * @param oids
			 * @return
			 */		
			public List<List<String>> getTableAsStrings(OID[] oids) {
				TableUtils tUtils = new TableUtils(snmp, new DefaultPDUFactory());
				
				@SuppressWarnings("unchecked") 
					List<TableEvent> events = tUtils.getTable(getPublicTarget(), oids, null, null);
				
				List<List<String>> list = new ArrayList<List<String>>();
				for (TableEvent event : events) {
					if(event.isError()) {
						throw new RuntimeException(event.getErrorMessage());
					}
					List<String> strList = new ArrayList<String>();
					list.add(strList);
					for(VariableBinding vb: event.getColumns()) {
						strList.add(vb.getVariable().toString());
					}
				}
				return list;
			}
			
			public static String extractSingleString(ResponseEvent event) {
				return event.getResponse().get(0).getVariable().toString();
			} 
			
			/* Set methods */
			/**
			 * Codigo a ser melhorado.
			 */
			public String setAsString(OID oid, Variable variable) throws IOException {
				PDU pdu = new PDU();
				pdu.add(new VariableBinding(oid, variable));
				pdu.setType(PDU.SET);
				System.out.println(pdu.toString());
				ResponseEvent response = snmp.send(pdu,this.getPrivateTarget());
				return response.getResponse().getErrorStatusText();
			}
		}

	

