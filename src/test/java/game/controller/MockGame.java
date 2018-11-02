package game.controller;

import game.core.*;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class MockGame implements Game {
    private final int id;
    private final List<Player> players;
    private final Board board;
    private final GameState state;

    public MockGame() {
        id = 1;
        players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Person person = new StandardPerson(i, "user_" + i, "first_" + i, "last_" + i);
            Player player = new StandardPlayer(person, i + 1);
            players.add(player);
        }
        List<Tile> tiles = new ArrayList<>();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Tile tile = new StandardTile(row, col, '.', 1, 1);
                tiles.add(tile);
            }
        }
        board = new StandardBoard(tiles);
        state = GameState.INITIALIZED;
    }

    public MockGame(MockGameOptions mockGameOptions) {
        id = mockGameOptions.id;
        players = mockGameOptions.players;
        board = mockGameOptions.board;
        state = mockGameOptions.state;

    }
    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getNumberOfPlayers() {
        return players.size();
    }

    @Override
    public List<Player> getPlayers() {
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

    public static class MockGameOptions {
        private int id;
        private List<Player> players;
        private Board board;
        private GameState state;

        public void setId(int id) {
            this.id = id;
        }

        public void setPlayers(List<Player> players) {
            this.players = players;
        }

        public void setBoard(Board board) {
            this.board = board;
        }

        public void setState(GameState state) {
            this.state = state;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MockGame mockGame = (MockGame) o;
        return id == mockGame.id &&
                Objects.equals(players, mockGame.players) &&
                Objects.equals(board, mockGame.board) &&
                state == mockGame.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, players, board, state);
    }
}
