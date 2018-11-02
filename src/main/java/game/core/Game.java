package game.core;

/**
 * @author Manish Shrestha
 */
public interface Game {
    int getId();

    int getNumberOfPlayers();

    Iterable<Player> getPlayers();

    Board getBoard();

    GameState getState();
}
