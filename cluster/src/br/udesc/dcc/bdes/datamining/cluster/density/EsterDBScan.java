package br.udesc.dcc.bdes.datamining.cluster.density;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class EsterDBScan<T> {
	private static final Integer NOISE = 0;
	private static final Integer UNCLASSIFIED = -1;
	
	public Map<T, Integer> dbscan(Collection<T> data, double eps, int minPts, BiFunction<T, T, Double> distanceFn) {
		Map<T, Integer> classificationMap = new HashMap<>();
		int clusterId = nextId(NOISE);
		
		for (T point : data) {
			if ( !classificationMap.containsKey(point) ) {
				boolean isClusterExpanded = expandCluster(data, point, clusterId, eps, minPts, classificationMap, distanceFn);
				if (isClusterExpanded) {
					clusterId = nextId(clusterId);
				}
			}
		}
		return classificationMap;
	}
	
	public DBScanResult<T> evaluate(Collection<T> data, double eps, int minPts, BiFunction<T, T, Double> distanceFn) {
		Map<T, Integer> classificationMap = dbscan(data, eps, minPts, distanceFn);
		
		DBScanResult<T> result = new DBScanResult<>();
		Map<Integer, Cluster<T>> clusters = new HashMap<>();
		for(T point : classificationMap.keySet()) {
			Integer id = classificationMap.get(point);
			if (id == NOISE ) {
				result.addNoise(point);
			} else {
				Cluster<T> cluster = clusters.get(id);
				if (cluster == null) {
					cluster = new Cluster<>("C"+id);
					clusters.put(id, cluster);
				}
				cluster.add(point);
			}
		}
		clusters.values().forEach( c -> result.addCluster(c));
		return result;	
	}
	
	
	
	public int nextId(int id) {
		return id + 1;
	}

	
	private List<T> regionQuery(Collection<T> data, T element, double eps, BiFunction<T, T, Double> distanceFn) {
		List<T> neighbors = new ArrayList<>();	
		
		for (T neighborCandidate : data) {
			double distance = distanceFn.apply(element, neighborCandidate).doubleValue();
			
			if (distance <= eps && !neighborCandidate.equals(element)) {
				neighbors.add(neighborCandidate);
			}
		}
		return neighbors;
	}

	private boolean expandCluster(Collection<T> data, T point, Integer clusterId, double eps, int minPts, Map<T, Integer> classificationMap, BiFunction<T, T, Double> distanceFn) {		
		List<T> seeds = regionQuery(data, point, eps, distanceFn); //regionQuery(point, eps)
		if (seeds.size() < minPts) {
			classificationMap.put(point, NOISE);
			return false;
		} else { // all points in seeds are density-reachable from Point
			//classificationMap.put(point, clusterId); //TODO: SEEDS
			seeds.forEach( p -> classificationMap.put(p, clusterId));
			seeds.remove(point);
			while(!seeds.isEmpty()) {
				T currentPoint = seeds.get(0); //first
				List<T> result = regionQuery(data, currentPoint, eps, distanceFn);
				if (result.size() >= minPts) {
					for(T resultP : result) {
						int classification = Optional.ofNullable(classificationMap.get(resultP)).orElse(UNCLASSIFIED);
						if (classification == UNCLASSIFIED || classification == NOISE) {
							if (classification == UNCLASSIFIED) {
								seeds.add(resultP);
							}
							classificationMap.put(resultP, clusterId);
						} // UNCLASSIFIED or NOISE
					} //END FOR
				} // END IF result.size >= MinPts
				seeds.remove(currentPoint);
			} // WHILE seeds <> Empty
			return true;
		}
	}
	
}



