package student_player.v2;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.*;

public class MyTools {
    public static final long TIME_LIMIT = 1950;
    public static final int WIN_SCORE = 10;
    public static final double EXPLORATION_PARAMETER = Math.sqrt(2);

    private static final Random rand = new Random();


    /* ======== MCTS Node ======== */

    public static class Node {
        PentagoBoardState state;
        PentagoMove move;
        Node parent;
        List<Node> childArray;
        int visitCount;
        double winScore;

        public Node(PentagoMove move, PentagoBoardState state, Node parent) {
            this.state = state;
            this.move = move;

            this.parent = parent;
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

        // ----- Setters ----- //

        public void incrementVisit(){
            this.visitCount++;
        }

        public void addScore(double i){
            this.winScore += i;
        }

        // ----- Getters ----- //

        public Node getRandomChild(){
            if (childArray.isEmpty()) return null;
            return childArray.get(rand.nextInt(childArray.size()));
        }

        public Node getChildWithMaxScore() {
            return Collections.max(this.childArray, Comparator.comparing(c -> c.winScore));
        }

    }

    /* ======== MCTS Tree ======== */

    public static class Tree {
        Node root;

        public Tree(PentagoBoardState rootState){
            this.root = new Node();
        }
    }

    /* ======== Upper Confidence Tree Functions ======== */

    private static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return ((double) nodeWinScore / (double) nodeVisit)
                + EXPLORATION_PARAMETER * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.visitCount;
        return Collections.max(
                node.childArray,
                Comparator.comparing(c -> uctValue(parentVisit,
                        c.winScore, c.visitCount)));
    }


}

