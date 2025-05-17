package Solver;
import Model.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class SolverUCS {
    /**
     * Path node for UCS algorithm to track path cost and moves
     */
    private static class PathNode {
        PathNode parent;
        CarMove move;
        int cost;
        int stateRepresentation;
        
        public PathNode(PathNode parent, CarMove move, int cost, int stateRepresentation) {
            this.parent = parent;
            this.move = move;
            this.cost = cost;
            this.stateRepresentation = stateRepresentation;
        }
    }
    
    /**
     * Uniform Cost Search algorithm returning optimal solution to the puzzle:
     * ------------------------------------------------------------------------
     * - Uses a priority queue to always expand the path with lowest cost first
     * - Keeps track of visited states to avoid cycles
     * - Tracks cost for each state to update paths if a cheaper path is found
     * 
     * @return A list of the winning moves constructed by backtracking from goal state
     */
    public static LinkedList<CarMove> solve(Move puzzle) {
        // Priority queue ordered by path cost (number of moves)
        PriorityQueue<PathNode> frontier = new PriorityQueue<>(
            Comparator.comparingInt(node -> node.cost)
        );
        
        // Add the initial state to the frontier with cost 0
        int initialState = puzzle.integerRepresentation();
        frontier.add(new PathNode(null, null, 0, initialState));
        
        // Keep track of visited states and their lowest cost
        HashMap<Integer, Integer> costSoFar = new HashMap<>();
        costSoFar.put(initialState, 0);
        
        // For tracking visited states to avoid revisiting
        HashSet<Integer> visited = new HashSet<>();
        
        // Store the initial state of the puzzle to restore later
        // We'll keep track of all moves applied to the puzzle
        LinkedList<CarMove> appliedMoves = new LinkedList<>();
        LinkedList<CarMove> solution = new LinkedList<>();
        
        while (!frontier.isEmpty()) {
            // Get the node with the lowest cost
            PathNode current = frontier.poll();
            
            // Skip if we've processed this state already
            if (visited.contains(current.stateRepresentation)) {
                continue;
            }
            
            // Reset puzzle to initial state and then apply all moves to reach current node
            resetToInitialState(puzzle, appliedMoves);
            appliedMoves = applyMovesToNode(puzzle, current);
            
            // Check if we've reached the goal
            if (puzzle.won()) {
                // Build solution by backtracking through parents
                PathNode node = current;
                while (node.parent != null) {
                    solution.addFirst(node.move);
                    node = node.parent;
                }
                
                // Add the final winning move to push car to exit
                Car redCar = puzzle.cars.get(1);
                int direction = 1; // Default for right exit
                int dist = puzzle.distToExit();
                
                // Determine direction of final move based on exit position
                if (puzzle.exitRow == -1 && puzzle.exitCol == -1) {
                    // Default right exit
                    direction = 1;
                } else if (redCar.isHorizontal) {
                    if ((puzzle.exitCol < 0 || puzzle.exitCol < redCar.x) && redCar.y == puzzle.exitRow) {
                        // Exit is to the left
                        direction = -1;
                    } else {
                        // Exit is to the right
                        direction = 1;
                    }
                } else { // Vertical car
                    if ((puzzle.exitRow < 0 || puzzle.exitRow < redCar.y) && redCar.x == puzzle.exitCol) {
                        // Exit is above
                        direction = -1;
                    } else {
                        // Exit is below
                        direction = 1;
                    }
                }
                
                solution.add(new CarMove(1, direction * dist));
                
                // Reset puzzle to initial state before returning
                resetToInitialState(puzzle, appliedMoves);
                return solution;
            }
            
            // Mark this state as visited
            visited.add(current.stateRepresentation);
            
            // Explore all possible moves from the current state
            for (CarMove nextMove : puzzle.getPossibleMoves()) {
                // Apply the move
                puzzle.move(nextMove);
                
                // Get representation of the new state
                int newStateRep = puzzle.integerRepresentation();
                
                // Calculate cost to reach new state (each move costs 1)
                int newCost = current.cost + 1;
                
                // If new state not seen before or we found a shorter path
                if (!costSoFar.containsKey(newStateRep) || 
                    newCost < costSoFar.get(newStateRep)) {
                    
                    // Update cost to reach this state
                    costSoFar.put(newStateRep, newCost);
                    
                    // Add new state to frontier with its cost
                    frontier.add(new PathNode(current, nextMove, newCost, newStateRep));
                }
                
                // Undo move to restore puzzle to previous state
                puzzle.cancelMove(nextMove);
            }
        }
        
        // If no solution was found, return empty list
        return solution;
    }
    
    /**
     * Helper method to reset the puzzle to its initial state
     * by canceling all applied moves in reverse order
     */
    private static void resetToInitialState(Move puzzle, LinkedList<CarMove> moves) {
        for (int i = moves.size() - 1; i >= 0; i--) {
            puzzle.cancelMove(moves.get(i));
        }
    }
    
    /**
     * Helper method to apply all moves to reach a specific node
     * Returns the list of applied moves
     */
    private static LinkedList<CarMove> applyMovesToNode(Move puzzle, PathNode node) {
        LinkedList<CarMove> moves = new LinkedList<>();
        
        // Build path from start to node
        LinkedList<CarMove> pathToNode = new LinkedList<>();
        PathNode current = node;
        while (current != null && current.move != null) {
            pathToNode.addFirst(current.move);
            current = current.parent;
        }
        
        // Apply all moves in the path
        for (CarMove move : pathToNode) {
            puzzle.move(move);
            moves.add(move);
        }
        
        return moves;
    }
}