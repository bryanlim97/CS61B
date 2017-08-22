package graph;

import org.junit.Test;
import java.util.List;
import java.util.HashMap;



import static org.junit.Assert.*;

/** Unit tests for the Graph class.
 *  @author Bryan Lim
 */
public class GraphTesting {

    @Test
    public void emptyGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    @Test
    public void checkAdd() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add(1, 2);
        assertEquals(2, g.vertexSize());
        assertEquals(1, g.edgeSize());
    }

    @Test
    public void checkRemoveVertex() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.remove(3);
        assertEquals(2, g.vertexSize());
        assertEquals(2, g.maxVertex());

        g.add();
        assertEquals(3, g.vertexSize());
        assertEquals(3, g.maxVertex());

        g.remove(2);
        g.remove(3);
        g.add();
        assertEquals(2, g.vertexSize());
        assertEquals(2, g.maxVertex());

        g.add();
        g.remove(2);
        assertEquals(2, g.vertexSize());
        assertEquals(3, g.maxVertex());
    }

    @Test
    public void checkRemoveEdge() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        g.add(1, 2);
        g.remove(1, 2);
        assertEquals(true, g.contains(1, 3));
        assertEquals(false, g.contains(1, 2));
        assertEquals(1, g.edgeSize());
    }

    @Test
    public void checkEdgeContain() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 4);
        g.add(1, 1);
        assertEquals(true, g.contains(1, 2));
        assertEquals(true, g.contains(1, 4));
        assertEquals(3, g.edgeSize());
    }

    @Test
    public void checkVertexContain() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        assertEquals(false, g.contains(5));
        assertEquals(true, g.contains(4));
    }

    @Test
    public void checkByeEdgeByeVertices() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        g.add(1, 2);

        assertEquals(2, g.edgeSize());

        g.remove(1);
        assertEquals(3, g.vertexSize());
        assertEquals(0, g.edgeSize());
    }

    @Test
    public void checkOutDegreeD() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 4);
        g.add(1, 3);
        assertEquals(2, g.outDegree(1));

        g.remove(1, 3);
        assertEquals(1, g.outDegree(1));
    }

    @Test
    public void checkOutDegreeU() {
        UndirectedGraph ug = new UndirectedGraph();
        ug.add();
        ug.add();
        ug.add();
        ug.add();
        ug.add();
        ug.add(1, 1);
        assertEquals(1, ug.degree(1));

        ug.add(1, 2);
        ug.add(2, 1);
        assertEquals(2, ug.degree(1));
        assertEquals(1, ug.degree(2));
        assertEquals(2, ug.edgeSize());

        ug.remove(2, 1);
        assertEquals(1, ug.degree(1));
        assertEquals(0, ug.degree(2));

        ug.remove(1, 1);
        assertEquals(0, ug.edgeSize());
        assertEquals(false, ug.contains(8, 1));
        assertEquals(false, ug.contains(1, 8));
        assertEquals(0, ug.edgeSize());

    }

    @Test
    public void checkEdgeList() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        g.add(1, 4);

        g.remove(1, 3);
        g.add(1, 3);
        assertEquals(3, g.edgeSize());
    }

    @Test
    public void checkRepeatedEdge() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        g.add(3, 1);
        assertEquals(2, g.edgeSize());
    }

    @Test
    public void checkSuccessor() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        g.add(1, 4);
        g.add(1, 2);
        assertEquals(3, g.successor(1, 0));
        assertEquals(2, g.successor(1, 2));
        assertEquals(0, g.successor(1, 3));
        assertEquals(0, g.successor(3, 1));

        g.remove(1, 4);
        g.add(1, 4);
        assertEquals(4, g.successor(1, 2));

    }

    @Test
    public void checkEdgeId() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        assertEquals(1, g.edgeId(1, 3));
        assertEquals(0, g.edgeId(3, 1));

        g.add(1, 4);
        g.add(1, 5);
        assertEquals(2, g.edgeId(1, 4));

        g.remove(1, 4);
        g.add(1, 4);
        assertEquals(3, g.edgeId(1, 5));
        assertEquals(4, g.edgeId(1, 4));
    }

    @Test
    public void checkUndirectedOperations() {
        UndirectedGraph ug = new UndirectedGraph();
        ug.add();
        ug.add();
        ug.add();
        ug.add();
        ug.add();
        ug.add();
        ug.add(1, 1);
        ug.add(1, 2);
        ug.add(2, 1);
        ug.add(3, 4);
        assertEquals(3, ug.edgeSize());

        ug.remove(4);
        assertEquals(2, ug.edgeSize());

        assertEquals(true, ug.contains(2, 1));

        ug.remove(2, 1);
        assertEquals(1, ug.edgeSize());

        ug.remove(1, 1);
        assertEquals(0, ug.edgeSize());
    }

    @Test
    public void checkDirectedOperations() {
        DirectedGraph ug = new DirectedGraph();
        ug.add();
        ug.add();
        ug.add();
        ug.add();
        ug.add();
        ug.add();
        ug.add(1, 1);
        ug.add(1, 2);
        ug.add(2, 1);
        ug.add(3, 4);
        assertEquals(4, ug.edgeSize());

        ug.remove(4);
        assertEquals(3, ug.edgeSize());

        ug.remove(2, 1);
        assertEquals(2, ug.edgeSize());
    }

    @Test
    public void simple() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.remove(2);
        g.remove(7);
        g.remove(6);
        g.add();
        g.add();

        assertEquals(6, g.maxVertex());
    }

    @Test
    public void simpleShortestTest1() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 3);
        g.add(3, 4);
        g.add(4, 5);
        g.add(1, 3);

        SPTest myTest = new SPTest(g, 1, 5);

        myTest.setHashWeight(1, 2, 1);
        myTest.setHashWeight(2, 3, 1);
        myTest.setHashWeight(3, 4, 1);
        myTest.setHashWeight(4, 5, 1);
        myTest.setHashWeight(1, 3, 1);

        myTest.setPaths();
        List<Integer> path = myTest.pathTo();
        assertArrayEquals(path.toArray(), new Integer[]{1, 3, 4, 5});
    }

    @Test
    public void simpleShortestTest2() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 8);
        g.add(2, 3);
        g.add(3, 4);
        g.add(4, 5);
        g.add(5, 6);
        g.add(6, 7);
        g.add(7, 8);

        SPTest myTest = new SPTest(g, 1, 8);
        myTest.setHashWeight(1, 2, 1);
        myTest.setHashWeight(2, 3, 1);
        myTest.setHashWeight(3, 4, 1);
        myTest.setHashWeight(4, 5, 1);
        myTest.setHashWeight(5, 6, 1);
        myTest.setHashWeight(6, 7, 1);
        myTest.setHashWeight(7, 8, 1);
        myTest.setHashWeight(2, 8, 8);

        myTest.setPaths();
        List<Integer> path = myTest.pathTo();
        assertArrayEquals(path.toArray(), new Integer[]{1, 2, 3
                , 4, 5, 6, 7, 8});
    }

    private class SPTest extends SimpleShortestPaths {
        public SPTest(Graph G, int u, int v) {
            super(G, u, v);
            _weights = new HashMap<>();
        }

        public void setHashWeight(int u, int v, int weight) {
            _weights.put(_G.edgeId(u, v), weight);
        }

        @Override
        public double getWeight(int u, int v) {
            return _weights.get(_G.edgeId(u, v));
        }

        private HashMap<Integer, Integer> _weights;
    }
}
