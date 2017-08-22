package graph;

/* See restrictions in Graph.java. */

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

/** The shortest paths through an edge-weighted graph.
 *  By overrriding methods getWeight, setWeight, getPredecessor, and
 *  setPredecessor, the client can determine how to represent the weighting
 *  and the search results.  By overriding estimatedDistance, clients
 *  can search for paths to specific destinations using A* search.
 *  @author Bryan Lim
 */
public abstract class ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public ShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public ShortestPaths(Graph G, int source, int dest) {
        _G = G;
        _source = source;
        _dest = dest;
    }

    /** Initialize the shortest paths.  Must be called before using
     *  getWeight, getPredecessor, and pathTo. */
    public void setPaths() {
        setWeight(getSource(), 0);
        TreeSetWrapper myTree = new TreeSetWrapper(new WeightComp());
        Dijkstra myPaths = new Dijkstra(_G, myTree);
        myPaths.traverse(getSource());
    }

    /** Returns the starting vertex. */
    public int getSource() {
        return _source;
    }

    /** Returns the target vertex, or 0 if there is none. */
    public int getDest() {
        return _dest;
    }

    /** Returns the current weight of vertex V in the graph.  If V is
     *  not in the graph, returns positive infinity. */
    public abstract double getWeight(int v);

    /** Set getWeight(V) to W. Assumes V is in the graph. */
    protected abstract void setWeight(int v, double w);

    /** Returns the current predecessor vertex of vertex V in the graph, or 0 if
     *  V is not in the graph or has no predecessor. */
    public abstract int getPredecessor(int v);

    /** Set getPredecessor(V) to U. */
    protected abstract void setPredecessor(int v, int u);

    /** Returns an estimated heuristic weight of the shortest path from vertex
     *  V to the destination vertex (if any).  This is assumed to be less
     *  than the actual weight, and is 0 by default. */
    protected double estimatedDistance(int v) {
        return 0.0;
    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    protected abstract double getWeight(int u, int v);

    /** Returns a list of vertices starting at _source and ending
     *  at V that represents a shortest path to V.  Invalid if there is a
     *  destination vertex other than V. */
    public List<Integer> pathTo(int v) {
        int temp = v;
        List<Integer> paths = new ArrayList<>();
        paths.add(temp);
        while (_G.contains(temp)) {
            paths.add(getPredecessor(temp));
            temp = getPredecessor(temp);
            if (temp == getSource()) {
                break;
            }
        }
        Collections.reverse(paths);
        return paths;
    }

    /** Returns a list of vertices starting at the source and ending at the
     *  destination vertex. Invalid if the destination is not specified. */
    public List<Integer> pathTo() {
        return pathTo(getDest());
    }

    /** Nested class that implements Dijkstra's Algorithm.*/
    class Dijkstra extends Traversal {

        /** Given G and FRINGE, assign values to Traversal. */
        Dijkstra(Graph G, Queue<Integer> fringe) {
            super(G, fringe);
        }

        /** Override the visit function in Traversal and perform
         * Dijkstra's Algorithm. */
        @Override
        protected boolean visit(int v) {
            if (v == getDest()) {
                return false;
            }
            Iterator<Integer> myEdges = _G.successors(v);
            while (myEdges.hasNext()) {
                int to = myEdges.next();
                if (getWeight(v) + getWeight(v, to) < getWeight(to)) {
                    setWeight(to, getWeight(v) + getWeight(v, to));
                    setPredecessor(to, v);
                    _fringe.remove(to);
                    _fringe.offer(to);
                }
            }
            return true;
        }
    }

    /** A comparator that compares vertices based on weights. */
    class WeightComp implements Comparator<Integer> {

        /** Overrides the compare function to compare weights. */
        @Override
         public int compare(Integer u, Integer v) {
            if (getWeight(u) + estimatedDistance(u) > getWeight(v)
                    + estimatedDistance(v)) {
                return 1;
            } else if (getWeight(u) + estimatedDistance(u)
                    == getWeight(v) + estimatedDistance(v)) {
                return u.compareTo(v);
            } else {
                return -1;
            }
        }
    }


    /** The graph being searched. */
    protected final Graph _G;
    /** The starting vertex. */
    private final int _source;
    /** The target vertex. */
    private final int _dest;

}
