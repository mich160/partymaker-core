package repository;

import db.H2ConnectionProvider;
import model.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserRepositoryTest {
    @Test
    void testCreatesUser() throws SQLException {
        UserRepository userRepository = getUserRepository();

        User user = new User("Kan");
        assertThat(user.getId()).isNull();

        User createdUser = userRepository.create(user);
        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getName()).isEqualTo("Kan");
    }

    @Test
    void testCreateNullUser() throws SQLException {
        UserRepository userRepository = getUserRepository();

        assertThatThrownBy(() -> userRepository.create(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testFindUser() throws SQLException {
        UserRepository userRepository = getUserRepository();

        User user = new User("Jan");
        User createdUser = userRepository.create(user);

        Optional<User> foundUser = userRepository.find(createdUser.getId());

        assertThat(foundUser).contains(createdUser);
    }

    @Test
    void testUserNotFound() throws SQLException {
        UserRepository userRepository = getUserRepository();

        Optional<User> notFound = userRepository.find(2137L);

        assertThat(notFound).isEmpty();
    }

    @Test
    void testFindAllUsers() throws SQLException {
        UserRepository userRepository = getUserRepository();
        User user1 = new User("Maciej");
        User user2 = new User("Maciej");

        userRepository.create(user1);
        userRepository.create(user2);

        List<User> foundUsers = userRepository.findAll();
        assertThat(foundUsers)
                .extracting(User::getId)
                .containsExactly(1L, 2L);

    }

    private UserRepository getUserRepository() throws SQLException {
        Connection connection = new H2ConnectionProvider().getConnection();
        return new UserRepository(connection);
    }
}
