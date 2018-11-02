package game.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import game.core.Player;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
@JsonRootName("player")
public class PlayerResource {
    private final PersonResource person;
    private final int order;

    public PlayerResource(@JsonProperty(value = "person") PersonResource person,
                          @JsonProperty(value = "order") int order) {
        this.person = person;
        this.order = order;
    }

    public final PersonResource getPerson() {
        return person;
    }

    public final int getOrder() {
        return order;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerResource that = (PlayerResource) o;
        return order == that.order &&
                Objects.equals(person, that.person);
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hash(person, order);
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "PlayerResource{" +
                "personResource=" + person +
                ", order=" + order +
                '}';
    }

    @JsonIgnore
    public static final PlayerResource fromPlayer(@NonNull Player player) {
        final PersonResource personResource = PersonResource.fromPerson(player.getPerson());
        return new PlayerResource(personResource, player.getOrder());
    }
}
