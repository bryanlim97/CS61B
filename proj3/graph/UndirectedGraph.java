package graph;

/* See restrictions in Graph.java. */

/** Represents an undirected graph.  Out edges and in edges are not
 *  distinguished.  Likewise for successors and predecessors.
 *
 *  @author Bryan Lim
 */
public class UndirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public int inDegree(int v) {
        return degree(v);
    }

    @Override
    public int predecessor(int v, int k) {
        return successor(v, k);
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        return successors(v);
    }
}
