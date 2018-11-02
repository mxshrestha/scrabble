package game.services;

import game.core.Person;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Manish Shrestha
 */
public interface PersonsDao {

    List<Person> getPersons(Set<Integer> ids);
}
