package br.udesc.dcc.bdes.datamining.cluster.density;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;


/**
 * Clustering algorithm based on DBScan proposal from Martin Ester and Hans-peter Kriegel and Jörg Sander and Xiaowei Xu:
 * "A density-based algorithm for discovering clusters in large spatial databases with noise", 1996, pag. 226--231, AAAI Press.
 * 
 * @author Marcio.Jasinski
 *
 * @param <T> The class type of point of set to be clustered.  
 */
public class EsterDBScan<T> {
	private static final Integer NOISE = 0;
	private static final Integer UNCLASSIFIED = -1;
	
	/**
	 * Run dbscan algorithm with given parameters and returns a map with element and it's cluster id.
	 * 
	 * @param data The set of points to be clustered
	 * @param eps The epsilon distance which will be used to find eps-neighborhood
	 * @param minPts The minimum number of points that must exists around eps distance to classify a CORE
	 * @param distanceFn The function to obtain a distance of two objects of type T.
	 * @return Mapped elements with cluster identification for every data element. 
	 */
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
	
	/**
	 * Run dbscan algorithm with given parameters and returns a map with element and it's cluster id.
	 * 
	 * @param data The set of points to be clustered
	 * @param eps The epsilon distance which will be used to find eps-neighborhood
	 * @param minPts The minimum number of points that must exists around eps distance to classify a CORE
	 * @param distanceFn The function to obtain a distance of two objects of type T.
	 * @return Return a DBScanResult with clusters and noise structure. 
	 */
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
	
	
	/**
	 * Create a new cluster id based on received parameter.
	 * @param id The current cluster id.
	 * @return A new cluster which is id incremented by 1.
	 */
	private int nextId(int id) {
		return id + 1;
	}

	/**
	 * Evaluate the region around a given point and return all eps-neighbors from it.
	 * An eps-neighbor is a element where distance from point parameter is equal or less than eps.
	 *  
	 * @param data The full set of points to evaluate as a eps-neighborhood
	 * @param point The element which will be evaluated
	 * @param eps The max distance where eps-neighbors should be reached by point.   
	 * @param distanceFn The function to obtain a distance of two objects of type T.
	 * @return The list of points which are eps-neighbor from point (including point itself).
	 */
	private List<T> regionQuery(Collection<T> data, T point, double eps, BiFunction<T, T, Double> distanceFn) {
		List<T> neighbors = new ArrayList<>();	
		
		for (T neighborCandidate : data) {
			double distance = distanceFn.apply(point, neighborCandidate).doubleValue();
			if (distance <= eps) {
				neighbors.add(neighborCandidate);
			}
		}
		return neighbors;
	}

	/**
	 * Evaluate a point and expand it when eps-neighborhood is bigger or equal to minPts.  
	 * 
	 * @param data
	 * @param point The point being evaluated 
	 * @param clusterId The current cluster id
	 * @param eps The epsilon distance which will be used to find eps-neighborhood
	 * @param minPts The minimum number of points that must exists around eps distance to classify a CORE
	 * @param classificationMap
	 * @param distanceFn The function to obtain a distance of two objects of type T.
	 * @return true if a cluster was detected and expanded and false otherwise.
	 */
	private boolean expandCluster(Collection<T> data, T point, Integer clusterId, double eps, int minPts, Map<T, Integer> classificationMap, BiFunction<T, T, Double> distanceFn) {		
		List<T> seeds = regionQuery(data, point, eps, distanceFn); //regionQuery(point, eps)
		if (seeds.size() < minPts) {
			classificationMap.put(point, NOISE);
			return false;
		} else { // all points in seeds are density-reachable from Point
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



