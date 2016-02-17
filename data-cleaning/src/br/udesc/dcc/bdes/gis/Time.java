package br.udesc.dcc.bdes.gis;

public class Time {
	private static int ONE_DAY_SECONDS = 24*3600;
	private static int ONE_HOUR_SECONDS = 3600;
	private static int ONE_MIN_SECONDS = 60;
	private long seconds;
	
	public Time(long seconds) {
		this.seconds = seconds;
	}
	
	public long getSeconds() {
		return seconds;
	}
	
	public double getHours() {
		return seconds/36000.0;
	}
	
	public String getTime() {
		long remaing = seconds;
		
		long days = seconds / ONE_DAY_SECONDS;
		remaing = seconds - (days * ONE_DAY_SECONDS);
		
		long hours = remaing / ONE_HOUR_SECONDS;
		remaing = remaing - (hours * ONE_HOUR_SECONDS);
		
		long minutes = remaing / ONE_MIN_SECONDS;
		remaing = remaing - (minutes * ONE_MIN_SECONDS);
		
		return twoDigits(days) + "d " + twoDigits(hours) + ":" + twoDigits(minutes) + ":" + twoDigits(remaing); 
	}
	
	private String twoDigits(long value) {
		String str = "" + value;
		return str.length() < 2 ? "0"+str : str;
	}

}
