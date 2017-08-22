package ataxx;

/* Author: P. N. Hilfinger, (C) 2008. */

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Formatter;
import java.util.Observable;

import static ataxx.PieceColor.*;
import static ataxx.GameException.error;

/** An Ataxx board.   The squares are labeled by column (a char value between
 *  'a' - 2 and 'g' + 2) and row (a char value between '1' - 2 and '7'
 *  + 2) or by linearized index, an integer described below.  Values of
 *  the column outside 'a' and 'g' and of the row outside '1' to '7' denote
 *  two layers of border squares, which are always blocked.
 *  This artificial border (which is never actually printed) is a common
 *  trick that allows one to avoid testing for edge conditions.
 *  For example, to look at all the possible moves from a square, sq,
 *  on the normal board (i.e., not in the border region), one can simply
 *  look at all squares within two rows and columns of sq without worrying
 *  about going off the board. Since squares in the border region are
 *  blocked, the normal logic that prevents moving to a blocked square
 *  will apply.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Bryan Lim
 */
class Board extends Observable {

    /** Number of squares on a side of the board. */
    static final int SIDE = 7;
    /** Length of a side + an artificial 2-deep border region. */
    static final int EXTENDED_SIDE = SIDE + 4;

    /** Number of non-extending moves before game ends. */
    static final int JUMP_LIMIT = 25;

    /** Represents the bottom left corner. */
    private int cornerbl = EXTENDED_SIDE * 2 + 2;

    /** Represents the bottom right corner. */
    private int cornerbr = EXTENDED_SIDE * 3 - 3;

    /** Represents the top left corner. */
    private int cornertl = (EXTENDED_SIDE - 3) * EXTENDED_SIDE + 2;

    /** Represents the top right corner. */
    private int cornertr = (EXTENDED_SIDE - 2) * EXTENDED_SIDE - 3;


    /** A new, cleared board at the start of the game. */
    Board() {
        _board = new PieceColor[EXTENDED_SIDE * EXTENDED_SIDE];
        boardStack = new Stack<Board>();
        _allMoves = new ArrayList<Move>();
        _bluePieces = 0;
        _redPieces = 0;
        clear();
    }

    /** A copy of B. */
    Board(Board b) {
        _board = b._board.clone();
        for (int i = 2; i < EXTENDED_SIDE - 2; i++) {
            for (int j = EXTENDED_SIDE * i + 2;
                 j < EXTENDED_SIDE * i + 2 + SIDE; j++) {
                unrecordedSet(j, b.get(j));
            }
        }
        _bluePieces = b.numPieces(BLUE);
        _redPieces = b.numPieces(RED);
        _numJumps = b.numJumps();
        _numMoves = b.numMoves();
        _whoseMove = b.whoseMove();
    }

    /** Return the linearized index of square COL ROW. */
    static int index(char col, char row) {
        return (row - '1' + 2) * EXTENDED_SIDE + (col - 'a' + 2);
    }

