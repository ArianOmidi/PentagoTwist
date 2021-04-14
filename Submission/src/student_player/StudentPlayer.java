package student_player;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import student_player.MyTools.Node;
import student_player.MyTools.Tree;

import java.util.ArrayList;

import static student_player.MyTools.TIME_LIMIT;
import static student_player.MyTools.findBestNodeWithUCT;

/** A player file submitted by a student. */
// Code inspired by: https://www.baeldung.com/java-monte-carlo-tree-search
public class StudentPlayer extends PentagoPlayer {

    static int curPlayer;
    static int opponent;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260835976");
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
        // define the time when search is terminated
        long endTime = System.currentTimeMillis() + TIME_LIMIT;

        // set curPlayer and opponent
        curPlayer = boardState.getTurnPlayer();
        opponent = (curPlayer == PentagoBoardState.WHITE) ? PentagoBoardState.BLACK: PentagoBoardState.WHITE;

        // create new MCTS tree
        Tree tree = new Tree();
        tree.root.state = boardState;

        while (System.currentTimeMillis() < endTime) {
            /* SELECTION : get most promising Node using UCT policy */
            Node promisingNode = selection(tree.root);

            /* EXPANSION : create the children of the selected node */
            if (!promisingNode.state.gameOver()) {
                expansion(promisingNode);
            }

            /* SIMULATION : select child and simulate to terminal node */
            Node nodeToExplore = promisingNode;
            if (!promisingNode.childArray.isEmpty()) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            int playoutResult = simulation(nodeToExplore);

            /* BACK PROPAGATION : update the win and visit counts for nodes
                                 on the path to the current node */
            backPropogation(nodeToExplore, playoutResult);
        }

        // Get child with max score and update the tree
        Node selectedNode = tree.root.getChildWithMaxScore();
        tree.root = selectedNode;
        return selectedNode.move;
    }

    private static Node selection(Node rootNode) {
        Node node = rootNode;
        while (!node.childArray.isEmpty()) {
            node = findBestNodeWithUCT(node);
        }
        return node;
    }

    private static void expansion(Node node) {
        ArrayList<PentagoMove> legalMoves = node.state.getAllLegalMoves();

        for (int i = 0; i < legalMoves.size(); i++) {
            PentagoBoardState childState = (PentagoBoardState) node.state.clone();
            childState.processMove(legalMoves.get(i));

            Node child = new Node(legalMoves.get(i), childState, node);
            node.childArray.add(child);
        }
    }

    private static int simulation(Node node) {
        PentagoBoardState tmpState = (PentagoBoardState) node.state.clone();
        PentagoMove tmpMove;

        // TODO remove
        // check if game is over and opponent won
        if (opponent == tmpState.getWinner()) {
            node.parent.winCount = Integer.MIN_VALUE;
            return opponent;
        }
        // if game is not over simulate to the end by selecting random moves
        while (tmpState.getWinner() == Board.NOBODY) {
            tmpMove = (PentagoMove) tmpState.getRandomMove();
            tmpState.processMove(tmpMove);
        }
        return tmpState.getWinner();
    }

    private static void backPropogation(Node leafNode, int winner) {
        Node tmp = leafNode;

        while (tmp != null) {
            tmp.visitCount++;
            if (winner == curPlayer) {
                tmp.winCount++;
            }
            tmp = tmp.parent;
        }
    }

}