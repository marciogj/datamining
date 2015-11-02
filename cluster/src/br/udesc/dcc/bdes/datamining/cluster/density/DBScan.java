package br.udesc.dcc.bdes.datamining.cluster.density;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class DBScan<T> {

	public DBScanResult<T> evaluate(Collection<T> data, double eps, int minPts, BiFunction<T, T, Double> distanceFn) {
		DBScanResult<T> result = new DBScanResult<>();
		ElementTable<T> table = new ElementTable<>();
		
		for (T element : data) {
			if(!table.isVisited(element)) {
				expandCluster(data, element, eps, minPts, table, distanceFn);
			}
		}
		
		
		for (ClassifiedElement<T> classifiedElement : table.getElements()) {
			if(classifiedElement.type == ElementType.UNKNOWN) {
				classifiedElement.type = ElementType.NOISE;
				result.addNoise(classifiedElement.element);
				//System.out.println("Discarding " + classifiedElement.element + " as NOISE - " + classifiedElement.isVisited);
			}
		} 
		
		int i = 1;
		for (CoreElement<T> core : table.cores) {
			if( !result.contains(core.element) ) {
				Cluster<T> cluster = new Cluster<>("C"+i);
				fillCluster(cluster, core);
				result.addCluster(cluster);
				i++;	
			}
		}
		
		return result;	
	}

	private void fillCluster(Cluster<T> cluster, CoreElement<T> core) {
		if(!cluster.contains(core.element)) {
			cluster.add(core.element);
		}
		
		for (CoreElement<T> connectedCore : core.cores) {
			if(!cluster.contains(connectedCore.element)) {
				fillCluster(cluster, connectedCore);
			}
		}
		
		for (T border : core.borders) {
			if(!cluster.contains(border)) {
				cluster.add(border);
			}
		}
	}



	private ElementType evaluate(T element, Collection<T> neighbors, int minPts) {
		int neighborsSize = neighbors.size();

		ElementType elementType = ElementType.UNKNOWN;
		if(neighborsSize >= (minPts - 1) ) { //minpts -1 since we dont cvonsider element itself as original implementation
			elementType = ElementType.CORE;
		}

		return elementType;
	}

	
	private Collection<T> regionQuery(Collection<T> data, T element, double eps, BiFunction<T, T, Double> distanceFn) {
		Collection<T> neighbors = new ArrayList<>();	
		
		for (T neighborCandidate : data) {
			double distance = distanceFn.apply(element, neighborCandidate).doubleValue();
			
			if (distance <= eps && !neighborCandidate.equals(element)) {
				neighbors.add(neighborCandidate);
			}
		}
		return neighbors;
	}

	private void expandCluster(Collection<T> data, T element, double eps, int minPts, ElementTable<T> table, BiFunction<T, T, Double> distanceFn) {
		Optional<ClassifiedElement<T>> optClassifiedElement = table.get(element);
		boolean isVisited = optClassifiedElement.isPresent() && optClassifiedElement.get().isVisited;
		
		if (!isVisited) {
			Collection<T> neighbors = regionQuery(data, element, eps, distanceFn);
			ElementType eType = evaluate(element, neighbors, minPts);
			
			if (optClassifiedElement.isPresent()) {
				optClassifiedElement.get().isVisited = true;
				//update only if its a new type
				if(eType != ElementType.UNKNOWN) {
					optClassifiedElement.get().type = eType;
				}
			} else {
				table.add(new ClassifiedElement<T>(element, eType, true));
			}	
			
			

			if (eType == ElementType.CORE) {
				CoreElement<T> core = new CoreElement<T>(element);
				table.add(core);

				for (T neighbor : neighbors) {
					Optional<ClassifiedElement<T>> optClassifiedNeighbor = table.get(neighbor);
					boolean isCoreNeighbor = optClassifiedNeighbor.isPresent() && optClassifiedNeighbor.get().type == ElementType.CORE;					
					
					if(isCoreNeighbor) {
						core.connectCore(table.getCore(neighbor).get());
					} else {
						core.connectBorder(neighbor);
						
						if(!optClassifiedNeighbor.isPresent()) {
							table.add(new ClassifiedElement<T>(neighbor, ElementType.BORDER));
						} else {
							optClassifiedNeighbor.get().type = ElementType.BORDER;
						}
						
					}
					
					
					if(!table.isVisited(neighbor)) {
						expandCluster(data, neighbor, eps, minPts, table, distanceFn);
					}
				}


			}
		}
	}

	
}



enum ElementType {
	CORE,
	BORDER,
	NOISE, 
	UNKNOWN
}

class ClassifiedElement<T> {
	protected T element;
	protected ElementType type;
	protected boolean isVisited;

	public ClassifiedElement(T element, ElementType type, boolean visited) {
		this.element = element;
		this.type = type;
		this.isVisited = visited;
	}

	public ClassifiedElement(T element, ElementType type) {
		this.element = element;
		this.type = type;
		this.isVisited = false;
	}

}

class ElementTable<T> {
	Collection<ClassifiedElement<T>> elements = new ArrayList<>();
	List<CoreElement<T>> cores = new ArrayList<>();

	public Optional<ClassifiedElement<T>> get(T element) {
		for (ClassifiedElement<T> classifiedElement : elements) {
			if (classifiedElement.element.equals(element)) {
				return Optional.of(classifiedElement);
			}
		}
		return Optional.empty();
	}

	public Collection<ClassifiedElement<T>> getElements() {
		return elements;
	}

	public boolean isVisited(T element) {
		for (ClassifiedElement<T> classifiedElement : elements) {
			if (classifiedElement.element.equals(element)) {
				return classifiedElement.isVisited;
			}
		}
		return false;
	}

	public Optional<CoreElement<T>> getCore(T coreCandidate) {
		for (CoreElement<T> core : cores) {
			if(core.element.equals(coreCandidate)) {
				return Optional.of(core);
			}
		}
		return Optional.empty();
	}

	public void add(ClassifiedElement<T> classifiedElement) {
		elements.add(classifiedElement);
	}

	public void add(CoreElement<T> core) {
		cores.add(core);
	}


}

class CoreElement<T> {
	T element;
	Collection<T> borders = new ArrayList<>();
	Collection<CoreElement<T>> cores = new ArrayList<>();

	public CoreElement(T element) {
		this.element = element;
	}

	public void connectCore(CoreElement<T> another) {
		cores.add(another);
		if(!another.isConnectedTo(this)) {
			another.connectCore(this);
		}
	}

	private boolean isConnectedTo(CoreElement<T> another) {
		for (CoreElement<T> element : cores) {
			if(element.equals(another)) {
				return true;
			}
		}
		return false;
	}

	public void connectBorder(T border) {
		borders.add(border);
	}
}
