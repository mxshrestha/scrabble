package game.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class GameResources {
    private final List<GameResource> games;
    private final int count;
    private final int limit;
    private final int offset;

    @JsonCreator
    public GameResources(@JsonProperty("games") List<GameResource> games, @JsonProperty("count") int count,
                         @JsonProperty("limit") int limit, @JsonProperty("offset") int offset) {
        this.games = games;
        this.count = count;
        this.limit = limit;
        this.offset = offset;
    }

    public List<GameResource> getGames() {
        return games;
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
        GameResources that = (GameResources) o;
        return count == that.count &&
                limit == that.limit &&
                offset == that.offset &&
                Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(games, count, limit, offset);
    }

    @Override
    public String toString() {
        return "GameResources{" +
                "games=" + games +
                ", count=" + count +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
