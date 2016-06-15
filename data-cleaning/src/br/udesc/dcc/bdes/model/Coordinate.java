package br.udesc.dcc.bdes.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * http://stackoverflow.com/questions/6754881/java-double-vs-bigdecimal-for-latitude-longtitude
 * 
 * @author marciogj
 */
public class Coordinate {
	double latitude; 
	double longitude; 
	double altitude;
	Double speed;
	double acceleration;
	double accuracy;
	double bearing;
	LocalDateTime dateTime;
	
	public Coordinate() {
		super();
	}

	public Coordinate(double latitude, double longitude, double altitude, LocalDateTime dateTime) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.dateTime = dateTime;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public long getDateTimeInMillis() {
		ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault());
		return zdt.toInstant().toEpochMilli();
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Optional<Double> getSpeed() {
		return Optional.ofNullable(speed);
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}
	
	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	@Override
	public String toString() {
		return dateTime + ", " + latitude + ", " + longitude + ", " + altitude + ", " + speed + ", " + acceleration;
	}

	/**
	 * 
	 * This uses the �haversine� formula to calculate the great-circle distance between two points � that is, the shortest distance over the earth�s surface � giving an �as-the-crow-flies� distance between the points (ignoring any hills they fly over, of course!).
	 * http://www.movable-type.co.uk/scripts/latlong.html
	 * Also avaliable in JavaScript: view-source:http://www.gpsvisualizer.com/calculators
	 * 
	 * This method is changed to return always distance in meters
	 * 
	 */
	public double distanceInMeters(Coordinate another) {
		double equatorialRadius = 6378137.0; //Earth�s equatorial radius (mean radius = 6,371km)
		double polarRadius = 6356752.0; //Earth�s polar radius (mean radius = 6,356km)

		double latitude1 = Math.toRadians(latitude);
		double latitude2 = Math.toRadians(another.getLatitude());

		double longitude1 = Math.toRadians(longitude);
		double longitude2 = Math.toRadians(another.longitude);

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
	
	public static double distance(Coordinate coordinate, Coordinate another) {
		return coordinate.distanceInMeters(another);
	}

	public double speedFrom(Coordinate previous) {
		double distance = Math.abs(distanceInMeters(previous));
		long time = Math.abs(getDateTimeInMillis() - previous.getDateTimeInMillis());
		return distance/time;
	}
	
}
