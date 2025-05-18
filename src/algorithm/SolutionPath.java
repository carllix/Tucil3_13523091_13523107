package algorithm;

import java.util.List;

import models.State;

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
}