package heuristic;

import model.*;
import util.Constants;

public class ManhattanDistance implements Heuristic {
    @Override
    public int calculate(Board board) {
        if (board.getExitPosition() == null || board.getPrimaryPieceId() == 0
                || !board.getPieces().containsKey(board.getPrimaryPieceId())) {
            return Integer.MAX_VALUE; // cek kondisi, heuristic gabisa dihitung
        }

        Piece primary = board.getPrimaryPiece();
        Position anchor = primary.getAnchor();

        int anchorRow = anchor.getRow();
        int anchorCol = anchor.getCol();

        Position exitPosition = board.getExitPosition();
        int exitRow = exitPosition.getRow();
        int exitCol = exitPosition.getCol();

        int rows = board.getRows();
        int cols = board.getCols();

        if (exitRow == -1) { // atas
            return Math.abs(anchorCol - exitCol) + (anchorRow + 1);
        } else if (exitRow == rows) { // bawah
            return Math.abs(anchorCol - exitCol) + (rows - anchorRow);
        } else if (exitCol == -1) { // kiri
            return Math.abs(anchorRow - exitRow) + (anchorCol + 1);
        } else if (exitCol == cols) { // kanan
            return Math.abs(anchorRow - exitRow) + (cols - anchorCol);
        } else { // jaga-jaga aja
            return Math.abs(anchorRow - exitRow) + Math.abs(anchorCol - exitCol);
        }
    }

    @Override
    public String getName() {
        return Constants.MANHATTAN_HEURISTIC;
    }
}
