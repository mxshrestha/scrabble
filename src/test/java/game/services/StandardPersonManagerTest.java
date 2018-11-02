package game.services;

import game.core.Person;
import game.exceptions.EntityNotFoundException;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.easymock.EasyMock.expect;

/**
 * @author Manish Shrestha
 */
public class StandardPersonManagerTest {

    @Test
    public void testGetPersonsTest() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);

        Set<Integer> ids = new HashSet<>();

        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Person person = ctrl.createMock("person" + i, Person.class);
            persons.add(person);
        }

        PersonsDao personsDao = ctrl.createMock(PersonsDao.class);

        expect(personsDao.getPersons(ids)).andReturn(persons);

        ctrl.replay();

        StandardPersonManager standardPersonManager = new StandardPersonManager(personsDao);
        final List<Person> returnedPersons = standardPersonManager.getPersons(ids);

        ctrl.verify();

        Assert.assertEquals(persons, returnedPersons);
    }

    @Test
    public void testGetPerson() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);
        PersonsDao personsDao = ctrl.createMock(PersonsDao.class);
        Person person = ctrl.createMock(Person.class);

        List<Person> persons = new ArrayList<>();
        persons.add(person);
        expect(personsDao.getPersons(Collections.singleton(1))).andReturn(persons);

        ctrl.replay();

        StandardPersonManager standardPersonManager = new StandardPersonManager(personsDao);
        Person returnedPerson = standardPersonManager.getPerson(1);

        ctrl.verify();

        Assert.assertEquals(person, returnedPerson);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetPersonInvalidId() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);
        PersonsDao personsDao = ctrl.createMock(PersonsDao.class);
        Person person = ctrl.createMock(Person.class);

        List<Person> persons = new ArrayList<>();
        expect(personsDao.getPersons(Collections.singleton(1))).andReturn(persons);

        ctrl.replay();

        StandardPersonManager standardPersonManager = new StandardPersonManager(personsDao);
        standardPersonManager.getPerson(1);

        ctrl.verify();
    }
}
