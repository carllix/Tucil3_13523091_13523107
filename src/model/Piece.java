package model;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private char id;
    private Position anchor;
    private int size;
    private boolean isPrimary;
    private boolean isHorizontal;

    public Piece(char id, Position anchor, int size, boolean isPrimary, boolean isHorizontal) {
        this.id = id;
        this.anchor = new Position(anchor);
        this.size = size;
        this.isPrimary = isPrimary;
        this.isHorizontal = isHorizontal;
    }

    public Piece(Piece other) {
        this.id = other.id;
        this.anchor = new Position(other.anchor);
        this.size = other.size;
        this.isPrimary = other.isPrimary;
        this.isHorizontal = other.isHorizontal;
    }

    public char getId() {
        return id;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public Position getAnchor() {
        return anchor;
    }

    public int getSize() {
        return size;
    }

    public void move(int direction, int distance) {
        switch (direction) {
            case Move.UP:
                anchor.setRow(anchor.getRow() - distance);
                break;
            case Move.RIGHT:
                anchor.setCol(anchor.getCol() + distance);
                break;
            case Move.DOWN:
                anchor.setRow(anchor.getRow() + distance);
                break;
            case Move.LEFT:
                anchor.setCol(anchor.getCol() - distance);
                break;
        }
    }

    public int canMove(Board board, int direction) {
        if (isHorizontal && (direction == Move.UP || direction == Move.DOWN)) {
            return 0;
        }
        if (!isHorizontal && (direction == Move.LEFT || direction == Move.RIGHT)) {
            return 0;
        }

        int maxDistance = 0;
        char[][] boardArray = board.getBoardArray();
        int rowCount = board.getRows();
        int colCount = board.getCols();

        int row = anchor.getRow();
        int col = anchor.getCol();

        Position exitPos = board.getExitPosition();

        switch (direction) {
            case Move.UP:
                row--;
                if (isPrimary && row < 0 && exitPos.getRow() == -1 && exitPos.getCol() == col) {
                    return 1;
                }

                while (row >= 0 && boardArray[row][col] == '.') {
                    maxDistance++;
                    row--;
                }
                break;

            case Move.RIGHT:
                col += size;
                if (isPrimary && col >= colCount && exitPos.getRow() == row && exitPos.getCol() == colCount) {
                    return 1;
                }

                while (col < colCount && boardArray[row][col] == '.') {
                    maxDistance++;
                    col++;
                }
                break;

            case Move.DOWN:
                row += size;
                if (isPrimary && row >= rowCount && exitPos.getRow() == rowCount && exitPos.getCol() == col) {
                    return 1;
                }

                while (row < rowCount && boardArray[row][col] == '.') {
                    maxDistance++;
                    row++;
                }
                break;

            case Move.LEFT:
                col--;
                if (isPrimary && col < 0 && exitPos.getRow() == row && exitPos.getCol() == -1) {
                    return 1;
                }

                while (col >= 0 && boardArray[row][col] == '.') {
                    maxDistance++;
                    col--;
                }
                break;
        }

        return maxDistance;
    }

    public List<Position> getAllPositions() {
        List<Position> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int row = anchor.getRow() + (isHorizontal ? 0 : i);
            int col = anchor.getCol() + (isHorizontal ? i : 0);
            result.add(new Position(row, col));
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Piece ").append(id);
        if (isPrimary)
            sb.append(" (Primary)");
        sb.append(": [");

        for (int i = 0; i < size; i++) {
            int row = anchor.getRow() + (isHorizontal ? 0 : i);
            int col = anchor.getCol() + (isHorizontal ? i : 0);
            sb.append("(").append(row).append(", ").append(col).append(")");
            if (i < size - 1)
                sb.append(", ");
        }

        sb.append("]");
        return sb.toString();
    }
}