package game.data;

import game.core.MoveDirection;
import game.core.Player;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public final class PlayerMove {
    private final String word;
    private final int row;
    private final int column;
    private MoveDirection moveDirection;
    private Player player;

    public PlayerMove(String word, int row, int column, MoveDirection moveDirection, Player player) {
        this.word = word;
        this.row = row;
        this.column = column;
        this.moveDirection = moveDirection;
        this.player = player;
    }

    public String getWord() {
        return word;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public MoveDirection getMoveDirection() {
        return moveDirection;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerMove that = (PlayerMove) o;
        return row == that.row &&
                column == that.column &&
                Objects.equals(word, that.word) &&
                moveDirection == that.moveDirection &&
                Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, row, column, moveDirection, player);
    }

    @Override
    public String toString() {
        return "PlayerMove{" +
                "word='" + word + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", moveDirection=" + moveDirection +
                ", player=" + player +
                '}';
    }
}
