package game.core;

import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class StandardGame implements Game {
    private final int id;
    private final int numberOfPlayers;
    private final Board board;
    private final List<Player> players;
    private final GameState state;

    public StandardGame(int id, List<Player> players, Board board, GameState state) {
        this.id = id;
        this.numberOfPlayers = players.size();
        this.players = players;
        this.board = board;
        this.state = state;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    @Override
    public Iterable<Player> getPlayers() {
        return players;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardGame that = (StandardGame) o;
        return id == that.id &&
                Objects.equals(board, that.board) &&
                state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, state);
    }

    @Override
    public String toString() {
        return "StandardGame{" +
                "id=" + id +
                ", numberOfPlayers=" + numberOfPlayers +
                ", board=" + board +
                ", players=" + players +
                ", state=" + state +
                '}';
    }
}
