package cli;

import model.Board;
import model.Move;
import model.State;
import util.Constants;
import util.FileHandler;
import algorithm.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static util.Constants.*;

public class CLI {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        printBanner();
        Board board = promptBoardPath();
        int algorithmChoice = promptAlgorithm();
        int heuristicChoice = 0;

        if (algorithmChoice == 2 || algorithmChoice == 3 || algorithmChoice == 4) {
            heuristicChoice = promptHeuristic();
        }

        printStartMessage(board, algorithmChoice, heuristicChoice);
        try {
            Algorithm solver;
            switch (algorithmChoice) {
                case 1 -> solver = new UCS();
                // case 2 -> solver = new GBFS(heuristicChoice);
                // case 3 -> solver = new AStar(heuristicChoice);
                default -> throw new UnsupportedOperationException("Algoritma belum diimplementasikan");
            }

            SolutionPath solution = solver.findSolution(board);

            if (solution.isSolutionFound()) {
                printSolutionToTerminal(board, solution.getPath(), solver.getName(), solution.getNodesVisited(), solution.getExecutionTimeMs());

                String outputPath = "test/output/solution.txt";
                FileHandler.writeSolutionToFile(outputPath, board, solution.getPath(), solver.getName(), solution.getNodesVisited(), solution.getExecutionTimeMs());
                System.out.println(BRIGHT_GREEN + "Solution successfully written to: " + outputPath + RESET);
            } else {
                System.out.println(RED + "No solution found." + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "Terjadi kesalahan: " + e.getMessage() + RESET);
            e.printStackTrace();
        }
    }

    private static void printBanner() {
        System.out.println(BRIGHT_CYAN + "\n==========================================");
        System.out.println("         RUSH HOUR SOLVER CLI      ");
        System.out.println("==========================================" + RESET);
    }

    private static Board promptBoardPath() {
        Board board = null;
        while (board == null) {
            System.out.print(BRIGHT_YELLOW + "\nMasukkan path absolut file konfigurasi papan: " + RESET);
            try {
                String path = reader.readLine();
                board = FileHandler.readInputFile(path);
            } catch (IOException e) {
                System.out.println(RED + "Path tidak valid atau file tidak bisa dibaca. Silakan coba lagi." + RESET);
            }
        }
        return board;
    }

    private static int promptAlgorithm() {
        int choice = -1;
        while (choice < 1 || choice > 4) {
            System.out.println(BRIGHT_GREEN + "\nPilih algoritma pencarian yang ingin digunakan:" + RESET);
            System.out.println("1. Uniform Cost Search (UCS)");
            System.out.println("2. Greedy Best First Search");
            System.out.println("3. A* Search");
            System.out.println("4. Bonus Algorithm (Coming Soon)");
            System.out.print(BRIGHT_YELLOW + "Masukkan pilihan (1-4): " + RESET);

            try {
                choice = Integer.parseInt(reader.readLine());
            } catch (NumberFormatException | IOException e) {
                System.out.println(RED + "Input tidak valid. Silakan masukkan angka 1 sampai 4." + RESET);
            }
        }
        return choice;
    }

    private static int promptHeuristic() {
        int choice = -1;
        while (choice < 1 || choice > 3) {
            System.out.println(BRIGHT_MAGENTA + "\nPilih heuristic yang ingin digunakan:" + RESET);
            System.out.println("1. Heuristic 1");
            System.out.println("2. Heuristic 2");
            System.out.println("3. Heuristic 3");
            System.out.print(BRIGHT_YELLOW + "Masukkan pilihan (1-3): " + RESET);

            try {
                choice = Integer.parseInt(reader.readLine());
            } catch (NumberFormatException | IOException e) {
                System.out.println(RED + "Input tidak valid. Silakan masukkan angka 1 sampai 3." + RESET);
            }
        }
        return choice;
    }

    private static void printStartMessage(Board board, int algorithm, int heuristic) {
        System.out.println(BRIGHT_CYAN + "\n================ INFO PERMULAAN ================" + RESET);
        System.out.println("Board berhasil dimuat:");
        System.out.println(board);
        System.out.println("Algoritma yang dipilih: " + Constants.getAlgorithmName(algorithm));
        if (algorithm >= 2 && algorithm <= 4) {
            System.out.println("Heuristic yang dipilih: " + Constants.getHeuristicName(heuristic));
        }
        System.out.println(BRIGHT_CYAN + "===============================================\n" + RESET);
    }

    public static void printSolutionToTerminal(
            Board initialBoard,
            List<State> solutionPath,
            String algorithm,
            int totalNodesVisited,
            long executionTime) {

        System.out.println(BRIGHT_CYAN + "\n=========== HASIL SOLUSI ============" + RESET);
        System.out.println("Algorithm: " + algorithm);
        System.out.println("Step count: " + (solutionPath.size() - 1));
        System.out.println("Nodes visited: " + totalNodesVisited);
        System.out.println("Execution time: " + executionTime + " ms\n");

        System.out.println(BRIGHT_YELLOW + "Initial Board" + RESET);
        System.out.println(initialBoard.toStringWithColor());

        for (int i = 1; i < solutionPath.size(); i++) {
            State state = solutionPath.get(i);
            Move move = state.getLastMove();

            System.out.println("Move " + i + ": " + move.getPieceId() + "-" + move.getDirectionString());
            System.out.println(state.getBoard().toStringWithColor(move));
        }
    }
}
