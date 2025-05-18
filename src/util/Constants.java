package util;

public class Constants {
    // Direction Constants
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    public static final String[] DIRECTION_STRINGS = { "atas", "kanan", "bawah", "kiri" };

    // Character Constants
    public static final char PRIMARY_PIECE_CHAR = 'P';
    public static final char EXIT_CHAR = 'K';
    public static final char EMPTY_CELL_CHAR = '.';

    // ANSI colors for console output
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";

    // Algorithm Labels
    public static final String UCS = "UCS";
    public static final String GBFS = "Greedy Best First Search";
    public static final String ASTAR = "A*";

    // Heuristic Labels
    public static final String BLOCKING_HEURISTIC = "Blocking";
    public static final String MANHATTAN_HEURISTIC = "Manhattan";
    // public static final String CUSTOM_HEURISTIC = "Custom";

    // Helper functions
    public static String getAlgorithmName(int id) {
        return switch (id) {
            case 1 -> UCS;
            case 2 -> GBFS;
            case 3 -> ASTAR;
            case 4 -> "Bonus Algorithm";
            default -> "Unknown";
        };
    }

    public static String getHeuristicName(int id) {
        return switch (id) {
            case 1 -> BLOCKING_HEURISTIC;
            case 2 -> MANHATTAN_HEURISTIC;
            case 3 -> "Heuristic 3";
            default -> "Unknown";
        };
    }
}
