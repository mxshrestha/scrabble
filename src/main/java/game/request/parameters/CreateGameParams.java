package game.request.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.validators.PlayerParamsInOrder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class CreateGameParams {

    @PositiveOrZero
    private final int boardSize;

    @NotEmpty
    @PlayerParamsInOrder
    private final List<PlayerParams> players;

    @PositiveOrZero
    private final int tilesPerPlayer;

    @JsonCreator
    public CreateGameParams(@JsonProperty(value = "boardSize", defaultValue = "15") int boardSize,
                            @JsonProperty(value = "players", required = true) List<PlayerParams> players,
                            @JsonProperty(value = "tilesPerPlayer", defaultValue = "100") int tilesPerPlayer) {
        this.boardSize = boardSize;
        this.players = players;
        this.tilesPerPlayer = tilesPerPlayer;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public List<PlayerParams> getPlayers() {
        return players;
    }

    public int getTilesPerPlayer() {
        return tilesPerPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateGameParams that = (CreateGameParams) o;
        return boardSize == that.boardSize &&
                tilesPerPlayer == that.tilesPerPlayer &&
                Objects.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardSize, players, tilesPerPlayer);
    }

    @Override
    public String toString() {
        return "CreateGameParams{" +
                "boardSize=" + boardSize +
                ", players=" + players +
                ", tilesPerPlayer=" + tilesPerPlayer +
                '}';
    }
}
