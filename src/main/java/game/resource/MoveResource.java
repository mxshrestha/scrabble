package game.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import game.core.Move;
import game.core.MoveDirection;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
@JsonRootName("move")
public final class MoveResource {

    private final String word;
    private final int row;
    private final int column;
    private final PlayerResource player;
    private final int direction;
    private final int points;
    private final long time;

    @JsonCreator
    public MoveResource(@JsonProperty("word") String word,
                        @JsonProperty("row") int row,
                        @JsonProperty("column") int column,
                        @JsonProperty("player") PlayerResource player,
                        @JsonProperty("direction") int direction,
                        @JsonProperty("points") int points,
                        @JsonProperty("time") long time) {
        this.word = word;
        this.row = row;
        this.column = column;
        this.player = player;
        this.direction = direction;
        this.points = points;
        this.time = time;
    }

    public final String getWord() {
        return word;
    }

    public final int getRow() {
        return row;
    }

    public final int getColumn() {
        return column;
    }

    public final PlayerResource getPlayer() {
        return player;
    }

    public final int getDirection() {
        return direction;
    }

    public final int getPoints() {
        return points;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveResource that = (MoveResource) o;
        return row == that.row &&
                column == that.column &&
                direction == that.direction &&
                points == that.points &&
                time == that.time &&
                Objects.equals(word, that.word) &&
                Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, row, column, player, direction, points, time);
    }

    @Override
    public String toString() {
        return "MoveResource{" +
                "word='" + word + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", player=" + player +
                ", direction=" + direction +
                ", points=" + points +
                '}';
    }

    public static MoveResource fromMove(Move move) {
        final PlayerResource player = PlayerResource.fromPlayer(move.getPlayer());
        final MoveDirection moveDirection = move.getDirection();
        return new MoveResource(move.getWord(), move.getRow(), move.getColumn(), player, moveDirection.getDirectionId(), move.getPoints(), move.getTime());
    }
}
