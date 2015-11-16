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

import br.udesc.dcc.bdes.geolife.GeolifeCoordinateFields;
import br.udesc.dcc.bdes.geolife.GeolifeLabelFields;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

public class Geolife2Weka {
	
	public static void main(String[] args) {
		String geolifeDir = "C:\\Users\\marciogj\\SkyDrive\\GPS_DATA\\geolife-5-years\\Data";
		File geolife = new File(geolifeDir);
		if (!geolife.exists()) {
			System.out.println("Geolife dir does not exists: " + geolifeDir);
		}
		
		if (!geolife.isDirectory()) {
			System.out.println("Input must be a directory...");
		}
		
		for(File file : geolife.listFiles() ) {
			if (file.isDirectory()) {
				Collection<Trajectory> trajectoriesLabels = processLabeledData(file);
				File[] trajectoriesFiles = loadTrajectories(file);
				for(File trajectoryFile : trajectoriesFiles) {
					//TODO: merge trajectory with labeled one
					//TODO: write the output to weka format
				}
				
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
				trajectories.add(parse(line));
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trajectories;
	}

	private static Trajectory parse(String line) {
		String[] parts = line.split(" +");
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
