package algorithm;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import models.Board;
import models.State;
import util.Constants;

public class UCS implements Algorithm {

    @Override
    public SolutionPath findSolution(Board initialBoard) {
        long startTime = System.currentTimeMillis();
        int nodesVisited = 0;


        State initialState = new State(initialBoard);
        PriorityQueue<State> frontier = new PriorityQueue<>(Comparator.comparing(State::getCost));
        
        // Use a set for visited states based on board configuration
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
                    frontier.add(childState);
                }
            }
        }
        
        // No solution found
        long endTime = System.currentTimeMillis();
        return new SolutionPath(nodesVisited, endTime - startTime);
    }

    @Override
    public String getName() {
        return Constants.UCS;
    }
}