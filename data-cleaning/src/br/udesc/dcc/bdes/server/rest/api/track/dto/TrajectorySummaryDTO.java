package br.udesc.dcc.bdes.server.rest.api.track.dto;

import java.util.ArrayList;
import java.util.List;


public class TrajectorySummaryDTO {
	public String evaluationId;
	public String trajectoryId;
	public String startDateTime; 
	public String endDateTime;
	
	
	public String trajectoryTime; //= "6:28";
	public Double totalDistance; // "317 km";
	public Double avgSpeed; //= "53 km/h";
	public Double maxSpeed; //= "153 km/h";
	
	public Double maxDec; //= "2 m/s²";
	public Double maxAcc; //= "4 m/s²";
	
	public List<String> streets = new ArrayList<>();
	public String mainStreet;
	
	
	public String wheatherCondition; // "Chuva Forte";
	public String trafficCondition; //= "Trânsito Intenso";
	public String hourClassification; //= comercial, madrugada
	public Integer riskAlerts; // = "3";
	public Integer speedChanges; //= "455";
	
	public Double agressiveIndex; //= "62";
	public Double speedAgressiveIndex;
	public Double accAgressiveIndex;
	public Double maxAgressiveIndex; //= "62";
	public String overtakeCount; // = "15";
	public Integer coordinateCount;
	
	public AccelerationCountDTO accEvaluation;
	public Double avgAcc; 
	
	public Integer speedUnderLimitCount = 0;
	public Integer speed10To20LimitCount = 0;
	public Integer speed21UpTo50LimitCount = 0;
	public Integer speed51UpLimitCount = 0;
	

}
