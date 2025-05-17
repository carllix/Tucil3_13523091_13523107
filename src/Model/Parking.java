package Model;

/**
 * Class representing a parking space
 */
public class Parking {
    public final int width;
    public final int height;
    private int[][] cars;

    // Constructor for creating a new empty parking space
    public Parking(int width, int height) {
        this.width = width;
        this.height = height;
        this.cars = new int[height][width];
    }

    // Check if a position is within parking bounds
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Check if a specific position is free
    public boolean isFree(int x, int y) {
        return isInBounds(x, y) && cars[y][x] == 0;
    }

    // Check if a range of positions is free for a car of given size and orientation
    public boolean isFree(int x, int y, int size, int orientation) {
        if (orientation == Car.HORIZONTAL) {
            for (int i = 0; i < size; i++) {
                if (!isFree(x + i, y)) {
                    return false;
                }
            }
        } else { // VERTICAL
            for (int i = 0; i < size; i++) {
                if (!isFree(x, y + i)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Place a car in the parking space
    public void putCar(Car car) {
        if (car.isHorizontal) {
            for (int i = 0; i < car.size; i++) {
                if (!isInBounds(car.x + i, car.y)) {
                    throw new Error("Car placed out of bounds at " + 
                        "(" + (car.x + i) + "," + car.y + ")");
                }
                if (cars[car.y][car.x + i] != 0) {
                    throw new Error("Car placed on a non-empty position at " + 
                        "(" + (car.x + i) + "," + car.y + ")");
                }
                cars[car.y][car.x + i] = car.id;
            }
        } else { // VERTICAL
            for (int i = 0; i < car.size; i++) {
                if (!isInBounds(car.x, car.y + i)) {
                    throw new Error("Car placed out of bounds at " + 
                        "(" + car.x + "," + (car.y + i) + ")");
                }
                if (cars[car.y + i][car.x] != 0) {
                    throw new Error("Car placed on a non-empty position at " + 
                        "(" + car.x + "," + (car.y + i) + ")");
                }
                cars[car.y + i][car.x] = car.id;
            }
        }
    }

    // Remove a car from the parking space
    public void removeCar(Car car) {
        if (car.isHorizontal) {
            for (int i = 0; i < car.size; i++) {
                if (!isInBounds(car.x + i, car.y)) {
                    throw new Error("Trying to remove a car outside the parking at " + 
                        "(" + (car.x + i) + "," + car.y + ")");
                }
                cars[car.y][car.x + i] = 0;
            }
        } else { // VERTICAL
            for (int i = 0; i < car.size; i++) {
                if (!isInBounds(car.x, car.y + i)) {
                    throw new Error("Trying to remove a car outside the parking at " + 
                        "(" + car.x + "," + (car.y + i) + ")");
                }
                cars[car.y + i][car.x] = 0;
            }
        }
    }

    // Show the current state of the parking
    public void showBoard() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(" " + cars[y][x]);
            }
            System.out.println();
        }
        System.out.println();
    }
}