package game.viewer;

import game.core.GameState;
import game.core.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manish Shrestha
 */
public class GameView {
    private final List<Person> players;
    private final List<GameState> states;
    private final List<Integer> boardSizes;

    public List<Person> getPlayers() {
        return players;
    }

    public List<GameState> getStates() {
        return states;
    }

    public List<Integer> getBoardSizes() {
        return boardSizes;
    }

    private GameView(Builder builder) {
        players = builder.players;
        states = builder.states;
        boardSizes = builder.boardSizes;
    }

    public static class Builder {
        private List<Person> players = new ArrayList<>();
        private List<GameState> states = new ArrayList<>();
        private List<Integer> boardSizes = new ArrayList<>();

        private Builder() {}

        public Builder setPlayers(List<Person> players) {
            this.players = players;
            return this;
        }

        public Builder setStates(List<GameState> states) {
            this.states = states;
            return this;
        }

        public Builder setBoardSizes(List<Integer> boardSizes) {
            this.boardSizes = boardSizes;
            return this;
        }

        public GameView build() {
            return new GameView(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
