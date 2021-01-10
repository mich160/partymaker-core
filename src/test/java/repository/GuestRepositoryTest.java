package repository;

import db.DBConnectionProvider;
import db.H2ConnectionProvider;
import model.Guest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

    private GuestRepository getGuestRepository() throws SQLException {
        Connection connection = new H2ConnectionProvider().getConnection();
        return new GuestRepository(connection);
    }
}
