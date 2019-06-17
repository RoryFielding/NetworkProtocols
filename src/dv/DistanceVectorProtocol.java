package dv;


import java.util.ArrayList;

import java.util.Iterator;
import java.util.TreeMap;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;

/**
 * The class implements a network layer distance vector protocol.
 * A distance vector protocol routinely sends its neighboring routers copies of its routing tables to keep them up-to-date. 
 * Each instance of the protocol then computes the shortest path tree using Bellman-Ford Algorithm
 * @author R. Fielding
 * @version 1.0 
 * March 2018
 */

public class DistanceVectorProtocol implements CDProtocol {
	
	private static final int INIT=1;
	private static final int BCST=2;
	private static final int CMPT=3;
	
	private ArrayList<Edge> graph; 		//network graph as a set of edges (Unvisited nodes)
	private TreeMap<Long, Path> paths;	//shortest path tree (visited nodes)
	private int phase;					//current phase of the protocol
	private boolean done;				//limits computation cycles to one 
	
	/**
	 * A contractor.
	 * @param prefix required by PeerSim to access protocol's alias in the configuration file.
	 */
	public DistanceVectorProtocol(String prefix) {
		this.phase = INIT;				//start in INIT phase
		this.done = false;				//enable computation cycle
	}
	
	@Override
	/**
	 * PeerSim cyclic service. To be execute every cycle.
	 * @param host reference to host node (peersim.core.GeralNode)
	 * @param pid  global protocol's ID in this simulation
	 */
	public void nextCycle(Node host, int pid) {
		
		Linkable lnk = (Linkable) host.getProtocol(FastConfig.getLinkable(pid));		//reference local Linkable protocol 
		
		long nodeId=host.getID(), neighborId;											//reference host's node ID 	
		switch(phase) {																	//current phase
		case INIT:																		 
																						// create information containers
			this.graph = new ArrayList<Edge>();											
			this.paths = new TreeMap<Long, Path>();
																						// add neighbours
			for(int i=0; i < lnk.degree(); i++) {										//access neighbours in the Linkable
				neighborId = lnk.getNeighbor(i).getID();								//get neighbour's i ID
				int cost = DVInitialise.getCost(nodeId, neighborId);					//get cost of the link between this node and neighbour i 				
				graph.add(new Edge(nodeId, neighborId, cost));      					//add edge to local graph
			}
																						// transit to next phase
			phase = BCST;
			break;
		case BCST:																		//broadcast the local graph		
		for(int i=0; i< lnk.degree(); i++) { //for each neighbor node in the work
			Node n = lnk.getNeighbor(i);	//set node to neighbor node
			if (i == host.getID()) { //if equal to itself
				continue; //exit and repeat loop
			}										
			DistanceVectorProtocol DV; 
			DV = (DistanceVectorProtocol)n.getProtocol(Configuration.lookupPid("dv"));	//access DV protocol in node i													
			ArrayList<Edge> g = new ArrayList<Edge>();
			for(int j=0; j<this.graph.size(); j++) {
				g.add(this.graph.get(j).copy());
			}
			DV.recieve(g);	//send the copy to each neighbor	
		}																		//Transit to next phase
			phase = CMPT;
			break;
		case CMPT:																		//compute shortest path using Bellman Ford algorithm		
				paths.put(nodeId, new Path(nodeId, nodeId, 0));							//assume host node as source node
				for( int i = 0; i < graph.size()-1; i++) {	
			    	TreeMap<Long, Path> temp = new TreeMap<Long, Path>();				//copy current path tree
			    	for(Path p : paths.values()) {
			    		temp.put(p.destination, p.copy());
				}
			    	for(Path p : temp.values()) {										//for each path, find a new edges if any, compute shortest path
			    		Iterator<Edge> itr = graph.iterator();
					    while(itr.hasNext()) {
					    	Edge e = itr.next(); 

					    	if(p.destination == e.source) {								//a new edge
					    		if(!paths.containsKey(e.destination)) { 		 	    //no path to edge destination, add new path to destination
					    			if(e.source == nodeId)
					    				paths.put(e.destination, new Path(e.destination, e.destination, e.cost));
					    			else 
					    				paths.put(e.destination, new Path(e.destination, p.predecessor, p.cost + e.cost));
					    		}
					    		else {
					    			if(paths.get(e.destination).cost > p.cost +e.cost) {
					    				paths.get(e.destination).cost = p.cost + e.cost;
					    				paths.get(e.destination).predecessor = p.predecessor;
					    			}
					    		}
					    	}
					    }
			    	}
				}
		}//switch
	}//next Cycle
	
	/**
	 * Receives a graph form a neighbour and updates local graph removes duplicate
	 * edges if any
	 * 
	 * @param neighborGraph
	 *            a copy of neighbour's local graph
	 */
	public void recieve(ArrayList<Edge> neighborGraph) {
		for (Edge edge : neighborGraph) { // for each edge in the neighbour's graph
			if (graph.contains(edge)) { 
				//do nothing
			}	
			else 
			{     
				graph.add(edge);
			}
		}
	}
	
	/**
	 * Access to local path tree. Used bye the observer.
	 * @return the local path tree
	 */
	public TreeMap<Long, Path> getPaths() { return paths; }								// return current path tree
	
	/**
	 * used by PeerSim to clone this protocol at the start of the simulation
	 */
	@Override   
	public Object clone() {
      Object o = null;
       try {
    	   o = super.clone(); 
       } catch(CloneNotSupportedException cnse) {/*do nothing*/};
       return o;
    }//clone
}
