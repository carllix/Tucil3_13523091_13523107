package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.*;

import java.util.ArrayList;

public class FileHandler {
    public static Board readInputFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] dimensions = reader.readLine().trim().split("\\s+");
            int expectedRows = Integer.parseInt(dimensions[0]);
            int expectedCols = Integer.parseInt(dimensions[1]);

            int numPiecesNoPrimary = Integer.parseInt(reader.readLine().trim());

            List<String> allLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allLines.add(line);
                }
            }

            int maxHeight = allLines.size();
            int maxWidth = 0;
            for (String s : allLines) {
                maxWidth = Math.max(maxWidth, s.length());
            }

            char[][] rawBoard = new char[maxHeight][maxWidth];
            for (int i = 0; i < maxHeight; i++) {
                String currentLine = allLines.get(i);
                for (int j = 0; j < maxWidth; j++) {
                    rawBoard[i][j] = (j < currentLine.length()) ? currentLine.charAt(j) : ' ';
                }
            }

            Position exitPosition = new Position(-1, -1);
            boolean hasTopK = false, hasBottomK = false, hasLeftK = false, hasRightK = false;

            if (maxHeight > 0) {
                for (int j = 0; j < maxWidth; j++) {
                    if (rawBoard[0][j] == Constants.EXIT_CHAR) {
                        exitPosition = new Position(-1, j);
                        hasTopK = true;
                        break;
                    }
                }
            }

            if (!hasTopK && maxHeight > 0) {
                for (int j = 0; j < maxWidth; j++) {
                    if (rawBoard[maxHeight - 1][j] == Constants.EXIT_CHAR) {
                        exitPosition = new Position(expectedRows, j);
                        hasBottomK = true;
                        break;
                    }
                }
            }

            if (!hasTopK && !hasBottomK) {
                for (int i = 0; i < maxHeight; i++) {
                    if (maxWidth > 0 && rawBoard[i][0] == Constants.EXIT_CHAR) {
                        exitPosition = new Position(i, -1);
                        hasLeftK = true;
                        break;
                    }
                }
            }

            if (!hasTopK && !hasBottomK && !hasLeftK && maxWidth > 0) {
                for (int i = 0; i < maxHeight; i++) {
                    if (rawBoard[i][maxWidth - 1] == Constants.EXIT_CHAR) {
                        exitPosition = new Position(i, expectedCols);
                        hasRightK = true;
                        break;
                    }
                }
            }

            int startRow = hasTopK ? 1 : 0;
            int startCol = hasLeftK ? 1 : 0;

            int actualRows = maxHeight - (hasTopK ? 1 : 0) - (hasBottomK ? 1 : 0);
            int actualCols = maxWidth - (hasLeftK ? 1 : 0) - (hasRightK ? 1 : 0);

            if (actualRows != expectedRows) {
                throw new IOException(
                        "Error: Row count mismatch. Expected: " + expectedRows + ", Found: " + actualRows);
            }

            if (actualCols != expectedCols) {
                throw new IOException(
                        "Error: Column count mismatch. Expected: " + expectedCols + ", Found: " + actualCols);
            }

            char[][] boardArray = new char[expectedRows][expectedCols];
            for (int i = 0; i < expectedRows; i++) {
                for (int j = 0; j < expectedCols; j++) {
                    int sourceRow = i + startRow;
                    int sourceCol = j + startCol;
                    char cell = (sourceRow < maxHeight && sourceCol < maxWidth) ? rawBoard[sourceRow][sourceCol] : ' ';
                    boardArray[i][j] = (cell == Constants.EXIT_CHAR) ? ' ' : cell;
                }
            }

            if (hasLeftK)
                exitPosition = new Position(exitPosition.getRow() - startRow, -1);
            if (hasRightK)
                exitPosition = new Position(exitPosition.getRow() - startRow, expectedCols);

            if (exitPosition.getRow() == -1 && exitPosition.getCol() == -1) {
                throw new IOException("Error: No exit position (K) found on board.");
            }

            Board board = BoardParser.parseBoard(expectedRows, expectedCols, boardArray, exitPosition);

            if (board.getPieceCountNoPrimary() != numPiecesNoPrimary) {
                throw new IOException("Error: Non-primary piece count mismatch.");
            }

            if (!board.validatePrimaryPieceAlignedWithExit()) {
                throw new IOException("Error: Primary piece is not aligned with exit.");
            }

            return board;
        }
    }

    public static void writeSolutionToFile(
            String outputPath,
            Board initialBoard,
            List<State> solutionPath,
            String algorithm,
            int totalNodesVisited,
            long executionTime) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("Algorithm: " + algorithm);
            writer.newLine();
            writer.write("Step count: " + (solutionPath.size() - 1));
            writer.newLine();
            writer.write("Nodes visited: " + totalNodesVisited);
            writer.newLine();
            writer.write("Execution time: " + executionTime + " ms");
            writer.newLine();
            writer.newLine();

            writer.write("Initial Board");
            writer.newLine();
            writer.write(initialBoard.toString());
            writer.newLine();

            for (int i = 1; i < solutionPath.size(); i++) {
                State state = solutionPath.get(i);
                Move move = state.getLastMove();

                writer.write("Move " + i + ": " +
                        move.getPieceId() + "-" +
                        move.getDirectionString());
                writer.newLine();
                writer.write(state.getBoard().toString());
                writer.newLine();
            }
        }
    }
}