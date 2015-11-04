package br.udesc.dcc.bdes.datamining.cluster.density;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public class EsterDBScanHeuristic {

	public static <T> List<Map.Entry<T, Double>> kdistance(Collection<T> data, int k, BiFunction<T, T, Double> distanceFn) {
		List<Map.Entry<T, Double>> kdistancies = new LinkedList<Map.Entry<T,Double>>();
		for (T point : data) {
			double distance = distanceFromKthNeighbor(data, point, k, distanceFn);
			kdistancies.add(new AbstractMap.SimpleEntry<T, Double>(point, distance));
		}
		
		//sort descending in order to enforce noises in the beggining
		Collections.sort(kdistancies, new Comparator<Map.Entry<T, Double>>() {
			@Override
			  public int compare(Entry<T, Double> e1, Entry<T, Double> e2) {
				double v1 = e1.getValue().doubleValue();
				double v2 = e2.getValue().doubleValue();
			    return v1 == v2 ?  0 : (v1 < v2 ? 1 : -1);
			  }
			}
		);
		
		return kdistancies;
	}

	private static <T> double distanceFromKthNeighbor(Collection<T> data, T element, int k,  BiFunction<T, T, Double> distanceFn) {
		double[] kdistances = new double[k];
		for (int i = 0; i < k; i++) {
			kdistances[i] = Double.MAX_VALUE;
		}
		
		for (T neighbor : data) {
			//ignore when neighbor is same as element
			if (neighbor.equals(element)) continue;
			
			double absDistance = Math.abs(distanceFn.apply(element, neighbor));
			//Update kdistances every time a shortest distance is discovered
			if(absDistance < kdistances[k-1]) {
				double auxValue = absDistance;
				for (int i = 0; i < k; i++) {
					if(auxValue < kdistances[i]){
						double tmpValue = kdistances[i];
						kdistances[i] = auxValue;
						auxValue = tmpValue;
					}
				}			
			} //absDistance under Kth neighbor distance
		}
		return kdistances[k-1];
	}
	
}

