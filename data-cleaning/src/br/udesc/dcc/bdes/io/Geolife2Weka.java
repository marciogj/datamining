package br.udesc.dcc.bdes.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.fields.GeolifeLabelFields;

public class Geolife2Weka {
	
	public static void main(String[] args) {
		int ignoredCoordinates = 0;
		int matched = 0;
		
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
			List<Trajectory> trajectoriesLabels = processLabeledData(dir);
			if (trajectoriesLabels.isEmpty()) {
				System.out.println("\tNo label info on dir " + dir.getName());
				continue;
			}
			
			//Sort trajectories to allow binary search later on
			trajectoriesLabels.sort( (t1, t2) -> {
				long t1Start = t1.getStart().get().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
				long t2Start = t2.getStart().get().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
				return (int) (t1Start - t2Start);
			});
			
			
			File[] trajectoriesFiles = loadTrajectories(dir);
			for(File trajectoryFile : trajectoriesFiles) {
				System.out.println("\tFile: " + trajectoryFile.getAbsolutePath());
				Trajectory trajectory = PltFileReader.read(trajectoryFile);
				
				//int rawCoordinates = trajectory.size();
				//System.out.println("\t\tRemoving noise from  " + rawCoordinates + " coordinates");
				//trajectory = TrajectoryCleaner.removeNoiseCoordinates(trajectory);
				//System.out.println("\t\tNoise count:   " + (rawCoordinates - trajectory.size()));
				
				for(Coordinate coordinate : trajectory.getCoordinates()) {	
					Optional<Trajectory> optTrajectory = binarySearch(trajectoriesLabels, coordinate);
					if (optTrajectory.isPresent()) {
						Trajectory labeledTrajectory = optTrajectory.get();
						String id = trajectory.getUserId() == null ? trajectoryFile.getName() : trajectory.getUserId() + "_" + trajectoryFile.getName(); 
						labeledTrajectory.setUserId(id);
						labeledTrajectory.add(coordinate);
						System.out.println("\t\t>> " + coordinate + " is " + labeledTrajectory.getTransportMean());
						matched++;
					} else {
						ignoredCoordinates++;
					}
				}
				
				System.out.println("\t\tIgnored: " + ignoredCoordinates + " - Matched: " + matched);
			}

			try {
				Collection<Trajectory> trajectories = new LinkedList<>();
				for(Trajectory trajectory : trajectoriesLabels) {
					if (trajectory.getUserId() == null ) continue;
					trajectories.add(trajectory);
				}

				TrajectoryWekaFileWriter.write(trajectories, "geolife-trajectories-raw.arff");
			} catch (Exception e) {
				e.printStackTrace();
			}


		}

	}

	public static Optional<Trajectory> binarySearch(List<Trajectory> trajectories, Coordinate coordinate) {
		LocalDateTime coordDateTime = coordinate.getDateTime();
		
		Trajectory trajectory = null;
		int index = trajectories.size()/2;
		int trajectoriesSize = trajectories.size();
		int maxIndex = trajectoriesSize;
		int minIndex = 0; 
		boolean isTrajectoryCoordinate = false;
		boolean isLastIndexReached = false;
		while(!isLastIndexReached) {
			trajectory = trajectories.get(index);
			isTrajectoryCoordinate = coordDateTime.isAfter(trajectory.getStart().get()) && coordDateTime.isBefore(trajectory.getEnd().get());
			if (isTrajectoryCoordinate) {
				return Optional.of(trajectory);
			}
			isLastIndexReached = (index == 0 || index == trajectoriesSize) || (maxIndex - minIndex <= 1);
			
			if (coordDateTime.isAfter(trajectory.getStart().get())) {
				minIndex = index;
				index += (maxIndex - index)/2;
				index = index >= trajectories.size() ? trajectoriesSize - 1 : index;
			} else {
				maxIndex = index;
				index -= (index - minIndex)/2;
				index = maxIndex == 1 ? 0 : index;
			}
		}
		return Optional.empty();		
	}
	
	//isUnder(date)
	// vai no meio

	private static List<Trajectory> processLabeledData(File dir) {
		Optional<File> label = loadLabel(dir);
		List<Trajectory> trajectories = new LinkedList<>();
		if (label.isPresent()) {
			trajectories = loadLabelsTrajectories(label.get());
		}

		return trajectories;
	}

	private static List<Trajectory> loadLabelsTrajectories(File file) {
		List<Trajectory> trajectories = new LinkedList<>();
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
		//TODO: Review a better way to trust on coordinate start and end time instead of force a extra variable 
		//LocalDateTime start = convertDateTime(parts[GeolifeLabelFields.START_DATE.getIndex()], parts[GeolifeLabelFields.START_TIME.getIndex()]);
		//LocalDateTime end = convertDateTime(parts[GeolifeLabelFields.END_DATE.getIndex()], parts[GeolifeLabelFields.END_TIME.getIndex()]);

		Trajectory trajectory = new Trajectory();
		trajectory.setTransportMean(parts[GeolifeLabelFields.TRANSPORT_MODE.getIndex()]);

		return trajectory;
	}

	/*
	private static LocalDateTime convertDateTime(String date, String time) { 
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		return LocalDateTime.parse(date + " " + time, formatter);
	}
	*/

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
