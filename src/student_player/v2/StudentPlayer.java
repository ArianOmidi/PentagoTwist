package student_player.v2;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import student_player.v2.MyTools.*;

import java.util.ArrayList;

import static student_player.v2.MyTools.*;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    static int curPlayer;
    static int opponent;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260835976_v2");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // Find move
        Move myMove = chooseMoveMCTS(boardState);

        // Return your move to be processed by the server.
        return myMove;
    }

    /* ======== Monte Carlo Search Tree Implementation ======== */

    public static Move chooseMoveMCTS(PentagoBoardState boardState) {
        // define an end time which will act as a terminating condition
        long endTime = System.currentTimeMillis() + TIME_LIMIT;

        // set curPlayer and opponent
        curPlayer = boardState.getTurnPlayer();
        opponent = (curPlayer == 0) ? PentagoBoardState.BLACK: PentagoBoardState.WHITE;

        Tree tree = new Tree(boardState);
        Node rootNode = tree.root;

        while (System.currentTimeMillis() < endTime) {
            Node promisingNode = selectPromisingNode(rootNode);
            if (!promisingNode.state.gameOver()) {
                expandNode(promisingNode);
            }
            Node nodeToExplore = promisingNode;
            if (promisingNode.childArray.size() > 0) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.root = winnerNode;
        return winnerNode.move;
    }

    private static Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.childArray.size() != 0) {
            node = findBestNodeWithUCT(node);
        }
        return node;
    }

    private static void expandNode(Node node) {
        ArrayList<PentagoMove> legalMoves = node.state.getAllLegalMoves();

        legalMoves.forEach(move -> {
            PentagoBoardState newState = (PentagoBoardState) node.state.clone();
            newState.processMove(move);

            Node newNode = new Node(move, newState, node);
            node.childArray.add(newNode);
        });
    }

    private static void backPropogation(Node nodeToExplore, int playerNo) {
        Node tempNode = nodeToExplore;

        while (tempNode != null) {
            tempNode.incrementVisit();
            if (curPlayer == playerNo) {
                tempNode.addScore(WIN_SCORE);
            }
            tempNode = tempNode.parent;
        }

    }

    private static int simulateRandomPlayout(Node node) {
        PentagoBoardState tmpState = (PentagoBoardState) node.state.clone();
        PentagoMove tmpMove;

        // check if game is over and opponent won
        if (tmpState.gameOver() && opponent == tmpState.getWinner()) {
            node.parent.winScore = Integer.MIN_VALUE;
            return opponent;
        }
        // if game is not over simulate to the end by selecting random moves
        while (tmpState.getWinner() == Board.NOBODY) {
            tmpMove = (PentagoMove) tmpState.getRandomMove();
            tmpState.processMove(tmpMove);
        }
        return tmpState.getWinner();
    }


}