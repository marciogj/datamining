package br.udesc.dcc.bdes.datamining.cluster.junit;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.udesc.dcc.bdes.datamining.cluster.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.Printer;
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
		KMeans fuzzyCMeans = new KMeans();
		ClusterSet solution = fuzzyCMeans.findClusterDistribuition(k, data, maxIterations);
		long elapsed = System.nanoTime() - startTime;
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		System.out.println(Printer.clusterSetToString(solution));
		
		evaluateSolution(solution);
		
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
