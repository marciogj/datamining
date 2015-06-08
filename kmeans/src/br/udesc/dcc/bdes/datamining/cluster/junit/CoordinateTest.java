package br.udesc.dcc.bdes.datamining.cluster.junit;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import br.udesc.dcc.bdes.datamining.cluster.element.Coordinate;

public class CoordinateTest {
	
	@Test
	public void distanceTest() {
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
