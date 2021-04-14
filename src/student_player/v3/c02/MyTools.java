package student_player.v3.c02;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.*;

public class MyTools {
    public static final long TIME_LIMIT = 1950;
    public static final double EXPLORATION_PARAMETER = Math.sqrt(2) / 7;

    private static final Random rand = new Random();


    /* ======== MCTS Node ======== */

    public static class Node {
        PentagoBoardState state;
        PentagoMove move;
        Node parent;
        List<Node> childArray;
        int visitCount;
        int winCount;

        public Node(PentagoMove move, PentagoBoardState state, Node parent) {
            this.state = state;
            this.move = move;

            this.parent = parent;
            this.childArray = new ArrayList<>();

            this.visitCount = 0;
            this.winCount = 0;
        }

        public Node() {
            this.state = null;
            this.move = null;

            this.parent = null;
            this.childArray = new ArrayList<>();

            this.visitCount = 0;
            this.winCount = 0;
        }

        // ----- Setters ----- //

        public void incrementVisit(){
            this.visitCount++;
        }

        public void addScore(double i){
            this.winCount += i;
        }

        // ----- Getters ----- //

        public Node getRandomChild(){
            if (childArray.isEmpty()) return null;
            return childArray.get(rand.nextInt(childArray.size()));
        }

        public Node getChildWithMaxScore() {
            return Collections.max(this.childArray, Comparator.comparing(c -> c.winCount));
        }

    }

    /* ======== MCTS Tree ======== */

    public static class Tree {
        Node root;

        public Tree(){
            this.root = new Node();
        }
    }

    /* ======== Upper Confidence Tree Functions ======== */

    private static double uctValue(double totalVisit, double nodeWinScore, double nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return (nodeWinScore / nodeVisit) + EXPLORATION_PARAMETER * Math.sqrt(Math.log(totalVisit) / nodeVisit);
    }

    public static Node findBestNodeWithUCT(Node node) {
        double parentVisit = node.visitCount;
        return Collections.max(
                node.childArray,
                Comparator.comparing(c -> uctValue(parentVisit, c.winCount, c.visitCount)));
    }


}

