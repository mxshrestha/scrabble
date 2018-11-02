package game.request.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class PlayerMoveParams {

    @NonNull
    @NotEmpty
    private final String word;

    @PositiveOrZero
    private final int row;

    @PositiveOrZero
    private final int column;

    private final int direction;

    @NonNull
    private final PlayerParams player;


    public PlayerMoveParams(@JsonProperty(value = "word", required = true) String word,
                            @JsonProperty(value = "row", required = true) int row,
                            @JsonProperty(value = "column", required = true) int column,
                            @JsonProperty(value = "direction", required = true) int direction,
                            @JsonProperty(value = "player", required = true) PlayerParams player) {
        this.word = word;
        this.row = row;
        this.column = column;
        this.direction = direction;
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

    public int getDirection() {
        return direction;
    }

    public PlayerParams getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerMoveParams that = (PlayerMoveParams) o;
        return row == that.row &&
                column == that.column &&
                direction == that.direction &&
                Objects.equals(word, that.word) &&
                Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, row, column, direction, player);
    }

    @Override
    public String toString() {
        return "PlayerMoveParams{" +
                "word='" + word + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", direction=" + direction +
                ", player=" + player +
                '}';
    }
}
