package br.udesc.dcc.bdes.test;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.Properties;

import org.junit.Test;

import br.udesc.dcc.bdes.openweather.OpenWeatherClient;
import br.udesc.dcc.bdes.openweather.dto.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.server.JettyServer;

public class OpenWeatherTest {

	@Test
	public void currentWeatherByCoordinates() {
		Properties properties = JettyServer.loadServerProperties();
		Optional<OpenWeatherConditionDTO> optDto = OpenWeatherClient.weatherByCooordinate(35, 139, properties.getProperty("open-weather-key"));
		
		//TODO: Consider a mock for open weather instead of call the real service.  
		assertTrue("With internet connection, a dto should be returned ", optDto.isPresent());
		OpenWeatherConditionDTO dto = optDto.get();
		
		assertEquals("Shuzenji", dto.name);
		assertEquals(34.97d, dto.coord.lat, 0.0);
		assertEquals(138.93d, dto.coord.lon, 0.0);
		assertEquals("JP", dto.sys.country);
	}

}
