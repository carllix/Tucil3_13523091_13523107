
package cli;

import util.Constants;
import util.FileHandler;
import algorithm.*;
import models.Board;
import models.Move;
import models.State;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static util.Constants.*;

public class CLI {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        clearScreen();
        displayStartupAnimation();
        Board board = promptBoardPath();
        int algorithmChoice = promptAlgorithm();
        int heuristicChoice = 0;

        // if (algorithmChoice == 2 || algorithmChoice == 3 || algorithmChoice == 4) {
        //     heuristicChoice = promptHeuristic();
        //     // Custom algorithm class for placeholder implementation
        //     private static class CustomAlgorithm implements Algorithm {
        //         private final int heuristicChoice;

        //         public CustomAlgorithm(int heuristicChoice) {
        //             this.heuristicChoice = heuristicChoice;
        //         }

        //         @Override
        //         public SolutionPath findSolution(Board board) {
        //             // Placeholder implementation - in reality, you would implement your own
        //             // algorithm here
        //             return new UCS().findSolution(board);
        //         }

        //         @Override
        //         public String getName() {
        //             return "Custom Algorithm (with Heuristic " + heuristicChoice + ")";
        //         }
        //     }
        // }

        printStartMessage(board, algorithmChoice, heuristicChoice);
        showLoadingAnimation("Solving puzzle");

        try {
            Algorithm solver;
            switch (algorithmChoice) {
                case 1 -> solver = new UCS();
                // case 2 -> solver = new GBFS(heuristicChoice);
                // case 3 -> solver = new AStar(heuristicChoice);
                // case 4 -> solver = new CustomAlgorithm(heuristicChoice);
                default -> throw new UnsupportedOperationException("Algoritma belum diimplementasikan");
            }

            SolutionPath solution = solver.findSolution(board);

            if (solution.isSolutionFound()) {
                clearScreen();
                printSolutionFoundAnimation();
                printSolutionToTerminal(board, solution.getPath(), solver.getName(), solution.getNodesVisited(),
                        solution.getExecutionTimeMs());

                // Ask if user wants to save the solution
                boolean saveResult = promptSaveOption();

                if (saveResult) {
                    String outputPath = promptOutputPath();
                    FileHandler.writeSolutionToFile(outputPath, board, solution.getPath(), solver.getName(),
                            solution.getNodesVisited(), solution.getExecutionTimeMs());
                    System.out
                            .println(BRIGHT_GREEN + BOLD + "✓ Solution successfully written to: " + outputPath + RESET);
                } else {
                    System.out.println(BRIGHT_YELLOW + "Solution was not saved to file." + RESET);
                }
            } else {
                System.out.println("\n" + BG_RED + WHITE + BOLD + " No solution found! " + RESET);
                System.out.println(RED + "The puzzle appears to be unsolvable with the selected algorithm." + RESET);
            }
        } catch (Exception e) {
            System.out.println("\n" + BG_RED + WHITE + BOLD + " ERROR " + RESET);
            System.out.println(RED + "Terjadi kesalahan: " + e.getMessage() + RESET);
            e.printStackTrace();
        }

        // Exit message
        System.out.println("\n" + BRIGHT_CYAN + "Press Enter to exit..." + RESET);
        try {
            reader.readLine();
        } catch (IOException e) {
            // Ignore
        }
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void displayStartupAnimation() {
        String[] frames = {
                "   R U S H   H O U R   S O L V E R   ",
                "   R-U-S-H   H-O-U-R   S-O-L-V-E-R   ",
                "   R-U-S-H---H-O-U-R---S-O-L-V-E-R   ",
                "   R U S H - H O U R - S O L V E R   "
        };

        try {
            for (int i = 0; i < 3; i++) {
                for (String frame : frames) {
                    clearScreen();
                    String[] colors = { BRIGHT_RED, BRIGHT_YELLOW, BRIGHT_GREEN, BRIGHT_CYAN, BRIGHT_MAGENTA };
                    for (int j = 0; j < frame.length(); j++) {
                        String color = colors[j % colors.length];
                        System.out.print(color + frame.charAt(j) + RESET);
                    }
                    System.out.println("\n\n" + BRIGHT_CYAN + "Starting application..." + RESET);
                    TimeUnit.MILLISECONDS.sleep(150);
                }
            }
        } catch (InterruptedException e) {
            // Ignore interruption
        }

        printBanner();
    }

