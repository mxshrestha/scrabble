package game.core;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class StandardPlayer implements Player {
    private final Person person;
    private final int order;

    public StandardPlayer(Person person, int order) {
        this.person = person;
        this.order = order;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardPlayer that = (StandardPlayer) o;
        return order == that.order &&
                Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, order);
    }

    @Override
    public String toString() {
        return "StandardPlayer{" +
                "person=" + person +
                ", order=" + order +
                '}';
    }
}
