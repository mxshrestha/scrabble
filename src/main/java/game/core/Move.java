package game.core;

/**
 * @author Manish Shrestha
 */
public interface Move {
    String getWord();

    int getRow();

    int getColumn();

    MoveDirection getDirection();

    Player getPlayer();

    long getTime();

    default int getPoints() {
        return getWord().length();
    }
}
