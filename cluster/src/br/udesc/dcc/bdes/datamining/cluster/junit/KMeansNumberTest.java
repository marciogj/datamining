package br.udesc.dcc.bdes.datamining.cluster.junit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;
import br.udesc.dcc.bdes.datamining.cluster.element.Number;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.KMeans;
import br.udesc.dcc.bdes.datamining.cluster.kmeans.Printer;

public class KMeansNumberTest {

	@Test
	public void kMeansNumberDistribuitionTest() {
		System.out.println("=========================");
		long startTime = System.nanoTime();
		KMeans kmeans = new KMeans();
		int maxIterations = 100;
		
		int k = 3;
		int dataSize = 5000;
		System.out.println("Solution for k=" + k);
		List<Element> data = createNumberList(dataSize);
		
		ClusterSet solution = kmeans.findClusterDistribuition(k, data, maxIterations);
		System.out.println(Printer.clusterSetToString(solution));
		long elapsed = System.nanoTime() - startTime;
		int percentageError = 3; // Lets start testing with 3% of error
		int expectedSize = dataSize/3; //a perfect distribution would be like this
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		for (Cluster cluster : solution.getClusters()) {
			assertTrue( isBalanced(expectedSize,cluster.size(), percentageError) );
		}
		assertTrue(true);
		System.out.println("=========================");
	}
	
	private boolean isBalanced(int expectedSize, int actualSize, int acceptedPercentageError) {
		double acceptedError = (expectedSize * acceptedPercentageError)/100.0;
		int difference = Math.abs(actualSize - expectedSize);
		
		return difference <= acceptedError;
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
