package br.udesc.dcc.bdes.datamining.cluster.density;

import java.util.Collection;
import java.util.List;

import br.udesc.dcc.bdes.datamining.cluster.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.element.Element;

public class DBScanResult {
	protected ClusterSet solution;
	protected Collection<Element> noise;
	
	public DBScanResult(ClusterSet clusters, List<Element> noises) {
		this.solution = clusters;
		this.noise = noises;
	}

	public ClusterSet getClusterSet() {
		return solution;
	}

	public Collection<Element> getNoise() {
		return noise;
	}
	
}
