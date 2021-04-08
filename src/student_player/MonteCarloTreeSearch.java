package student_player;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.*;

public class MonteCarloTreeSearch {
    static final long TIME_LIMIT = 1500;
    static final int WIN_SCORE = 10;

    static int curPlayer;
    static int opponent;

    private static Random rand = new Random(2019);

    public static class Node {
        PentagoBoardState state;
        PentagoMove move;
        Node parent;
        List<Node> childArray;
        int visitCount;
        double winScore;

        public Node(PentagoBoardState state) {
            this.state = state;
            this.move = null;

            this.parent = null;
            this.childArray = new ArrayList<>();

            this.visitCount = 0;
            this.winScore = 0;
        }

        public Node() {
            this.state = null;
            this.move = null;

            this.parent = null;
            this.childArray = new ArrayList<>();

            this.visitCount = 0;
            this.winScore = 0;
        }

        // setters and getters

        public void incrementVisit(){
            this.visitCount++;
        }

        public void addScore(double i){
            this.winScore += i;
        }

        public Node getRandomChild(){
            if (childArray.isEmpty()) return null;
            return childArray.get(rand.nextInt(childArray.size()));
        }

        public Node getChildWithMaxScore() {
            double maxScore = Integer.MIN_VALUE;
            Node maxChild = null;
            for (int i = 0; i < childArray.size(); i++) {
                if (maxScore < childArray.get(i).winScore){
                    maxScore = childArray.get(i).winScore;
                    maxChild = childArray.get(i);
                }
            }
            return maxChild;
        }

    }
    public static class Tree {
        Node root;

        public Tree(){
            this.root = new Node();
        }
    }

    public static class UCT {
        public static double uctValue(
                int totalVisit, double nodeWinScore, int nodeVisit) {
            if (nodeVisit == 0) {
                return Integer.MAX_VALUE;
            }
            return ((double) nodeWinScore / (double) nodeVisit)
                    + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
        }

        public static Node findBestNodeWithUCT(Node node) {
            int parentVisit = node.visitCount;
            return Collections.max(
                    node.childArray,
                    Comparator.comparing(c -> uctValue(parentVisit,
                            c.winScore, c.visitCount)));
        }
    }


    public static Move findNextMove(PentagoBoardState boardState) {
        // define an end time which will act as a terminating condition
        curPlayer = boardState.getTurnPlayer();
        opponent = (curPlayer == 0) ? PentagoBoardState.BLACK: PentagoBoardState.WHITE;
        Tree tree = new Tree();
        Node rootNode = tree.root;
        rootNode.state = boardState;

        long endTime = System.currentTimeMillis() + TIME_LIMIT;

        while (System.currentTimeMillis() < endTime) {
//            System.out.println("SELECTING...");
            Node promisingNode = selectPromisingNode(rootNode);
            if (!promisingNode.state.gameOver()) {
//                System.out.println("EXPANDING...");
                expandNode(promisingNode);
            }
            Node nodeToExplore = promisingNode;
            if (promisingNode.childArray.size() > 0) {
//                System.out.println("EXPLORING...");
                nodeToExplore = promisingNode.getRandomChild();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
//            System.out.println("PROPAGATING...");
            backPropogation(nodeToExplore, playoutResult);
        }

//        System.out.println("======= Child Size: " + rootNode.childArray.size() + " =======");

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.root = winnerNode;
        return winnerNode.move;
    }

    private static Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.childArray.size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private static void expandNode(Node node) {
        ArrayList<PentagoMove> possibleMoves = node.state.getAllLegalMoves();

        possibleMoves.forEach(move -> {
            PentagoBoardState newState = (PentagoBoardState) node.state.clone();
            newState.processMove(move);

            Node newNode = new Node(newState);
            newNode.move = move;
            newNode.parent = node;
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
