package game.core;

import game.exceptions.IncorrectMoveDirectionException;

/**
 * @author Manish Shrestha
 */
public enum MoveDirection {
    LEFT_RIGHT(0),
    TOP_BOTTOM(1);

    private final int directionId;

    MoveDirection(int directionId) {
        this.directionId = directionId;
    }

    public final int getDirectionId() {
        return directionId;
    }

    public static MoveDirection fromId(int id) {
        switch (id) {
            case 0:
                return LEFT_RIGHT;
            case 1:
                return TOP_BOTTOM;
        }
        throw new IncorrectMoveDirectionException("incorrect move direction id specified. Id: " + id);
    }
}
