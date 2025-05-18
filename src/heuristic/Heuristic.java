package heuristic;

import models.Board;

public interface Heuristic {
    int calculate(Board board);

    String getName();
}