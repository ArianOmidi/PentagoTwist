package student_player.v5.c15;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;
import pentago_twist.PentagoMove;

import java.util.*;

public class MyTools {
    public static final long FIRST_MOVE_TIME_LIMIT = 1950;
    public static final long MOVE_TIME_LIMIT = 1950;
    public static final int WIN_SCORE = 10;
    public static final double EXPLORATION_PARAMETER = Math.sqrt(1.5);

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

        public Node getChildWithState(PentagoBoardState state) {
            for (int i = 0; i < childArray.size(); i++) {
                if (equalsBoard(childArray.get(i).state, state)){
                    return childArray.get(i);
                }
            }

            return null;
        }

        public List<Integer> getChildVisited(){
            ArrayList<Integer> arrayList = new ArrayList<>();

            for (int i = 0; i < childArray.size(); i++) {
                arrayList.add(childArray.get(i).visitCount);
            }

            Collections.sort(arrayList);

            return arrayList;
        }
    }

    /* ======== MCTS Tree ======== */

    public static class Tree {
        Node root;

        public Tree(){
            this.root = new Node();
        }

        public void pruneTree(PentagoBoardState state) {
            this.root = root.getChildWithState(state);
            this.root.parent = null;
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

    /* ======== Helper Functions ======== */

    public static boolean equalsBoard(PentagoBoardState s1, PentagoBoardState s2) {
        if (s1 == s2) {
            return true;
        }

        Piece[][] s1Board = s1.getBoard();
        Piece[][] s2Board = s2.getBoard();
        for (int i = 0; i < s1Board.length; i++) {
            for (int j = 0; j < s1Board[0].length; j++) {
                if (s1Board[i][j] != s2Board[i][j])
                    return false;
            }
        }
        if (s1.getTurnPlayer() != s2.getTurnPlayer())
            return false;
        if (s1.getWinner() != s2.getWinner())
            return false;
        if (s1.getTurnNumber() != s2.getTurnNumber())
            return false;

        return true;
    }

}

