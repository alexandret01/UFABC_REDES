/*
 * Created on 12/08/2011.
 */
package br.ufabc.polatis;

import org.snmp4j.PDU;

/**
 * @author Gustavo Sousa Pavani
 * @version 1.0
 */
public interface SNMPTrap {
	public void processPDU(PDU pdu);
}
