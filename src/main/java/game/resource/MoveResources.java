package game.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class MoveResources {
    private final List<MoveResource> moves;
    private final int count;
    private final int limit;
    private final int offset;

    @JsonCreator
    public MoveResources(@JsonProperty("moves") List<MoveResource> moves,
                         @JsonProperty("count") int count,
                         @JsonProperty("limit") int limit,
                         @JsonProperty("offset") int offset) {
        this.moves = moves;
        this.count = count;
        this.limit = limit;
        this.offset = offset;
    }

    public List<MoveResource> getMoves() {
        return moves;
    }

    public int getCount() {
        return count;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveResources that = (MoveResources) o;
        return count == that.count &&
                limit == that.limit &&
                offset == that.offset &&
                Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moves, count, limit, offset);
    }

    @Override
    public String toString() {
        return "MoveResources{" +
                "moves=" + moves +
                ", count=" + count +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
