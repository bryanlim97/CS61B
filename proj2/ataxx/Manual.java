package ataxx;

import static ataxx.PieceColor.*;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author Bryan Lim
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Command moveCom = game().getMoveCmnd(myColor().toString() + ": ");
        if (moveCom.commandType() == Command.Type.PASS) {
            return Move.pass();
        }
        String[] moveArray = moveCom.operands();
        char fromCol = moveArray[0].charAt(0);
        char fromRow = moveArray[0].charAt(1);
        char toCol = moveArray[1].charAt(0);
        char toRow = moveArray[1].charAt(1);
        Move move;
        move = Move.move(fromCol, fromRow, toCol, toRow);
        return move;
    }

}
