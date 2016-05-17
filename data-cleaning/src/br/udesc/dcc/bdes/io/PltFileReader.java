package br.udesc.dcc.bdes.io;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import br.udesc.dcc.bdes.io.fields.GeolifeCoordinateFields;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;

public class PltFileReader {
	public static int HEADER_SIZE = 8;
	
	public static Trajectory read(String path) {
		File file = new File(path);
		return read(file);
	}
	
	public static Trajectory read(File file) {
		return CoordinateFileReader.parse(file, HEADER_SIZE, PltFileReader::parse);
	}
	
	
	private static Optional<Coordinate> parse(String line) {
		String[] parts = line.split(",");
		Coordinate coordinate = new Coordinate();
		coordinate.setAltitude(Double.parseDouble(parts[GeolifeCoordinateFields.ALTITUDE.getIndex()]));
		coordinate.setLatitude(Double.parseDouble(parts[GeolifeCoordinateFields.LATITUDE.getIndex()]));
		coordinate.setLongitude(Double.parseDouble(parts[GeolifeCoordinateFields.LONGITUDE.getIndex()]));
		String strDateTime = parts[GeolifeCoordinateFields.DATE.getIndex()] + " " + parts[GeolifeCoordinateFields.TIME.getIndex()];  
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		coordinate.setDateTime(LocalDateTime.parse(strDateTime, formatter));
		
		return Optional.of(coordinate);
	}

}
