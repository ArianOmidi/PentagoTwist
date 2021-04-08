package student_player.swap.montecarlo;

import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import student_player.swap.PentagoSimpleHeuristics;
import student_player.swap.montecarlo.MonteCarloOptimizer;

/** A player file submitted by a student. */
public class MonteCarloPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public MonteCarloPlayer() {
        super("260692352");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState pentagoBoardState) {

        final boolean DEBUG = false;
        long start = System.currentTimeMillis();
        PentagoSimpleHeuristics simpleHeuristics = new PentagoSimpleHeuristics();
        PentagoMove winningMove = simpleHeuristics.getNextMove(pentagoBoardState);
//        System.out.println(String.format("Time for 1 Move check (s): %f", (System.currentTimeMillis() - start) / 1000f));
        if (winningMove != null) {
            //noinspection ConstantConditions
            if (DEBUG) {
                System.out.println(String.format("Time for Move (s): %f", (System.currentTimeMillis() - start) / 1000f));
                pentagoBoardState.printBoard();
            }
            return winningMove;
        }

        MonteCarloOptimizer mctsOptimizer = new MonteCarloOptimizer();
        Move myMove = mctsOptimizer.findNextMove(pentagoBoardState);

        //noinspection ConstantConditions
        if (DEBUG) {
            System.out.println(String.format("Time for Move (s): %f", (System.currentTimeMillis() - start) / 1000f));
            pentagoBoardState.printBoard();
        }

        return myMove;
    }
}
