package br.udesc.dcc.bdes.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.fields.UdescCoordinateFields;

public class UdescCSVFileReader {
	private static int HEADER_SIZE = 8;

	public static Trajectory read(String path) {
		return CoordinateFileReader.parse(new File(path), HEADER_SIZE, UdescCSVFileReader::parse);
	}

	public static Trajectory read(File file) {
		updateHeaders(file);
		return CoordinateFileReader.parse(file, HEADER_SIZE, UdescCSVFileReader::parse);
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
	
	private static void updateHeaders(File file) {
		String headerEnd = "-----";
		int headerCount = 0;
		String[] headerColumns = null;
		//UDESC files are not standardized files.
		try ( BufferedReader reader = new BufferedReader(new FileReader(file))) {			
			String line = reader.readLine();  
			
			
			while (!headerEnd.equals(line.trim())) {
				if (line.startsWith("@;")) {
					headerColumns = line.split(";");
					updateCoordinateFields(headerColumns);
				}
				line = reader.readLine();
				headerCount++;
			}

			HEADER_SIZE = headerCount;
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void updateCoordinateFields(String[] fields) {
		for (int i = 0;i < fields.length; i++) {
			if(fields[i].equals("Accelerometer_x")) {
				UdescCoordinateFields.ACCELEROMETER_X.setIndex(i);
			} else if(fields[i].equals("Accelerometer_y")) {
				UdescCoordinateFields.ACCELEROMETER_Y.setIndex(i);
			} else if(fields[i].equals("Accelerometer_z")) {
				UdescCoordinateFields.ACCELEROMETER_Z.setIndex(i);
			} else if(fields[i].equals("Latitude")) {
				UdescCoordinateFields.LATITUDE.setIndex(i);
			} else if(fields[i].equals("Longitude")) {
				UdescCoordinateFields.LONGITUDE.setIndex(i);
			} else if(fields[i].equals("Altitude")) {
				UdescCoordinateFields.ALTITUDE.setIndex(i);
			} else if(fields[i].equals("Speed")) {
				UdescCoordinateFields.SPEED.setIndex(i);
			} else if(fields[i].equals("Accuracy")) {
				UdescCoordinateFields.ACCURACY.setIndex(i);
			} else if(fields[i].equals("Bearing")) {
				UdescCoordinateFields.BEARING.setIndex(i);
			} else if(fields[i].equals("Battery_%")) {
				UdescCoordinateFields.BATTERY.setIndex(i);
			} else if(fields[i].equals("Battery_%")) {
				UdescCoordinateFields.BATTERY.setIndex(i);
			} else if(fields[i].equals("Year")) {
				UdescCoordinateFields.YEAR.setIndex(i);
			} else if(fields[i].equals("Month")) {
				UdescCoordinateFields.MONTH.setIndex(i);
			} else if(fields[i].equals("Day")) {
				UdescCoordinateFields.DAY.setIndex(i);
			} else if(fields[i].equals("Hour")) {
				UdescCoordinateFields.HOUR.setIndex(i);
			} else if(fields[i].equals("Minute")) {
				UdescCoordinateFields.MINUTE.setIndex(i);
			} else if(fields[i].equals("Seconds")) {
				UdescCoordinateFields.SECOND.setIndex(i);
			} else if(fields[i].equals("Milliseconds")) {
				UdescCoordinateFields.MILISECOND.setIndex(i);
			} else if(fields[i].equals("Time_since_start_in_ms")) {
				UdescCoordinateFields.RUNNING_TIME.setIndex(i);
			}
		}
	}
	


}
