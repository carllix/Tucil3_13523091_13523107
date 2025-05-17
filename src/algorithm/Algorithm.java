package algorithm;
import model.Board;

public interface Algorithm {
    SolutionPath findSolution(Board initialBoard);

    String getName();
}
