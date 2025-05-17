package Model;
import java.util.ArrayList;
import java.util.HashMap;

public class Move {
    public Parking parking;
    private int maxPosition;
    
    public boolean redCarCreated = false;
    public int numCars = 1;
    public HashMap<Integer, Car> cars = new HashMap<Integer, Car>();
    
    // Exit coordinates
    public int exitRow = -1;
    public int exitCol = -1;
    
    public Move(int width, int height) {
        parking = new Parking(width, height);
        maxPosition = Math.max(width, height);
    }
    
    // Set the exit position
    public void setExit(int row, int col) {
        this.exitRow = row;
        this.exitCol = col;
    }
    
    // Ubah posisi menjadi angka unik (id)
    public int integerRepresentation() {
        int representation = 0;
        int power = 1;
        for (int carId = 1; carId < numCars + 1; carId++) {
            representation += power * cars.get(carId).getVariableCoordinate();
            power *= maxPosition;
        }
        return representation;
    }
    
    // Give the id the next car to create
    // We can only have one red car, it has id = 1. 
    private int nextCarId(boolean isRed) {
        int carId = 0;
        if (isRed) {
            if (redCarCreated)
                throw new Error("Red car has already been created, you cannot add a second one");
            carId = 1;
            redCarCreated = true;
        } else
            carId = ++numCars;
        return carId;
    }
    
    public boolean addCar(int x, int y, int size, int orientation, boolean isRed) {
        if (!parking.isFree(x, y, size, orientation)) {
            System.out.println("the chosen parking place ("
                + x + "," + y + "," + size + "," 
                + (orientation==Car.HORIZONTAL? "HORIZONTAL": "VERTICAL") +
                ") is not available or out of bounds");
            return false;
        }
        int carId = nextCarId(isRed);
        Car car = new Car(carId, x, y, size, orientation);
        parking.putCar(car);
        // add to the cars' hashmap
        cars.put(carId, car);
        return true;
    }
    
    public boolean addCar(int x, int y, int size, int orientation) {
        return addCar(x, y, size, orientation, false);
    }
    
    // return all the possible moves of a car
    public ArrayList<CarMove> getCarPossibleMoves(int carId) {
        var car = cars.get(carId);
        var carMoves = new ArrayList<CarMove>();
        if (car.isHorizontal) {
            int dx = -1;
            while (parking.isFree(car.x + dx, car.y))
                carMoves.add(new CarMove(car.id, dx--));
            dx = 1;
            while (parking.isFree(car.x + car.size - 1 + dx, car.y))
                carMoves.add(new CarMove(car.id, dx++));
        }
        else {
            int dy = -1;
            while (parking.isFree(car.x, car.y + dy))
                carMoves.add(new CarMove(car.id, dy--));
            dy = 1;
            while (parking.isFree(car.x, car.y + car.size - 1 + dy))
                carMoves.add(new CarMove(car.id, dy++));
        }
        return carMoves;
    }
    
    // return a list of all the possible moves of all the cars
    public ArrayList<CarMove> getPossibleMoves() {
        return getPossibleMoves(-1);
    }
    
    // return a list of all the possible moves of all the cars except 
    // the car with (id == ignoreCarId)
    public ArrayList<CarMove> getPossibleMoves(int ignoreCarId) {
        var allMoves = new ArrayList<CarMove>();
        for (var carId: cars.keySet()) {
            if (carId != ignoreCarId) {
                var carMoves = getCarPossibleMoves(carId);
                allMoves.addAll(carMoves);
            }
        }
        return allMoves;        
    }
    
    // "move" must be an available move, otherwise an error is thrown in parking.putCar(car).
    public void move(CarMove carMove) {
        var car = cars.get(carMove.carId);
        // remove car from parking
        parking.removeCar(car);
        // modify car coordinates
        if (car.isHorizontal)
            car.x += carMove.dxy;
        else
            car.y += carMove.dxy;
        // put it back in the parking
        parking.putCar(car);
    }
    
    // do the opposite move of carMove
    public void cancelMove(CarMove carMove) {
        move(new CarMove(carMove.carId, - carMove.dxy));
    }
    
    // returns the distance between the red car and the exit cell
    public int distToExit() {
        Car redCar = cars.get(1);
        
        // If no custom exit was set, use the default (right edge)
        if (exitRow == -1 && exitCol == -1) {
            return parking.width - (redCar.x + redCar.size);
        }
        
        // For exits in different directions
        if (redCar.isHorizontal) {
            // Horizontal car can exit to left or right
            if (redCar.y == exitRow) {
                if (exitCol >= parking.width || (exitCol > redCar.x && exitRow >= 0)) {
                    // Exit on right side
                    return exitCol - (redCar.x + redCar.size);
                } else if (exitCol < 0 || exitCol < redCar.x) {
                    // Exit on left side
                    return redCar.x - exitCol;
                }
            }
        } else {
            // Vertical car can exit to top or bottom
            if (redCar.x == exitCol && exitCol >= 0) {
                if (exitRow >= parking.height || exitRow > redCar.y) {
                    // Exit on bottom side
                    return exitRow - (redCar.y + redCar.size);
                } else if (exitRow < 0 || exitRow < redCar.y) {
                    // Exit on top side
                    return redCar.y - exitRow;
                }
            }
        }
        
        // If exit is not accessible from current position
        return Integer.MAX_VALUE;
    }
    
    // returns true if the game can be won in one move
    public boolean won() {
        Car redCar = cars.get(1);
        int dist = distToExit();
        
        // If distance is MAX_VALUE, exit is not accessible
        if (dist == Integer.MAX_VALUE) {
            return false;
        }
        
        // If no custom exit was set, use the default (right edge)
        if (exitRow == -1 && exitCol == -1) {
            return parking.isFree(redCar.x + redCar.size, redCar.y, dist, Car.HORIZONTAL);
        }
        
        // Check path to custom exit based on direction
        if (redCar.isHorizontal && redCar.y == exitRow) {
            if ((exitCol >= parking.width || exitCol > redCar.x) && exitRow >= 0) {
                // Exit on right side
                return parking.isFree(redCar.x + redCar.size, redCar.y, dist, Car.HORIZONTAL);
            } else if (exitCol < 0 || exitCol < redCar.x) {
                // Exit on left side
                int startX = Math.max(0, exitCol + 1);
                return parking.isFree(startX, redCar.y, redCar.x - startX, Car.HORIZONTAL);
            }
        } else if (!redCar.isHorizontal && redCar.x == exitCol) {
            if ((exitRow >= parking.height || exitRow > redCar.y) && exitCol >= 0) {
                // Exit on bottom side
                return parking.isFree(redCar.x, redCar.y + redCar.size, dist, Car.VERTICAL);
            } else if (exitRow < 0 || exitRow < redCar.y) {
                // Exit on top side
                int startY = Math.max(0, exitRow + 1);
                return parking.isFree(redCar.x, startY, redCar.y - startY, Car.VERTICAL);
            }
        }
        
        return false;
    }
    
    // testing
    public void showParking() {
        parking.showBoard();
    }
}