package br.udesc.dcc.bdes.io.fields;

public enum SeniorCoordinateFields {

	TIMESTAMP(0), LONGITUDE(1), LATITUDE(2), ALTITUDE(3), ACCURACY(4), BEARING(5),  SPEED(6);

	private int index;

	SeniorCoordinateFields(int index) {
		this.index = index;
	}

	public int getIndex() { 
		return index; 
	}
	
	public void setIndex(int newIndex) { 
		index = newIndex; 
	}
}
