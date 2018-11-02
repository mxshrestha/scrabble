package game.core;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class StandardMove implements Move {

    private final String word;
    private final int row;
    private final int column;
    private final MoveDirection moveDirection;
    private final Player player;
    private final long time;

    public StandardMove(String word, int row, int column, MoveDirection moveDirection, Player player, long time) {
        this.word = word;
        this.row = row;
        this.column = column;
        this.moveDirection = moveDirection;
        this.player = player;
        this.time = time;
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public MoveDirection getDirection() {
        return moveDirection;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardMove that = (StandardMove) o;
        return row == that.row &&
                column == that.column &&
                time == that.time &&
                Objects.equals(word, that.word) &&
                moveDirection == that.moveDirection &&
                Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, row, column, moveDirection, player, time);
    }

    @Override
    public String toString() {
        return "StandardMove{" +
                "word='" + word + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", moveDirection=" + moveDirection +
                ", player=" + player +
                ", time=" + time +
                '}';
    }
}