    /** Return the linearized index of the square that is DC columns and DR
     *  rows away from the square with index SQ. */
    static int neighbor(int sq, int dc, int dr) {
        return sq + dc + dr * EXTENDED_SIDE;
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions and no blocks. */
    void clear() {
        _bluePieces = 0;
        _redPieces = 0;
        _whoseMove = RED;
        for (int i = 0; i < EXTENDED_SIDE * EXTENDED_SIDE; i++) {
            if (i % EXTENDED_SIDE == 0 || i % EXTENDED_SIDE == 1
                || (i + 2) % EXTENDED_SIDE == 0
                || (i + 2) % EXTENDED_SIDE == 1
                || (i < 2 * EXTENDED_SIDE)
                || (i > EXTENDED_SIDE * (EXTENDED_SIDE - 2) - 3)) {
                unrecordedSet(i, BLOCKED);
            } else {
                unrecordedSet(i, EMPTY);
            }
        }
        unrecordedSet(cornerbl, BLUE);
        unrecordedSet(cornerbr, RED);
        unrecordedSet(cornertl, RED);
        unrecordedSet(cornertr, BLUE);

        incrPieces(RED, 2);
        incrPieces(BLUE, 2);


        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if neither side has
     *  any moves, if one side has no pieces, or if there have been
     *  MAX_JUMPS consecutive jumps without intervening extends. */
    boolean gameOver() {
        if (numJumps() >= JUMP_LIMIT || !(canMove(RED) || canMove(BLUE))
            || (redPieces() == 0 || bluePieces() == 0)) {
            return true;
        }
        return false;

    }

    /** Return number of red pieces on the board. */
    int redPieces() {
        return numPieces(RED);
    }

    /** Return number of blue pieces on the board. */
    int bluePieces() {
        return numPieces(BLUE);
    }

    /** Return number of COLOR pieces on the board. */
    int numPieces(PieceColor color) {
        if (color.equals(BLUE)) {
            return _bluePieces;
        }
        if (color.equals(RED)) {
            return _redPieces;
        }
        return 0;
    }

    /** Increment numPieces(COLOR) by K. */
    private void incrPieces(PieceColor color, int k) {
        if (color.equals(BLUE)) {
            _bluePieces += k;
        }
        if (color.equals(RED)) {
            _redPieces += k;
        }
    }

    /** The current contents of square CR, where 'a'-2 <= C <= 'g'+2, and
     *  '1'-2 <= R <= '7'+2.  Squares outside the range a1-g7 are all
     *  BLOCKED.  Returns the same value as get(index(C, R)). */
    PieceColor get(char c, char r) {
        return _board[index(c, r)];
    }

    /** Return the current contents of square with linearized index SQ. */
    PieceColor get(int sq) {
        return _board[sq];
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'g', and
     *  '1' <= R <= '7'. */
    private void set(char c, char r, PieceColor v) {
        set(index(c, r), v);
    }

    /** Set square with linearized index SQ to V.  This operation is
     *  undoable. */
    private void set(int sq, PieceColor v) {
        _board[sq] = v;
    }

    /** Set square at C R to V (not undoable). */
    private void unrecordedSet(char c, char r, PieceColor v) {
        _board[index(c, r)] = v;
    }

    /** Set square at linearized index SQ to V (not undoable). */
    private void unrecordedSet(int sq, PieceColor v) {
        _board[sq] = v;
    }

    /** Return true iff MOVE is legal on the current board. */
    boolean legalMove(Move move) {
        if (move == null) {
            return false;
        }
        if (move.isPass()) {
            if (numPieces(whoseMove()) != 0 && canMove(whoseMove())) {
                return false;
            }
            return true;
        }

        if (get(move.fromIndex()) != whoseMove()) {
            return false;
        }

        if ((!move.isExtend() && !move.isJump())
            || get(move.col0(), move.row0()).equals(EMPTY)
            || get(move.col0(), move.row0()).equals(BLOCKED)
            || !get(move.col1(), move.row1()).equals(EMPTY)) {
            return false;
        }
        return true;
    }

    /** Return true iff player WHO can move, ignoring whether it is
     *  that player's move and whether the game is over. */
    boolean canMove(PieceColor who) {
        for (int i = 2; i < EXTENDED_SIDE - 2; i++) {
            for (int j = EXTENDED_SIDE * i + 2;
                j < EXTENDED_SIDE * i + 2 + SIDE; j++) {
                if (get(j).equals(who)) {
                    for (int k = -2; k < 3; k++) {
                        for (int l = -2; l < 3; l++) {
                            if (get(neighbor(j, k, l)).equals(EMPTY)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Return total number of moves and passes since the last
     *  clear or the creation of the board. */
    int numMoves() {
        return _numMoves;
    }

    /** Return number of non-pass moves made in the current game since the
     *  last extend move added a piece to the board (or since the
     *  start of the game). Used to detect end-of-game. */
    int numJumps() {
        return _numJumps;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        if (c0 == '-') {
            makeMove(Move.pass());
        } else {
            makeMove(Move.move(c0, r0, c1, r1));
        }
    }

    /** Make the MOVE on this Board, assuming it is legal. */
    void makeMove(Move move) {
        if (!legalMove(move)) {
            System.out.println("illegal move.");
        } else {
            boardStack.push(new Board(this));
            if (move.isPass()) {
                pass();
                _numMoves++;
                return;
            }
            int endPos = move.toIndex();
            unrecordedSet(endPos, _whoseMove);
            if (move.isJump()) {
                unrecordedSet(move.fromIndex(), EMPTY);
            }
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (!(get(neighbor(endPos, i, j)).equals(BLOCKED))
                            && !(get(neighbor(endPos, i, j)).equals(EMPTY))) {
                        if (!get(neighbor(endPos, i, j)).equals(_whoseMove)) {
                            incrPieces(_whoseMove, 1);
                            incrPieces(_whoseMove.opposite(), -1);
                        }
                        unrecordedSet(neighbor(endPos, i, j), _whoseMove);
                    }
                }
            }
            _numMoves++;
            if (move.isExtend()) {
                _numJumps = 0;
                incrPieces(_whoseMove, 1);
            } else {
                _numJumps++;
            }

            _allMoves.add(move);
            PieceColor opponent = _whoseMove.opposite();
            _whoseMove = opponent;

            setChanged();
            notifyObservers();
        }
    }

    /** Update to indicate that the current player passes, assuming it
     *  is legal to do so.  The only effect is to change whoseMove(). */
    void pass() {
        assert !canMove(_whoseMove);
        PieceColor opponent = _whoseMove.opposite();
        _whoseMove = opponent;
        setChanged();
        notifyObservers();
    }

    /** Undo the last move. */
    void undo() {
        Board prev = boardStack.pop();
        for (int i = 2; i < EXTENDED_SIDE - 2; i++) {
            for (int j = EXTENDED_SIDE * i + 2;
                 j < EXTENDED_SIDE * i + 2 + SIDE; j++) {
                unrecordedSet(j, prev.get(j));
            }
        }
        _bluePieces = prev.numPieces(BLUE);
        _redPieces = prev.numPieces(RED);
        _numJumps = prev.numJumps();
        _numMoves = prev.numMoves();
        _whoseMove = prev.whoseMove();

        setChanged();
        notifyObservers();
    }

    /** Indicate beginning of a move in the undo stack. */
    private void startUndo() {
    }

    /** Add an undo action for changing SQ to NEWCOLOR on current
     *  board. */
    private void addUndo(int sq, PieceColor newColor) {
    }

    /** Return true iff it is legal to place a block at C R. */
    boolean legalBlock(char c, char r) {
        int place = index(c, r);
        if (!get(c, r).equals(EMPTY) || place == cornerbl || place == cornerbr
            || place == cornertl || place == cornertr || _numMoves > 0) {
            return false;
        }
        return true;
    }

    /** Return true iff it is legal to place a block at CR. */
    boolean legalBlock(String cr) {
        return legalBlock(cr.charAt(0), cr.charAt(1));
    }

    /** Set a block on the square C R and its reflections across the middle
     *  row and/or column, if that square is unoccupied and not
     *  in one of the corners. Has no effect if any of the squares is
     *  already occupied by a block.  It is an error to place a block on a
     *  piece. */
    void setBlock(char c, char r) {
        if (!legalBlock(c, r)) {
            throw error("illegal block placement");
        }
        unrecordedSet(c, r, BLOCKED);

        int newC = c - 'a' + 2;
        int newR = r - '1' + 2;
        if (get(reflect(newC, newR)).equals(EMPTY)
            && (get(flipUp(newC, newR)).equals(EMPTY)
                || flipUp(newC, newR) == index(c, r))
            && (get(flipSide(newC, newR)).equals(EMPTY)
                || flipSide(newC, newR) == index(c, r))) {

            unrecordedSet(reflect(newC, newR), BLOCKED);
            unrecordedSet(flipUp(newC, newR), BLOCKED);
            unrecordedSet(flipSide(newC, newR), BLOCKED);
        } else {
            throw error("reflection is illegal");
        }
        setChanged();
        notifyObservers();
    }

    /** Given C and R, reflects the block across the center @return an int. */
    int reflect(int c, int r) {
        int toCol = EXTENDED_SIDE - c;
        int toRow = EXTENDED_SIDE - r;
        return (toRow - 1) * EXTENDED_SIDE + toCol - 1;
    }

    /** Given C and R, reflects on the x-axis @return an int. */
    int flipUp(int c, int r) {
        int toRow = EXTENDED_SIDE - r;
        return (toRow - 1) * EXTENDED_SIDE + c;
    }

    /** Given C and R, reflects on the y-axis @return an int. */
    int flipSide(int c, int r) {
        int toCol = EXTENDED_SIDE - c;
        return (r * EXTENDED_SIDE) + toCol - 1;
    }

    /** Place a block at CR. */
    void setBlock(String cr) {
        setBlock(cr.charAt(0), cr.charAt(1));
    }

    /** Return a list of all moves made since the last clear (or start of
     *  game). */
    List<Move> allMoves() {
        return _allMoves;
    }

    /** Reset the number of jumps. */
    void resetJumps() {
        _numJumps = 0;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /* .equals used only for testing purposes. */
    @Override
    public boolean equals(Object obj) {
        Board other = (Board) obj;
        return Arrays.equals(_board, other._board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(_board);
    }

    /** Return a text depiction of the board (not a dump).  If LEGEND,
     *  supply row and column numbers around the edges. */
    String toString(boolean legend) {
        Formatter out = new Formatter();
        int sideLength = this.EXTENDED_SIDE;
        for (int i = sideLength - 3; i > 1; i--) {
            System.out.print("  ");
            for (int j = i * sideLength + 2;
                 j < i * sideLength + sideLength - 2; j++) {
                if (this.get(j).equals(RED)) {
                    System.out.print("r");
                } else if (this.get(j).equals(BLUE)) {
                    System.out.print("b");
                } else if (this.get(j).equals(BLOCKED)) {
                    System.out.print("X");
                } else {
                    System.out.print("-");
                }
                if (j < i * sideLength + sideLength - 3) {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
        System.out.println();
        return out.toString();
    }

    /** For reasons of efficiency in copying the board,
     *  we use a 1D array to represent it, using the usual access
     *  algorithm: row r, column c => index(r, c).
     *
     *  Next, instead of using a 7x7 board, we use an 11x11 board in
     *  which the outer two rows and columns are blocks, and
     *  row 2, column 2 actually represents row 0, column 0
     *  of the real board.  As a result of this trick, there is no
     *  need to special-case being near the edge: we don't move
     *  off the edge because it looks blocked.
     *
     *  Using characters as indices, it follows that if 'a' <= c <= 'g'
     *  and '1' <= r <= '7', then row c, column r of the board corresponds
     *  to board[(c -'a' + 2) + 11 (r - '1' + 2) ], or by a little
     *  re-grouping of terms, board[c + 11 * r + SQUARE_CORRECTION]. */
    private final PieceColor[] _board;

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Number of red pieces. */
    private int _redPieces;

    /** Number of blue pieces. */
    private int _bluePieces;

    /** Number of moves. */
    private int _numMoves;

    /** Number of jumps. */
    private int _numJumps;

    /** Keeps track of all my moves. */
    private List<Move> _allMoves;

    /** Is the stack of boards. */
    private Stack<Board> boardStack;



}
