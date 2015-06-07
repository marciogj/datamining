package br.udesc.dcc.bdes.datamining.cluster;

import java.util.ArrayList;
import java.util.List;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;
import br.udesc.dcc.bdes.datamining.cluster.element.NumberElement;

public class Main {
	
	public static void main(String[] args) {
		System.out.println("=========================");
		long startTime = System.nanoTime();
		KMeans kmeans = new KMeans();
		int maxIterations = 100;
		
		int k = 3;
		System.out.println("k=" + k);
		List<Element> data = createNumberList();
		
		kmeans.findClusterDistribuition(k, data, maxIterations);
		long elapsed = System.nanoTime() - startTime;
		System.out.println("Elapsed Time: " + elapsed + " nano seconds");
		System.out.println("=========================");
	}
	
	
	private static List<Element> createNumberList() {
		List<Element> data = new ArrayList<Element>();
		Element element = null;
		String strElements = "";
		for(int i=1; i < 5000; i++) {
			element = new NumberElement(i);
			data.add(element);
			strElements +=  i == 1 ? element : ", " + element;
		}
		System.out.println("["+strElements+"]");
		return data;
	}

}
