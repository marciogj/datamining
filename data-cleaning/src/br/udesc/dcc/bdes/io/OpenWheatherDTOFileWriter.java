package br.udesc.dcc.bdes.io;

import java.io.PrintWriter;

import br.udesc.dcc.bdes.openweather.dto.OpenWeatherConditionDTO;

import com.google.gson.Gson;

public class OpenWheatherDTOFileWriter {

	public static void write(OpenWeatherConditionDTO openWeatherDto, String filename) {
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			Gson json = new Gson();
			String jsonStr = json.toJson(openWeatherDto);
			writer.println(jsonStr);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
