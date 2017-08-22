package ataxx;

import static ataxx.PieceColor.*;
import static java.lang.Math.min;
import static java.lang.Math.max;
import java.util.ArrayList;

/** A Player that computes its own moves.
 *  @author Bryan Lim
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for red if positive, blue
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        if (!board().canMove(myColor())) {
            System.out.println(myColor().toString() + " passes.");
            return Move.pass();
        }
        Move move = findMove();
        System.out.println(myColor().toString()
                + " moves " + move.toString() + ".");
        return move;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (myColor() == RED) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** Used to communicate best moves found by findMove, when asked for. */
    private Move _lastFoundMove;

    /** Stores the best move. */
    private Move _bestMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value >= BETA if SENSE==1,
     *  and minimal value or value <= ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels before using a static estimate. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {

        int bestSoFar;
        if (sense == 1) {
            if (depth == 0 || board.gameOver()) {
                return simpleFindMove(board, sense, alpha, beta);
            }
            bestSoFar = -INFTY;
            ArrayList<Move> possible = findAllMoves(board);
            for (int i = 0; i < possible.size(); i++) {
                board().makeMove(possible.get(i));
                int response = findMove(board(),
                        depth - 1, false, sense * -1, alpha, beta);
                board().undo();
                if (response >= bestSoFar) {
                    bestSoFar = response;
                    if (saveMove) {
                        _lastFoundMove = possible.get(i);
                    }
                    alpha = max(alpha, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }

        } else {
            if (depth == 0 || board.gameOver()) {
                return simpleFindMove(board, sense, alpha, beta);
            }
            bestSoFar = INFTY;
            ArrayList<Move> possible = findAllMoves(board);
            for (int i = 0; i < possible.size(); i++) {
                board().makeMove(possible.get(i));
                int response = findMove(board(),
                        depth - 1, false, sense * -1, alpha, beta);
                board().undo();
                if (response <= bestSoFar) {
                    bestSoFar = response;
                    if (saveMove) {
                        _lastFoundMove = possible.get(i);
                    }
                    beta = min(beta, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }
        return bestSoFar;

    }

    /** Returns the ArrayList of all possible moves on BOARD. */
    private ArrayList<Move> findAllMoves(Board board) {
        ArrayList<Move> allMoves = new ArrayList<Move>();
        for (int i = 2; i < board.EXTENDED_SIDE - 2; i++) {
            for (int j = board.EXTENDED_SIDE * i + 2;
                 j < board.EXTENDED_SIDE * i + 2 + board.SIDE; j++) {
                if (board.get(j) == board().whoseMove()) {
                    for (int sur = -2; sur < 3; sur++) {
                        for (int sur2 = -2; sur2 < 3; sur2++) {
                            char col0 = (char) (j % board.EXTENDED_SIDE
                                    + 'a' - 2);
                            char row0 = (char) (i + '1' - 2);
                            char col1 = (char) (j % board.EXTENDED_SIDE
                                    + sur + 'a' - 2);
                            char row1 = (char) (i + sur2 + '1' - 2);
                            Move move = Move.move(col0, row0, col1, row1);
                            if (board.legalMove(move)) {
                                allMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        return allMoves;
    }




    /** Given BOARD, SENSE, ALPHA, and BETA,
     * finds the best move @return the best int. */
    private int simpleFindMove(Board board, int sense, int alpha, int beta) {
        int bestSoFar;
        if (sense == 1) {
            if (board.numPieces(RED) > board.numPieces(BLUE)
                    && board.gameOver()) {
                return INFTY;
            } else if (board.numPieces(BLUE) > board.numPieces(RED)
                    && board.gameOver()) {
                return -INFTY;
            }
            bestSoFar = -INFTY;
            Board copyBoard = new Board(board);
            ArrayList<Move> possible = findAllMoves(copyBoard);
            for (int i = 0; i < possible.size(); i++) {
                board().makeMove(possible.get(i));
                int val = staticScore(board());
                board().undo();
                if (val >= bestSoFar) {
                    bestSoFar = val;
                    alpha = max(alpha, val);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        } else {
            if (board.numPieces(RED) > board.numPieces(BLUE)
                    && board.gameOver()) {
                return INFTY;
            } else if (board.numPieces(BLUE) > board.numPieces(RED)
                    && board.gameOver()) {
                return -INFTY;
            }
            bestSoFar = INFTY;
            Board copyBoard = new Board(board);
            ArrayList<Move> possible = findAllMoves(copyBoard);
            for (int i = 0; i < possible.size(); i++) {
                board().makeMove(possible.get(i));
                int val = staticScore(board());
                board().undo();
                if (val <= bestSoFar) {
                    bestSoFar = val;
                    beta = min(beta, val);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }
        return bestSoFar;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        return board.redPieces() - board.bluePieces();
    }
}
