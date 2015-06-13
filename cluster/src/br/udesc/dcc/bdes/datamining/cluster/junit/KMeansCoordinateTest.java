package br.udesc.dcc.bdes.datamining.cluster.junit;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;
import br.udesc.dcc.bdes.datamining.cluster.element.gis.Coordinate;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.KMeans;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.Printer;

public class KMeansCoordinateTest {

	@Test
	public void kMeansCoordinateDistribuitionTest() {
		System.out.println("=========================");
		long startTime = System.nanoTime();
		KMeans kmeans = new KMeans();
		int maxIterations = 1000;
		
		int k = 3;
		int dataSize = 5000;
		System.out.println("Solution for k=" + k);
		List<Element> data = createCoordinateData(dataSize, k);
		
		ClusterSet solution = kmeans.findClusterDistribuition(k, data, maxIterations);
		System.out.println(Printer.clusterSetToString(solution));
		long elapsed = System.nanoTime() - startTime;
		int percentageError = 3; // Lets start testing with 3% of error
		int expectedSize = dataSize/3; //a perfect distribution would be like this
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		for (Cluster cluster : solution.getClusters()) {
			assertTrue( isBalanced(expectedSize,cluster.size(), percentageError) );
		}
		System.out.println("=========================");
	}
	
	private boolean isBalanced(int expectedSize, int actualSize, int acceptedPercentageError) {
		double acceptedError = (expectedSize * acceptedPercentageError)/100.0;
		int difference = Math.abs(actualSize - expectedSize);
		
		return difference <= acceptedError;
	}
	
	/**
	 * Created real GPS data from http://www.geomidpoint.com/random/
	 */
	private static List<Element> createCoordinateData(int numberOfElements, int k) {
		List<Element> data = new ArrayList<Element>();
		int dataDivision = numberOfElements/k;
		int remaingData = numberOfElements;
		double motion = 0.00001;
		
		Coordinate indaial = new Coordinate(-26.8997445, -49.235898099999986);
		data.addAll( createCoordinates(indaial, dataDivision, motion));
		
		remaingData -= dataDivision; 
		
		Coordinate blumenau = new Coordinate(-26.9165792, -49.07173310000002);
		data.addAll( createCoordinates(blumenau, dataDivision, motion));
		
		remaingData -= dataDivision;
		
		Coordinate florianopolis = new Coordinate(-27.5953778, -48.548049900000024);
		data.addAll( createCoordinates(florianopolis, remaingData, motion));
		
		return data;
	}
	
	private static List<Element> createCoordinates(Coordinate initialPlace, int numberOfCoordinates, double motion) {
		double latitude = initialPlace.getLatitude();
		double longitude = initialPlace.getLongitude();
		List<Element> data = new ArrayList<Element>();
		
		for(int i=1; i <= numberOfCoordinates; i++) {
			data.add(new Coordinate(latitude, longitude));
			latitude += motion;
			longitude += motion;
		}
		return data;
	}
}
