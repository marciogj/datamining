package br.udesc.dcc.bdes.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import br.udesc.dcc.bdes.analysis.TrajectoryCleaner;
import br.udesc.dcc.bdes.geolife.GeolifeLabelFields;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

public class Geolife2Weka {

	public static void main(String[] args) {
		//String geolifeDir = "C:\\Users\\marcio.jasinski\\tmp\\2015.11.20";
		String geolifeDir = "C:\\Users\\marcio.jasinski\\OneDrive\\GPS_DATA\\geolife-5-years\\Data";
		File geolife = new File(geolifeDir);
		if (!geolife.exists()) {
			System.out.println("Geolife dir does not exists: " + geolifeDir);
		}

		if (!geolife.isDirectory()) {
			System.out.println("Input must be a directory...");
		}


		for(File dir : geolife.listFiles() ) {
			System.out.println("Dir: " + dir.getAbsolutePath());
			Collection<Trajectory> trajectoriesLabels = processLabeledData(dir);
			if (trajectoriesLabels.isEmpty()) {
				System.out.println("\tNo label info on dir " + dir.getName());
				continue;
			}
			
			File[] trajectoriesFiles = loadTrajectories(dir);
			for(File trajectoryFile : trajectoriesFiles) {
				System.out.println("\tFile: " + trajectoryFile.getAbsolutePath());
				Trajectory trajectory = PltFileReader.read(trajectoryFile);
				trajectory = TrajectoryCleaner.removeNoiseCoordinates(trajectory);

				for(Coordinate coordinate : trajectory.getCoordinates()) {
					LocalDateTime coordDateTime = coordinate.getDateTime();
					for(Trajectory label : trajectoriesLabels) {
						if (coordDateTime.isAfter(label.getStart()) && coordDateTime.isBefore(label.getEnd())) {
							label.setId(trajectoryFile.getName());
							label.add(coordinate);
							System.out.println("\t>> " + coordinate + " is " + label.getTransportMean());
							break; //get out from label for since it found his place
						}
					}
				}
			}

			try {
				Collection<Trajectory> trajectories = new LinkedList<>();
				for(Trajectory trajectory : trajectoriesLabels) {
					if (trajectory.getId() == null ) continue;
					trajectories.add(trajectory);
				}

				TrajectoryWekaFileWriter.write(trajectories, "geolife-trajectories.arff");
			} catch (Exception e) {
				e.printStackTrace();
			}


		}

	}

	private static Collection<Trajectory> processLabeledData(File dir) {
		Optional<File> label = loadLabel(dir);
		Collection<Trajectory> trajectories = new LinkedList<>();
		if (label.isPresent()) {
			trajectories = loadLabelsTrajectories(label.get());
		}

		return trajectories;
	}

	private static Collection<Trajectory> loadLabelsTrajectories(File file) {
		Collection<Trajectory> trajectories = new LinkedList<>();
		try ( BufferedReader reader = new BufferedReader(new FileReader(file))) {			
			String line = reader.readLine(); //first line is the header

			while( line != null ) {
				line = reader.readLine();
				if (line != null && line.trim().length() != 0) {
					trajectories.add(parse(line));
				}
			}

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return trajectories;
	}

	private static Trajectory parse(String line) {
		String[] parts = line.split(" +|\t+");
		LocalDateTime start = convertDateTime(parts[GeolifeLabelFields.START_DATE.getIndex()], parts[GeolifeLabelFields.START_TIME.getIndex()]);
		LocalDateTime end = convertDateTime(parts[GeolifeLabelFields.END_DATE.getIndex()], parts[GeolifeLabelFields.END_TIME.getIndex()]);

		Trajectory trajectory = new Trajectory();
		trajectory.setStart(start);
		trajectory.setEnd(end);
		trajectory.setTransportMean(parts[GeolifeLabelFields.TRANSPORT_MODE.getIndex()]);

		return trajectory;
	}

	private static LocalDateTime convertDateTime(String date, String time) { 
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		return LocalDateTime.parse(date + " " + time, formatter);
	}

	private static File[] loadTrajectories(File dir) {
		File[] trajectoryDir = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().equals("trajectory");
			}
		});

		if (trajectoryDir.length != 0) {
			return trajectoryDir[0].listFiles();
		}

		return new File[0];
	}


	private static Optional<File> loadLabel(File dir) {
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().equals("labels.txt");
			}
		});

		if (files.length != 0) {
			return Optional.of(files[0]);
		}

		return Optional.empty();
	}



}
