package Solver;
import Model.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.swing.JPanel;

public class AnimatedSolverUCS extends JPanel {
    // Colors and dimensions
    final int CELL_SIZE = 100;
    final int MARGIN = CELL_SIZE / 8;
    final Color BACKGROUND = new Color(200, 200, 170);
    final Color RED = new Color(220, 0, 0);
    final Color BLUE = new Color(0, 120, 170);
    
    // The puzzle
    Move puzzle;
    public final int PANEL_WIDTH;
    public final int PANEL_HEIGHT;
    
    // for animating the moves
    private CarMove currMove = new CarMove(-1, 0); // the move being animated
    private float moveFraction = 0; // indicates the advancement of the animation of currMove
    private int numFrames = 10; // number of frames appearing in one cell shift
    private int animationTimeStep; // time delay between two frames
    private int animationShift = 0; // used to have smooth animation
    
    // Path node for UCS
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
    
    public AnimatedSolverUCS(Move puzzle, int animationTimeStep) {
        this.puzzle = puzzle;
        PANEL_WIDTH = puzzle.parking.width * CELL_SIZE;
        PANEL_HEIGHT = puzzle.parking.height * CELL_SIZE;
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT)); 
        this.setBackground(BACKGROUND);
        this.animationTimeStep = animationTimeStep;
    }
    
    // Method for drawing the current configuration of the puzzle, with the currMove animated
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // draw grid
        g2d.setColor(new Color(100, 100, 100));
        for (var x = 0; x < puzzle.parking.width + 1; x++)
            g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, PANEL_HEIGHT);
        for (var y = 0; y < puzzle.parking.height + 1; y++)
            g2d.drawLine(0, y * CELL_SIZE, PANEL_WIDTH, y * CELL_SIZE);
                
        // draw cars
        g2d.setStroke(new BasicStroke(3));
        for (var car: puzzle.cars.values()) {
            if (car.id == 1)
                g2d.setColor(RED);
            else
                g2d.setColor(BLUE);
            var carWidth = (car.isHorizontal ? car.size : 1);
            var carHeight = (car.isHorizontal ? 1 : car.size);
            // animating the move
            int dx = 0;
            int dy = 0;
            if (car.id == currMove.carId) {
                if (car.isHorizontal)
                    dx += (int) ((moveFraction + animationShift) * CELL_SIZE);
                else
                    dy += (int) ((moveFraction + animationShift) * CELL_SIZE);
            }
            // car fill
            g2d.fillRoundRect(CELL_SIZE * car.x + MARGIN + dx,
                    CELL_SIZE * car.y + MARGIN + dy,
                    CELL_SIZE * carWidth - 2 * MARGIN,
                    CELL_SIZE * carHeight - 2 * MARGIN,
                    2 * MARGIN, 2 * MARGIN);
            // car parameter
            g2d.setColor(Color.black);
            g2d.drawRoundRect(CELL_SIZE * car.x + MARGIN + dx,
                    CELL_SIZE * car.y + MARGIN + dy,
                    CELL_SIZE * carWidth - 2 * MARGIN,
                    CELL_SIZE * carHeight - 2 * MARGIN,
                    2 * MARGIN, 2 * MARGIN);
        }
    }
    
    /**
     * Uniform Cost Search algorithm with animation functionality
     * Finds the optimal solution to the Rush Hour puzzle by expanding the path with the lowest cost first
     */
    public LinkedList<CarMove> solve() {
        // Priority queue ordered by path cost (number of moves)
        PriorityQueue<PathNode> frontier = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));
        
        // Add the initial state to the frontier with cost 0
        frontier.add(new PathNode(null, null, 0, puzzle.integerRepresentation()));
        
        // Keep track of visited states and their lowest cost
        HashMap<Integer, Integer> costSoFar = new HashMap<>();
        costSoFar.put(puzzle.integerRepresentation(), 0);
        
        // Keep track of configurations we've already seen
        HashSet<Integer> visited = new HashSet<>();
        
        PathNode goalNode = null;
        LinkedList<CarMove> exploredMoves = new LinkedList<>(); // For animation purposes
        
        while (!frontier.isEmpty()) {
            // Get the node with the lowest cost
            PathNode current = frontier.poll();
            
            // If we've already processed this state with a better cost, skip it
            if (visited.contains(current.stateRepresentation)) {
                continue;
            }
            
            // Apply all moves to bring the puzzle to the current state (for animation)
            applyMovesToReachNode(exploredMoves, current);
            
            // Check if we've reached the goal
            if (puzzle.won()) {
                goalNode = current;
                break;
            }
            
            // Mark this state as visited
            visited.add(current.stateRepresentation);
            
            // Explore all possible moves from the current state
            for (CarMove nextMove : puzzle.getPossibleMoves()) {
                // Apply the move
                puzzle.move(nextMove);
                
                // Animate this move (but only if animation is enabled)
                if (animationTimeStep > 0) {
                    animateMove(nextMove);
                }
                
                // Get the representation of the new state
                int newStateRepresentation = puzzle.integerRepresentation();
                
                // Calculate the cost to reach this new state
                int newCost = current.cost + 1; // Each move costs 1
                
                // If we haven't seen this state before or have found a cheaper path
                if (!costSoFar.containsKey(newStateRepresentation) || 
                    newCost < costSoFar.get(newStateRepresentation)) {
                    
                    // Update the cost to reach this state
                    costSoFar.put(newStateRepresentation, newCost);
                    
                    // Add this state to the frontier
                    frontier.add(new PathNode(current, nextMove, newCost, newStateRepresentation));
                }
                
                // Undo the move to restore the puzzle to its previous state
                puzzle.cancelMove(nextMove);
                
                // Animate canceling this move
                if (animationTimeStep > 0) {
                    animateMove(nextMove, -1);
                }
            }
            
            // Undo all moves to restore the puzzle to its original state
            undoAllMoves(exploredMoves);
        }
        
        // Reconstruct the solution path
        LinkedList<CarMove> solution = new LinkedList<>();
        
        if (goalNode != null) {
            // Work backwards from goal node to initial node
            PathNode current = goalNode;
            while (current.parent != null) {
                solution.addFirst(current.move);
                current = current.parent;
            }
            
            // Add the final winning move, pushing the red car to the exit
            solution.add(new CarMove(1, puzzle.distToExit()));
        }
        
        return solution;
    }
    
    // Helper method to apply all moves needed to reach a specific node
    private void applyMovesToReachNode(LinkedList<CarMove> exploredMoves, PathNode node) {
        // Clear any previous moves
        undoAllMoves(exploredMoves);
        exploredMoves.clear();
        
        // Build up the path from the start to this node
        LinkedList<CarMove> pathToNode = new LinkedList<>();
        PathNode current = node;
        while (current != null && current.move != null) {
            pathToNode.addFirst(current.move);
            current = current.parent;
        }
        
        // Apply all moves in the path
        for (CarMove move : pathToNode) {
            exploredMoves.add(move);
            puzzle.move(move);
            if (animationTimeStep > 0) {
                animateMove(move);
            }
        }
    }
    
    // Helper method to undo all moves in the explored path
    private void undoAllMoves(LinkedList<CarMove> exploredMoves) {
        for (int i = exploredMoves.size() - 1; i >= 0; i--) {
            CarMove move = exploredMoves.get(i);
            puzzle.cancelMove(move);
            if (animationTimeStep > 0) {
                animateMove(move, -1);
            }
        }
    }
    
    // Shows an animation playing the input list of moves 
    public void playMoves(LinkedList<CarMove> moves) {
        var defaultNumFrames = numFrames;
        var defaultAnimationTimeStep = animationTimeStep;
        animationShift = 0;
        numFrames = 100;
        animationTimeStep = 1;
        
        Car redCar = puzzle.cars.get(1);
        
        for (var carMove : moves) {
            // If this is the red car's final exit move, handle specially
            if (carMove.carId == 1 && isExitMove(redCar, carMove)) {
                // Only animate the portion of the move that's within bounds
                int safeDistance = getSafeAnimationDistance(redCar, carMove);
                if (safeDistance > 0) {
                    CarMove safeMove = new CarMove(carMove.carId, 
                            carMove.dxy > 0 ? safeDistance : -safeDistance);
                    animateMove(safeMove);
                }
                
                // Instead of moving the car out of bounds, just mark it as exited
                System.out.println("Red car has exited the puzzle!");
                break;
            } else {
                // Regular move animation
                animateMove(carMove);
                puzzle.move(carMove);
            }
        }
        
        numFrames = defaultNumFrames;
        animationShift = defaultAnimationTimeStep;
    }
    
    // Determines if this move will cause the car to exit the puzzle
    private boolean isExitMove(Car car, CarMove move) {
        // If no custom exit is set, only right exit is possible (default behavior)
        if (puzzle.exitRow == -1 && puzzle.exitCol == -1) {
            return car.isHorizontal && 
                   car.x + car.size + move.dxy > puzzle.parking.width;
        }
        
        // Check against custom exit
        if (car.isHorizontal) {
            if (car.y == puzzle.exitRow) {
                // Right exit
                if (puzzle.exitCol >= puzzle.parking.width || puzzle.exitCol > car.x) {
                    return car.x + car.size + move.dxy > puzzle.parking.width || 
                           car.x + car.size + move.dxy > puzzle.exitCol;
                }
                // Left exit
                else if (puzzle.exitCol < 0 || puzzle.exitCol < car.x) {
                    return car.x + move.dxy < 0 || car.x + move.dxy < puzzle.exitCol;
                }
            }
        } else { // Vertical car
            if (car.x == puzzle.exitCol) {
                // Bottom exit
                if (puzzle.exitRow >= puzzle.parking.height || puzzle.exitRow > car.y) {
                    return car.y + car.size + move.dxy > puzzle.parking.height || 
                           car.y + car.size + move.dxy > puzzle.exitRow;
                }
                // Top exit
                else if (puzzle.exitRow < 0 || puzzle.exitRow < car.y) {
                    return car.y + move.dxy < 0 || car.y + move.dxy < puzzle.exitRow;
                }
            }
        }
        
        return false;
    }
    
    // Determines the maximum safe distance to animate before car would exit the puzzle
    private int getSafeAnimationDistance(Car car, CarMove move) {
        int originalDxy = Math.abs(move.dxy);
        int safeDistance = originalDxy;
        
        if (car.isHorizontal) {
            if (move.dxy > 0) { // Moving right
                safeDistance = Math.min(safeDistance, puzzle.parking.width - (car.x + car.size));
            } else { // Moving left
                safeDistance = Math.min(safeDistance, car.x);
            }
        } else { // Vertical car
            if (move.dxy > 0) { // Moving down
                safeDistance = Math.min(safeDistance, puzzle.parking.height - (car.y + car.size));
            } else { // Moving up
                safeDistance = Math.min(safeDistance, car.y);
            }
        }
        
        return safeDistance;
    }
    
    // Shows an animation of playing carMove, we can specify an optional argument
    // of direction: when (direction = -1) show animation of the opposite move
    private void animateMove(CarMove carMove) {
        animateMove(carMove, 1);
    }
    
    private void animateMove(CarMove carMove, int direction) {
        if (animationTimeStep == 0)
            return;
        currMove = carMove;
        moveFraction = 0;
        int signDxy = (carMove.dxy > 0 ? 1 : -1);
        for (int i = 1; i < numFrames * signDxy * carMove.dxy; i++) {
            moveFraction += signDxy * direction / (float) numFrames;
            wait(animationTimeStep);
            repaint();
            wait(animationTimeStep);
        }
        currMove = new CarMove(-1, 0);
        moveFraction = 0;
    }
    
    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}