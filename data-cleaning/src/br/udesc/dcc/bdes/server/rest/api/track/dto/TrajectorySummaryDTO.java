package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.util.ArrayList;
import java.util.List;

public class TrajectorySummaryDTO {
	public String evaluationId;
	public String trajectoryId;
	public String startDateTime; 
	public String endDateTime;
	
	
	public String trajectoryTime; //= "6:28";
	public String totalDistance; // "317 km";
	public String avgSpeed; //= "53 km/h";
	public String maxSpeed; //= "153 km/h";
	
	public String maxDec; //= "2 m/s²";
	public String maxAcc; //= "4 m/s²";

	public List<String> streets = new ArrayList<>();
	
	
	public String wheatherCondition; // "Chuva Forte";
	public String trafficCondition; //= "Trânsito Intenso";
	public String hourClassification; //= comercial, madrugada
	public Integer riskAlerts; // = "3";
	public Integer speedChanges; //= "455";
	
	public Integer agressiveIndex; //= "62";
	public String overtakeCount; // = "15";
	public Integer coordinateCount;
	
	public AccelerationCountDTO accEvaluation; 
	

}
