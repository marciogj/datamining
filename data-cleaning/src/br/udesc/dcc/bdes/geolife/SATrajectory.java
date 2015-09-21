package br.udesc.dcc.bdes.geolife;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class SATrajectory {
	private static final BigDecimal MILIS = new BigDecimal(1000);
	List<SACoordinate> coordinates = new ArrayList<>();
	SACoordinate first = null;
	SACoordinate last = null;
	
	public SATrajectory() {}
	
	public void add(GeolifeCoordinate coordinate) {
		SACoordinate saCoordinate = null;
		if(last == null) {
			saCoordinate = new SACoordinate(coordinate);
			first = saCoordinate;
			last = saCoordinate;
		} else {
			MovementInfo movement = this.evaluate(last, coordinate);
			saCoordinate = new SACoordinate(coordinate, movement.speed, movement.acceleration);
			last = saCoordinate;
		}
		
		coordinates.add(saCoordinate);
	}
	
	public MovementInfo evaluate(SACoordinate previousCoordinate, GeolifeCoordinate currentCoordinate) {
		long coordinateTime = currentCoordinate.getTimeInMillis();
		long previousCoordinateTime = previousCoordinate.getTimeInMillis();
		BigDecimal deltaTSeconds = new BigDecimal(coordinateTime - previousCoordinateTime).divide(MILIS);
		
		BigDecimal distance = currentCoordinate.distanceInBDMeters(previousCoordinate);
		BigDecimal speed = distance.divide(deltaTSeconds, MathContext.DECIMAL32);
		BigDecimal deltaV = speed.subtract( previousCoordinate.speedMetersPerSecond );
		MovementInfo movement = new MovementInfo();
		movement.speed = speed;
		movement.acceleration = deltaV.divide(deltaTSeconds, MathContext.DECIMAL32);
		
		return movement;
	}

	public List<SACoordinate> getCoordinates() {
		return coordinates;
	}

}

class MovementInfo {
	BigDecimal speed;
	BigDecimal acceleration;
}
