package Input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import Model.*;

public class Input {
    public static Move loadFromFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String[] size = br.readLine().trim().split(" ");
        int height = Integer.parseInt(size[0]);
        int width = Integer.parseInt(size[1]);

        int carCount = Integer.parseInt(br.readLine().trim());

        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();

        int exitRow = -1, exitCol = -1;
        List<String> boardLines = new ArrayList<>();

        // Detect exit position 'K' before or after the board
        if (!lines.isEmpty() && lines.get(0).trim().contains("K")) {
            // Exit at the top
            String topLine = lines.get(0);
            exitRow = -1; // Special value indicating top exit
            exitCol = topLine.indexOf('K');
            lines.remove(0); // Remove exit line
            System.out.println("Top exit detected at column: " + exitCol);
        } else if (lines.size() > height && lines.get(height).trim().contains("K")) {
            // Exit at the bottom
            String bottomLine = lines.get(height);
            exitRow = height; // Value equal to height indicates bottom exit
            exitCol = bottomLine.indexOf('K');
            lines.remove(height); // Remove exit line
            System.out.println("Bottom exit detected at column: " + exitCol);
        }

        // Now lines only contains board rows (should be 'height' rows)
        for (int y = 0; y < Math.min(height, lines.size()); y++) {
            line = lines.get(y);
            // Check for exit on left or right
            if (line.length() > width && line.charAt(width) == 'K') {
                exitRow = y;
                exitCol = width; // Value equal to width indicates right exit
                System.out.println("Right exit detected at row: " + y);
            } else if (line.length() > 0 && line.charAt(0) == 'K') {
                exitRow = y;
                exitCol = -1; // Special value indicating left exit
                System.out.println("Left exit detected at row: " + y);
            }
            
            // Ensure we only take the relevant part of the line for the board
            if (line.length() > width) {
                boardLines.add(line.substring(0, width));
            } else {
                boardLines.add(line);
            }
        }

        // Create board
        char[][] board = new char[height][width];
        for (int y = 0; y < height; y++) {
            if (y < boardLines.size()) {
                line = boardLines.get(y);
                for (int x = 0; x < width; x++) {
                    board[y][x] = (x < line.length()) ? line.charAt(x) : '.';
                }
            } else {
                // Fill with empty spaces if we don't have enough lines
                for (int x = 0; x < width; x++) {
                    board[y][x] = '.';
                }
            }
        }

        Move puzzle = new Move(width, height);
        
        // Set exit position
        if (exitRow != -1 || exitCol != -1) {
            puzzle.setExit(exitRow, exitCol);
            System.out.println("Exit position set to: row=" + exitRow + ", col=" + exitCol);
        }

        Set<Character> processed = new HashSet<>();

        // Add main car 'P'
        outer:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (board[y][x] == 'P') {
                    int sizeP = 1;
                    boolean horizontal = true;
                    if (x + 1 < width && board[y][x + 1] == 'P') {
                        while (x + sizeP < width && board[y][x + sizeP] == 'P') sizeP++;
                        horizontal = true;
                    } else if (y + 1 < height && board[y + 1][x] == 'P') {
                        while (y + sizeP < height && board[y + sizeP][x] == 'P') sizeP++;
                        horizontal = false;
                    }
                    
                    boolean success = puzzle.addCar(x, y, sizeP, horizontal ? Car.HORIZONTAL : Car.VERTICAL, true);
                    if (success) {
                        System.out.println("Added red car P at (" + x + "," + y + ") with size " + sizeP + 
                            " orientation: " + (horizontal ? "HORIZONTAL" : "VERTICAL"));
                        processed.add('P');
                    } else {
                        System.out.println("Failed to add red car P!");
                    }
                    break outer;
                }
            }
        }

        // Add other cars
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = board[y][x];
                if (c == '.' || c == 'P' || c == 'K' || processed.contains(c)) continue;

                int sizee = 1;
                boolean horizontal = true;
                if (x + 1 < width && board[y][x + 1] == c) {
                    while (x + sizee < width && board[y][x + sizee] == c) sizee++;
                    horizontal = true;
                } else {
                    horizontal = false;
                    while (y + sizee < height && board[y + sizee][x] == c) sizee++;
                }

                boolean success = puzzle.addCar(x, y, sizee, horizontal ? Car.HORIZONTAL : Car.VERTICAL, false);
                if (success) {
                    System.out.println("Added car " + c + " at (" + x + "," + y + ") with size " + sizee + 
                        " orientation: " + (horizontal ? "HORIZONTAL" : "VERTICAL"));
                    processed.add(c);
                }
            }
        }
        
        return puzzle;
    }
}