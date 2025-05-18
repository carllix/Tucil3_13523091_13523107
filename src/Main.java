import java.io.IOException;
import java.util.List;

import algorithm.Algorithm;
import algorithm.SolutionPath;
import algorithm.UCS;
import cli.CLI;
import model.Board;
import model.Move;
import model.State;
import util.FileHandler;

public class Main {

    public static void main(String[] args) {
        try {
            String filepath = "test/input/test4.txt";
            Board board = FileHandler.readInputFile(filepath);

            System.out.println("Board successfully read:");
            System.out.print(board);

            System.out.println("Exit position: " + board.getExitPosition());
            System.out.println("Primary piece: " + board.getPrimaryPiece());

            Algorithm ucs = new UCS();
            SolutionPath solution = ucs.findSolution(board);

            if (solution.isSolutionFound()) {
                System.out.println("\nSolution found!");
                System.out.println();
                CLI.printSolutionToTerminal(board, solution.getPath(), ucs.getName(),
                        solution.getNodesVisited(), solution.getExecutionTimeMs());

                // Simpan ke file
                String outputPath = "test/output/solution.txt";
                FileHandler.writeSolutionToFile(outputPath, board, solution.getPath(), ucs.getName(),
                        solution.getNodesVisited(), solution.getExecutionTimeMs());
                System.out.println("\nSolution successfully written to: " + outputPath);
            } else {
                System.out.println("No solution found.");
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
