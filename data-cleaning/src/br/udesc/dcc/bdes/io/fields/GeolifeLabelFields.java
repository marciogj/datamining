package br.udesc.dcc.bdes.io.fields;

public enum GeolifeLabelFields {

	START_DATE(0), START_TIME(1), END_DATE(2), END_TIME(3), TRANSPORT_MODE(4);

	private int index;

	GeolifeLabelFields(int index) {
		this.index = index;
	}

	public int getIndex() { 
		return index; 
	}
	
	
}
