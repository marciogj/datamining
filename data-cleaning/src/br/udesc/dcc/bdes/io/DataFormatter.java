package br.udesc.dcc.bdes.io;

public class DataFormatter {
	
	public static String format(final double value) {
		return format(value, 8);
	}

	//String.format ("%.3f", number)
	public static String format(final double value, int length) {
		String strValue = ""+value;
		strValue = strValue.contains(".") ? strValue : strValue + ".0";
		int currentSize = strValue.split("\\.")[1].length(); 
		while ( currentSize < length) {
			strValue += "0";
		}
		return strValue;
	}
	
}
