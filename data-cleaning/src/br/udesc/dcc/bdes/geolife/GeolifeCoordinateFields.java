package br.udesc.dcc.bdes.geolife;

public enum GeolifeCoordinateFields {

	LATITUDE(0), LONGITUDE(1), ALTITUDE(3), FRACTION_1899_DATE(4), DATE(5), TIME(6);

	private int index;

	GeolifeCoordinateFields(int index) {
		this.index = index;
	}

	public int getIndex() { 
		return index; 
	}
}
