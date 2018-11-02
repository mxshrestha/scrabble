package game.services;

import game.core.Person;
import game.core.StandardPerson;
import game.utils.SQLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author Manish Shrestha
 */
@Service
class StandardPersonsDao implements PersonsDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    StandardPersonsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Person> getPersons(Set<Integer> ids) {
        final String GET_PERSONS = "SELECT id, user_name, first_name, last_name FROM persons WHERE id IN (" + SQLUtils.inClause(ids.size()) + ")";
        return jdbcTemplate.query(GET_PERSONS, ids.toArray(), (resultSet, i) -> {
            final int id = resultSet.getInt("id");
            final String userName = resultSet.getString("user_name");
            final String firstName = resultSet.getString("first_name");
            final String lastName = resultSet.getString("last_name");

            return new StandardPerson(id, userName, firstName, lastName);
        });
    }
}
