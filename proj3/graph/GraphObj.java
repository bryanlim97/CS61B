package graph;
import java.util.ArrayList;
import java.util.List;


/* See restrictions in Graph.java. */

/** A partial implementation of Graph containing elements common to
 *  directed and undirected graphs.
 *
 *  @author Bryan Lim
 */
abstract class GraphObj extends Graph {

    /** A new, empty Graph. */
    GraphObj() {
        _edgeID = 1;
        _vertexSize = 0;
        _maxVertex = 0;
        _edgeSize = 0;
        _allEdges = new ArrayList<>();
        _allVertices = new ArrayList<>();
        _allVertices.add(Integer.MAX_VALUE);
    }

    @Override
    public int vertexSize() {
        return _vertexSize;
    }

    @Override
    public int maxVertex() {
        return _maxVertex;
    }

    @Override
    public int edgeSize() {
        return _edgeSize;
    }

    @Override
    public abstract boolean isDirected();

    @Override
    public int outDegree(int v) {
        int count = 0;
        if (contains(v)) {
            for (int i = 0; i < allEdges().size(); i++) {
                if ((allEdges().get(i).one() == v
                        && allEdges().get(i).direction() == 1)
                        || ((allEdges().get(i).direction()
                        == 0) && (allEdges().get(i).one() == v
                        || allEdges().get(i).two() == v))) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public abstract int inDegree(int v);

    @Override
    public boolean contains(int u) {
        return allVertices().contains(u);
    }

    @Override
    public boolean contains(int u, int v) {
        if (contains(u) && contains(v)) {
            for (int i = 0; i < allEdges().size(); i++) {
                if (allEdges().get(i) != null) {
                    Edge currEdge = allEdges().get(i);
                    if ((currEdge.one() == u && currEdge.two() == v)) {
                        return true;
                    } else if (!isDirected() && currEdge.one()
                            == v && currEdge.two() == u) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int add() {
        if (allVertices().contains(null) && vertexSize() > 0) {
            int place = allVertices().indexOf(null);
            if (place == maxVertex() + 1) {
                _maxVertex = place;
            }
            _allVertices.set(place, place);
            _vertexSize++;
            return place;
        } else {
            _allVertices.add(maxVertex() + 1);
            _maxVertex++;
            _vertexSize++;
            return maxVertex();
        }
    }

    @Override
    public int add(int u, int v) {
        if (!(contains(u, v)) && contains(u) && contains(v)) {
            if (isDirected()) {
                _allEdges.add(new Edge(u, v, 1, _edgeID));
            } else {
                _allEdges.add(new Edge(u, v, 0, _edgeID));
            }
            _edgeID++;
            _edgeSize++;
            return edgeId(u, v);
        }
        int count = 0;
        for (int i = 0; i < edgeSize(); i++) {
            Edge currEdge = allEdges().get(i);
            if (currEdge.one() == u && currEdge.two() == v) {
                count = currEdge.edgeID();
            }
        }
        return count;
    }

    @Override
    public void remove(int v) {
        /*if (allVertices().contains(v)) {
            if (v == maxVertex()) {

            }
        }
        */

        if (allVertices().contains(v)) {
            for (int j = 0; j < allVertices().size(); j++) {
                if (contains(v, j) || contains(j, v)) {
                    remove(v, j);
                    remove(j, v);
                }
            }
            if (allVertices().indexOf(v) == maxVertex()) {
                _allVertices.set(_allVertices.indexOf(v), null);
                _vertexSize--;
                for (int i = allVertices().size() - 1; i > 0; i--) {
                    if (allVertices().get(i) != null) {
                        _maxVertex = i;
                        break;
                    }
                }
            } else {
                _allVertices.set(_allVertices.indexOf(v), null);
                _vertexSize--;
            }
        }
    }

    @Override
    public void remove(int u, int v) {
        if (contains(u, v)) {
            for (int i = 0; i < allEdges().size(); i++) {
                Edge currEdge = allEdges().get(i);
                if (currEdge.one() == u && currEdge.two() == v) {
                    _allEdges.remove(i);
                    _edgeSize--;
                } else if (!isDirected() && currEdge.one() == v
                        && currEdge.two() == u) {
                    _allEdges.remove(i);
                    _edgeSize--;
                }

            }
        }
    }

    @Override
    public Iteration<Integer> vertices() {
        List<Integer> edgeCopy = new ArrayList<>();
        for (int i = 1; i < allVertices().size(); i++) {
            if (allVertices().get(i) != null) {
                edgeCopy.add(allVertices().get(i));
            }
        }
        return Iteration.iteration(edgeCopy.iterator());
    }

    @Override
    public int successor(int v, int k) {
        if (!contains(v)) {
            return 0;
        }
        List<Edge> mySucc = new ArrayList<>();
        for (int i = 0; i < allEdges().size(); i++) {
            if (allEdges().get(i).one() == v) {
                mySucc.add(allEdges().get(i));
            }
        }
        if (k >= mySucc.size()) {
            return 0;
        }
        return mySucc.get(k).two();
    }

    @Override
    public abstract int predecessor(int v, int k);

    @Override
    public Iteration<Integer> successors(int v) {
        if (!contains(v)) {
            List<Integer> mySucc = new ArrayList<>();
            return Iteration.iteration(mySucc.iterator());
        }
        List<Integer> mySucc = new ArrayList<>();
        for (int i = 0; i < allEdges().size(); i++) {
            if (allEdges().get(i).one() == v) {
                mySucc.add(allEdges().get(i).two());
            } else if (!isDirected() && allEdges().get(i).two() == v) {
                mySucc.add(allEdges().get(i).one());
            }
        }
        return Iteration.iteration(mySucc.iterator());
    }

    @Override
    public abstract Iteration<Integer> predecessors(int v);

    @Override
    public Iteration<int[]> edges() {
        List<int[]> myEdges = new ArrayList<>();
        for (int i = 0; i < allEdges().size(); i++) {
            int[] curr = new int[2];
            curr[0] = allEdges().get(i).one();
            curr[1] = allEdges().get(i).two();
            myEdges.add(curr);
        }
        return Iteration.iteration(myEdges.iterator());
    }

    @Override
    protected void checkMyVertex(int v) {
        if (!contains(v)) {
            throw new IllegalArgumentException("vertex not from Graph");
        }
    }

    @Override
    protected int edgeId(int u, int v) {
        if (contains(u, v)) {
            for (int i = 0; i < allEdges().size(); i++) {
                if (allEdges().get(i).one() == u
                        && allEdges().get(i).two() == v) {
                    return allEdges().get(i).edgeID();
                }
            }
        }
        return 0;
    }

    /** RETURNS a list containing all legitimate edges. */
    private List<Edge> allEdges() {
        return _allEdges;
    }

    /** RETURNS a list containing all legitimate vertices. */
    private List<Integer> allVertices() {
        return _allVertices;
    }

    /** Class that provides to, from, and ID information for edges. */
    class Edge {
        /** Constructor that assigns U, V, W, and ID to appropriate indices.
         * U is from, V is to, W determines isDirected() and ID is the ID. */
        Edge(int u, int v, int w, int id) {
            _edge = new int[3];
            _edge[0] = u;
            _edge[1] = v;
            _edge[2] = w;
            _edgeID = id;
        }

        /** Returns the first index (from). */
        int one() {
            return _edge[0];
        }

        /** Returns the second index (to). */
        int two() {
            return _edge[1];
        }

        /** Returns the third index (direction). */
        int direction() {
            return _edge[2];
        }

        /** Returns the ID. */
        int edgeID() {
            return _edgeID;
        }

        /** Represents a single edge. */
        private int[] _edge;

        /** Represents the ID of a given edge.*/
        private int _edgeID;
    }

    /** The number of vertices. */
    private int _vertexSize;
    /** The value of the highest vertex. */
    private int _maxVertex;
    /** The number of edges. */
    private int _edgeSize;
    /** The list that contains all edges. */
    private List<Edge> _allEdges;
    /** The list that contains all vertices. */
    private List<Integer> _allVertices;
    /** The ID of an edge. */
    private int _edgeID;

}
