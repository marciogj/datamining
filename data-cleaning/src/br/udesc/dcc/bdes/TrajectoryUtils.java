package br.udesc.dcc.bdes;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import br.udesc.dcc.bdes.analysis.deprecated.EvaluatedTrajectory;
import br.udesc.dcc.bdes.io.TrajectoryCSVFileWriter;
import br.udesc.dcc.bdes.model.Coordinate;


public class TrajectoryUtils {

	public static void save(Collection<Coordinate> data, Collection<Coordinate> noises) {
		save("", data, noises);
	}

	public static void save(String identifier, Collection<Coordinate> data, Collection<Coordinate> noises) {
		String datetime = new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(new Date());
		try {
			TrajectoryCSVFileWriter.write(data, identifier+"_dbscan_data_"+datetime+".csv");
			TrajectoryCSVFileWriter.write(noises, identifier+"_dbscan_noise_"+datetime+".csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void save(String identifier, Collection<Coordinate> data) {
		String datetime = new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(new Date());
		try {
			TrajectoryCSVFileWriter.write(data, identifier+"_dbscan_data_"+datetime+".csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void print(EvaluatedTrajectory trajectory) {
		System.out.println(evaluatedTrajectoryToString(trajectory));
	}
	
	public static String evaluatedTrajectoryToString(EvaluatedTrajectory trajectory) {
		StringBuffer str = new StringBuffer();
		str.append("Coordinates: " + trajectory.getTotalCoordinates());
		str.append("\nCoordinate Rate: " + trajectory.getCoordinateRate() + " seconds/coordinate");
		str.append("\nDistance: " + trajectory.getTotalDistance()/1000 + " km");
		str.append("\nTime: " + trajectory.getTotalTime());

		str.append("\nAvg Speed: " + trajectory.getAvgSpeed()*3.6 + " km/h");
		str.append("\nMax Speed: " + trajectory.getMaxSpeed()*3.6 + " km/h");

		str.append("\nMax Slowdown: " + trajectory.getMaxSlowdown()*3.6 + " km/h");
		str.append("\nMax Speedup: " + trajectory.getMaxSpeedUp()*3.6 + " km/h");
		str.append("\nAcceleration changes: " + trajectory.getSpeedUpDownOscilations());
		return str.toString();
	}
	

}
