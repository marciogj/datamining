package br.udesc.dcc.bdes.datamining.cluster.junit;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.junit.Before;
import org.junit.Test;

import br.udesc.dcc.bdes.datamining.cluster.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.Printer;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScan;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScanResult;
import br.udesc.dcc.bdes.datamining.cluster.element.Element;
import br.udesc.dcc.bdes.datamining.cluster.element.Number;
import br.udesc.dcc.bdes.datamining.cluster.fuzzy.FuzzyCMeans;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.KMeans;

public class NumberClusteringTest {
	int dataSize;
	List<Element> data;
	int k;
	int maxIterations;
	
	@Before
	public void setup() {
		dataSize = 5000;
		data = createNumberList(dataSize);
		maxIterations = 100;
		k = 3;
	}
	

	@Test
	public void fuzzyCMeansNumberTest() {
		System.out.println("=== FuzzyCMeans ===");
		System.out.println("Solution for k=" + k + " dataSize " +data.size() );

		long startTime = System.nanoTime();
		FuzzyCMeans fuzzyCMeans = new FuzzyCMeans();
		ClusterSet solution = fuzzyCMeans.findClusterDistribuition(k, data, maxIterations);
		long elapsed = System.nanoTime() - startTime;
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		System.out.println(Printer.clusterSetToString(solution));
		
		evaluateSolution(solution);
		
		System.out.println("=========================");
	}
	
	@Test
	public void kMeansNumberTest() {
		System.out.println("=== KMeansMeans ===");
		System.out.println("Solution for k=" + k + " dataSize " +data.size() );

		long startTime = System.nanoTime();
		KMeans kMeans = new KMeans();
		ClusterSet solution = kMeans.findClusterDistribuition(k, data, maxIterations);
		long elapsed = System.nanoTime() - startTime;
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		System.out.println(Printer.clusterSetToString(solution));
		
		evaluateSolution(solution);
		
		System.out.println("=========================");
	}
	
	@Test
	public void dbScan2NumberTest() {
		System.out.println("=== DBScan ===");
		
		Collection<Number> d1 = new ArrayList<>();
		d1.add(new Number(44));
		d1.add(new Number(30));
		d1.add(new Number(25));
		d1.add(new Number(20));
		d1.add(new Number(23));
		d1.add(new Number(6));
		d1.add(new Number(100));
		d1.add(new Number(115));
		d1.add(new Number(88));
		d1.add(new Number(101));
		d1.add(new Number(114));
		d1.add(new Number(1000));
		d1.add(new Number(2000));
		
		DBScan dbscan = new DBScan();
		DBScanResult solution = dbscan.dbscan(d1, 15, 5);
		System.out.println(Printer.clusterSetToString(solution.getClusterSet()));
		assertTrue(true);
	}
	
	@Test
	public void dbScanNumberTest() {
		System.out.println("=== DBScan ===");
		
		Collection<Number> d1 = new ArrayList<>();
		d1.add(new Number(9));
		d1.add(new Number(8));
		d1.add(new Number(12));
		d1.add(new Number(10));
		d1.add(new Number(15));
		d1.add(new Number(20));
		d1.add(new Number(21));
		d1.add(new Number(22));
		d1.add(new Number(25));
		d1.add(new Number(26));
		d1.add(new Number(45));
		d1.add(new Number(42));
		d1.add(new Number(50));
		d1.add(new Number(55));
		d1.add(new Number(49));
		d1.add(new Number(39));
		d1.add(new Number(35));
		d1.add(new Number(114));
		d1.add(new Number(250));
		
		
		
		
		
		long startTime = System.nanoTime();
		DBScan dbscan = new DBScan();
		double eps = 5.0;
		int minPts = 3;
		
		System.out.println("=== Apache ===");
		DBSCANClusterer<Number> apacheDbscan = new DBSCANClusterer<>(eps, minPts);
		List<org.apache.commons.math3.ml.clustering.Cluster<Number>> clusters = apacheDbscan.cluster(d1);
		int i = 0;
		for (org.apache.commons.math3.ml.clustering.Cluster<Number> cluster : clusters) {
			System.out.println("Cluster " + i);
			
			List<Number> numbers = cluster.getPoints();
			for (Number number : numbers) {
				System.out.print(number +", ");
			}
			System.out.println();
			i++;
		}
		
		
		System.out.println("=== My ===");
		DBScanResult solution = dbscan.dbscan(d1, eps, minPts);;
		long elapsed = System.nanoTime() - startTime;
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		System.out.println(Printer.clusterSetToString(solution.getClusterSet()));
		
		//evaluateSolution(solution);
		
		System.out.println("=========================");
	}
	
	private void evaluateSolution(ClusterSet solution) {
		int percentageError = 3; // Lets start testing with 3% of error
		int expectedSize = dataSize/3; //a perfect distribution would be like this
		for (Cluster cluster : solution.getClusters()) {
			assertTrue( TestUtils.isBalanced(expectedSize,cluster.size(), percentageError) );
		}
		assertTrue(true);
	}
	
	private static List<Element> createNumberList(int numberOfElements) {
		List<Element> data = new ArrayList<Element>();
		Element element = null;
		for(int i=1; i <= numberOfElements; i++) {
			element = new Number(i);
			data.add(element);
		}
		return data;
	}
}
