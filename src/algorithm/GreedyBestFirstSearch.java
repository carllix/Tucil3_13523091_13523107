
package algorithm;

import heuristic.Heuristic;
import models.Board;
import models.State;
import util.Constants;

public class GreedyBestFirstSearch implements Algorithm {
    private Heuristic heuristic;

    public GreedyBestFirstSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public SolutionPath findSolution(Board initialBoard) {
        long startTime = System.currentTimeMillis();
        int nodesVisited = 0;

        // Buat state awal
        State initialState = new State(initialBoard);
        initialState.setHeuristicValue(heuristic.calculate(initialBoard));

        // Algo nya disini len
        // .....
        // ....

        
        // Ini kalo ga nemu solusi
        long endTime = System.currentTimeMillis();
        return new SolutionPath(nodesVisited, endTime - startTime);
    }

    @Override
    public String getName() {
        return Constants.GBFS;
    }
}