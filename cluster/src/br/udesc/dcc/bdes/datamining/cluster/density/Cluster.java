package br.udesc.dcc.bdes.datamining.cluster.density;

import java.util.ArrayList;
import java.util.Collection;

public class Cluster<T> {
	private String name;
	private Collection<T> elements = new ArrayList<>();
	
	public Cluster(String name) {
		this.name = name;
	}
	
	public void add(T element) {
		elements.add(element);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<T> getElements() {
		return elements;
	}

	public int size() {
		return elements.size();
	}

	public boolean contains(T element) {
		for (T clusterElement : elements) {
			if(clusterElement.equals(element)) {
				return true;
			}
		}
		return false;
	}
	
}
