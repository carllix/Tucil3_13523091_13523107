package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class State implements Comparable<State> {
    private Board board; // Konfigurasi papan pada state ini
    private int cost; // Jumlah langkah yang diperlukan untuk mencapai state ini (g(n))
    private int heuristicValue; // Nilai heuristik dari state ini (h(n))
    private Move lastMove; // Gerakan yang menyebabkan state ini
    private State parent; // State sebelumnya
    private List<Move> moveHistory; // Riwayat gerakan untuk mencapai state ini

    public State(Board board) {
        this.board = board;
        this.cost = 0;
        this.heuristicValue = 0;
        this.lastMove = null;
        this.parent = null;
        this.moveHistory = new ArrayList<>();
    }

    public State(Board board, State parent, Move lastMove, int cost, int heuristicValue) {
        this.board = board;
        this.parent = parent;
        this.lastMove = lastMove;
        this.cost = cost;
        this.heuristicValue = heuristicValue;

        // Salin riwayat gerakan dari parent dan tambahkan gerakan terakhir
        this.moveHistory = new ArrayList<>();
        if (parent != null) {
            this.moveHistory.addAll(parent.getMoveHistory());
        }

        if (lastMove != null) {
            this.moveHistory.add(lastMove);
        }
    }

    public int getTotalValue() {
        return cost + heuristicValue;
    }

    @Override
    public int compareTo(State other) {
        return Integer.compare(this.getTotalValue(), other.getTotalValue());
    }

    public List<State> generateChildStates(int heuristicValue) {
        List<State> childStates = new ArrayList<>();
        List<Move> possibleMoves = this.board.getPossibleMoves();

        for (Move move : possibleMoves) {
            // Buat salinan board
            Board newBoard = new Board(this.board);

            // Terapkan gerakan
            boolean moveSuccess = newBoard.movePiece(
                    move.getPieceId(),
                    move.getDirection(),
                    move.getDistance());

            if (moveSuccess) {
                // Buat state baru
                State childState = new State(
                        newBoard,
                        this,
                        move,
                        this.cost + move.getDistance(), // Tambahkan cost sesuai dengan jarak gerakan
                        heuristicValue);

                childStates.add(childState);
            }
        }

        return childStates;
    }

    public List<State> generateChildStates() {
        return generateChildStates(0);
    }

    public List<State> getSolutionPath() {
        List<State> path = new ArrayList<>();
        State current = this;

        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }

        return path;
    }

    public int getPathLength() {
        return moveHistory.size();
    }

    // Getters and setters

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(int heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }

    public State getParent() {
        return parent;
    }

    public void setParent(State parent) {
        this.parent = parent;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        State state = (State) o;
        return Objects.equals(board, state.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State {\n");
        sb.append("  cost: ").append(cost).append(",\n");
        sb.append("  heuristic: ").append(heuristicValue).append(",\n");
        sb.append("  total: ").append(getTotalValue()).append(",\n");
        if (lastMove != null) {
            sb.append("  lastMove: ").append(lastMove).append(",\n");
        }
        sb.append("  board: \n").append(board.toString()).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
