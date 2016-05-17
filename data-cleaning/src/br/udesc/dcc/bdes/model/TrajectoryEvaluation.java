package br.udesc.dcc.bdes.model;

import java.time.LocalDate;

public class TrajectoryEvaluation {
	public TrajectoryEvaluationId id;
	public Trajectory trajectory;
	public String vehicleId;
	public LocalDate updateTime;
	public Distance trajectoryDistance;
	public Time trajectoryTime;
	public Speed maxSpeed;
	public Speed avgSpeed;
	public Acceleration maxAcc;
	public Acceleration maxDec;
	
	public int accCount;
	public int decCount;
	
	public Speed maxAllowedSpeed;
	public int overMaxAllowedSpeedCount;
	
	public Speed maxSecureAcc;
	public int overMaxSecureAccCount;
	public int overMaxSecureDecCount;
	public double coordRate;
	
		
}
