package dv;

/**
 * The class represent an edge E in a graph<V,E>.
 * @author R. Fielding
 * @version 1.0 
 * March 2018
 */
public class Edge implements Comparable<Edge>{
	public long source;					//source node ID
	public long destination;			//destination node ID
	public int cost;					//edge cost/weight
	
	/**
	 * A constructor.
	 * @param source
	 * @param destination
	 * @param cost
	 */
	public Edge(long source, long destination, int cost) {
		this.source = source;
		this.destination = destination;
		this.cost = cost;
	}
	
	/**
	 * copy edge information into new Edge instance.
	 * @return the new edge copy
	 */
	public Edge copy() { return new Edge(source, destination, cost); }
	
	/**
	 * compare two edges by the cost, used for sorting edges
	 */
	@Override
	public int compareTo(Edge o) {
		return this.cost - o.cost;
	}
	
	@Override
	public int hashCode() { return (int)destination; }
	
	@Override
	public boolean equals(Object o) {
	    // self check
	    if (this == o)					return true;
	    // null check
	    if (o == null)					return false;
	    // type check and cast
	    if (getClass() != o.getClass())	return this.hashCode()==o.hashCode();
	    // field comparison
	    Edge e = (Edge) o;
	    return e.source == this.source && e.destination == this.destination;
	}
	
	@Override
	public String toString() {
		return "[" + source + ", " + destination + ", " + cost + "]";
	}
}
