package game.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import game.core.Tile;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
@JsonRootName("tile")
public final class TileResource {
    private final int row;
    private final int column;
    private final char value;
    private final int boost;
    private final int charBoost;

    @JsonCreator
    public TileResource(@JsonProperty("row") int row,
                        @JsonProperty("column") int column,
                        @JsonProperty("value") char value,
                        @JsonProperty("boost") int boost,
                        @JsonProperty("charBoost") int charBoost) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.boost = boost;
        this.charBoost = charBoost;
    }

    public final int getRow() {
        return row;
    }

    public final int getColumn() {
        return column;
    }

    public final char getValue() {
        return value;
    }

    public final int getBoost() {
        return boost;
    }

    public final int getCharBoost() {
        return charBoost;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileResource that = (TileResource) o;
        return row == that.row &&
                column == that.column &&
                value == that.value &&
                boost == that.boost &&
                charBoost == that.charBoost;
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hash(row, column, value, boost, charBoost);
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "TileResource{" +
                "row=" + row +
                ", column=" + column +
                ", value=" + value +
                ", boost=" + boost +
                ", charBoost=" + charBoost +
                '}';
    }

    @JsonIgnore
    public static TileResource fromTile(@NonNull Tile tile) {
        return new TileResource(tile.getRow(), tile.getColumn(), tile.getValue(), tile.getBoost(), tile.getCharBoost());
    }
}
