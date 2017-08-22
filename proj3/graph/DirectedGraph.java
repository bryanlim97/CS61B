package graph;

/* See restrictions in Graph.java. */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Represents a general unlabeled directed graph whose vertices are denoted by
 *  positive integers. Graphs may have self edges.
 *
 *  @author Bryan Lim
 */
public class DirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return true;
    }

    @Override
    public int inDegree(int v) {
        Iterator<int[]> myEdges = edges();
        int count = 0;
        while (myEdges.hasNext()) {
            int [] curr = myEdges.next();
            if (curr[1] == v) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int predecessor(int v, int k) {
        if (!contains(v)) {
            return 0;
        }
        List<int[]> track = new ArrayList<>();
        Iterator<int[]> myEdges = edges();
        while (myEdges.hasNext()) {
            int [] curr = myEdges.next();
            if (curr[1] == v) {
                track.add(curr);
            }
        }
        if (k >= track.size()) {
            return 0;
        }
        return track.get(k)[0];
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        if (!contains(v)) {
            List<Integer> myPred = new ArrayList<>();
            return Iteration.iteration(myPred.iterator());
        }
        Iterator<int[]> myEdges = edges();
        List<Integer> myPred = new ArrayList<>();
        while (myEdges.hasNext()) {
            int[] curr = myEdges.next();
            if (curr[1] == v) {
                myPred.add(curr[0]);
            }
        }
        return Iteration.iteration(myPred.iterator());
    }

}
