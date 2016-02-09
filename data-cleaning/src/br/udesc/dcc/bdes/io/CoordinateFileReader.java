package br.udesc.dcc.bdes.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

public class CoordinateFileReader {	
	public static int HEADER_SIZE = 8;
	
	public static Trajectory parse(String path,  int headerLines, Function<String, Optional<Coordinate>> lineParser) {
		File file = new File(path);
		return parse(file, headerLines, lineParser);
	}
	
	public static Trajectory parse(File file, int headerLines, Function<String, Optional<Coordinate>> lineParser) {
		Trajectory trajectory = new Trajectory();
		try ( BufferedReader reader = new BufferedReader(new FileReader(file))) {			
			String line = null;  
			int headerCount = 0;
			while (headerCount < headerLines) {
				line = reader.readLine();
				headerCount++;
			}
			
			line = reader.readLine();
			while( line != null ) {
				Optional<Coordinate> optCoordinate = lineParser.apply(line);
				if (optCoordinate.isPresent()) {
					trajectory.add(optCoordinate.get());
				}
				
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return trajectory;
	}
	
	public static Trajectory parseWithHeader(File file, int headerLines, Function<String, Optional<Coordinate>> lineParser, BiFunction<String, Trajectory, Trajectory> headerParser) {
		Trajectory trajectory = new Trajectory();
		try ( BufferedReader reader = new BufferedReader(new FileReader(file))) {			
			String line = null;  
			int headerCount = 0;
			while (headerCount < headerLines) {
				line = reader.readLine();
				headerParser.apply(line, trajectory);
				headerCount++;
			}
			
			line = reader.readLine();
			while( line != null ) {
				Optional<Coordinate> optCoordinate = lineParser.apply(line);
				if (optCoordinate.isPresent()) {
					trajectory.add(optCoordinate.get());
				}
				
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return trajectory;
	}

}
