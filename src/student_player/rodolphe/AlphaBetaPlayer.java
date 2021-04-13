package student_player.rodolphe;

import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import student_player.rodolphe.abprune.ABPrune;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Random;

public class AlphaBetaPlayer extends PentagoPlayer {
    private static final String PLAYER ="alpha-beta";
    private ArrayList<PentagoMove> centers = new ArrayList<>();
    private Random rnd = new Random();
    private boolean firstTurn = true;

    public AlphaBetaPlayer() {
        super(PLAYER);
    }

    public Move chooseMove(PentagoBoardState boardState) {
        long start = System.currentTimeMillis();

        // first turn
        if (firstTurn) {
            firstTurn = false;
            centers.add(new PentagoMove(1,1, 3, 0, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(4,1, 2, 0, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(1,4, 3, 0, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(4,4, 2, 0, boardState.getTurnPlayer()));
        }

        //PentagoMove myMove = null;

        AbstractMap.SimpleEntry<Integer, PentagoMove> myMove = new AbstractMap.SimpleEntry<> (0, null);

        if(centers.size() > 1) {
            while (!(myMove.getValue() != null && boardState.isPlaceLegal(myMove.getValue().getMoveCoord()) ) && centers.size() != 0) {
                int index = rnd.nextInt(centers.size());
                myMove.setValue(centers.get(index));
                centers.remove(index);
            }
        }

        if (centers.size() <= 1 && (myMove.getValue() == null ||!boardState.isPlaceLegal(myMove.getValue().getMoveCoord())) ) {
            long abstart = System.currentTimeMillis();
            //myMove = ABPrune.minimax(2, boardState.getTurnPlayer(), boardState).getValue();
            myMove = ABPrune.abp(3, boardState.getTurnPlayer(), boardState, Integer.MIN_VALUE, Integer.MAX_VALUE);
            long abend = System.currentTimeMillis();

            if (((double)(abend-abstart)/1000) < 1.0) {
                AbstractMap.SimpleEntry<Integer, PentagoMove> opMove = ABPrune.abp(3, boardState.getTurnPlayer() ^ 1, boardState, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (opMove.getKey() > myMove.getKey()) {
                    myMove = opMove;
                }
            }
        }


        long end = System.currentTimeMillis();

        System.out.println("abp move took " + (double)(end-start)/1000 + "s");

        return myMove.getValue();
    }
}