package br.udesc.dcc.bdes;
import java.util.Locale;


public class CoordinateGridPrinter {

	public static void main(String[] args) {
		double lon10k = 0.09771d;
		double lat10k = 0.09005d;
		
		//RJ Open Weather coordinates
		double lat0 = -22.73439;
		double lon0 = -43.67600;
		
		double lat, lon;
		
		int rows = 4;
		int cols = 6;
		
		Locale.setDefault(new Locale("en", "US"));
		lat = lat0;
		lon = lon0;
		for (int i=0; i < rows; i++) {
			for(int j=0; j < cols; j++) {
				System.out.println(String.format ("%.5f", lat) + "," + String.format ("%.5f", lon));
				lon += lon10k;
			}
			lat -= lat10k;
			lon = lon0;
		}
		
	}

	
}
