package game.data.dao;

import game.core.MoveDirection;
import game.core.Player;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class MoveOptions {
    private final int gameId;
    private final int row;
    private final int col;
    private final MoveDirection direction;
    private final String word;
    private final Player player;

    public MoveOptions(int gameId, int row, int col, MoveDirection direction, String word, Player player) {
        this.gameId = gameId;
        this.row = row;
        this.col = col;
        this.direction = direction;
        this.word = word;
        this.player = player;
    }

    public int getGameId() {
        return gameId;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    public String getWord() {
        return word;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveOptions that = (MoveOptions) o;
        return gameId == that.gameId &&
                row == that.row &&
                col == that.col &&
                direction == that.direction &&
                Objects.equals(word, that.word) &&
                Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, row, col, direction, word, player);
    }

    @Override
    public String toString() {
        return "MoveOptions{" +
                "gameId=" + gameId +
                "row=" + row +
                ", col=" + col +
                ", direction=" + direction +
                ", word='" + word + '\'' +
                ", player=" + player +
                '}';
    }
}
