package br.udesc.dcc.bdes.datamining.cluster.centroid.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.udesc.dcc.bdes.datamining.cluster.centroid.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.centroid.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.centroid.Printer;
import br.udesc.dcc.bdes.datamining.cluster.centroid.element.Element;
import br.udesc.dcc.bdes.datamining.cluster.centroid.element.gis.Coordinate;
import br.udesc.dcc.bdes.datamining.cluster.centroid.fuzzy.FuzzyCMeans;
import br.udesc.dcc.bdes.datamining.cluster.centroid.kmeans.KMeans;

public class CoordinateClusteringTest {
	int dataSize;
	List<Element> data;
	int k;
	int maxIterations;
	
	@Before
	public void setup() {
		dataSize = 5000;
		k = 3;
		data = createCoordinateData(dataSize, k);
		maxIterations = 100;
	}

	@Test
	public void kMeansCoordinateTest() {
		System.out.println("=== KMeans ===");
		long startTime = System.nanoTime();
		KMeans kmeans = new KMeans();
		
		System.out.println("Solution for k=" + k);
		List<Element> data = createCoordinateData(dataSize, k);
		ClusterSet solution = kmeans.findClusterDistribuition(k, data, maxIterations);
		long elapsed = System.nanoTime() - startTime;
		System.out.println(Printer.clusterSetToString(solution));
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		
		int percentageError = 3; // Lets start testing with 3% of error
		int expectedSize = dataSize/3; //a perfect distribution would be like this
		for (Cluster cluster : solution.getClusters()) {
			TestUtils.isBalanced(expectedSize, cluster.size(), percentageError);
		}
		System.out.println("=========================");
	}
	
	@Test
	public void fuzzyCMeansCoordinateTest() {
		System.out.println("=== FuzzCMeans ===");
		long startTime = System.nanoTime();
		FuzzyCMeans kmeans = new FuzzyCMeans();
		
		System.out.println("Solution for k=" + k);
		List<Element> data = createCoordinateData(dataSize, k);
		ClusterSet solution = kmeans.findClusterDistribuition(k, data, maxIterations);
		long elapsed = System.nanoTime() - startTime;
		System.out.println(Printer.clusterSetToString(solution));
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		
		int percentageError = 3; // Lets start testing with 3% of error
		int expectedSize = dataSize/3; //a perfect distribution would be like this
		for (Cluster cluster : solution.getClusters()) {
			TestUtils.isBalanced(expectedSize, cluster.size(), percentageError);
		}
		System.out.println("=========================");
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
