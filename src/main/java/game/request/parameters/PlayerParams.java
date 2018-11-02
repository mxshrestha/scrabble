package game.request.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class PlayerParams {

    private final int id;
    private final int order;

    @JsonCreator
    public PlayerParams(@JsonProperty(value = "id", required = true) int id,
                        @JsonProperty(value = "order", required = true) int order) {
        this.id = id;
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerParams that = (PlayerParams) o;
        return id == that.id &&
                order == that.order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }

    @Override
    public String toString() {
        return "PlayerParams{" +
                "id=" + id +
                ", order=" + order +
                '}';
    }
}
