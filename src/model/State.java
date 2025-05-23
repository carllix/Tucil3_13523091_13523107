package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class State implements Comparable<State> {
    private Board board; 
    private int cost; 
    private int heuristicValue; 
    private Move lastMove;
    private State parent; 
    private List<Move> moveHistory; 

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
            Board newBoard = new Board(this.board);

            boolean moveSuccess = newBoard.movePiece(
                    move.getPieceId(),
                    move.getDirection(),
                    move.getDistance());

            if (moveSuccess) {
                State childState = new State(
                        newBoard,
                        this,
                        move,
                        this.cost + move.getDistance(),
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
        List<State> tempPath = new ArrayList<>();

        while (current != null) {
            tempPath.add(current);
            current = current.getParent();
        }

        if (!tempPath.isEmpty()) {
            State lastAddedState = tempPath.get(tempPath.size() - 1);
            path.add(lastAddedState);

            char lastPieceId = 0;
            int lastDirection = -1;

            for (int i = tempPath.size() - 2; i >= 0; i--) {
                State state = tempPath.get(i);
                Move move = state.getLastMove();

                if (move != null) {
                    if (move.getPieceId() != lastPieceId || move.getDirection() != lastDirection) {
                        path.add(state);
                        lastPieceId = move.getPieceId();
                        lastDirection = move.getDirection();
                    } else {
                        path.set(path.size() - 1, state);
                    }
                } else {
                    path.add(state);
                }
            }
        }

        return path;
    }

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
