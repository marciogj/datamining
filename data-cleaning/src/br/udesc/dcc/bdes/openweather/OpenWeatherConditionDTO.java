package br.udesc.dcc.bdes.openweather;

import java.util.List;

/**
 * 
 * Based on API of OpenWeather
 * Current weather is specified on follow URL: http://openweathermap.org/current
 * 
 */
public class OpenWeatherConditionDTO {
	public CoordDTO coord;
	public List<WeatherDTO> weather;
	public String base;
	public TemperatureDTO main;
	public WindDTO wind;
	public String dt;
	public SystemDTO sys;
	public String id;
	public String name;
	public int cod;
	
}

