package game.services;

import game.core.Person;
import game.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Manish Shrestha
 */
@Service
class StandardPersonManager implements PersonManager {

    private final PersonsDao personsDao;

    @Autowired
    StandardPersonManager(PersonsDao personsDao) {
        this.personsDao = personsDao;
    }

    @Override
    public List<Person> getPersons(Set<Integer> ids) {
        return personsDao.getPersons(ids);
    }

    @Override
    public Person getPerson(int id) {
        List<Person> persons = getPersons(Collections.singleton(id));
        if (persons.isEmpty()) {
            throw new EntityNotFoundException("person specified by id " + id + " not found");
        }
        return persons.get(0);
    }
}
