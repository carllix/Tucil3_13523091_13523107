import java.io.IOException;
import java.util.List;

import algorithm.Algorithm;
import algorithm.SolutionPath;
import algorithm.UCS;
import util.FileHandler;
import model.Board;
import model.State;

public class Main {
    public static void main(String[] args) {
        try {
            String filepath = "test/test3.txt";
            Board board = FileHandler.readInputFile(filepath);

            System.out.println("Board successfully read:");
            System.out.print(board);

            System.out.println("Exit position: " + board.getExitPosition());
            System.out.println("Primary piece: " + board.getPrimaryPiece());

            Algorithm ucs = new UCS();
            SolutionPath solution = ucs.findSolution(board);
            if (solution.isSolutionFound()) {
                System.out.println("\nSolution found!");
                System.out.println("Steps: " + solution.getStepCount());
                System.out.println("Nodes visited: " + solution.getNodesVisited());
                System.out.println("Execution time: " + solution.getExecutionTimeMs() + " ms");
                
                List<State> path = solution.getPath();
                System.out.println("\nSolution moves:");
                for (int i = 1; i < path.size(); i++) {
                    State state = path.get(i);
                    System.out.println("Move " + i + ": " + state.getLastMove());
                }
            } else {
                System.out.println("\nNo solution found.");
                System.out.println("Nodes visited: " + solution.getNodesVisited());
                System.out.println("Execution time: " + solution.getExecutionTimeMs() + " ms");
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
