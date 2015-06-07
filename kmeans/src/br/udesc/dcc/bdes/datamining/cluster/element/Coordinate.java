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
	 */
	@Override
	public double distance(Element element) {
		Coordinate another = (Coordinate) element;
		double R = 6371000.0; //R is earth’s radius (mean radius = 6,371km)
		double latPhi1 = Math.toRadians(latitute);
		double latPhi2 = Math.toRadians(another.latitute);
		
		double deltaPhi = Math.toRadians(another.latitute-latitute);
		double deltaLambda = Math.toRadians(another.longitude-longitude);
		
		
		double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi) +
				   Math.cos(latPhi1) * Math.cos(latPhi2) * 
				   Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
		
		double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;
		return d;
	}

	@Override
	public Element plus(Element element) {
		Coordinate another = (Coordinate) element;
		return new Coordinate(latitute + another.latitute, longitude + another.latitute);
	}

	@Override
	public Element divide(int size) {
		// TODO Auto-generated method stub
		return new Coordinate(latitute/size, longitude/size);
	}

	@Override
	public Element zero() {
		return new Coordinate(0, 0);
	}

}
