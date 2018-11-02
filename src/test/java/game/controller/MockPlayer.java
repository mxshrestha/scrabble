package game.controller;

import game.core.Person;
import game.core.Player;
import game.core.StandardPerson;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class MockPlayer implements Player {
    private final int id;
    private final int order;

    public MockPlayer(int id, int order) {
       this.id = id;
       this.order = order;
    }

    public MockPlayer() {
        id = 0;
        order = 1;
    }

    @Override
    public Person getPerson() {
        return new StandardPerson(id, "user_" + id, "first_" + id, "last_" + id);
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MockPlayer that = (MockPlayer) o;
        return id == that.id &&
                order == that.order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }
}
