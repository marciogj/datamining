package br.udesc.dcc.bdes.io;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Collection;

import br.udesc.dcc.bdes.analysis.deprecated.DeprecatedEvaluatedTrajectory;
import br.udesc.dcc.bdes.analysis.deprecated.DeprecatedTrajectoryEvaluator;
import br.udesc.dcc.bdes.model.Trajectory;

public class TrajectoryWekaFileWriter {
	
	
	public static void write(Collection<Trajectory> trajectories, String filename) throws Exception {
		
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			writer.println("% 1. Title: Geolife Trajectories");
			writer.println("% 2. Source: ");
			writer.println("%      (a) Creator: Geolife");
			writer.println("%      (b) Donor: Marcio Jasinski");
			writer.println("%      (c) Date: " + LocalDate.now().getMonth().name() + ", " + LocalDate.now().getYear());
			writer.println("@RELATION geolife");
			writer.println("");
			writer.println("@ATTRIBUTE id           string");
			writer.println("@ATTRIBUTE avgSpeed     numeric");
			writer.println("@ATTRIBUTE maxSpeed     numeric");
			writer.println("@ATTRIBUTE maxSpeedUp   numeric");
			writer.println("@ATTRIBUTE maxSlowdown  numeric");
			writer.println("@ATTRIBUTE accChanges   numeric");
			writer.println("@ATTRIBUTE distance     numeric");
			writer.println("@ATTRIBUTE totalTime    numeric");
			writer.println("@ATTRIBUTE coordinates  numeric");
			writer.println("@ATTRIBUTE class  {subway, taxi, walk, train, bus, car, bike, airplane}");
			writer.println("");
			writer.println("");
			writer.println("");
			writer.println("@DATA");
			
			for (Trajectory trajectory : trajectories) {
				DeprecatedEvaluatedTrajectory evaluatedTrajectory = DeprecatedTrajectoryEvaluator.evaluate(trajectory);
				StringBuffer line = new StringBuffer();
				line.append(trajectory.getUserId() + ",");
				line.append(evaluatedTrajectory.getAvgSpeed() + ",");
				line.append(evaluatedTrajectory.getMaxSpeed() + ",");
				line.append(evaluatedTrajectory.getMaxSpeedUp() + ",");
				line.append(evaluatedTrajectory.getMaxSlowdown() + ",");
				line.append(evaluatedTrajectory.getSpeedUpDownOscilations() + ",");
				line.append(evaluatedTrajectory.getTotalDistance() + ",");
				line.append(evaluatedTrajectory.getTotalTime() + ",");
				line.append(evaluatedTrajectory.getTotalCoordinates() + ","); 
				line.append(trajectory.getTransportMean());
				
				writer.println(line.toString());

			}
			writer.close();
		}
	}
	

}
