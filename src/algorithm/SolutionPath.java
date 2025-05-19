package algorithm;

import java.util.List;

import model.Move;
import model.State;

import java.util.ArrayList;

public class SolutionPath {
    private List<State> path; // Urutan state dari awal sampai solusi
    private int nodesVisited;
    private long executionTimeMs;
    private boolean solutionFound;

    public SolutionPath(List<State> path, int nodesVisited, long executionTimeMs) {
        this.path = path;
        this.nodesVisited = nodesVisited;
        this.executionTimeMs = executionTimeMs;
        this.solutionFound = true;
    }

    // Constructor untuk solusi tidak ditemukan
    public SolutionPath(int nodesVisited, long executionTimeMs) {
        this.path = new ArrayList<>();
        this.nodesVisited = nodesVisited;
        this.executionTimeMs = executionTimeMs;
        this.solutionFound = false;
    }

    public List<State> getPath() {
        return path;
    }

    public int getStepCount() {
        // Kurangi 1 karena path termasuk state awal
        return path.size() > 0 ? path.size() - 1 : 0;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }

    public int getGroupedStepCount() {
        if (path.size() <= 1)
            return 0;

        int count = 1; // Setidaknya satu grup
        Move prevMove = path.get(1).getLastMove();

        for (int i = 2; i < path.size(); i++) {
            Move currentMove = path.get(i).getLastMove();
            if (!currentMove.equals(prevMove)) {
                count++;
                prevMove = currentMove;
            }
        }

        return count; // Tidak perlu tambah count++ lagi karena sudah dimulai dari 1
    }

    public List<String> getGroupedMoveDescriptions() {
        List<String> result = new ArrayList<>();
        if (path.size() <= 1)
            return result;

        Move prevMove = path.get(1).getLastMove();
        int repeat = 1;

        for (int i = 2; i < path.size(); i++) {
            Move currentMove = path.get(i).getLastMove();
            if (currentMove.equals(prevMove)) {
                repeat++;
            } else {
                result.add(formatMove(prevMove, repeat));
                prevMove = currentMove;
                repeat = 1;
            }
        }

        result.add(formatMove(prevMove, repeat)); // Tambahkan langkah terakhir
        return result;
    }

    private String formatMove(Move move, int count) {
        return move.toString() + (count > 1 ? " x" + count : "");
    }

}