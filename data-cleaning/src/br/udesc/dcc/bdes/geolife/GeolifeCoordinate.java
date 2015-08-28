package br.udesc.dcc.bdes.geolife;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class GeolifeCoordinate {
	BigDecimal latitude; 
	BigDecimal longitude; 
	BigDecimal altitude; 
	LocalDateTime dateTime; 
	
	public GeolifeCoordinate() {
		super();
	}

	public GeolifeCoordinate(BigDecimal latitude, BigDecimal longitude, 	BigDecimal altitude, LocalDateTime dateTime) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.dateTime = dateTime;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getAltitude() {
		return altitude;
	}

	public void setAltitude(BigDecimal altitude) {
		this.altitude = altitude;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public String toString() {
		return latitude + ", " + longitude + ", " + altitude + ", " + dateTime;
	}
	
	/**
	 * 
	 * This uses the ‘haversine’ formula to calculate the great-circle distance between two points – that is, the shortest distance over the earth’s surface – giving an ‘as-the-crow-flies’ distance between the points (ignoring any hills they fly over, of course!).
	 * http://www.movable-type.co.uk/scripts/latlong.html
	 * Also avaliable in JavaScript: view-source:http://www.gpsvisualizer.com/calculators
	 * 
	 * This method is changed to return always distance in meters
	 * 
	 */
	public double distanceInMeters(GeolifeCoordinate another) {
		double equatorialRadius = 6378137.0; //Earth’s equatorial radius (mean radius = 6,371km)
		double polarRadius = 6356752.0; //Earth’s polar radius (mean radius = 6,356km)
		
		double latitude1 = Math.toRadians(latitude.doubleValue());
		double latitude2 = Math.toRadians(another.getLatitude().doubleValue());
		
		double longitude1 = Math.toRadians(longitude.doubleValue());
		double longitude2 = Math.toRadians(another.longitude.doubleValue());
		
		double deltaLatitude = latitude2 - latitude1;
		double deltaLongitude = longitude2 - longitude1;
		
		// from http://www.newton.dep.anl.gov/askasci/gen99/gen99915.htm
		double pRadiusPowered = polarRadius * polarRadius;
		double eRadiusPowered = equatorialRadius * equatorialRadius;
		double sin45Powered = Math.sin(45) * Math.sin(45);
		double r45 = equatorialRadius * Math.sqrt( (1 + ( (pRadiusPowered-eRadiusPowered)/eRadiusPowered ) * sin45Powered ));
		
		
		double a = ( Math.sin(deltaLatitude/2) * Math.sin(deltaLatitude/2) ) + ( Math.cos(latitude1) * Math.cos(latitude2) * Math.sin(deltaLongitude/2) * Math.sin(deltaLongitude/2) );
		double c = 2 * Math.atan( Math.sqrt(a)/Math.sqrt(1-a) );
		double d_ellipse = r45 * c;
		
		double dist = d_ellipse / 1000;
		return (Math.round(1000 * 1 * dist) / 1);
	}
	
	
	
}
