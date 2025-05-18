package heuristic;

import model.Board;
import model.Piece;
import model.Position;
import util.Constants;

public class BlockingHeuristic implements Heuristic {

    @Override
    public int calculate(Board board) { // kalau ga valid
        if (board.getExitPosition() == null || board.getPrimaryPieceId() == 0
                || !board.getPieces().containsKey(board.getPrimaryPieceId())) {
            return Integer.MAX_VALUE;
        }

        Piece primary = board.getPrimaryPiece();
        Position anchor = primary.getAnchor();
        int row = anchor.getRow();
        int col = anchor.getCol();
        int length = primary.getSize();

        Position exit = board.getExitPosition();
        int exitRow = exit.getRow();
        int exitCol = exit.getCol();

        int blockingCount = 0;

        // Arah exit gate
        int dirRow = 0, dirCol = 0;

        if (exitRow < 0)
            dirRow = -1; // atas
        else if (exitRow >= board.getRows())
            dirRow = 1; // bawah
        else if (exitCol < 0)
            dirCol = -1; // kiri
        else if (exitCol >= board.getCols())
            dirCol = 1; // kanan
        else
            return Integer.MAX_VALUE; // ga valid

        // posisi awal untuk cek
        int startRow = row;
        int startCol = col;

        if (!primary.isHorizontal()) {
            if (dirRow > 0)
                startRow = row + length - 1;
        } else {
            if (dirCol > 0)
                startCol = col + length - 1;
        }

        int currentRow = startRow + dirRow;
        int currentCol = startCol + dirCol;

        while (currentRow >= 0 && currentRow < board.getRows() &&
                currentCol >= 0 && currentCol < board.getCols()) {

            int cellValue = board.getBoardArray()[currentRow][currentCol];
            if (cellValue != 0 && cellValue != board.getPrimaryPieceId()) { // ada mobil lain
                blockingCount++;
            }

            currentRow += dirRow;
            currentCol += dirCol;
        }

        return blockingCount;
    }

    @Override
    public String getName() {
        return Constants.BLOCKING_HEURISTIC;
    }
}