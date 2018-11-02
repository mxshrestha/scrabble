package game.core;

import game.exceptions.IncorrectStateException;

/**
 * @author Manish Shrestha
 */
public enum GameState {
    INITIALIZED(0),
    IN_PROGRESS(1),
    FINISHED(2);

    private final int stateId;

    GameState(int stateId) {
        this.stateId = stateId;
    }

    public final int getStateId() {
        return stateId;
    }

    public static GameState fromId(int id) {
        switch (id) {
            case 0:
                return INITIALIZED;
            case 1:
                return IN_PROGRESS;
            case 2:
                return FINISHED;
        }
        throw new IncorrectStateException("incorrect game state id specified. State: " + id);
    }
}
