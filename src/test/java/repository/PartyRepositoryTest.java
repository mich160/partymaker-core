package repository;

import db.DBConnectionProvider;
import db.H2ConnectionProvider;
import model.Party;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

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

    @AfterEach
    public void cleanUpPartiesTableAfterTest() throws SQLException {
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

    @Test
    void testFindPartyV1() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);
        Party partyinDb = partyRepository.create(new Party("Test party", partyTime));

        Optional<Party> foundParty = partyRepository.find(partyinDb.getId());
        assertThat(foundParty.get().getName()).isEqualTo("Test party");
        assertThat(foundParty.get().getDate()).isEqualTo(partyTime);
    }

    @Test
    void testFindPartyV2() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);

        partyRepository.create(new Party("Party with id equal 1", partyTime));
        partyRepository.create(new Party("Party with id equal 2", partyTime));

        assertThat(partyRepository.find(1L).get().getName()).isEqualTo("Party with id equal 1");
        assertThat(partyRepository.find(2L).get().getName()).isEqualTo("Party with id equal 2");
    }

    @Test
    void testPartyNotFound() throws SQLException{
        PartyRepository partyRepository = getPartyRepository();

        Optional<Party> foundParty = partyRepository.find(1234L);
        assertThat(foundParty).isEmpty();
    }

    @Test
    void testFindAllPartiesV1() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);

        for (int i = 1; i <= 10; i++) {
            partyRepository.create(new Party(String.format("Test Party nr %d", i), partyTime));
        }

        List<Party> foundParties = partyRepository.findAll();
        assertThat(foundParties.size()).isEqualTo(10);
    }

    @Test
    void testFindAllPartiesV2() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);

        partyRepository.create(new Party("Party nr 1", partyTime));
        partyRepository.create(new Party("Party nr 2", partyTime));

        List<Party> foundParties = partyRepository.findAll();
        assertThat(foundParties)
                .extracting(Party::getId)
                .containsExactly(1L, 2L);
    }

    private PartyRepository getPartyRepository() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        return new PartyRepository(dbConnectionProvider.getConnection());
    }
}
