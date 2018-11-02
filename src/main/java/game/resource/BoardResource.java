package game.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import game.core.Board;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Manish Shrestha
 */
@JsonRootName("board")
public final class BoardResource {
    private final int size;

    private final List<TileResource> tiles;

    @JsonCreator
    public BoardResource(@JsonProperty("size") int size,
                         @JsonProperty("tiles") List<TileResource> tiles) {
        this.size = size;
        this.tiles = tiles;
    }

    public final int getSize() {
        return size;
    }

    public final List<TileResource> getTiles() {
        return tiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardResource that = (BoardResource) o;
        return size == that.size &&
                Objects.equals(tiles, that.tiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, tiles);
    }

    @Override
    public String toString() {
        return "BoardResource{" +
                "size=" + size +
                ", tiles=" + tiles +
                '}';
    }

    @JsonIgnore
    public static BoardResource fromBoard(Board board) {
      final int size = board.getSize();
      final List<TileResource> tiles = board.getTiles().stream()
              .map(tile -> TileResource.fromTile(tile))
              .collect(Collectors.toList());

      return new BoardResource(size, tiles);
    }
}
