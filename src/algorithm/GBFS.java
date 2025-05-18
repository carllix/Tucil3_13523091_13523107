package algorithm;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import heuristic.Heuristic;
import model.Board;
import model.State;
import util.Constants;

public class GBFS implements Algorithm {

    private Heuristic heuristic;

    @Override
    public SolutionPath findSolution(Board initialBoard) {
        long startTime = System.currentTimeMillis();
        int nodesVisited = 0;

        State initialState = new State(initialBoard);
        initialState.setHeuristicValue(heuristic.calculate(initialBoard));
  
        PriorityQueue<State> frontier = new PriorityQueue<>(Comparator.comparing(State::getHeuristicValue));

        Set<String> visited = new HashSet<>();

        frontier.add(initialState);

        while (!frontier.isEmpty()) {
            State currentState = frontier.poll();
            nodesVisited++;

            if (currentState.getBoard().isSolved()) {
                long endTime = System.currentTimeMillis();
                List<State> path = currentState.getSolutionPath();
                return new SolutionPath(path, nodesVisited, endTime - startTime);
            }

            String boardStr = currentState.getBoard().toString();
            if (visited.contains(boardStr)) {
                continue;
            }

            visited.add(boardStr);

            List<State> childStates = currentState.generateChildStates();

            for (State childState : childStates) {
                String childBoardStr = childState.getBoard().toString();
                if (!visited.contains(childBoardStr)) {
                    childState.setHeuristicValue(heuristic.calculate(childState.getBoard()));
                    frontier.add(childState);
                }
            }
        }

        // No solution found
        long endTime = System.currentTimeMillis();
        return new SolutionPath(nodesVisited, endTime - startTime);
    }
    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return Constants.GBFS;
    }
}
