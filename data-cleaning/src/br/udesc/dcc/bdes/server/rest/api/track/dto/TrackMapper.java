package br.udesc.dcc.bdes.server.rest.api.track.dto;

import br.udesc.dcc.bdes.gis.Coordinate;

public class TrackMapper {
	
	public Coordinate fromDto(CoordinateDTO dto) {
		Coordinate coordinate = new Coordinate();
		coordinate.setLatitude(dto.latitude);
		coordinate.setLongitude(dto.longitude);
		coordinate.setAltitude(dto.altitude);
		coordinate.setAcceleration(dto.acceleration);
		coordinate.setDateTime(dto.dateTime);
		coordinate.setSpeed(dto.speed);
		return coordinate;
	}

}
