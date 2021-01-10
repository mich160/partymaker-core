package repository;

import db.DBConnectionProvider;
import db.H2ConnectionProvider;
import model.Guest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GuestRepositoryTest {
    @BeforeEach
    void cleanUpGuestsTable() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        Statement sqlStatement = dbConnectionProvider.getConnection().createStatement();
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY FALSE");
        sqlStatement.execute("TRUNCATE TABLE guests");
        sqlStatement.execute("ALTER TABLE guests ALTER COLUMN guest_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE participations");
        sqlStatement.execute("ALTER TABLE participations ALTER COLUMN participation_id RESTART WITH 1");
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

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
    void testDeleteGuest() throws SQLException {
        GuestRepository guestRepository = getGuestRepository();

        guestRepository.create(new Guest("Guest 1"));
        guestRepository.create(new Guest("Guest 2"));

        List<Guest> guestList = guestRepository.findAll();
        assertThat(guestList.size()).isEqualTo(2);

        guestRepository.delete(1);
        guestRepository.delete(2);
        List<Guest> guestListAfterDeletion = guestRepository.findAll();
        assertThat(guestListAfterDeletion.size()).isEqualTo(0);
    }

    @Test
    void testFindGuestV1() throws SQLException{
        GuestRepository guestRepository = getGuestRepository();
        Guest guestInDb = guestRepository.create(new Guest("John"));

        Optional<Guest> foundGuest = guestRepository.find(guestInDb.getId());
        assertThat(foundGuest.get().getName()).isEqualTo("John");
    }

    @Test
    void testFindGuestV2() throws SQLException {
        GuestRepository guestRepository = getGuestRepository();

        guestRepository.create(new Guest("Guest with id equal 1"));
        guestRepository.create(new Guest("Guest with id equal 2"));

        assertThat(guestRepository.find(1L).get().getName()).isEqualTo("Guest with id equal 1");
        assertThat(guestRepository.find(2L).get().getName()).isEqualTo("Guest with id equal 2");
    }

    @Test
    void testFindGuestV3() throws SQLException {
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

    private GuestRepository getGuestRepository() throws SQLException {
        Connection connection = new H2ConnectionProvider().getConnection();
        return new GuestRepository(connection);
    }
}
