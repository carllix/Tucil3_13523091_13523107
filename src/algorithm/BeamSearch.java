package algorithm;

import java.util.*;

import heuristic.Heuristic;
import model.Board;
import model.State;
import util.Constants;

public class BeamSearch implements Algorithm {

    private Heuristic heuristic;
    private final int beamWidth = 5;
    private final int maxIterations = 10000;

    @Override
    public SolutionPath findSolution(Board initialBoard) {
        long startTime = System.currentTimeMillis();
        int nodesVisited = 0;
        int iterations = 0;

        State initialState = new State(initialBoard);
        initialState.setHeuristicValue(heuristic.calculate(initialBoard));

        PriorityQueue<State> frontier = new PriorityQueue<>(Comparator.comparing(State::getHeuristicValue));

        frontier.add(initialState);

        Set<String> visited = new HashSet<>();
        visited.add(initialBoard.toString());

        while (!frontier.isEmpty() && iterations < maxIterations) {
            iterations++;
            List<State> current = new ArrayList<>(); // Ambil sesuai beamWitdth terbaik
            int count = 0;
            while (!frontier.isEmpty() && count < beamWidth) {
                current.add(frontier.poll());
                count++;
            }

            List<State> nextCandidates = new ArrayList<>();

            for (State currentState : current) {
                nodesVisited++;
                if (currentState.getBoard().isSolved()) {
                    long endTime = System.currentTimeMillis();
                    List<State> path = currentState.getSolutionPath();
                    return new SolutionPath(path, nodesVisited, endTime - startTime);
                }

                List<State> childStates = currentState.generateChildStates();

                for (State childState : childStates) {
                    String boardKey = childState.getBoard().toString();
                    if (!visited.contains(boardKey)) {
                        childState.setHeuristicValue(heuristic.calculate(childState.getBoard()));
                        nextCandidates.add(childState);
                        visited.add(boardKey);
                    }
                }
            }

            nextCandidates.sort(Comparator.comparing(State::getHeuristicValue));

            frontier.clear();
            for (int i = 0; i < Math.min(beamWidth * 2, nextCandidates.size()); i++) {
                frontier.add(nextCandidates.get(i));
            }
        }

        long endTime = System.currentTimeMillis();
        return new SolutionPath(nodesVisited, endTime - startTime);
    }

    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return Constants.BEAM;
    }
}