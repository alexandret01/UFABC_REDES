package br.ufabc.dataplane.alarms;

public enum RsvpNotifyIndication {
	
	OPTICAL_BELOW_MINIMUM(1, "Potência do sinal ótico está abaixo do mínimo (domínio ótico)"),
	OPTICAL_ABOVE_MAXIMUM(2, "Potência do sinal ótico está acima do máximo (domínio ótico)"),
	ELETRICAL_BELOW_MINIMUM(1, "Potência do sinal ótico está abaixo do mínimo (domínio elétrico)"),
	LSP_LOCALLY_FAILED(11,"Falha local do LSP");
	
	int cod;
	String desc;
	RsvpNotifyIndication(int cod, String desc){
		this.cod = cod;
		this.desc = desc;
	}
	
	public String getDescription(){
		return desc;
	}
	public int getCode(){
		return cod;
	}

}
