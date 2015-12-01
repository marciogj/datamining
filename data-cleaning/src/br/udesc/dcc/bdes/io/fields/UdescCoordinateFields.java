package br.udesc.dcc.bdes.io.fields;

public enum UdescCoordinateFields {

	ACCELEROMETER_X(1), ACCELEROMETER_Y(2), ACCELEROMETER_Z(3), 
	LATITUDE(4), LONGITUDE(5), ALTITUDE(6), SPEED(7), ACCURACY(8), BEARING(9),  BATTERY(9), 
	YEAR(11), MONTH(12), DAY(13), HOUR(14), MINUTE(15), SECOND(16), MILISECOND(17), RUNNING_TIME(18);

	private int index;

	UdescCoordinateFields(int index) {
		this.index = index;
	}

	public int getIndex() { 
		return index; 
	}
}
