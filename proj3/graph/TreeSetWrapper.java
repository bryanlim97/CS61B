package graph;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/** Wrapper class for TreeSet to allow Queue functionality.
 *  @author Bryan Lim
 */
class TreeSetWrapper extends AbstractQueue<Integer> {

    /** Takes in C to order elements in the TreeSet. */
    TreeSetWrapper(Comparator<Integer> c) {
        _tree = new TreeSet<>(c);
    }

    @Override
    public Integer poll() {
        return _tree.pollFirst();
    }

    @Override
    public boolean offer(Integer v) {
        return _tree.add(v);
    }

    @Override
    public Integer peek() {
        if (_tree.size() > 0) {
            return _tree.first();
        }
        return null;
    }

    @Override
    public Iterator<Integer> iterator() {
        return _tree.iterator();
    }

    @Override
    public int size() {
        return _tree.size();
    }

    /** TreeSet of vertices. */
    private TreeSet<Integer> _tree;
}
