package br.udesc.dcc.bdes.geolife;

import java.util.ArrayList;
import java.util.List;

public class GeolifeTrajectory {
	List<GeolifeCoordinate> coordinates = new ArrayList<>();

	public GeolifeTrajectory() {
		
	}

	public void add(GeolifeCoordinate coordinate) {
		if (coordinate != null) {
			coordinates.add(coordinate);
		}
	}

	public List<GeolifeCoordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<GeolifeCoordinate> coordinates) {
		this.coordinates = coordinates;
	}
	

}
