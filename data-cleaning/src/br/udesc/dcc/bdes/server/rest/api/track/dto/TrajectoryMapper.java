package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.analysis.AccelerationEvaluator;
import br.udesc.dcc.bdes.analysis.AccelerationLimit;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.model.Acceleration;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Speed;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TrajectoryEvaluation;
import br.udesc.dcc.bdes.openweather.dto.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.openweather.dto.WeatherDTO;

public class TrajectoryMapper {
	
	public static Coordinate fromDto(CoordinateDTO dto) {
		Coordinate coordinate = new Coordinate();
		coordinate.setLatitude(dto.latitude);
		coordinate.setLongitude(dto.longitude);
		coordinate.setAltitude(dto.altitude);
		coordinate.setAcceleration(Double.parseDouble(dto.acceleration));	
		coordinate.setDateTime(ZonedDateTime.parse(dto.dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime());
		coordinate.setSpeed(Double.parseDouble(dto.speed));
		coordinate.setAccuracy(Double.parseDouble(dto.accuracy));
		coordinate.setBearing(Double.parseDouble(dto.bearing));
		return coordinate;
	}

	public static CoordinateDTO toDto(Coordinate entity) {
		CoordinateDTO dto = new CoordinateDTO();
		dto.latitude = entity.getLatitude();
		dto.longitude = entity.getLongitude();
		dto.altitude = entity.getAltitude();
		dto.acceleration = String.format("%.2f", new Acceleration(entity.getAcceleration()).getMPerSec2());
		dto.dateTime = entity.getDateTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		dto.speed = entity.getSpeed().isPresent() ? String.format("%.2f", new Speed(entity.getSpeed().get()).getKmh()) : "-";
		dto.accuracy = String.format("%.2f", entity.getAccuracy());
		dto.bearing = String.format("%.2f", entity.getBearing());
		return dto;
	}
	
	public static Trajectory fromDto(TrackDTO dto) {
		Trajectory trajectory = new Trajectory();
		trajectory.setDeviceId(dto.deviceId);
		trajectory.setUserId(dto.userId);
		dto.coordinates.forEach( c -> trajectory.add( TrajectoryMapper.fromDto(c) ));
		return trajectory;
	}

	public static TrajectorySummaryDTO toDto(TrajectoryEvaluator evaluation) {
		//TODO: Map all parameters from telemetry
		TrajectorySummaryDTO dto = new TrajectorySummaryDTO();
		dto.trajectoryId = evaluation.getTrajectory().getId().getValue();
		dto.evaluationId = evaluation.getId();
		
		TrajectoryEvaluation telemetry = evaluation.getCurrentTelemetry();
		//dto.agressiveIndex = "62";
		dto.startDateTime = evaluation.getStartDate();
		dto.endDateTime = evaluation.getEndDate();
		
		dto.avgSpeed = String.format("%.2f km/h", telemetry.avgSpeed.getKmh());
		dto.maxSpeed = String.format("%.2f km/h", telemetry.maxSpeed.getKmh());

		dto.maxAcc = String.format("%.2f m/s²", telemetry.maxAcc.getMPerSec2()); 
		dto.maxDec = String.format("%.2f m/s²", telemetry.maxDec.getMPerSec2());
		
		
		dto.overtakeCount = "-";
		dto.riskAlerts = 0;
		dto.speedChanges = telemetry.accCount + telemetry.decCount;
		dto.totalDistance = String.format("%.2f km", telemetry.trajectoryDistance.getKilometers());
		
		dto.trafficCondition = evaluation.getTrafficInfo();
		dto.hourClassification = evaluation.getTimeInfo();
		dto.trajectoryTime = telemetry.trajectoryTime.getTime();
		dto.coordinateCount = evaluation.getTrajectory().size();
		dto.accEvaluation = toDto(evaluation.getAccEvaluator());
		
		evaluation.getStreets().forEach( streetName -> dto.streets.add(streetName));
		
		
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

	private static AccelerationCountDTO toDto(AccelerationEvaluator accEval) {
		AccelerationCountDTO dto = new AccelerationCountDTO();
		int accCount = 0;
		int desaccCount = 0;
		
		for (AccelerationLimit accLimit : accEval.getAccEval()) {
			dto.limitCount.add( toDto(accLimit) );
			dto.fullAvg += accLimit.getSum();
			if (accLimit.getLimit() > 0 ) {
				dto.accAvg += accLimit.getSum(); 
				accCount += accLimit.getCount();
			} else {
				dto.desaccAvg += accLimit.getSum();
				desaccCount += accLimit.getCount();
			}	
		}
		
		dto.fullAvg = (accCount + desaccCount) == 0 ? 0 : dto.fullAvg / (accCount + desaccCount);
		dto.accAvg = accCount == 0 ? 0 : dto.accAvg / accCount;
		dto.desaccAvg = desaccCount == 0 ? 0 : dto.desaccAvg / desaccCount;
		return dto;
	}

	private static AccelerationLimitDTO toDto(AccelerationLimit accLimit) {
		AccelerationLimitDTO dto = new AccelerationLimitDTO();
		dto.avg = accLimit.getAvg();
		dto.count = accLimit.getCount();
		dto.description = accLimit.getDescription();
		dto.limit = accLimit.getLimit();
		return dto;
	}
	
	public static SpeedTelemetryDTO toDto(List<Coordinate> coordinates) {
		SpeedTelemetryDTO dto = new SpeedTelemetryDTO();
		dto.speedList = new ArrayList<>(coordinates.size());
		for (Coordinate coordinate : coordinates) {
			double speed = coordinate.getSpeed().isPresent() ? new Speed(coordinate.getSpeed().get()).getKmh() : 0;
			dto.speedList.add(Math.round(speed));
		}
		return dto;
	}

	public static TrajectoryDTO toDto(Trajectory trajectory) {
		TrajectoryDTO dto = new TrajectoryDTO();
		dto.deviceId = trajectory.getDeviceId();
		dto.id = trajectory.getId().getValue();
		dto.sourceProvider = trajectory.getSourceProvider();
		dto.transportMean = trajectory.getTransportMean();
		dto.userId = trajectory.getUserId();
		for(Coordinate coordinate : trajectory.getCoordinates()) {
			dto.coordinates.add(toDto(coordinate));
		}
		
		return dto;
	}

}
