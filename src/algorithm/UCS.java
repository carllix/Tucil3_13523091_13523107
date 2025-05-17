package algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import model.Board;
import model.State;
import util.Constants;

public class UCS implements Algorithm {

    @Override
    public SolutionPath findSolution(Board initialBoard) {
        long startTime = System.currentTimeMillis();
        int nodesVisited = 0;

        // Buat state awal
        State initialState = new State(initialBoard);

        // Algo nya disini len
        // .....
        // ....

        
        // Ini kalo ga nemu solusi
        long endTime = System.currentTimeMillis();
        return new SolutionPath(nodesVisited, endTime - startTime);
    }

    @Override
    public String getName() {
        return Constants.UCS;
    }
}