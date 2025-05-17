package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private int rows;
    private int cols;
    private char[][] boardArray;
    private Position exitPosition;
    private Map<Character, Piece> pieces;
    private char primaryPieceId;

    public Board(int rows, int cols, char[][] boardArray) {
        this.rows = rows;
        this.cols = cols;
        this.boardArray = boardArray;
        this.pieces = new HashMap<>();
    }

    public Board(Board other) {
        this.rows = other.rows;
        this.cols = other.cols;
        this.boardArray = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(other.boardArray[i], 0, this.boardArray[i], 0, cols);
        }

        if (other.exitPosition != null) {
            this.exitPosition = new Position(other.exitPosition);
        }

        this.pieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : other.pieces.entrySet()) {
            this.pieces.put(entry.getKey(), new Piece(entry.getValue()));
        }

        this.primaryPieceId = other.primaryPieceId;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public char[][] getBoardArray() {
        return boardArray;
    }

    public Position getPrimaryPiecePosition() {
        Piece primaryPiece = pieces.get(primaryPieceId);
        if (primaryPiece != null) {
            return primaryPiece.getAnchor();
        }
        return null;
    }

    public Position getExitPosition() {
        return exitPosition;
    }

    public void setExitPosition(Position exitPosition) {
        this.exitPosition = exitPosition;
    }

    public char getPrimaryPieceId() {
        return primaryPieceId;
    }

    public Piece getPrimaryPiece() {
        return pieces.get(primaryPieceId);
    }

    public Map<Character, Piece> getPieces() {
        return pieces;
    }

    public void addPiece(char id, Piece piece) {
        pieces.put(id, piece);
    }

    public void setPrimaryPieceId(char primaryPieceId) {
        this.primaryPieceId = primaryPieceId;
    }

    public boolean isExitPosition(int row, int col) {
        return exitPosition != null && exitPosition.getRow() == row && exitPosition.getCol() == col;
    }

    public List<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();

        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            char id = entry.getKey();
            Piece piece = entry.getValue();

            int[] directions = piece.isHorizontal() ? new int[] { Move.LEFT, Move.RIGHT }
                    : new int[] { Move.UP, Move.DOWN };

            for (int direction : directions) {
                int maxDistance = piece.canMove(this, direction);
                for (int dist = 1; dist <= maxDistance; dist++) {
                    possibleMoves.add(new Move(id, direction, dist));
                }
            }
        }

        return possibleMoves;
    }

    public boolean movePiece(char id, int direction, int distance) {
        Piece piece = pieces.get(id);
        if (piece == null)
            return false;

        // Hapus posisi lama dari board
        for (Position pos : piece.getAllPositions()) {
            boardArray[pos.getRow()][pos.getCol()] = '.';
        }

        // Geser piece
        piece.move(direction, distance);

        // Tambahkan posisi baru ke board
        for (Position pos : piece.getAllPositions()) {
            boardArray[pos.getRow()][pos.getCol()] = id;
        }

        return true;
    }

    public int getPieceCountNoPrimary() {
        int count = 0;
        for (Piece piece : pieces.values()) {
            if (!piece.isPrimary()) {
                count++;
            }
        }
        return count;
    }

    public boolean validatePrimaryPieceAlignedWithExit() {
        if (exitPosition == null || primaryPieceId == 0 || !pieces.containsKey(primaryPieceId)) {
            return false;
        }

        Piece primary = getPrimaryPiece();
        Position anchor = primary.getAnchor();

        if (exitPosition.getRow() == -1 || exitPosition.getRow() == rows) {
            // Exit di atas atau bawah
            return !primary.isHorizontal() && exitPosition.getCol() >= anchor.getCol()
                    && exitPosition.getCol() < anchor.getCol() + 1;
        } else if (exitPosition.getCol() == -1 || exitPosition.getCol() == cols) {
            // Exit di kiri atau kanan
            return primary.isHorizontal() && exitPosition.getRow() >= anchor.getRow()
                    && exitPosition.getRow() < anchor.getRow() + 1;
        }

        return false;
    }

    public boolean isSolved() {
        Piece primary = getPrimaryPiece();
        if (primary == null)
            return false;

        for (Position pos : primary.getAllPositions()) {
            if (pos.getRow() >= 0 && pos.getRow() < rows &&
                    pos.getCol() >= 0 && pos.getCol() < cols) {
                return false; 
            }
        }
        
        return true; // Semua bagian P sudah keluar board
    }    
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(boardArray[i][j]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}