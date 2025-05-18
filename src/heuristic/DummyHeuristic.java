package heuristic;

import model.*;

public class DummyHeuristic implements Heuristic {
    @Override
    public int calculate(Board board) {
        // Implementasi Manhattan distance
        // ...

        // Contoh dummy return untuk sekarang
        return 0;
    }

    @Override
    public String getName() {
        return "Dummy Distance";
    }
}
