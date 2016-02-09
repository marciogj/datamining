package br.udesc.dcc.bdes.server.rest.api.track.dto;

import br.udesc.dcc.bdes.gis.Coordinate;

public class TrackMapper {
	
	public static Coordinate fromDto(CoordinateDTO dto) {
		Coordinate coordinate = new Coordinate();
		coordinate.setLatitude(dto.latitude);
		coordinate.setLongitude(dto.longitude);
		coordinate.setAltitude(dto.altitude);
		coordinate.setAcceleration(dto.acceleration);
		coordinate.setDateTime(dto.dateTime);
		coordinate.setSpeed(dto.speed);
		return coordinate;
	}

	public static CoordinateDTO toDto(Coordinate entity) {
		CoordinateDTO dto = new CoordinateDTO();
		dto.latitude = entity.getLatitude();
		dto.longitude = entity.getLongitude();
		dto.altitude = entity.getAltitude();
		dto.acceleration = entity.getAcceleration();
		dto.dateTime = entity.getDateTime();
		dto.speed = entity.getSpeed();
		return dto;
	}

}
