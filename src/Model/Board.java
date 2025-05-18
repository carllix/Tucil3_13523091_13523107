package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Constants;

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

    public int getPieceCount() {
        return pieces.size();
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
            int row = pos.getRow();
            int col = pos.getCol();
            if (row >= 0 && row < boardArray.length && col >= 0 && col < boardArray[0].length) {
                boardArray[row][col] = '.'; // hanya tandain yang ada di dalam board 
            }
        }

        piece.move(direction, distance);

        for (Position pos : piece.getAllPositions()) {
            int row = pos.getRow();
            int col = pos.getCol();

            if (row >= 0 && row < boardArray.length && col >= 0 && col < boardArray[0].length) {
                boardArray[row][col] = id;
            }
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
        if (exitPosition == null || primaryPieceId == 0 || !pieces.containsKey(primaryPieceId)) {
            return false;
        }

        Piece primary = getPrimaryPiece();
        if (primary == null) {
            return false;
        }

        Position anchor = primary.getAnchor();
        int size = primary.getSize();

        if (primary.isHorizontal()) {
            int endCol = anchor.getCol() + size - 1;

            // Kanan
            if (exitPosition.getCol() == cols) {
                return anchor.getCol() >= cols; 
            }

            // Kiri
            if (exitPosition.getCol() == -1) {
                return endCol < 0; 
            }
        } else {
            int endRow = anchor.getRow() + size - 1;

            // Bawah
            if (exitPosition.getRow() == rows) {
                return anchor.getRow() >= rows; 
            }

            // Atas
            if (exitPosition.getRow() == -1) {
                return endRow < 0; 
            }
        }

        return false;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        boolean hasLeftExit = (exitPosition != null && exitPosition.getCol() == -1);
        boolean hasRightExit = (exitPosition != null && exitPosition.getCol() == cols);
        boolean hasTopExit = (exitPosition != null && exitPosition.getRow() == -1);
        boolean hasBottomExit = (exitPosition != null && exitPosition.getRow() == rows);

        if (hasTopExit) {
            if (hasLeftExit)
                sb.append(" ");
            for (int j = 0; j < cols; j++) {
                if (exitPosition.getCol() == j) {
                    sb.append(Constants.EXIT_CHAR);
                } else {
                    sb.append(' ');
                }
            }
            if (hasRightExit)
                sb.append(" ");
            sb.append("\n");
        }

        for (int i = 0; i < rows; i++) {
            if (hasLeftExit) {
                if (exitPosition != null && exitPosition.getRow() == i && exitPosition.getCol() == -1) {
                    sb.append(Constants.EXIT_CHAR);
                } else {
                    sb.append(' ');
                }
            }

            for (int j = 0; j < cols; j++) {
                sb.append(boardArray[i][j]);
            }

            if (hasRightExit) {
                if (exitPosition != null && exitPosition.getRow() == i && exitPosition.getCol() == cols) {
                    sb.append(Constants.EXIT_CHAR);
                } else {
                    sb.append(' ');
                }
            }

            sb.append("\n");
        }

        if (hasBottomExit) {
            if (hasLeftExit)
                sb.append(" ");
            for (int j = 0; j < cols; j++) {
                if (exitPosition.getCol() == j) {
                    sb.append(Constants.EXIT_CHAR);
                } else {
                    sb.append(' ');
                }
            }
            if (hasRightExit)
                sb.append(" ");
            sb.append("\n");
        }

        return sb.toString();
    }

    public String toStringWithColor() {
        StringBuilder sb = new StringBuilder();

        boolean hasLeftExit = (exitPosition != null && exitPosition.getCol() == -1);
        boolean hasRightExit = (exitPosition != null && exitPosition.getCol() == cols);
        boolean hasTopExit = (exitPosition != null && exitPosition.getRow() == -1);
        boolean hasBottomExit = (exitPosition != null && exitPosition.getRow() == rows);

        if (hasTopExit) {
            if (hasLeftExit)
                sb.append(" ");
            for (int j = 0; j < cols; j++) {
                if (exitPosition.getCol() == j) {
                    sb.append(Constants.GREEN).append(Constants.EXIT_CHAR).append(Constants.RESET);
                } else {
                    sb.append(" ");
                }
            }
            if (hasRightExit)
                sb.append(" ");
            sb.append("\n");
        }

        for (int i = 0; i < rows; i++) {
            if (hasLeftExit) {
                if (exitPosition != null && exitPosition.getRow() == i && exitPosition.getCol() == -1) {
                    sb.append(Constants.GREEN).append(Constants.EXIT_CHAR).append(Constants.RESET);
                } else {
                    sb.append(" ");
                }
            }

            for (int j = 0; j < cols; j++) {
                char c = boardArray[i][j];
                if (c == Constants.PRIMARY_PIECE_CHAR) {
                    sb.append(Constants.RED).append(c).append(Constants.RESET);
                } else {
                    sb.append(c);
                }
            }

            if (hasRightExit) {
                if (exitPosition != null && exitPosition.getRow() == i && exitPosition.getCol() == cols) {
                    sb.append(Constants.GREEN).append(Constants.EXIT_CHAR).append(Constants.RESET);
                } else {
                    sb.append(" ");
                }
            }

            sb.append("\n");
        }

        if (hasBottomExit) {
            if (hasLeftExit)
                sb.append(" ");
            for (int j = 0; j < cols; j++) {
                if (exitPosition.getCol() == j) {
                    sb.append(Constants.GREEN).append(Constants.EXIT_CHAR).append(Constants.RESET);
                } else {
                    sb.append(" ");
                }
            }
            if (hasRightExit)
                sb.append(" ");
            sb.append("\n");
        }

        return sb.toString();
    }

    public String toStringWithColor(Move lastMove, Board beforeMoveBoard) {
        StringBuilder sb = new StringBuilder();

        boolean hasLeftExit = (exitPosition != null && exitPosition.getCol() == -1);
        boolean hasRightExit = (exitPosition != null && exitPosition.getCol() == cols);
        boolean hasTopExit = (exitPosition != null && exitPosition.getRow() == -1);
        boolean hasBottomExit = (exitPosition != null && exitPosition.getRow() == rows);

        List<Position> movePath = new ArrayList<>();
        if (lastMove != null) {
            Piece before = beforeMoveBoard.getPieces().get(lastMove.getPieceId());
            for (Position pos : before.getAllPositions()) {
                for (int d = 1; d <= lastMove.getDistance(); d++) {
                    int row = pos.getRow();
                    int col = pos.getCol();
                    switch (lastMove.getDirection()) {
                        case Move.LEFT -> movePath.add(new Position(row, col - d));
                        case Move.RIGHT -> movePath.add(new Position(row, col + d));
                        case Move.UP -> movePath.add(new Position(row - d, col));
                        case Move.DOWN -> movePath.add(new Position(row + d, col));
                    }
                }
            }
        }

        if (hasTopExit) {
            if (hasLeftExit)
                sb.append(" ");
            for (int j = 0; j < cols; j++) {
                if (exitPosition.getCol() == j) {
                    sb.append(Constants.GREEN).append(Constants.EXIT_CHAR).append(Constants.RESET);
                } else {
                    sb.append(" ");
                }
            }
            if (hasRightExit)
                sb.append(" ");
            sb.append("\n");
        }

        for (int i = 0; i < rows; i++) {
            if (hasLeftExit) {
                if (exitPosition.getRow() == i) {
                    sb.append(Constants.GREEN).append(Constants.EXIT_CHAR).append(Constants.RESET);
                } else {
                    sb.append(" ");
                }
            }

            for (int j = 0; j < cols; j++) {
                char c = boardArray[i][j];
                Position current = new Position(i, j);
                boolean isMoved = movePath.contains(current);
                boolean isPrimary = (c == Constants.PRIMARY_PIECE_CHAR);
                boolean isMovingPiece = (lastMove != null && c == lastMove.getPieceId());

                if (isMoved) {
                    if (isPrimary) {
                        sb.append(Constants.YELLOW).append(Constants.RED).append(c).append(Constants.RESET);
                    } else if (isMovingPiece) {
                        sb.append(Constants.YELLOW).append(Constants.BLUE).append(c).append(Constants.RESET);
                    } else {
                        sb.append(Constants.YELLOW).append(c).append(Constants.RESET);
                    }
                } else if (isMovingPiece) {
                    if (isPrimary) {
                        sb.append(Constants.RED).append(c).append(Constants.RESET);
                    } else {
                        sb.append(Constants.BLUE).append(c).append(Constants.RESET);
                    }
                } else if (isPrimary) {
                    sb.append(Constants.RED).append(c).append(Constants.RESET);
                } else {
                    sb.append(c);
                }
            }

            if (hasRightExit) {
                if (exitPosition.getRow() == i) {
                    sb.append(Constants.GREEN).append(Constants.EXIT_CHAR).append(Constants.RESET);
                } else {
                    sb.append(" ");
                }
            }

            sb.append("\n");
        }

        if (hasBottomExit) {
            if (hasLeftExit)
                sb.append(" ");
            for (int j = 0; j < cols; j++) {
                if (exitPosition.getCol() == j) {
                    sb.append(Constants.GREEN).append(Constants.EXIT_CHAR).append(Constants.RESET);
                } else {
                    sb.append(" ");
                }
            }
            if (hasRightExit)
                sb.append(" ");
            sb.append("\n");
        }

        return sb.toString();
    }    
}