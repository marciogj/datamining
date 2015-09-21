package br.udesc.dcc.bdes.geolife;

import java.math.BigDecimal;

public class SACoordinate extends GeolifeCoordinate {
	BigDecimal speedMetersPerSecond;
	BigDecimal accelerationMetersPerSecnd;
	
	public SACoordinate(){}
	
	public SACoordinate(GeolifeCoordinate coordinate) {
		this.altitude = coordinate.getAltitude();
		this.dateTime = coordinate.getDateTime();
		this.latitude = coordinate.getLatitude();
		this.longitude = coordinate.getLongitude();
		this.accelerationMetersPerSecnd = BigDecimal.ZERO;
		this.speedMetersPerSecond = BigDecimal.ZERO;
	}
	
	public SACoordinate(GeolifeCoordinate coordinate, BigDecimal speedMS, BigDecimal accelerationMS) {
		this(coordinate);
		this.accelerationMetersPerSecnd = accelerationMS;
		this.speedMetersPerSecond = speedMS;
	}

	public BigDecimal getSpeedMetersPerSecond() {
		return speedMetersPerSecond;
	}

	public void setSpeedMetersPerSecond(BigDecimal speedMetersPerSecond) {
		this.speedMetersPerSecond = speedMetersPerSecond;
	}

	public BigDecimal getAccelerationMetersPerSecnd() {
		return accelerationMetersPerSecnd;
	}

	public void setAccelerationMetersPerSecnd(BigDecimal accelerationMetersPerSecnd) {
		this.accelerationMetersPerSecnd = accelerationMetersPerSecnd;
	}
	
}
