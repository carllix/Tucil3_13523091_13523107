package model;

import util.Constants;

public class Move {
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    
    private char pieceId;
    private int direction;
    private int distance;

    public Move(char pieceId, int direction, int distance) {
        this.pieceId = pieceId;
        this.direction = direction;
        this.distance = distance;
    }

    public char getPieceId() {
        return pieceId;
    }

    public int getDirection() {
        return direction;
    }
    
    public int getDistance() {
        return distance;
    }

    public String getDirectionString() {
        switch (direction) {
            case UP:
                return Constants.DIRECTION_STRINGS[0];
            case RIGHT:
                return Constants.DIRECTION_STRINGS[1];
            case DOWN:
                return Constants.DIRECTION_STRINGS[2];
            case LEFT:
                return Constants.DIRECTION_STRINGS[3];
            default:
                return "unknown";
        }
    }

    @Override
    public String toString() {
        return pieceId + "-" + getDirectionString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Move move = (Move) obj;
        return pieceId == move.pieceId && 
               direction == move.direction &&
               distance == move.distance;
    }

    @Override
    public int hashCode() {
        int result = Character.hashCode(pieceId);
        result = 31 * result + direction;
        result = 31 * result + distance;
        return result;
    }
}