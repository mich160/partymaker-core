package repository;

import db.DBConnectionProvider;
import db.H2ConnectionProvider;
import model.Party;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PartyRepositoryTest {
    @BeforeEach
    public void cleanUpPartiesTable() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        Statement sqlStatement = dbConnectionProvider.getConnection().createStatement();
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY FALSE");
        sqlStatement.execute("TRUNCATE TABLE parties");
        sqlStatement.execute("ALTER TABLE parties ALTER COLUMN party_id RESTART WITH 1");
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Test
    void testCreateParty() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);

        Party partyInDb = partyRepository.create(new Party("Testowa imprezka", partyTime));

        assertThat(partyInDb.getName()).isEqualTo("Testowa imprezka");
        assertThat(partyInDb.getDate()).isEqualTo(partyTime);
    }

    @Test
    void testCreateNullParty() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();

        assertThatThrownBy(() ->
                partyRepository.create(null)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testDeleteParty() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);
        Party party = new Party("Imprezka do usunięcia", partyTime);

        Party partyInDb = partyRepository.create(party);
        assertThat(partyInDb.getName()).isEqualTo("Imprezka do usunięcia");
        List<Party> partyList = partyRepository.findAll();
        assertThat(partyList.size()).isEqualTo(1);

        partyRepository.delete(Math.toIntExact(partyInDb.getId()));
        List<Party> partyListAfterDeletion = partyRepository.findAll();
        assertThat(partyListAfterDeletion.size()).isEqualTo(0);
    }

    private PartyRepository getPartyRepository() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        return new PartyRepository(dbConnectionProvider.getConnection());
    }
}
