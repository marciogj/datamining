package br.udesc.dcc.bdes.datamining.cluster.element;

public class Coordinate implements Element {
	protected double latitute;
	protected double longitude;
	
	public Coordinate(double latitude, double longitude) {
		this.latitute = latitude;
		this.longitude = longitude;
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
	@Override
	public double distance(Element element) {
		Coordinate another = (Coordinate) element;
		double equatorialRadius = 6378137.0; //Earth’s equatorial radius (mean radius = 6,371km)
		double polarRadius = 6356752.0; //Earth’s polar radius (mean radius = 6,356km)
		double latitude1 = Math.toRadians(latitute);
		double latitude2 = Math.toRadians(another.latitute);
		
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
	
	@Override
	public Element plus(Element element) {
		Coordinate another = (Coordinate) element;
		return new Coordinate(latitute + another.latitute, longitude + another.longitude);
	}

	@Override
	public Element divide(double size) {
		return new Coordinate(latitute/size, longitude/size);
	}

	@Override
	public Element zero() {
		return new Coordinate(0, 0);
	}

	public double getLatitude() {
		return latitute;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String toString() {
		return "(lat:" + latitute + ", lon: " + longitude + ")";
	}

}
