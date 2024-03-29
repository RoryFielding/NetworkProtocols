package dv;

/**
 * The class represent a shortest path from source node(host node) to destination
 * @author R.Fielding
 * @version 1.0 
 * March 2018
 */
public class Path {
	public long destination;			//destination node ID
	public long predecessor;			//predecessor node ID from source node
	public int cost;					//total path cost to destination

	/**
	 * A constructor.
	 * @param destination
	 * @param predecessor
	 * @param cost
	 */
	public Path(long destination, long predecessor, int cost) {
		this.destination = destination;
		this.predecessor = predecessor;
		this.cost = cost;
	}
	
	/**
	 * copy path information into new Path instance.
	 * @return the new path copy
	 */
	public Path copy() { return new Path(destination, predecessor, cost); }
	
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
		Path p = (Path) o;			    return p.destination == this.destination;
	}
	
	@Override
	public String toString() {
		return "[" + destination + "->" + predecessor + ", " + cost + "]";
	}
}