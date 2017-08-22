package ataxx;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests of the Board class.
 *  @author
 */
public class BoardTest {

    private static final String[]
        GAME1 = { "a7-b7", "a1-a2",
                  "a7-a6", "a2-a3",
                  "a6-a5", "a3-a4" };

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(s.charAt(0), s.charAt(1),
                       s.charAt(3), s.charAt(4));
        }
    }

    @Test public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);
        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }
        assertEquals("failed to return to start", b1, b0);
        makeMoves(b0, GAME1);
        assertEquals("second pass failed to reach same position", b2, b0);
    }

    @Test public void testUndo2() {
        Board b0 = new Board();

        b0.makeMove('a', '7', 'c', '7');

        b0.makeMove('g', '7', 'g', '6');

        b0.makeMove('c', '7', 'e', '7');

        b0.makeMove('g', '7', 'g', '5');

        b0.makeMove('e', '7', 'g', '7');

        b0.makeMove('g', '5', 'g', '4');

        b0.makeMove('g', '7', 'f', '7');

        b0.makeMove('g', '5', 'g', '3');

        b0.makeMove('g', '7', 'g', '5');

        Board b1 = new Board(b0);

        b0.makeMove('a', '1', 'a', '2');
        b0.undo();

        assertEquals(b1, b0);
    }



}
