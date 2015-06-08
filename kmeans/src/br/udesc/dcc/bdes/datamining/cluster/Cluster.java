package br.udesc.dcc.bdes.datamining.cluster;

import java.util.ArrayList;
import java.util.List;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class Cluster {
	private String name;
	private List<Element> elements;
	protected Element centroid;
	
	public Cluster(String name) {
		this.name = name;
		this.elements = new ArrayList<Element>();
	}
	
	public Element meanCentroid() {
		int total = elements.size();
		if (total == 0) return centroid;
		Element sum = centroid.zero(); 
		for (Element element : elements) {
			sum = sum.plus(element);
		}
		
		return sum.divide(total);
	}
	
	//http://www.decom.cefetmg.br/docentes/flavio_cardeal/Teaching/mnc/aula_ajuste.pdf
	//http://en.wikipedia.org/wiki/Residual_sum_of_squares
	public int squareResidualSum() {
		int sum = 0;
		for (Element element : elements) {
			sum += Math.sqrt(centroid.distance(element)); 
		}
		return sum;
	}
	
	public void add(Element element) {
		elements.add(element);
	}
	
	public Element getCentroid() {
		return centroid;
	}
	
	public void setCentroid(Element centroid) {
		this.centroid = centroid;
	}

	public boolean isCentroid(Element element) {
		if (centroid == null) return element == null;
		return centroid.equals(element);
	}

	public void switchCentroid(Element mean) {
		elements.add(centroid);
		centroid = mean;
		
	}

	public void removeAllElements() {
		elements.clear();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Element> getElements() {
		return elements;
	}

	public int size() {
		return elements.size();
	}
	
}
