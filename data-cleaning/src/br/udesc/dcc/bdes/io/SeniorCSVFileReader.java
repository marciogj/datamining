package br.udesc.dcc.bdes.io;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import br.udesc.dcc.bdes.io.fields.SeniorCoordinateFields;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;

public class SeniorCSVFileReader {
	private static int HEADER_SIZE = 2;

	public static Trajectory read(String path) {
		return CoordinateFileReader.parseWithHeader(new File(path), HEADER_SIZE, SeniorCSVFileReader::parseCoordinate, SeniorCSVFileReader::parseHeader);
	}

	public static Trajectory read(File file) {
		return CoordinateFileReader.parseWithHeader(file, HEADER_SIZE, SeniorCSVFileReader::parseCoordinate, SeniorCSVFileReader::parseHeader);
	}

	private static Optional<Coordinate> parseCoordinate(String line) {
		String[] parts = line.split(",");
		Coordinate coordinate = new Coordinate();
		coordinate.setAltitude(Double.parseDouble(parts[SeniorCoordinateFields.ALTITUDE.getIndex()]));
		coordinate.setLatitude(Double.parseDouble(parts[SeniorCoordinateFields.LATITUDE.getIndex()]));
		coordinate.setLongitude(Double.parseDouble(parts[SeniorCoordinateFields.LONGITUDE.getIndex()]));
		coordinate.setSpeed(Double.parseDouble(parts[SeniorCoordinateFields.SPEED.getIndex()])/3.6);
		coordinate.setAccuracy(Double.parseDouble(parts[SeniorCoordinateFields.ACCURACY.getIndex()]));
		coordinate.setBearing(Double.parseDouble(parts[SeniorCoordinateFields.BEARING.getIndex()]));
		long timestamp = Long.parseLong(parts[SeniorCoordinateFields.TIMESTAMP.getIndex()]); 
		coordinate.setDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
		return Optional.of(coordinate);
	}
	
	private static Trajectory parseHeader(String headerLine, Trajectory trajectory) {
		if(headerLine.contains("@")) {
			String[] parts = headerLine.split("@");
			trajectory.setUserId(parts[1]);
			trajectory.setDeviceId(parts[0]);
		}
		return trajectory;
	}

}
