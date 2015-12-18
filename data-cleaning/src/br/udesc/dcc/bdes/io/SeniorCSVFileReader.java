package br.udesc.dcc.bdes.io;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.fields.SeniorCoordinateFields;

public class SeniorCSVFileReader {
	private static int HEADER_SIZE = 2;

	public static Trajectory read(String path) {
		return CoordinateFileReader.read(new File(path), HEADER_SIZE, SeniorCSVFileReader::parse);
	}

	public static Trajectory read(File file) {
		return CoordinateFileReader.read(file, HEADER_SIZE, SeniorCSVFileReader::parse);
	}

	private static Optional<Coordinate> parse(String line) {
		String[] parts = line.split(",");
		Coordinate coordinate = new Coordinate();
		coordinate.setAltitude(Double.parseDouble(parts[SeniorCoordinateFields.ALTITUDE.getIndex()]));
		coordinate.setLatitude(Double.parseDouble(parts[SeniorCoordinateFields.LATITUDE.getIndex()]));
		coordinate.setLongitude(Double.parseDouble(parts[SeniorCoordinateFields.LONGITUDE.getIndex()]));
		coordinate.setSpeed(Double.parseDouble(parts[SeniorCoordinateFields.SPEED.getIndex()]));
		long timestamp = Long.parseLong(parts[SeniorCoordinateFields.TIMESTAMP.getIndex()]); 
		coordinate.setDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
		return Optional.of(coordinate);
	}

}
