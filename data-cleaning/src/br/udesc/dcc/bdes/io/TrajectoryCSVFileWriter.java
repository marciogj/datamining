package br.udesc.dcc.bdes.io;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import br.udesc.dcc.bdes.analysis.deprecated.EvaluatedTrajectory;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

public class TrajectoryCSVFileWriter {

	public static void write(EvaluatedTrajectory evaluatedTrajectory, String filename) throws Exception {
		write(evaluatedTrajectory.getTrajectory().getCoordinates(), filename);
	}
	
	public static void write(Trajectory trajectory, String filename) throws Exception {
		write(trajectory.getCoordinates(), filename);
	}

	public static void write(Collection<Coordinate> coordinates, String filename) throws Exception {
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			for (Coordinate coordinate : coordinates) {
				writer.println(
						coordinate.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE) + ", " +
								DataFormatter.format(coordinate.getLongitude()) + ", " + 
								DataFormatter.format(coordinate.getLatitude()) + ", " + 
								DataFormatter.format(coordinate.getAltitude()) + ", " + 
								DataFormatter.format(coordinate.getSpeed()) + ", " + 
								DataFormatter.format(coordinate.getAcceleration())
						);

			}
			writer.close();
		}
	}

}
