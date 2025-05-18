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

    // Box drawing characters for borders
    public static final char TOP_LEFT = '╔';
    public static final char TOP_RIGHT = '╗';
    public static final char BOTTOM_LEFT = '╚';
    public static final char BOTTOM_RIGHT = '╝';
    public static final char HORIZONTAL = '═';
    public static final char VERTICAL = '║';

    // ANSI colors for console output
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String WHITE = "\u001B[37m";
    public static final String BLACK = "\u001B[30m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String BLINK = "\u001B[5m";
    public static final String INVERSE = "\u001B[7m";
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_PURPLE = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";

    // Algorithm Labels
    public static final String UCS = "UCS";
    public static final String GBFS = "Greedy Best First Search";
    public static final String ASTAR = "A*";
    public static final String BEAM = "Beam Search";

    // Heuristic Labels
    public static final String BLOCKING_HEURISTIC = "Blocking Heuristic";
    public static final String MANHATTAN_HEURISTIC = "Manhattan Distance";
    // public static final String CUSTOM_HEURISTIC = "Custom";

    // Helper functions
    public static String getAlgorithmName(int id) {
        return switch (id) {
            case 1 -> UCS;
            case 2 -> GBFS;
            case 3 -> ASTAR;
            case 4 -> BEAM;
            default -> "Unknown";
        };
    }

    public static String getHeuristicName(int id) {
        return switch (id) {
            case 1 -> BLOCKING_HEURISTIC;
            case 2 -> MANHATTAN_HEURISTIC;
            default -> "Unknown";
        };
    }
}
