package repository;

import db.DBConnectionProvider;
import db.H2ConnectionProvider;
import model.Guest;
import model.Participation;
import model.Party;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ParticipationRepositoryTest {
    @BeforeEach
    public void cleanUpParticipationsTable() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        Statement sqlStatement = dbConnectionProvider.getConnection().createStatement();
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY FALSE");
        sqlStatement.execute("TRUNCATE TABLE parties");
        sqlStatement.execute("ALTER TABLE parties ALTER COLUMN party_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE guests");
        sqlStatement.execute("ALTER TABLE guests ALTER COLUMN guest_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE participations");
        sqlStatement.execute("ALTER TABLE participations ALTER COLUMN participation_id RESTART WITH 1");
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Test
    void testCreateParticipationV1() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);
        GuestRepository guestRepository = getGuestRepository();
        ParticipationRepository participationRepository = getParticipationRepository();

        partyRepository.create(new Party("Party", partyTime));
        guestRepository.create(new Guest("John"));
        guestRepository.create(new Guest("Micah"));

        Participation participationInDb = participationRepository.create(new Participation(1L, 2L));

        assertThat(participationInDb.getPartyID()).isEqualTo(1L);
        assertThat(participationInDb.getGuestID()).isEqualTo(2L);
    }

    @Test
    void testCreateParticipationV2() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);
        GuestRepository guestRepository = getGuestRepository();
        ParticipationRepository participationRepository = getParticipationRepository();

        for (int i = 1; i <= 4; i++) {
            partyRepository.create(new Party(String.format("Party with id equal %d", i), partyTime));
        }

        for (int i = 1; i <= 10; i++) {
            guestRepository.create(new Guest(String.format("Guest with id equal %d", i)));
        }

        Participation participationInDb1 = participationRepository.create(new Participation(1L, 1L));
        Participation participationInDb2 = participationRepository.create(new Participation(4L, 10L));

        assertThat(participationInDb1.getPartyID()).isEqualTo(1L);
        assertThat(participationInDb1.getGuestID()).isEqualTo(1L);

        assertThat(participationInDb2.getPartyID()).isEqualTo(4L);
        assertThat(participationInDb2.getGuestID()).isEqualTo(10L);
    }

    @Test
    void testCreateNullParticipation() throws SQLException {
        ParticipationRepository participationRepository = getParticipationRepository();

        assertThatThrownBy(() -> participationRepository.create(null))
                .isInstanceOf(NullPointerException.class);
    }

    private PartyRepository getPartyRepository() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        return new PartyRepository(dbConnectionProvider.getConnection());
    }

    private ParticipationRepository getParticipationRepository() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        return new ParticipationRepository(dbConnectionProvider.getConnection());
    }

    private GuestRepository getGuestRepository() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        return new GuestRepository(dbConnectionProvider.getConnection());
    }
}
