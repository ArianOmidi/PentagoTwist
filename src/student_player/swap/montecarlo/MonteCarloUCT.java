package student_player.swap.montecarlo;

import java.util.Collections;
import java.util.Comparator;

public class MonteCarloUCT {
    private static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return (nodeWinScore / (double) nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static MonteCarloNode findBestNodeWithUCT(MonteCarloNode node) {
        int parentVisit = node.getState().getVisitCount();
        return Collections.max(
                node.getChildren(),
                Comparator.comparing(c -> uctValue(parentVisit, c.getState().getWinScore(), c.getState().getVisitCount())));
    }
}
