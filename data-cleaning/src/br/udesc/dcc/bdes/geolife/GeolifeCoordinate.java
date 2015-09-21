package br.udesc.dcc.bdes.geolife;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.BitSet;


public class GeolifeCoordinate {
	BigDecimal latitude; 
	BigDecimal longitude; 
	BigDecimal altitude; 
	LocalDateTime dateTime; 

	public GeolifeCoordinate() {
		super();
	}

	public GeolifeCoordinate(BigDecimal latitude, BigDecimal longitude, BigDecimal altitude, LocalDateTime dateTime) {
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

	public long getTimeInMillis() {
		ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault());
		return zdt.toInstant().toEpochMilli();
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

	public BigDecimal distanceInBDMeters(GeolifeCoordinate another) {
		return new BigDecimal(distanceInMeters(another));
		/*
		BigDecimal equatorialRadius = new BigDecimal(6378137); //Earth’s equatorial radius (mean radius = 6,371km)
		BigDecimal polarRadius =  new BigDecimal(6356752); //Earth’s polar radius (mean radius = 6,356km)


		double latitude1 = Math.toRadians(latitude.doubleValue());
		double latitude2 = Math.toRadians(another.getLatitude().doubleValue());

		double longitude1 = Math.toRadians(longitude.doubleValue());
		double longitude2 = Math.toRadians(another.longitude.doubleValue());

		double deltaLatitude = latitude2 - latitude1;
		double deltaLongitude = longitude2 - longitude1;

		// from http://www.newton.dep.anl.gov/askasci/gen99/gen99915.htm
		BigDecimal pRadiusPowered = polarRadius.multiply(polarRadius);
		BigDecimal eRadiusPowered = equatorialRadius.multiply(equatorialRadius);
		BigDecimal sin45Powered = new BigDecimal(Math.sin(45)).pow(2);

		BigDecimal sqrtFormula = BigDecimal.ONE.add( pRadiusPowered.subtract(eRadiusPowered).divide(eRadiusPowered, MathContext.DECIMAL64).multiply(sin45Powered) );
		BigDecimal r45 = equatorialRadius.multiply( sqrt(sqrtFormula));

		BigDecimal a =  new BigDecimal( ( Math.sin(deltaLatitude/2) * Math.sin(deltaLatitude/2) ) + ( Math.cos(latitude1) * Math.cos(latitude2) * Math.sin(deltaLongitude/2) * Math.sin(deltaLongitude/2) ) );
		BigDecimal sqrtOneMinusA = sqrt(BigDecimal.ONE.subtract(a));
		BigDecimal c = new BigDecimal(2 * Math.atan( sqrt(a).divide(sqrtOneMinusA, MathContext.DECIMAL64).doubleValue()) );
		BigDecimal d_ellipse = r45.multiply(c);

		BigDecimal dist = d_ellipse.divide(new BigDecimal(1000), MathContext.DECIMAL64);
		return dist; */
	}
	
	//https://en.wikipedia.org/wiki/Methods_of_computing_square_roots
	public BigDecimal sqrt(final BigDecimal num) {
		BigDecimal bdnum = num;
		BigDecimal res = BigDecimal.ZERO;

		int i = 2;

		BitSet bitSet = new BitSet();
		bitSet.set(i);

		// "bit" starts at the highest power of four <= the argument.
		BigDecimal bit = new BigDecimal(new BigInteger(bitSet.toByteArray()));
		BigDecimal lastBit = bit;
		while (bit.compareTo(bdnum) < 0) {
			lastBit = bit;
			bit = bit.multiply(BigDecimal.valueOf(4L));
		}

		bit = lastBit;

		while (!bit.equals(BigInteger.ZERO)) {
			BigDecimal resPlusBit = res.add(bit);
			if (bdnum.compareTo(resPlusBit) >= 0) {
				bdnum = bdnum.subtract(resPlusBit);
				res = (res.divide(BigDecimal.valueOf(2))).add(bit);
			} else {
				res = res.divide(BigDecimal.valueOf(2));
			}
			bit = bit.divide(BigDecimal.valueOf(4));
		}
		return res;
	}


}
