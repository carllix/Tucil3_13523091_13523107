package Model;

/**
 * Class representing a car in the rush hour puzzle
 */
public class Car {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    public final int id;
    public int x, y;
    public final int size;
    public final boolean isHorizontal;

    public Car(int id, int x, int y, int size, int orientation) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
        this.isHorizontal = (orientation == HORIZONTAL);
    }
    
    // For the integer representation, we only keep track of the variable coordinate
    public int getVariableCoordinate() {
        return isHorizontal ? x : y;
    }
}