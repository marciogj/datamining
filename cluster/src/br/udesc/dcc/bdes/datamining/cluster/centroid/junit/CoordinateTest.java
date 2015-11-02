package br.udesc.dcc.bdes.datamining.cluster.centroid.junit;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import br.udesc.dcc.bdes.datamining.cluster.centroid.element.gis.Coordinate;

public class CoordinateTest {
	
	/**
	 * Check details for straight line here:
	 * http://www.gpsvisualizer.com/map?format=google&units=metric&lat1=-26.8997445&lon1=-49.2358981&lat2=-27.5953778&lon2=-48.5480499&name1=-26.8997445%2C+-49.235898099999986&name2=-27.5953778%2C+-48.548049900000024&desc1=-26.8997445%2C+-49.2358981&desc2=-27.5953778%2C+-48.5480499&convert_format=&gc_segments=&gc_altitude=&tickmark_interval=&show_wpt=3&add_elevation=&trk_colorize=
	 */
	@Test
	public void distanceTest() {
		//Random GPS data might be generated here http://www.geomidpoint.com/random/
		Coordinate blumenau = new Coordinate(-26.9165792, -49.07173310000002);
		Coordinate florianopolis = new Coordinate(-27.5953778, -48.548049900000024);
		Coordinate indaial  = new Coordinate(-26.8997445, -49.235898099999986);
		Coordinate tijucas = new Coordinate(-27.240063, -48.633618299999966);
		
		double indaialBlumenau = blumenau.distance(indaial);
		double indaialTijucas = indaial.distance(tijucas);
		double indaialFlorianopolis = indaial.distance(florianopolis);
		
		double blumenauFlorianopolis = blumenau.distance(florianopolis);
		double blumenauTijucas = blumenau.distance(tijucas);
		
		double tijucasFlorianopolis = tijucas.distance(florianopolis);
		
				System.out.println("Indaial - Blumenau: "+ indaialBlumenau);
		System.out.println("Indaial - Florianopolis: " + indaialFlorianopolis);
		System.out.println("Indaial - Tijucas: " + indaialTijucas);
		System.out.println("Blumenau - Florianopolis: " + blumenauFlorianopolis);
		System.out.println("Blumenau - Tijucas: " + blumenauTijucas);
		System.out.println("Tijucas - Florianopolis: "  + tijucasFlorianopolis);
		
		
		assertTrue(indaialBlumenau < indaialTijucas);
		assertTrue(indaialTijucas < indaialFlorianopolis);
		assertTrue(blumenauTijucas < blumenauFlorianopolis);
		assertTrue(tijucasFlorianopolis > indaialBlumenau);
	}

}
