package br.udesc.dcc.bdes.io;

public class DataFormatter {
	
	public static String format(final double value) {
		String strValue = ""+value;
		strValue = strValue.contains(".") ? strValue : strValue + ".0";
		while (strValue.split("\\.")[1].length() < 8) {
			strValue += "0";
		}
		return strValue;
	}

}
