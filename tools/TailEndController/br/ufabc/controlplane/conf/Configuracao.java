package br.ufabc.controlplane.conf;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class Configuracao {
	private String nomeLocal;
	private String nomeExperimento;
	private String ipSPVL;
	private String ipSPVJ;
	private boolean temAmplificador;
	private int timeout;
	private int updateLSP;
	private boolean downstream;
	private int incrementGainValue;
	private int initialGainValue;
	private boolean runTests;
	private int dataPlaneTimeToWait;
	private int numberOfPowerSamples;
	private int intervalBetweenSamples;
	private int lamda;

	public static Configuracao carregarConfiguracao(String arq_conf) throws ExcecaoConfiguracao, IOException, SAXException{
		Digester digester = new Digester();
		digester.setValidating( false );

		digester.addObjectCreate( "configuracao",Configuracao.class);


		digester.addBeanPropertySetter("configuracao/nome_local", "nomeLocal" );
		digester.addBeanPropertySetter("configuracao/nome_experimento", "nomeExperimento" );
		digester.addBeanPropertySetter("configuracao/ip_spvl", "ipSPVL" );
		digester.addBeanPropertySetter("configuracao/ip_spvj", "ipSPVJ" );
		digester.addBeanPropertySetter("configuracao/tem_amplificador", "temAmplificador" );
		digester.addBeanPropertySetter("configuracao/timeout_lsp", "timeout" );
		digester.addBeanPropertySetter("configuracao/update_lsp", "updateLSP" );
		digester.addBeanPropertySetter("configuracao/downstream", "downstream" );
		digester.addBeanPropertySetter("configuracao/increment_gain_value", "incrementGainValue" );
		digester.addBeanPropertySetter("configuracao/initial_gain_value", "initialGainValue" );
		digester.addBeanPropertySetter("configuracao/run_tests", "runTests" );
		digester.addBeanPropertySetter("configuracao/data_plane_time_to_wait", "dataPlaneTimeToWait" );
		digester.addBeanPropertySetter("configuracao/number_of_power_samples", "numberOfPowerSamples" );
		digester.addBeanPropertySetter("configuracao/interval_between_samples", "intervalBetweenSamples" );
		digester.addBeanPropertySetter("configuracao/lamda", "lamda" );

		File file = new File(arq_conf);
		Reader reader= new FileReader(file);

		Configuracao conf = (Configuracao)digester.parse( reader );
		return conf;
	}

	/**
	 * @return the nomeLocal
	 */
	public String getNomeLocal() {
		return nomeLocal;
	}

	/**
	 * @param nomeLocal the nomeLocal to set
	 */
	public void setNomeLocal(String nomeLocal) {
		this.nomeLocal = nomeLocal;
	}

	/**
	 * @return the ipSPVL
	 */
	public String getIpSPVL() {
		return ipSPVL;
	}

	/**
	 * @param ipSPVL the ipSPVL to set
	 */
	public void setIpSPVL(String ipSPVL) {
		this.ipSPVL = ipSPVL;
	}

	/**
	 * @return the ipSPVJ
	 */
	public String getIpSPVJ() {
		return ipSPVJ;
	}

	/**
	 * @param ipSPVJ the ipSPVJ to set
	 */
	public void setIpSPVJ(String ipSPVJ) {
		this.ipSPVJ = ipSPVJ;
	}

	/**
	 * @return the temAmplificador
	 */
	public boolean isTemAmplificador() {
		return temAmplificador;
	}

	/**
	 * @param temAmplificador the temAmplificador to set
	 */
	public void setTemAmplificador(boolean temAmplificador) {
		this.temAmplificador = temAmplificador;
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
	 * @return the downstream
	 */
	public boolean isDownstream() {
		return downstream;
	}

	/**
	 * @param downstream the downstream to set
	 */
	public void setDownstream(boolean downstream) {
		this.downstream = downstream;
	}

	/**
	 * @return the runTests
	 */
	public boolean isRunTests() {
		return runTests;
	}

	/**
	 * @param runTests the runTests to set
	 */
	public void setRunTests(boolean runTests) {
		this.runTests = runTests;
	}

	/**
	 * @return the incrementGainValue
	 */
	public int getIncrementGainValue() {
		return incrementGainValue;
	}

	/**
	 * @param incrementGainValue the incrementGainValue to set
	 */
	public void setIncrementGainValue(int incrementGainValue) {
		this.incrementGainValue = incrementGainValue;
	}

	/**
	 * @return the initialGainValue
	 */
	public int getInitialGainValue() {
		return initialGainValue;
	}

	/**
	 * @param initialGainValue the initialGainValue to set
	 */
	public void setInitialGainValue(int initialGainValue) {
		this.initialGainValue = initialGainValue;
	}

	/**
	 * @return the updateLSP
	 */
	public int getUpdateLSP() {
		return updateLSP;
	}

	/**
	 * @param updateLSP the updateLSP to set
	 */
	public void setUpdateLSP(int updateLSP) {
		this.updateLSP = updateLSP;
	}

	/**
	 * @return the dataPlaneTimeToWait
	 */
	public int getDataPlaneTimeToWait() {
		return dataPlaneTimeToWait;
	}

	/**
	 * @param dataPlaneTimeToWait the dataPlaneTimeToWait to set
	 */
	public void setDataPlaneTimeToWait(int dataPlaneTimeToWait) {
		this.dataPlaneTimeToWait = dataPlaneTimeToWait;
	}

	/**
	 * @return the numberOfPowerSamples
	 */
	public int getNumberOfPowerSamples() {
		return numberOfPowerSamples;
	}

	/**
	 * @param numberOfPowerSamples the numberOfPowerSamples to set
	 */
	public void setNumberOfPowerSamples(int numberOfPowerSamples) {
		this.numberOfPowerSamples = numberOfPowerSamples;
	}

	/**
	 * @return the intervalBetweenSamples
	 */
	public int getIntervalBetweenSamples() {
		return intervalBetweenSamples;
	}

	/**
	 * @param intervalBetweenSamples the intervalBetweenSamples to set
	 */
	public void setIntervalBetweenSamples(int intervalBetweenSamples) {
		this.intervalBetweenSamples = intervalBetweenSamples;
	}

	/**
	 * @return the nomeExperimento
	 */
	public String getNomeExperimento() {
		return nomeExperimento;
	}

	/**
	 * @param nomeExperimento the nomeExperimento to set
	 */
	public void setNomeExperimento(String nomeExperimento) {
		this.nomeExperimento = nomeExperimento;
	}

	/**
	 * Atribui o valor do canal ótico
	 * @lamda canal otico
	 * */
	public void setLamda(int lamda) {
		this.lamda = lamda;
	}

	/**
	 * Retorna o valor do canal ótico
	 * @retur canal otico
	 * */
	public int getLamda() {
		return lamda;
	}




}
