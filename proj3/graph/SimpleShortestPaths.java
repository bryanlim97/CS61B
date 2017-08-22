package graph;
import java.util.List;
import java.util.ArrayList;

/* See restrictions in Graph.java. */

/** A partial implementation of ShortestPaths that contains the weights of
 *  the vertices and the predecessor edges.   The client needs to
 *  supply only the two-argument getWeight method.
 *  @author Bryan Lim
 */
public abstract class SimpleShortestPaths extends ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public SimpleShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public SimpleShortestPaths(Graph G, int source, int dest) {
        super(G, source, dest);
        _weights = new ArrayList<>();
        _preds = new ArrayList<>();
        Iteration<Integer> myVertices = G.vertices();
        while (myVertices.hasNext()) {
            int now = myVertices.next();
            double[] current = {now, Double.POSITIVE_INFINITY};
            int[] againCurrent = {now, 0};
            _weights.add(current);
            _preds.add(againCurrent);
        }

    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    @Override
    protected abstract double getWeight(int u, int v);

    @Override
    public double getWeight(int v) {
        if (!_G.contains(v)) {
            return Double.POSITIVE_INFINITY;
        } else {
            for (int i = 0; i < _weights.size(); i++) {
                double [] curr = _weights.get(i);
                if (curr[0] == v) {
                    return curr[1];
                }
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    @Override
    protected void setWeight(int v, double w) {
        if (_G.contains(v)) {
            for (int i = 0; i < _weights.size(); i++) {
                double [] curr = _weights.get(i);
                if (curr[0] == v) {
                    double[] place = {v, w};
                    _weights.set(i, place);
                }
            }
        }
    }

    @Override
    public int getPredecessor(int v) {
        if (!_G.contains(v)) {
            return 0;
        } else {
            for (int i = 0; i < _preds.size(); i++) {
                int [] curr = _preds.get(i);
                if (curr[0] == v) {
                    return curr[1];
                }
            }
        }
        return 0;
    }

    @Override
    protected void setPredecessor(int v, int u) {
        if (_G.contains(v)) {
            for (int i = 0; i < _preds.size(); i++) {
                int [] curr = _preds.get(i);
                if (curr[0] == v) {
                    int [] place = {v, u};
                    _preds.set(i, place);
                }
            }
        }
    }

    /** List representing vertices and their weights. */
    private List<double[]> _weights;
    /** List representing vertices and their predecessors. */
    private List<int[]> _preds;

}
