package util;

public class Constants {
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    public static final String[] DIRECTION_STRINGS = { "atas", "kanan", "bawah", "kiri" };

    public static final char PRIMARY_PIECE_CHAR = 'P';
    public static final char EXIT_CHAR = 'K';
    public static final char EMPTY_CELL_CHAR = '.';

    // ANSI colors for console output
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m"; // Primary piece
    public static final String ANSI_GREEN = "\u001B[32m"; // Exit
    public static final String ANSI_BLUE = "\u001B[34m"; // Moved piece

    public static final String UCS = "UCS";
    public static final String GBFS = "Greedy Best First Search";
    public static final String ASTAR = "A*";

    public static final String BLOCKING_HEURISTIC = "Blocking";
    public static final String MANHATTAN_HEURISTIC = "Manhattan";
    // public static final String CUSTOM_HEURISTIC = "Custom";
}
