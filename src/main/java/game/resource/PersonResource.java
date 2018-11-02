package game.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import game.core.Person;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
@JsonRootName("person")
public final class PersonResource {
    private final int id;
    private final String userName;
    private final String firstName;
    private final String lastName;

    @JsonCreator
    public PersonResource(@JsonProperty("id") int id,
                          @JsonProperty("userName") String userName,
                          @JsonProperty("firstName") String firstName,
                          @JsonProperty("lastName") String lastName) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public final int getId() {
        return id;
    }

    public final String getUserName() {
        return userName;
    }

    public final String getFirstName() {
        return firstName;
    }

    public final String getLastName() {
        return lastName;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonResource that = (PersonResource) o;
        return id == that.id &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hash(id, userName, firstName, lastName);
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "PersonResource{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @JsonIgnore
    public static PersonResource fromPerson(@NonNull Person person) {
        return new PersonResource(person.getId(), person.getUserName(), person.getFirstName(), person.getLastName());
    }
}
