import java.io.IOException;
import java.util.List;

import algorithm.Algorithm;
import algorithm.SolutionPath;
import algorithm.UCS;
import model.Board;
import model.State;
import model.Move;
import util.FileHandler;

public class Main {

    // Ini sementara aja nanti pindah ke class cli
    public static void printSolutionToTerminal(
            Board initialBoard,
            List<State> solutionPath,
            String algorithm,
            int totalNodesVisited,
            long executionTime) {

        System.out.println("Algorithm: " + algorithm);
        System.out.println("Step count: " + (solutionPath.size() - 1));
        System.out.println("Nodes visited: " + totalNodesVisited);
        System.out.println("Execution time: " + executionTime + " ms");
        System.out.println();

        System.out.println("Initial Board");
        System.out.println(initialBoard.toStringWithColor());

        for (int i = 1; i < solutionPath.size(); i++) {
            State state = solutionPath.get(i);
            Move move = state.getLastMove();

            System.out.println("Move " + i + ": " + move.getPieceId() + "-" + move.getDirectionString());
            System.out.println(state.getBoard().toStringWithColor(move));
        }        
    }

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
                printSolutionToTerminal(board, solution.getPath(), ucs.getName(),
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
