package br.udesc.dcc.bdes.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import br.udesc.dcc.bdes.geolife.GeolifeCoordinateFields;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

public class PltFileReader {
	public static int HEADER_SIZE = 6;
	
	public static Trajectory read(String path) {
		System.out.println(path );
		Trajectory trajectory = new Trajectory();
		File file = new File(path);
		try ( BufferedReader reader = new BufferedReader(new FileReader(file))) {			
			String line = null;  
			int headerCount = 0;
			while (headerCount < HEADER_SIZE) {
				line = reader.readLine();
				headerCount++;
			}
			
			line = reader.readLine();
			while( line != null ) {
				Coordinate coordinate = parse(line);
				trajectory.add(coordinate);
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return trajectory;
	}
	
	
	private static Coordinate parse(String line) {
		String[] parts = line.split(",");
		Coordinate coordinate = new Coordinate();
		coordinate.setAltitude(Double.parseDouble(parts[GeolifeCoordinateFields.ALTITUDE.getIndex()]));
		coordinate.setLatitude(Double.parseDouble(parts[GeolifeCoordinateFields.LATITUDE.getIndex()]));
		coordinate.setLongitude(Double.parseDouble(parts[GeolifeCoordinateFields.LONGITUDE.getIndex()]));
		String strDateTime = parts[GeolifeCoordinateFields.DATE.getIndex()] + " " + parts[GeolifeCoordinateFields.TIME.getIndex()];  
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		coordinate.setDateTime(LocalDateTime.parse(strDateTime, formatter));
		
		return coordinate;
	}

}
