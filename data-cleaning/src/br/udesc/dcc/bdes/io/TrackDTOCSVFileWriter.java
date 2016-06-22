package br.udesc.dcc.bdes.io;

import java.io.PrintWriter;

import br.udesc.dcc.bdes.server.rest.api.track.dto.CoordinateDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;

public class TrackDTOCSVFileWriter {

	public static void write(TrackDTO trackDto, String filename) {
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			writer.println(trackDto.userId + "@" + trackDto.deviceId);
			writer.println("datetime,longitude,latitude,altitude,accuracy,bearing,speed");
			
			for (CoordinateDTO coordinate : trackDto.coordinates) {
				writer.println(
						coordinate.timestamp + ", " +
						coordinate.longitude + ", " + 
						coordinate.latitude + ", " + 
						coordinate.accuracy + ", " +
						coordinate.bearing + ", " + 
						coordinate.speed + ", "
				);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
