package game.core;

import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class StandardBoard implements Board {
    private final List<Tile> tiles;

    public StandardBoard(List<Tile> tiles) {
        this.tiles = tiles;
    }

    @Override
    public int getSize() {
        return (int)Math.sqrt(tiles.size());
    }

    @Override
    public List<Tile> getTiles() {
        return tiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardBoard that = (StandardBoard) o;
        return Objects.equals(tiles, that.tiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tiles);
    }

    @Override
    public String toString() {
        return "StandardBoard{" +
                "tiles=" + tiles +
                '}';
    }
}
