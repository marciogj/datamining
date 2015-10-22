package br.udesc.dcc.bdes.io;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import br.udesc.dcc.bdes.analysis.EvaluatedTrajectory;
import br.udesc.dcc.bdes.gis.Coordinate;

public class TrajectoryCSVFileWriter {
	
	public static void write(EvaluatedTrajectory evaluatedTrajectory, String filename) throws Exception {
		
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			
			for (Coordinate coordinate : evaluatedTrajectory.getTrajectory().getCoordinates()) {
				writer.println(
						coordinate.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE) + ", " +
						format(coordinate.getLongitude()) + ", " + 
						format(coordinate.getLatitude()) + ", " + 
						format(coordinate.getAltitude()) + ", " + 
						format(coordinate.getSpeed()) + ", " + 
						format(coordinate.getAcceleration())
				);
				
			}
			
			writer.close();
		}
	}
	
	private static String format(final double value) {
		String strValue = ""+value;
		strValue = strValue.contains(".") ? strValue : strValue + ".0";
		System.err.println(strValue);
		while (strValue.split("\\.")[1].length() < 8) {
			strValue += "0";
		}
		return strValue;
	}

}
