package game.core;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class StandardPerson implements Person {
    private final int id;
    private final String userName;
    private final String firstName;
    private final String lastName;

    public StandardPerson(int id, String userName, String firstName, String lastName) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardPerson that = (StandardPerson) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StandardPerson{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
