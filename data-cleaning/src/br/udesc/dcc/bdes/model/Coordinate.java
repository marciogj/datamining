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
	protected double latitude; 
	protected double longitude; 
	protected double altitude;
	protected Double speed;
	protected double acceleration;
	protected double accuracy;
	protected double bearing;
	protected LocalDateTime dateTime;
	
	//----
	protected boolean isNoise;
	protected boolean isNearImportantePlace;
	protected boolean isOvertake;
	//protected String weatherCondition;
	
	protected TransportType type;
	
	protected double maxSpeed;
	protected SafetyClassification speedCategory;
	protected SafetyClassification accCategory;
	
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

	public Coordinate(Speed speed) {
		this.speed = speed.getMs();
	}

	public Coordinate(Coordinate coord) {
		this.acceleration = coord.getAcceleration();
		this.accuracy = coord.accuracy;
		this.altitude = coord.altitude;
		this.bearing = coord.bearing;
		this.dateTime = coord.dateTime;
		this.latitude = coord.latitude;
		this.longitude = coord.longitude;
		this.speed = coord.speed;
		this.isNoise = coord.isNoise;
		this.isNearImportantePlace = coord.isNearImportantePlace;
		this.isOvertake = coord.isOvertake;
		this.type = coord.type;
		this.maxSpeed = coord.maxSpeed;
		this.speedCategory = coord.speedCategory;
		this.accCategory = coord.accCategory;
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
	
/*
	public double getAngle(Coordinate target) {
		//http://www.movable-type.co.uk/scripts/latlong.html
		//Formula:	θ = atan2( sin Δλ ⋅ cos φ2 , cos φ1 ⋅ sin φ2 − sin φ1 ⋅ cos φ2 ⋅ cos Δλ )
		//where	φ1,λ1 is the start point, φ2,λ2 the end point (Δλ is the difference in longitude)
		double φ1 = latitude;
		double φ2 = target.getLatitude();
		double λ1 = longitude;
		double λ2 = target.getLongitude();
		
		double Δλ = λ2 - λ1;
		double y = Math.sin(Δλ) * Math.cos(φ2);
		double x = Math.cos(φ1) * Math.sin(φ2) - Math.sin(φ1) * Math.cos(φ2) * Math.cos(Δλ);
		double θ = Math.atan2(y, x);
		
		double angle = Math.toDegrees(θ);
		if(angle < 0){
		    	angle += 360;
		}
		
		return angle;
	}
	*/
	
	//---
	
	public boolean isNoise() {
		return isNoise;
	}
	
	public void setNoise(boolean isNoise) {
		this.isNoise = isNoise;
	}
	
	public boolean isNearImportantePlace() {
		return isNearImportantePlace;
	}
	
	public void setNearImportantePlace(boolean isNearImportantePlace) {
		this.isNearImportantePlace = isNearImportantePlace;
	}
	
	public boolean isOvertake() {
		return isOvertake;
	}
	
	public void setOvertake(boolean isOvertake) {
		this.isOvertake = isOvertake;
	}
	
	public TransportType getTransportType() {
		return type;
	}
	
	public void setTransportType(TransportType type) {
		this.type = type;
	}
	
	public double getMaxSpeed() {
		return maxSpeed;
	}
	
	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public SafetyClassification getSpeedCategory() {
		return speedCategory;
	}
	
	public void setSpeedCategory(SafetyClassification speedCategory) {
		this.speedCategory = speedCategory;
	}
	
	public SafetyClassification getAccCategory() {
		return accCategory;
	}
	
	public void setAccCategory(SafetyClassification accCategory) {
		this.accCategory = accCategory;
	}

	
	
}
