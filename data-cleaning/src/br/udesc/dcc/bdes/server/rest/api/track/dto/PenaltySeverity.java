package br.udesc.dcc.bdes.server.rest.api.track.dto;

public enum PenaltySeverity {
	MEDIUM(10), //10-19 Exceder velocidade em até 20% (infração média)  = R$85,00 + 4 pontos ;
	SEVERE(20), //20-49 Exceder velocidade de 20 até 50% (infração grave) = R$127,00 + 5 pontos;
	VERY_SEVERE(50); // >50 Exceder velocidade acima de 50% (infração gravíssima) = R$574,00 + 7 pontos + apreensão da carteira + suspensão do direito de dirigir.
	
	int value;
	
	PenaltySeverity(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}