package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluation;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.openweather.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.openweather.WeatherDTO;
import br.udesc.dcc.bdes.server.model.TrajectoryTelemetry;

public class TrajectoryMapper {
	
	public static Coordinate fromDto(CoordinateDTO dto) {
		Coordinate coordinate = new Coordinate();
		coordinate.setLatitude(dto.latitude);
		coordinate.setLongitude(dto.longitude);
		coordinate.setAltitude(dto.altitude);
		coordinate.setAcceleration(dto.acceleration);
		coordinate.setDateTime(ZonedDateTime.parse(dto.dateTime).toLocalDateTime());
		coordinate.setSpeed(dto.speed);
		return coordinate;
	}

	public static CoordinateDTO toDto(Coordinate entity) {
		CoordinateDTO dto = new CoordinateDTO();
		dto.latitude = entity.getLatitude();
		dto.longitude = entity.getLongitude();
		dto.altitude = entity.getAltitude();
		dto.acceleration = entity.getAcceleration();
		dto.dateTime = entity.getDateTime().atZone(ZoneId.systemDefault()).toString();
		dto.speed = entity.getSpeed();
		return dto;
	}
	
	public static Trajectory fromDto(TrackDTO dto) {
		Trajectory trajectory = new Trajectory();
		trajectory.setDeviceId(dto.deviceId);
		trajectory.setUserId(dto.userId);
		dto.coordinates.forEach( c -> trajectory.add( TrajectoryMapper.fromDto(c) ));
		return trajectory;
	}

	public static TrajectorySummaryDTO toDto(TrajectoryEvaluation evaluation) {
		//TODO: Map all parameters from telemetry
		TrajectorySummaryDTO dto = new TrajectorySummaryDTO();
		TrajectoryTelemetry telemetry = evaluation.getCurrentTelemetry();
		//dto.agressiveIndex = "62";
		dto.startDateTime = evaluation.getStartDate();
		dto.endDateTime = evaluation.getEndDate();
		dto.avgSpeed = String.format("%.2f km/h", telemetry.avgSpeed.getKmh());
		dto.overtakeCount = "-";
		dto.riskAlerts = "-";
		//dto.speedChanges = telemetry.speedChanges;
		dto.totalDistance = String.format("%.2f km", telemetry.trajectoryDistance.getKilometers());
		dto.trafficCondition = "Trânsito Intenso";
		dto.trajectoryTime = telemetry.trajectoryTime.getTime();
		dto.coordinateCount = ""+evaluation.getTrajectory().size();
		
		dto.wheatherCondition = "-";
		Optional<OpenWeatherConditionDTO> weatherData = evaluation.getCurrentWeather();
		if(weatherData.isPresent()) {
			Optional<WeatherDTO> weather = weatherData.get().weather.isEmpty() ? Optional.empty() : Optional.of(weatherData.get().weather.get(0));
			if (weather.isPresent()) {
				dto.wheatherCondition = weather.get().main + " " + weather.get().description;
			}
		}
		
		
		return dto;
	}

}
