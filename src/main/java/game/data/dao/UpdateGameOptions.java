package game.data.dao;

import game.core.GameState;
import game.core.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manish Shrestha
 */
public class UpdateGameOptions {

    private int gameId;
    private int boardSize;
    private List<Player> players;
    private GameState state;

    private final Map<Property, Boolean> propertyMap;

    public UpdateGameOptions(int gameId) {
        this.gameId = gameId;
        propertyMap = new HashMap<>();
        propertyMap.put(Property.BOARD_SIZE, false);
        propertyMap.put(Property.PLAYERS, false);
        propertyMap.put(Property.STATE, false);
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
        propertyMap.put(Property.BOARD_SIZE, true);
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        propertyMap.put(Property.PLAYERS, true);
    }

    public void setState(GameState state) {
        propertyMap.put(Property.STATE, true);
        this.state = state;
    }

    public int getGameId() {
        return gameId;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public final boolean isUpdatePlayers() {
        return propertyMap.get(Property.PLAYERS);
    }

    public final boolean isUpdateState() {
        return propertyMap.get(Property.STATE);
    }

    public final boolean isUpdateSize() {
        return propertyMap.get(Property.BOARD_SIZE);
    }

    enum Property {
        PLAYERS, STATE, BOARD_SIZE
    }
}
