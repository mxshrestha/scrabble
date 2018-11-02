package game.core;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class StandardTile implements Tile {
    private final int row;
    private final int column;
    private final char value;
    private final int boost;
    private final int charBoost;

    public StandardTile(int row, int column, char value, int boost, int charBoost) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.boost = boost;
        this.charBoost = charBoost;
    }

    public StandardTile(int row, int column, char value) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.boost = 1;
        this.charBoost = 1;
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
    public char getValue() {
        return value;
    }

    @Override
    public int getBoost() {
        return boost;
    }

    @Override
    public int getCharBoost() {
        return charBoost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardTile tile = (StandardTile) o;
        return row == tile.row &&
                column == tile.column &&
                value == tile.value &&
                boost == tile.boost &&
                charBoost == tile.charBoost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, value, boost, charBoost);
    }

    @Override
    public String toString() {
        return "StandardTile{" +
                "row=" + row +
                ", column=" + column +
                ", value=" + value +
                ", boost=" + boost +
                ", charBoost=" + charBoost +
                '}';
    }
}
