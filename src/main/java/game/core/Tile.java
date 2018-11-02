package game.core;

/**
 * @author Manish Shrestha
 */
public interface Tile {
    int getRow();

    int getColumn();

    default char getValue() {
        return '.';
    }

    default int getBoost() {
        return 1;
    }

    default int getCharBoost() {
        return 1;
    }
}
