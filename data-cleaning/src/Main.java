import java.time.ZoneId;
import java.time.ZonedDateTime;

import br.udesc.dcc.bdes.geolife.GeolifeCoordinate;
import br.udesc.dcc.bdes.geolife.GeolifeTrajectory;
import br.udesc.dcc.bdes.geolife.PltFileReader;


public class Main {

	public static void main(String[] args) {
		GeolifeTrajectory trajectory = PltFileReader.read();
		GeolifeCoordinate previousCoordinate = null;
		for (GeolifeCoordinate coordinate : trajectory.getCoordinates()) {
			if (previousCoordinate != null) {
				double distance = coordinate.distanceInMeters(previousCoordinate);
				ZonedDateTime zdt = coordinate.getDateTime().atZone(ZoneId.systemDefault());
				long coordinateNanoTime = zdt.toInstant().toEpochMilli();;
				zdt = previousCoordinate.getDateTime().atZone(ZoneId.systemDefault());
				long previousCoordinateNanoTime = zdt.toInstant().toEpochMilli();;
				double secondsPassed = (coordinateNanoTime - previousCoordinateNanoTime)/1000.0;
				
				//System.out.println(coordinate.getDateTime() + " - " + previousCoordinate.getDateTime());
				//System.out.println(coordinateNanoTime + " - " + previousCoordinateNanoTime);
				
				System.out.println(distance + "m  " + secondsPassed + " s");
				double ms = distance/secondsPassed;
				double kmh = ms * 3.6;
				System.out.println("V = " + ms + " m/s");
				System.out.println("V = " + kmh + " km/h");
				System.out.println("---");
			}
			previousCoordinate = coordinate;
			
		}
		
		
	}
	
	
	
	
}
