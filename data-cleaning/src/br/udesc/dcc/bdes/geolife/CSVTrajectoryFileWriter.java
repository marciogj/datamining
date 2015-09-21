package br.udesc.dcc.bdes.geolife;

import java.io.PrintWriter;

public class CSVTrajectoryFileWriter {
	
	public static void write(SATrajectory trajectory, String filename) throws Exception {
		
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			
			for (SACoordinate coordinate : trajectory.getCoordinates()) {
				
				writer.println(
						coordinate.getDateTime() + "," +
						coordinate.getLongitude() + ", " + 
						coordinate.getLatitude() + ", " + 
						coordinate.getAltitude() + ", " + 
						coordinate.getSpeedMetersPerSecond() + ", " + 
						coordinate.getAccelerationMetersPerSecnd()
				);
				
			}	
			
			writer.close();
		}
	}

}
