package repository;

import db.H2ConnectionProvider;
import model.Guest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GuestRepositoryTest {
    @Test
    void testCreatesGuest() throws SQLException {
        GuestRepository guestRepository = getGuestRepository();

        Guest guest = new Guest("Kan");
        assertThat(guest.getId()).isNull();

        Guest createdGuest = guestRepository.create(guest);
        assertThat(createdGuest.getId()).isPositive();
        assertThat(createdGuest.getName()).isEqualTo("Kan");
    }

    @Test
    void testCreateNullGuest() throws SQLException {
        GuestRepository guestRepository = getGuestRepository();

        assertThatThrownBy(() -> guestRepository.create(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testFindGuest() throws SQLException {
        GuestRepository guestRepository = getGuestRepository();

        Guest guest = new Guest("Jan");
        Guest createdGuest = guestRepository.create(guest);

        Optional<Guest> foundGuest = guestRepository.find(createdGuest.getId());

        assertThat(foundGuest).contains(createdGuest);
    }

    @Test
    void testGuestNotFound() throws SQLException {
        GuestRepository guestRepository = getGuestRepository();

        Optional<Guest> notFound = guestRepository.find(2137L);

        assertThat(notFound).isEmpty();
    }

    @Test
    void testFindAllGuests() throws SQLException {
        GuestRepository guestRepository = getGuestRepository();
        Guest guest1 = new Guest("Maciej");
        Guest guest2 = new Guest("Maciej");

        guestRepository.create(guest1);
        guestRepository.create(guest2);

        List<Guest> foundGuests = guestRepository.findAll();
        assertThat(foundGuests)
                .extracting(Guest::getId)
                .containsExactly(1L, 2L);

    }

    private GuestRepository getGuestRepository() throws SQLException {
        Connection connection = new H2ConnectionProvider().getConnection();
        return new GuestRepository(connection);
    }
}
