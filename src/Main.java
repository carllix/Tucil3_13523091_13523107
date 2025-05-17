import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import Model.*;
import Input.*;
import Solver.*;

public class Main {

	public static void main(String[] args)  throws IOException {
		// Coba
		//------------------
		// var puzzle = easyPuzzle();
		// var puzzle = mediumPuzzle();
		// var puzzle = hardPuzzle();
		Move puzzle = Input.loadFromFile("./test/test3.txt");
		//var puzzle = hardestPuzzle();
		// puzzle.showParking();
		
		
		// Create the animator
		//--------------------
		// the second argument is the waiting time between animation frames,
		// make it bigger to slow down the backtracking animation 
		var animatedSolver = new AnimatedSolverUCS(puzzle, 3);
		createFrame(animatedSolver);
		// show the initial configuration for 1 second
		animatedSolver.wait(100);

		var winningMoves = SolverUCS.solve(puzzle);
	    // var winningMoves = animatedSolver.solve();
	    System.out.println("The optimal number of moves to solve the puzzle is " + winningMoves.size());
	    
	    // Animated solution
	    //------------------
	    // wait 2 second before showing the animated solution
	    animatedSolver.wait(2000);
	    animatedSolver.playMoves(winningMoves);
	}
	
	// create a frame to the put the animatedSolver in
	public static void createFrame(JPanel anim) {
		JFrame frame = new JFrame("Rectangles");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(anim);
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
	
	// Some puzzle configurations
	//---------------------------
	// easy: optimal solution in 6 moves
	public static Move easyPuzzle() {
		var puzzle = new Move(6,6);
		puzzle.addCar(0,3,2,Car.HORIZONTAL, true); //1
		puzzle.addCar(2,0,2,Car.HORIZONTAL); //2
		puzzle.addCar(3,2,2,Car.HORIZONTAL); //3
		puzzle.addCar(4,0,2,Car.VERTICAL);   //4
		puzzle.addCar(1,0,2,Car.VERTICAL);   //5
		puzzle.addCar(3,3,3,Car.VERTICAL);	 //6
		puzzle.addCar(2,2,2,Car.VERTICAL);	 //7
		return puzzle;
	}
	
	// medium: optimal solution in 16 moves
	public static Move mediumPuzzle() {
		var puzzle = new Move(6,6);
		puzzle.addCar(2,2,2,Car.HORIZONTAL, true); //1
		puzzle.addCar(2,1,2,Car.HORIZONTAL); //2
		puzzle.addCar(0,5,2,Car.HORIZONTAL); //3
		puzzle.addCar(3,4,2,Car.HORIZONTAL); //4
		puzzle.addCar(3,5,2,Car.HORIZONTAL); //5
		puzzle.addCar(1,0,2,Car.VERTICAL);	 //7
		puzzle.addCar(1,2,3,Car.VERTICAL);	 //8
		puzzle.addCar(2,4,2,Car.VERTICAL);	 //9
		puzzle.addCar(4,0,3,Car.VERTICAL);	 //10
		return puzzle;
	}
	
	// hard: optimal solution in 26 moves
	public static Move hardPuzzle() {
		var puzzle = new Move(6,6);
		puzzle.addCar(1,2,2,Car.HORIZONTAL, true); //1
		puzzle.addCar(3,0,3,Car.HORIZONTAL); //2
		puzzle.addCar(4,1,2,Car.HORIZONTAL); //3
		puzzle.addCar(4,3,2,Car.HORIZONTAL); //4
		puzzle.addCar(2,4,2,Car.HORIZONTAL); //5
		puzzle.addCar(1,5,3,Car.HORIZONTAL); //6
		puzzle.addCar(2,0,2,Car.VERTICAL);	 //7
		puzzle.addCar(3,1,3,Car.VERTICAL);	 //8
		puzzle.addCar(0,1,2,Car.VERTICAL);	 //9
		puzzle.addCar(0,4,2,Car.VERTICAL);	 //10
		puzzle.addCar(1,3,2,Car.VERTICAL);	 //11
		puzzle.addCar(5,4,2,Car.VERTICAL);	 //12
		return puzzle;
	}
	
	
	// hardest: optimal solution in 51 moves
	// this is hardest 6x6 configuration of Move (https://www.michaelfogleman.com/rush/)
	public static Move hardestPuzzle() {
		var puzzle = new Move(6,6);
		puzzle.addCar(3,2,2,Car.HORIZONTAL, true); //1
		puzzle.addCar(1,0,2,Car.HORIZONTAL); //2
		puzzle.addCar(0,3,3,Car.HORIZONTAL); //3
		puzzle.addCar(0,5,2,Car.HORIZONTAL); //4
		puzzle.addCar(3,5,2,Car.HORIZONTAL); //5
		puzzle.addCar(4,4,2,Car.HORIZONTAL); //6
		puzzle.addCar(0,0,3,Car.VERTICAL);	 //7
		puzzle.addCar(1,1,2,Car.VERTICAL);	 //8
		puzzle.addCar(2,1,2,Car.VERTICAL);	 //9
		puzzle.addCar(4,0,2,Car.VERTICAL);	 //10
		puzzle.addCar(3,3,2,Car.VERTICAL);	 //11
		puzzle.addCar(2,4,2,Car.VERTICAL);	 //12
		puzzle.addCar(5,1,3,Car.VERTICAL);	 //13
		return puzzle;
	}

}
