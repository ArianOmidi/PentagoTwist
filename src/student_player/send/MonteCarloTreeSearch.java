package student_player.send;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import student_player.send.MyTools;

import java.util.ArrayList;

import static student_player.send.MyTools.*;

public class MonteCarloTreeSearch {
    static int curPlayer;
    static int opponent;

    public static Move chooseMove(PentagoBoardState boardState) {
        // define the time when search is terminated
        long endTime = System.currentTimeMillis() + TIME_LIMIT;

        // set curPlayer and opponent
        curPlayer = boardState.getTurnPlayer();
        opponent = (curPlayer == PentagoBoardState.WHITE) ? PentagoBoardState.BLACK: PentagoBoardState.WHITE;

        // create new MCTS tree
        MyTools.Tree tree = new MyTools.Tree();
        tree.root.state = boardState;

        while (System.currentTimeMillis() < endTime) {
            /* SELECTION : get most promising Node using UCT policy */
            MyTools.Node promisingNode = selectPromisingNode(tree.root);

            /* EXPANSION : create the children of the selected node */
            if (!promisingNode.state.gameOver()) {
                expandNode(promisingNode);
            }

            /* SIMULATION : select child and simulate to terminal node */
            MyTools.Node nodeToExplore = promisingNode;
            if (!promisingNode.childArray.isEmpty()) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);

            /* BACK PROPAGATION : update the win and visit counts for nodes
                                 on the path to the current node */
            backPropogation(nodeToExplore, playoutResult);
        }

        // Get child with max score and update the tree
        MyTools.Node selectedNode = tree.root.getChildWithMaxScore();
        tree.root = selectedNode;
        return selectedNode.move;
    }

    private static MyTools.Node selectPromisingNode(MyTools.Node rootNode) {
        MyTools.Node node = rootNode;
        while (!node.childArray.isEmpty()) {
            node = findBestNodeWithUCT(node);
        }
        return node;
    }

    private static void expandNode(MyTools.Node node) {
        ArrayList<PentagoMove> legalMoves = node.state.getAllLegalMoves();

        for (int i = 0; i < legalMoves.size(); i++) {
            PentagoBoardState childState = (PentagoBoardState) node.state.clone();
            childState.processMove(legalMoves.get(i));

            MyTools.Node child = new MyTools.Node(legalMoves.get(i), childState, node);
            node.childArray.add(child);
        }
    }

    private static void backPropogation(MyTools.Node leafNode, int winner) {
        MyTools.Node tmp = leafNode;

        while (tmp != null) {
            tmp.incrementVisit();
            if (winner == curPlayer) {
                tmp.addScore(WIN_SCORE);
            }
            tmp = tmp.parent;
        }
    }

    private static int simulateRandomPlayout(Node node) {
        PentagoBoardState tmpState = (PentagoBoardState) node.state.clone();
        PentagoMove tmpMove;

        // check if game is over and opponent won
        if (opponent == tmpState.getWinner()) {
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
