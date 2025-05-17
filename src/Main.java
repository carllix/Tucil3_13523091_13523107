import java.io.IOException;

import util.FileHandler;
import model.Board;

public class Main {
    public static void main(String[] args) {
        try {
            String filepath = "test/test4.txt";
            Board board = FileHandler.readInputFile(filepath);

            System.out.println("Board successfully read:");
            System.out.print(board);

            System.out.println("Exit position: " + board.getExitPosition());
            System.out.println("Primary piece: " + board.getPrimaryPiece());

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
