package br.udesc.dcc.bdes.server.rest.api.track.dto;

public class SpeedDistDTO {
	public int underLimit;
	public int fromLimitTo10;
	public int from10to20;
	public int from20to50;
	public int over50;
	
	public int totalCount;
	
	public double underLimitAvg;
	public double fromLimitTo10Avg;
	public double from10to20Avg;
	public double from20to50Avg;
	public double over50Avg;
	
	public double underLimitPerc;
	public double fromLimitTo10Perc;
	public double from10to20Perc;
	public double from20to50Perc;
	public double over50Perc;
}
