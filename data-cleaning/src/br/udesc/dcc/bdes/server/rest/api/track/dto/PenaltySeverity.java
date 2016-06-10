package br.udesc.dcc.bdes.server.rest.api.track.dto;

public enum PenaltySeverity {
	SEVERE(20),
	VERY_SEVERE(50);
	
	int value;
	
	PenaltySeverity(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}