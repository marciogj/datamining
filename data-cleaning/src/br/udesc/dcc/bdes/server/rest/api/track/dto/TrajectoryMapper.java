package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluation;
import br.udesc.dcc.bdes.analysis.TrajectoryTelemetry;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

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
		dto.avgSpeed = String.format("%.2f km/h", telemetry.avgSpeed.getKmh());
		dto.overtakeCount = "4";
		dto.riskAlerts = "5";
		//dto.speedChanges = telemetry.speedChanges;
		dto.totalDistance = telemetry.trajectoryDistance.getKilometers() + " km";
		dto.trafficCondition = "Trânsito Intenso";
		dto.trajectoryTime = telemetry.trajectoryTime.getTime();
		dto.wheatherCondition = "Chuva moderada";
		return dto;
	}

}
