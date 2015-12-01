package br.udesc.dcc.bdes.io;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.fields.UdescCoordinateFields;

public class UdescCSVFileReader {
	private static final int HEADER_SIZE = 8;

	public static Trajectory read(String path) {
		return CoordinateFileReader.read(new File(path), HEADER_SIZE, UdescCSVFileReader::parse);
	}
	
	public static Trajectory read(File file) {
		return CoordinateFileReader.read(file, HEADER_SIZE, UdescCSVFileReader::parse);
	}

	private static Optional<Coordinate> parse(String line) {
		if (!line.startsWith("@")) {
			return Optional.empty();
		}
		
		String[] parts = line.split(";");
		Coordinate coordinate = new Coordinate();
		coordinate.setAltitude(Double.parseDouble(parts[UdescCoordinateFields.ALTITUDE.getIndex()]));
		coordinate.setLatitude(Double.parseDouble(parts[UdescCoordinateFields.LATITUDE.getIndex()]));
		coordinate.setLongitude(Double.parseDouble(parts[UdescCoordinateFields.LONGITUDE.getIndex()]));
		coordinate.setSpeed(Double.parseDouble(parts[UdescCoordinateFields.SPEED.getIndex()]));


		String strDate = parts[UdescCoordinateFields.YEAR.getIndex()] + "-" + parts[UdescCoordinateFields.MONTH.getIndex()] + "-" + parts[UdescCoordinateFields.DAY.getIndex()];
		String strTime = parts[UdescCoordinateFields.HOUR.getIndex()] + ":" + parts[UdescCoordinateFields.MINUTE.getIndex()] + ":" + parts[UdescCoordinateFields.SECOND.getIndex()]; 
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		coordinate.setDateTime(LocalDateTime.parse(strDate + " " + strTime, formatter));

		return Optional.of(coordinate);
	}


}
