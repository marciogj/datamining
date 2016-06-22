package br.udesc.dcc.bdes.io;

import java.io.PrintWriter;

import br.udesc.dcc.bdes.google.GeocodeAddress;

import com.google.gson.Gson;

public class GeocodeAddressDTOFileWriter {
	
	public static void write(GeocodeAddress addressDto, String filename) {
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			Gson json = new Gson();
			String jsonStr = json.toJson(addressDto);
			writer.println(jsonStr);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
