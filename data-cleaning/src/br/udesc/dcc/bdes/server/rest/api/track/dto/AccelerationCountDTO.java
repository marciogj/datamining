package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.util.ArrayList;
import java.util.List;

public class AccelerationCountDTO {
	public double accAvg;
	public double desaccAvg;
	public double fullAvg;
	public List<AccelerationLimitDTO> limitCount = new ArrayList<>();
}
