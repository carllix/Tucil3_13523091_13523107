package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Board;
import model.Piece;
import model.Position;

public class BoardParser {
    public static Board parseBoard(int rows, int cols, char[][] boardArray, Position exitPosition) {
        Board board = new Board(rows, cols, boardArray);
        board.setExitPosition(exitPosition);

        Map<Character, List<Position>> piecesPositions = findPiecesPositions(boardArray);
        createPieces(board, piecesPositions);

        return board;
    }

    private static Map<Character, List<Position>> findPiecesPositions(char[][] boardArray) {
        Map<Character, List<Position>> piecesPositions = new HashMap<>();

        int rows = boardArray.length;
        int cols = boardArray[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cellChar = boardArray[i][j];

                if (cellChar == Constants.EMPTY_CELL_CHAR || cellChar == Constants.EXIT_CHAR) {
                    continue;
                }

                piecesPositions.computeIfAbsent(cellChar, k -> new ArrayList<>()).add(new Position(i, j));
            }
        }

        return piecesPositions;
    }

    private static void createPieces(Board board, Map<Character, List<Position>> piecesPositions) {
        for (Map.Entry<Character, List<Position>> entry : piecesPositions.entrySet()) {
            char id = entry.getKey();
            List<Position> positions = entry.getValue();

            Position anchor = positions.get(0);
            boolean isHorizontal = positions.stream().allMatch(p -> p.getRow() == anchor.getRow());
            int size = positions.size();

            boolean isPrimary = id == Constants.PRIMARY_PIECE_CHAR;
            Piece piece = new Piece(id, anchor, size, isPrimary, isHorizontal);
            board.addPiece(id, piece);

            if (isPrimary) {
                board.setPrimaryPieceId(id);
            }
        }
    }
}