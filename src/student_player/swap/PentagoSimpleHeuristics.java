package student_player.swap;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;

public class PentagoSimpleHeuristics {
    public PentagoSimpleHeuristics() {super();}

    public PentagoMove getNextMove(PentagoBoardState pentagoBoardState) {
        ArrayList<PentagoMove> legalMoves = pentagoBoardState.getAllLegalMoves();

        for(PentagoMove move : legalMoves) {
            PentagoBoardState boardStateClone = (PentagoBoardState) pentagoBoardState.clone();

            boardStateClone.processMove(move);
            if (boardStateClone.getWinner() == pentagoBoardState.getTurnPlayer()) {
                return move;
            }
        }

        return null;
    }
}
