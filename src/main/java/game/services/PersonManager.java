package game.services;

import game.core.Person;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author Manish Shrestha
 */
public interface PersonManager {

    List<Person> getPersons(Set<Integer> ids);

    Person getPerson(int id);
}
