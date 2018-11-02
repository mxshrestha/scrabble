package game.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import game.core.Game;
import game.core.GameState;
import game.core.Player;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
@JsonRootName("game")
public final class GameResource {
    private final int id;
    private final BoardResource board;
    private final List<PlayerResource> players;
    private final int state;
    private final PlayerResource nextTurnPlayer;

    @JsonCreator
    public GameResource(@JsonProperty("id") int id,
                        @JsonProperty("board") BoardResource board,
                        @JsonProperty("players") List<PlayerResource> players,
                        @JsonProperty("state") int state,
                        @JsonProperty("nextTurnPlayer") PlayerResource nextTurnPlayer) {
        this.id = id;
        this.board = board;
        this.players = players;
        this.state = state;
        this.nextTurnPlayer = nextTurnPlayer;
    }

    public final int getId() {
        return id;
    }

    public final BoardResource getBoard() {
        return board;
    }

    public final List<PlayerResource> getPlayers() {
        return players;
    }

    public final int getState() {
        return state;
    }

    public final PlayerResource getNextTurnPlayer() {
        return nextTurnPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameResource that = (GameResource) o;
        return id == that.id &&
                state == that.state &&
                Objects.equals(board, that.board) &&
                Objects.equals(players, that.players) &&
                Objects.equals(nextTurnPlayer, that.nextTurnPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, players, state, nextTurnPlayer);
    }

    @Override
    public String toString() {
        return "GameResource{" +
                "id=" + id +
                ", board=" + board +
                ", players=" + players +
                ", state=" + state +
                ", nextTurnPlayer=" + nextTurnPlayer +
                '}';
    }

    @JsonIgnore
    public static GameResource fromGame(@NonNull Game game, Player nextTurnPlayer) {
        final BoardResource boardResource = BoardResource.fromBoard(game.getBoard());
        final List<PlayerResource> players = new ArrayList<>();
        game.getPlayers().forEach(player -> players.add(PlayerResource.fromPlayer(player)));
        final PlayerResource nextTurnPlayerResource = PlayerResource.fromPlayer(nextTurnPlayer);
        final GameState state = game.getState();

        return new GameResource(game.getId(), boardResource, players, state.getStateId(), nextTurnPlayerResource);
    }
}
