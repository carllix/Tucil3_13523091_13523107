package algorithm;
import heuristic.Heuristic;
import model.Board;

public interface Algorithm {
    SolutionPath findSolution(Board initialBoard);

    String getName();
    default void setHeuristic(Heuristic h){}
}
