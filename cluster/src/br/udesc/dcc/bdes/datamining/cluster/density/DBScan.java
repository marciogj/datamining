package br.udesc.dcc.bdes.datamining.cluster.density;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.datamining.cluster.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.element.Element;

/**
 * 
 * @author marciogj
 *
 */
public class DBScan {

	/**
	 * Eps is a radius number which is used do "draw" a circular area around of a point "p";
	 * Every other point "q" which is inside of such area will be included as Eps neighborhood of "p".  
	 * Formally, Neps(p) = { distance(p,q) <= Eps} where both p and q are in the set of evaluated points.
	 * 
	 */	
	public ClusterSet dbscan(Collection<? extends Element> data, double eps, int minPts) {
		ClusterSet clusters = new ClusterSet();
		ElementTable table = new ElementTable();

		List<Element> noises = new ArrayList<>();
		for (Element element : data) {
			if(!table.isVisited(element)) {
				expandCluster(data, element, eps, minPts, table);
			}
		}
		
		
		for (ClassifiedElement classifiedElement : table.elements) {
			if(classifiedElement.type == ElementType.UNKNOWN) {
				classifiedElement.type = ElementType.NOISE;
				noises.add(classifiedElement.element);
				System.out.println("Discarding " + classifiedElement.element + " as NOISE");
			}
		}
		
		int i = 1;
		for (CoreElement core : table.cores) {
			if( !clusters.contains(core.element) ) {
				Cluster cluster = new Cluster("C"+i);
				fillCluster(cluster, core);
				clusters.add(cluster);
				i++;	
			}
		}
		

		return clusters;
	}
	
	private void fillCluster(Cluster cluster, CoreElement core) {
		if(!cluster.contains(core.element)) {
			cluster.add(core.element);
		}
		
		for (CoreElement connectedCore : core.cores) {
			if(!cluster.contains(connectedCore.element)) {
				fillCluster(cluster, connectedCore);
			}
		}
		
		for (Element border : core.borders) {
			if(!cluster.contains(border)) {
				cluster.add(border);
			}
		}
	}



	private ElementType evaluate(Element element, List<Element> neighbors, int minPts) {
		int neighborsSize = neighbors.size();

		ElementType elementType = ElementType.UNKNOWN;
		if(neighborsSize >= (minPts - 1) ) { //minpts -1 since we dont cvonsider element itself as original implementation
			elementType = ElementType.CORE;
		}

		return elementType;
	}

	/**
	 * Return
	 *  
	 * @param data
	 * @param element
	 * @param eps
	 * @return
	 */
	private List<Element> regionQuery(Collection<? extends Element> data, Element element, double eps) {
		List<Element> neighbors = new ArrayList<>();

		for (Element neighborCandidate : data) {
			double distance = element.distance(neighborCandidate);
			if (distance <= eps && !neighborCandidate.equals(element)) {
				neighbors.add(neighborCandidate);
			}
		}
		return neighbors;
	}

	private void expandCluster(Collection<? extends Element> data, Element element, double eps, int minPts, ElementTable table) {
		Optional<ClassifiedElement> optClassifiedElement = table.get(element);
		boolean isVisited = optClassifiedElement.isPresent() && optClassifiedElement.get().isVisited;
		
		if (!isVisited) {
			List<Element> neighbors = regionQuery(data, element, eps);
			ElementType eType = evaluate(element, neighbors, minPts);
			
			if (optClassifiedElement.isPresent()) {
				optClassifiedElement.get().isVisited = true;
				//update only if its a new type
				if(eType != ElementType.UNKNOWN) {
					optClassifiedElement.get().type = eType;
				}
			} else {
				table.add(new ClassifiedElement(element, eType, true));
			}	
			
			

			if (eType == ElementType.CORE) {
				CoreElement core = new CoreElement(element);
				table.add(core);

				for (Element neighbor : neighbors) {
					Optional<ClassifiedElement> optClassifiedNeighbor = table.get(neighbor);
					boolean isCoreNeighbor = optClassifiedNeighbor.isPresent() && optClassifiedNeighbor.get().type == ElementType.CORE;					
					
					if(isCoreNeighbor) {
						core.connectCore(table.getCore(neighbor).get());
					} else {
						core.connectBorder(neighbor);
						
						if(!optClassifiedNeighbor.isPresent()) {
							table.add(new ClassifiedElement(neighbor, ElementType.BORDER));
						} else {
							optClassifiedNeighbor.get().type = ElementType.BORDER;
						}
						
					}
					
					
					if(!table.isVisited(neighbor)) {
						expandCluster(data, neighbor, eps, minPts, table);
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

class ClassifiedElement {
	protected Element element;
	protected ElementType type;
	protected boolean isVisited;

	public ClassifiedElement(Element element, ElementType type, boolean visited) {
		this.element = element;
		this.type = type;
		this.isVisited = visited;
	}

	public ClassifiedElement(Element element, ElementType type) {
		this.element = element;
		this.type = type;
		this.isVisited = false;
	}

}

class ElementTable {
	Collection<ClassifiedElement> elements = new ArrayList<>();
	List<CoreElement> cores = new ArrayList<>();

	public Optional<ClassifiedElement> get(Element element) {
		for (ClassifiedElement classifiedElement : elements) {
			if (classifiedElement.element.equals(element)) {
				return Optional.of(classifiedElement);
			}
		}
		return Optional.empty();
	}

	public boolean isVisited(Element element) {
		for (ClassifiedElement classifiedElement : elements) {
			if (classifiedElement.element.equals(element)) {
				return classifiedElement.isVisited;
			}
		}
		return false;
	}

	public Optional<CoreElement> getCore(Element coreCandidate) {
		for (CoreElement core : cores) {
			if(core.element.equals(coreCandidate)) {
				return Optional.of(core);
			}
		}
		return Optional.empty();
	}

	public void add(ClassifiedElement classifiedElement) {
		elements.add(classifiedElement);
	}

	public void add(CoreElement core) {
		cores.add(core);
	}


}

class CoreElement {
	Element element;
	Collection<Element> borders = new ArrayList<>();
	Collection<CoreElement> cores = new ArrayList<>();

	public CoreElement(Element element) {
		this.element = element;
	}

	public void connectCore(CoreElement another) {
		cores.add(another);
		if(!another.isConnectedTo(this)) {
			another.connectCore(this);
		}
	}

	private boolean isConnectedTo(CoreElement another) {
		for (CoreElement element : cores) {
			if(element.equals(another)) {
				return true;
			}
		}
		return false;
	}

	public void connectBorder(Element border) {
		borders.add(border);
	}
}
