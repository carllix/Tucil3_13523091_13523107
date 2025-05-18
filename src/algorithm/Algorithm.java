package algorithm;
import models.Board;

public interface Algorithm {
    SolutionPath findSolution(Board initialBoard);

    String getName();
}
