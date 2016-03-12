package br.udesc.dcc.bdes.server.model;

import java.time.LocalDate;

import br.udesc.dcc.bdes.gis.Acceleration;
import br.udesc.dcc.bdes.gis.Distance;
import br.udesc.dcc.bdes.gis.Speed;
import br.udesc.dcc.bdes.gis.Time;

public class TrajectoryTelemetry {
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
