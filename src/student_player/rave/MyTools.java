package student_player.rave;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.*;

public class MyTools {
    public static final long TIME_LIMIT = 1950;
    public static final double b = 0.5;
    public static final double EXPLORATION_PARAMETER = Math.sqrt(2);

    private static final Random rand = new Random();

    static Map<String, List<Node>> map = new HashMap<>();


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

    private static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return ((double) nodeWinScore / (double) nodeVisit)
                + EXPLORATION_PARAMETER * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    private static double value(int totalVisit, double nodeWinScore, int nodeVisit, int wi, int ni) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        if (ni == 0) {
            return ((double) nodeWinScore / (double) nodeVisit)
                    + EXPLORATION_PARAMETER * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
        }

        double beta = ni / (nodeVisit + ni + 4 * b * ni * nodeVisit);
        return (1 - beta) * ((double) nodeWinScore / (double) nodeVisit)
                + beta * ((double) wi / (double) ni)
                + EXPLORATION_PARAMETER * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static Node findBestNode(Node node) {
        int parentVisit = node.visitCount;
        int ni = 0;
        int wi = 0;
        List<Node> nodes = map.get(node.state.toString());

        for (int i = 0; i < nodes.size(); i++) {
            ni += nodes.get(i).visitCount;
            wi += nodes.get(i).winCount;
        }

        if (ni == 0) ni = 1;

        int finalWi = wi;
        int finalNi = ni;
        return Collections.max(
                node.childArray,
                Comparator.comparing(c -> value(parentVisit, c.winCount, c.visitCount, finalWi, finalNi)));
    }


}

