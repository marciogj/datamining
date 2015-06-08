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
		
		//double latitudeAverage = (latitude1 + latitude2)/2;
		
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
	/*
	function Haversine_Distance(lat1,lon1,lat2,lon2,us) {
		// http://www.movable-type.co.uk/scripts/LatLong.html
		if (Math.abs(parseFloat(lat1)) > 90 || Math.abs(parseFloat(lon1)) > 180 || Math.abs(parseFloat(lat2)) > 90 || Math.abs(parseFloat(lon2)) > 180) { return 'n/a'; }
		lat1 = deg2rad(lat1); lon1 = deg2rad(lon1);
		lat2 = deg2rad(lat2); lon2 = deg2rad(lon2);
		var dlat = lat2-lat1; // delta
		var dlon = lon2-lon1; // delta
		
		var alat = (lat1+lat2)/2; // average
		var re = 6378137; // equatorial radius
		var rp = 6356752; // polar radius
		
		var r45 = re * Math.sqrt( (1 + ( (rp*rp-re*re)/(re*re) ) * (Math.sin(45)*Math.sin(45)) ) ) // from http://www.newton.dep.anl.gov/askasci/gen99/gen99915.htm
		var a = ( Math.sin(dlat/2) * Math.sin(dlat/2) ) + ( Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon/2) * Math.sin(dlon/2) );
		var c = 2 * Math.atan( Math.sqrt(a)/Math.sqrt(1-a) );
		var d_ellipse = r45 * c;
		if (us) {
			var dist = d_ellipse / 1609.344;
			if (dist < 1) {
				return (Math.round(5280 * 1 * dist) / 1) + ' ft';
			} else {
				return (Math.round(100 * dist) / 100) + ' mi';
			}
		} else {
			var dist = d_ellipse / 1000;
			if (dist < 1) {
				return (Math.round(1000 * 1 * dist) / 1) + ' m';
			} else {
				return (Math.round(100 * dist) / 100) + ' km';
			}
		}
	}
*/
	@Override
	public Element plus(Element element) {
		Coordinate another = (Coordinate) element;
		return new Coordinate(latitute + another.latitute, longitude + another.latitute);
	}

	@Override
	public Element divide(int size) {
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
