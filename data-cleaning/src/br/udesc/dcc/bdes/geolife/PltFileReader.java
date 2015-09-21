package br.udesc.dcc.bdes.geolife;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PltFileReader {
	public static int HEADER_SIZE = 6;
	
	public static GeolifeTrajectory read(String path) {
		GeolifeTrajectory trajectory = new GeolifeTrajectory();
		File file = new File(path);
		try ( BufferedReader reader = new BufferedReader(new FileReader(file))) {			
			String line = null;  
			System.out.println("=== HEADER ====");
			int headerCount = 0;
			while (headerCount < HEADER_SIZE) {
				line = reader.readLine();
				System.out.println(line);
				headerCount++;
			}
			
			System.out.println("=== DATA ====");
			line = reader.readLine();
			while( line != null ) {
				GeolifeCoordinate coordinate = parse(line);
				trajectory.add(coordinate);
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Coordinates: " + trajectory.coordinates.size());
		System.out.println("---------");
		return trajectory;
	}
	
	
	private static GeolifeCoordinate parse(String line) {
		String[] parts = line.split(",");
		GeolifeCoordinate coordinate = new GeolifeCoordinate();
		coordinate.setAltitude(new BigDecimal(parts[GeolifeCoordinateFields.ALTITUDE.getIndex()]));
		coordinate.setLatitude(new BigDecimal(parts[GeolifeCoordinateFields.LATITUDE.getIndex()]));
		coordinate.setLongitude(new BigDecimal(parts[GeolifeCoordinateFields.LONGITUDE.getIndex()]));
		String strDateTime = parts[GeolifeCoordinateFields.DATE.getIndex()] + " " + parts[GeolifeCoordinateFields.TIME.getIndex()];  
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		coordinate.setDateTime(LocalDateTime.parse(strDateTime, formatter));
		
		return coordinate;
	}

}
