package game.request.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.validators.PlayerParamsInOrder;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class UpdateGameParams {

    @PositiveOrZero
    private final Integer boardSize;

    @PlayerParamsInOrder
    private final List<PlayerParams> players;

    @PositiveOrZero
    private final Integer tilesPerPlayer;

    private final Integer state;

    @JsonCreator
    public UpdateGameParams(@PositiveOrZero
                            @JsonProperty("boardSize") Integer boardSize,
                            @JsonProperty("players") List<PlayerParams> players,
                            @PositiveOrZero
                            @JsonProperty("tilesPerPlayer")Integer tilesPerPlayer,
                            @JsonProperty("state") Integer state) {
        this.boardSize = boardSize;
        this.players = players;
        this.tilesPerPlayer = tilesPerPlayer;
        this.state = state;
    }

    public Integer getBoardSize() {
        return boardSize;
    }

    public List<PlayerParams> getPlayers() {
        return players;
    }

    public Integer getTilesPerPlayer() {
        return tilesPerPlayer;
    }

    public Integer getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateGameParams that = (UpdateGameParams) o;
        return Objects.equals(boardSize, that.boardSize) &&
                Objects.equals(players, that.players) &&
                Objects.equals(tilesPerPlayer, that.tilesPerPlayer) &&
                Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardSize, players, tilesPerPlayer, state);
    }

    @Override
    public String toString() {
        return "UpdateGameParams{" +
                "boardSize=" + boardSize +
                ", players=" + players +
                ", tilesPerPlayer=" + tilesPerPlayer +
                ", state=" + state +
                '}';
    }
}
