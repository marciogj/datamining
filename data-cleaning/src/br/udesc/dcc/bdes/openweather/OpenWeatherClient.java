package br.udesc.dcc.bdes.openweather;

import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.udesc.dcc.bdes.openweather.dto.OpenWeatherConditionDTO;

public class OpenWeatherClient {
	public static final String OPEN_WEATHER_API = "http://api.openweathermap.org";
	
	public static Optional<OpenWeatherConditionDTO> weatherByCooordinate(double latitude, double longitude, String apiKey) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(OPEN_WEATHER_API);
		Response response = target.path("data").path("2.5").path("weather")
				.queryParam("lat", latitude)
				.queryParam("lon", longitude)
				.queryParam("appid", apiKey)
				.request(MediaType.APPLICATION_JSON).get();
		
		OpenWeatherConditionDTO weather = response.readEntity(OpenWeatherConditionDTO.class);
		return Optional.of(weather);
	}

}