    private static void showLoadingAnimation(String message) {
        String[] spinner = { "⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏" };

        System.out.print("\n");
        Thread loadingThread = new Thread(() -> {
            int i = 0;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.print("\r" + BRIGHT_CYAN + spinner[i % spinner.length] + " " + message + "..." + RESET);
                    TimeUnit.MILLISECONDS.sleep(100);
                    i++;
                }
            } catch (InterruptedException e) {
                // Thread was interrupted, exit
            }
        });

        loadingThread.start();
        try {
            TimeUnit.SECONDS.sleep(2); // Simulate loading for at least 2 seconds
        } catch (InterruptedException e) {
            // Ignore
        }
        loadingThread.interrupt();
        System.out.println("\r" + " ".repeat(message.length() + 15)); // Clear the line
    }

    private static void printSolutionFoundAnimation() {
        String message = "Solution found!";
        try {
            for (int i = 0; i < message.length(); i++) {
                clearScreen();
                System.out.println("\n\n");
                System.out.print(BRIGHT_GREEN + " ".repeat((80 - message.length()) / 2));
                for (int j = 0; j <= i; j++) {
                    System.out.print(BOLD + message.charAt(j) + RESET);
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            // Ignore interruption
        }
    }

    private static void printBanner() {
        String title = " RUSH HOUR SOLVER CLI ";
        int width = 50;
        int padding = (width - title.length()) / 2;

        StringBuilder banner = new StringBuilder();
        banner.append(BRIGHT_CYAN + BOLD);
        banner.append("\n");

        // Top border with fancy characters
        banner.append(" " + TOP_LEFT);
        for (int i = 0; i < width - 2; i++) {
            banner.append(HORIZONTAL);
        }
        banner.append(TOP_RIGHT + "\n");

        // Middle part with title
        banner.append(" " + VERTICAL);
        banner.append(" ".repeat(padding));
        banner.append(BRIGHT_YELLOW + title + BRIGHT_CYAN);
        banner.append(" ".repeat(width - 2 - padding - title.length()));
        banner.append(VERTICAL + "\n");

        // Bottom border
        banner.append(" " + BOTTOM_LEFT);
        for (int i = 0; i < width - 2; i++) {
            banner.append(HORIZONTAL);
        }
        banner.append(BOTTOM_RIGHT);
        banner.append(RESET);

        System.out.println(banner.toString());
        System.out.println("\n" + BRIGHT_GREEN + "Welcome to the Rush Hour Puzzle Solver!" + RESET);
        System.out.println(BRIGHT_WHITE
                + "This application will help you find the optimal solution to Rush Hour puzzles." + RESET);
    }

    private static Board promptBoardPath() {
        Board board = null;
        while (board == null) {
            printBoxedPrompt("FILE CONFIGURATION", "Enter the absolute path to the board configuration file:");

            try {
                String path = reader.readLine();
                if (path.trim().isEmpty()) {
                    printErrorMessage("Path cannot be empty. Please enter a valid file path.");
                    continue;
                }

                showLoadingAnimation("Loading board");
                board = FileHandler.readInputFile(path);
                printSuccessMessage("Board loaded successfully!");
            } catch (IOException e) {
                printErrorMessage("Path tidak valid atau file tidak bisa dibaca: " + e.getMessage());
            }
        }
        return board;
    }

    private static int promptAlgorithm() {
        int choice = -1;
        while (choice < 1 || choice > 4) {
            clearScreen();

            String[] options = {
                    "Uniform Cost Search (UCS)",
                    "Greedy Best First Search",
                    "A* Search",
                    "Bonus Algorithm (Custom Implementation)"
            };

            printBoxedMenu("ALGORITHM SELECTION", options);

            try {
                System.out.print(BRIGHT_YELLOW + "Enter your choice (1-4): " + RESET);
                String input = reader.readLine();

                if (input.trim().isEmpty()) {
                    printErrorMessage("Input cannot be empty. Please enter a number between 1 and 4.");
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }

                try {
                    choice = Integer.parseInt(input);
                    if (choice < 1 || choice > 4) {
                        printErrorMessage("Invalid choice. Please enter a number between 1 and 4.");
                        TimeUnit.SECONDS.sleep(1);
                    }
                } catch (NumberFormatException e) {
                    printErrorMessage("Invalid input. Please enter a valid number.");
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (IOException | InterruptedException e) {
                printErrorMessage("An error occurred: " + e.getMessage());
            }
        }

        printSuccessMessage("Algorithm selected: " + Constants.getAlgorithmName(choice));
        return choice;
    }

    private static int promptHeuristic() {
        int choice = -1;
        while (choice < 1 || choice > 3) {
            clearScreen();

            String[] options = {
                    "Blocking Vehicles Heuristic (Counts vehicles blocking the exit path)",
                    "Manhattan Distance Heuristic (Distance to goal position)",
                    "Combined Heuristic (Weighted combination of both)"
            };

            printBoxedMenu("HEURISTIC SELECTION", options);

            try {
                System.out.print(BRIGHT_YELLOW + "Enter your choice (1-3): " + RESET);
                String input = reader.readLine();

                if (input.trim().isEmpty()) {
                    printErrorMessage("Input cannot be empty. Please enter a number between 1 and 3.");
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }

                try {
                    choice = Integer.parseInt(input);
                    if (choice < 1 || choice > 3) {
                        printErrorMessage("Invalid choice. Please enter a number between 1 and 3.");
                        TimeUnit.SECONDS.sleep(1);
                    }
                } catch (NumberFormatException e) {
                    printErrorMessage("Invalid input. Please enter a valid number.");
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (IOException | InterruptedException e) {
                printErrorMessage("An error occurred: " + e.getMessage());
            }
        }

        printSuccessMessage("Heuristic selected: " + Constants.getHeuristicName(choice));
        return choice;
    }

    private static void printBoxedPrompt(String title, String message) {
        clearScreen();
        int width = Math.max(title.length() + 4, message.length() + 4);

        System.out.println(BRIGHT_CYAN);
        // Top border
        System.out.print(" " + TOP_LEFT);
        for (int i = 0; i < width; i++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(TOP_RIGHT);

        // Title
        System.out.print(" " + VERTICAL + " " + BRIGHT_YELLOW + BOLD + title + RESET + BRIGHT_CYAN);
        System.out.print(" ".repeat(width - title.length() - 1));
        System.out.println(VERTICAL);

        // Separator
        System.out.print(" " + VERTICAL);
        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println(VERTICAL);

        // Message
        System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + message + RESET + BRIGHT_CYAN);
        System.out.print(" ".repeat(width - message.length() - 1));
        System.out.println(VERTICAL);

        // Bottom border
        System.out.print(" " + BOTTOM_LEFT);
        for (int i = 0; i < width; i++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(BOTTOM_RIGHT);
        System.out.println(RESET);
    }

    private static void printBoxedMenu(String title, String[] options) {
        int width = Math.max(title.length() + 4, 60);
        for (String option : options) {
            width = Math.max(width, option.length() + 8); // Account for option number and padding
        }

        System.out.println(BRIGHT_CYAN);
        // Top border
        System.out.print(" " + TOP_LEFT);
        for (int i = 0; i < width; i++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(TOP_RIGHT);

        // Title
        System.out.print(" " + VERTICAL + " " + BRIGHT_YELLOW + BOLD + title + RESET + BRIGHT_CYAN);
        System.out.print(" ".repeat(width - title.length() - 1));
        System.out.println(VERTICAL);

        // Separator
        System.out.print(" " + VERTICAL);
        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println(VERTICAL);

        // Options
        for (int i = 0; i < options.length; i++) {
            System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + (i + 1) + ". " + options[i] + RESET + BRIGHT_CYAN);
            System.out.print(" ".repeat(width - options[i].length() - 4)); // 4 = "X. ".length()
            System.out.println(VERTICAL);
        }

        // Bottom border
        System.out.print(" " + BOTTOM_LEFT);
        for (int i = 0; i < width; i++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(BOTTOM_RIGHT);
        System.out.println(RESET);
    }

    private static void printErrorMessage(String message) {
        System.out.println(BG_RED + WHITE + BOLD + " ERROR " + RESET + " " + RED + message + RESET);
    }

    private static void printSuccessMessage(String message) {
        System.out.println(BG_GREEN + BLACK + BOLD + " SUCCESS " + RESET + " " + BRIGHT_GREEN + message + RESET);
    }

    private static void printStartMessage(Board board, int algorithm, int heuristic) {
        clearScreen();
        int width = 60;

        System.out.println(BRIGHT_CYAN + BOLD);
        // Top border
        System.out.print(" " + TOP_LEFT);
        for (int i = 0; i < width; i++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(TOP_RIGHT);

        // Title
        String title = " CONFIGURATION SUMMARY ";
        int padding = (width - title.length()) / 2;
        System.out.print(" " + VERTICAL);
        System.out.print(" ".repeat(padding));
        System.out.print(BRIGHT_YELLOW + BOLD + title + RESET + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(width - padding - title.length()));
        System.out.println(VERTICAL);

        // Separator
        System.out.print(" " + VERTICAL);
        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println(VERTICAL);

        // Board information
        System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + "Board size: " + board.getRows() + "x"
                + board.getCols() + RESET + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(
                width - 13 - String.valueOf(board.getRows()).length() - String.valueOf(board.getCols()).length()));
        System.out.println(VERTICAL);

        System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + "Number of vehicles: " + board.getPieces().size()
                + RESET + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(width - 20 - String.valueOf(board.getPieces().size()).length()));
        System.out.println(VERTICAL);

        // Algorithm & Heuristic information
        System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + "Selected algorithm: "
                + Constants.getAlgorithmName(algorithm) + RESET + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(width - 20 - Constants.getAlgorithmName(algorithm).length()));
        System.out.println(VERTICAL);

        if (algorithm >= 2 && algorithm <= 4) {
            System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + "Selected heuristic: "
                    + Constants.getHeuristicName(heuristic) + RESET + BRIGHT_CYAN + BOLD);
            System.out.print(" ".repeat(width - 20 - Constants.getHeuristicName(heuristic).length()));
            System.out.println(VERTICAL);
        }

        // Bottom border
        System.out.print(" " + BOTTOM_LEFT);
        for (int i = 0; i < width; i++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(BOTTOM_RIGHT);
        System.out.println(RESET);

        // Board visualization
        System.out.println(BRIGHT_YELLOW + BOLD + "\nInitial Board State:" + RESET);
        System.out.println(board.toStringWithColor());
    }

    private static boolean promptSaveOption() {
        while (true) {
            System.out.println("\n" + BRIGHT_CYAN + "Do you want to save the solution to a file?" + RESET);
            System.out.println(BRIGHT_WHITE + "1. " + BRIGHT_GREEN + "Yes" + RESET);
            System.out.println(BRIGHT_WHITE + "2. " + BRIGHT_RED + "No" + RESET);

            try {
                System.out.print(BRIGHT_YELLOW + "Enter your choice (1-2): " + RESET);
                String input = reader.readLine();

                if (input.trim().isEmpty()) {
                    printErrorMessage("Input cannot be empty. Please enter 1 for Yes or 2 for No.");
                    continue;
                }

                if (input.equals("1")) {
                    return true;
                } else if (input.equals("2")) {
                    return false;
                } else {
                    printErrorMessage("Invalid choice. Please enter 1 for Yes or 2 for No.");
                }
            } catch (IOException e) {
                printErrorMessage("Error reading your input: " + e.getMessage());
            }
        }
    }

    private static String promptOutputPath() {
        String defaultPath = "test/output/solution.txt";
        while (true) {
            printBoxedPrompt("SAVE SOLUTION", "Enter the path where you want to save the solution file:");
            System.out.println(BRIGHT_CYAN + "Default path: " + BRIGHT_WHITE + defaultPath + RESET);
            System.out.println(BRIGHT_CYAN + "Press Enter to use default path or type a new path." + RESET);

            try {
                System.out.print(BRIGHT_YELLOW + "Path: " + RESET);
                String input = reader.readLine();

                if (input.trim().isEmpty()) {
                    return defaultPath;
                }

                // Check if directory exists and create if needed
                File file = new File(input);
                File parentDir = file.getParentFile();

                if (parentDir != null && !parentDir.exists()) {
                    System.out.println(
                            BRIGHT_YELLOW + "Directory doesn't exist. Do you want to create it? (Y/N)" + RESET);
                    String createDir = reader.readLine();

                    if (createDir.equalsIgnoreCase("Y")) {
                        if (parentDir.mkdirs()) {
                            printSuccessMessage("Directory created successfully!");
                            return input;
                        } else {
                            printErrorMessage("Failed to create directory. Using default path instead.");
                            return defaultPath;
                        }
                    } else {
                        printErrorMessage("Directory not created. Using default path instead.");
                        return defaultPath;
                    }
                }

                return input;
            } catch (IOException e) {
                printErrorMessage("Error reading your input: " + e.getMessage());
                return defaultPath;
            }
        }
    }

    public static void printSolutionToTerminal(
            Board initialBoard,
            List<State> solutionPath,
            String algorithm,
            int totalNodesVisited,
            long executionTime) {

        int width = 60;

        System.out.println(BRIGHT_CYAN + BOLD);
        // Top border
        System.out.print(" " + TOP_LEFT);
        for (int i = 0; i < width; i++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(TOP_RIGHT);

        // Title
        String title = " SOLUTION FOUND ";
        int padding = (width - title.length()) / 2;
        System.out.print(" " + VERTICAL);
        System.out.print(" ".repeat(padding));
        System.out.print(BRIGHT_GREEN + BOLD + title + RESET + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(width - padding - title.length()));
        System.out.println(VERTICAL);

        // Separator
        System.out.print(" " + VERTICAL);
        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println(VERTICAL);

        // Solution information
        System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + "Algorithm: " + algorithm + RESET + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(width - 12 - algorithm.length()));
        System.out.println(VERTICAL);

        System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + "Step count: " + (solutionPath.size() - 1) + RESET
                + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(width - 13 - String.valueOf(solutionPath.size() - 1).length()));
        System.out.println(VERTICAL);

        System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + "Nodes visited: " + totalNodesVisited + RESET
                + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(width - 15 - String.valueOf(totalNodesVisited).length()));
        System.out.println(VERTICAL);

        System.out.print(" " + VERTICAL + " " + BRIGHT_WHITE + "Execution time: " + executionTime + " ms" + RESET
                + BRIGHT_CYAN + BOLD);
        System.out.print(" ".repeat(width - 18 - String.valueOf(executionTime).length()));
        System.out.println(VERTICAL);

        // Bottom border
        System.out.print(" " + BOTTOM_LEFT);
        for (int i = 0; i < width; i++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(BOTTOM_RIGHT);
        System.out.println(RESET);

        System.out.println(BRIGHT_YELLOW + BOLD + "\nInitial Board" + RESET);
        System.out.println(initialBoard.toStringWithColor());

        // Solution moves
        if (solutionPath.size() > 1) {
            System.out.println(BRIGHT_CYAN + BOLD + "\nSolution Steps:" + RESET);

            Board previousBoard = initialBoard;

            for (int i = 1; i < solutionPath.size(); i++) {
                State state = solutionPath.get(i);
                Move move = state.getLastMove();
                Board currentBoard = state.getBoard();

                System.out.println(BRIGHT_GREEN + "Step " + i + ": " + BRIGHT_YELLOW + "Move " +
                        BRIGHT_WHITE + move.getPieceId() + BRIGHT_YELLOW + " " +
                        move.getDirectionString() + RESET);

                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Print board with movement highlighted
                System.out.println(currentBoard.toStringWithColor(move, previousBoard));

                previousBoard = currentBoard;
            }
        }
    }
}