package br.udesc.dcc.bdes.io;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import br.udesc.dcc.bdes.analysis.deprecated.DeprecatedEvaluatedTrajectory;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;

public class TrajectoryCSVFileWriter {

	public static void write(DeprecatedEvaluatedTrajectory evaluatedTrajectory, String filename) throws Exception {
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
								(coordinate.getSpeed().isPresent() ? DataFormatter.format(coordinate.getSpeed().get()) : 0) + ", " + 
								DataFormatter.format(coordinate.getAcceleration())
						);

			}
			writer.close();
		}
	}

}
