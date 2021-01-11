package repository;

import db.DBConnectionProvider;
import db.H2ConnectionProvider;
import model.Contribution;
import model.Guest;
import model.Participation;
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

public class ContributionRepositoryTest {
    @BeforeEach
    public void cleanUpContributionsTable() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        Statement sqlStatement = dbConnectionProvider.getConnection().createStatement();
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY FALSE");
        sqlStatement.execute("TRUNCATE TABLE parties");
        sqlStatement.execute("ALTER TABLE parties ALTER COLUMN party_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE guests");
        sqlStatement.execute("ALTER TABLE guests ALTER COLUMN guest_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE participations");
        sqlStatement.execute("ALTER TABLE participations ALTER COLUMN participation_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE contributions");
        sqlStatement.execute("ALTER TABLE contributions ALTER COLUMN contribution_id RESTART WITH 1");
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @AfterEach
    public void cleanUpContributionsTableAfterTest() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        Statement sqlStatement = dbConnectionProvider.getConnection().createStatement();
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY FALSE");
        sqlStatement.execute("TRUNCATE TABLE parties");
        sqlStatement.execute("ALTER TABLE parties ALTER COLUMN party_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE guests");
        sqlStatement.execute("ALTER TABLE guests ALTER COLUMN guest_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE participations");
        sqlStatement.execute("ALTER TABLE participations ALTER COLUMN participation_id RESTART WITH 1");
        sqlStatement.execute("TRUNCATE TABLE contributions");
        sqlStatement.execute("ALTER TABLE contributions ALTER COLUMN contribution_id RESTART WITH 1");
        sqlStatement.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Test
    void testCreateContribution() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);
        GuestRepository guestRepository = getGuestRepository();
        ParticipationRepository participationRepository = getParticipationRepository();
        ContributionRepository contributionRepository = getContributionRepository();

        partyRepository.create(new Party("Party", partyTime));
        guestRepository.create(new Guest("John"));
        participationRepository.create(new Participation(1L, 1L));

        Contribution contributionInDb = contributionRepository.create(new Contribution("Scrabble", 1L));
        assertThat(contributionInDb.getId()).isEqualTo(1L);
        assertThat(contributionInDb.getName()).isEqualTo("Scrabble");
    }

    @Test
    void testCreateNullContribution() throws SQLException {
        ContributionRepository contributionRepository = getContributionRepository();

        assertThatThrownBy(() -> contributionRepository.create(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testDeleteContribution() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);
        GuestRepository guestRepository = getGuestRepository();
        ParticipationRepository participationRepository = getParticipationRepository();
        ContributionRepository contributionRepository = getContributionRepository();

        partyRepository.create(new Party("Party", partyTime));
        guestRepository.create(new Guest("John"));
        participationRepository.create(new Participation(1L, 1L));
        contributionRepository.create(new Contribution("Monopoly", 1L));
        contributionRepository.create(new Contribution("Twister", 1L));
        contributionRepository.create(new Contribution("Chess", 1L));

        List<Contribution> listBeforeDeletion = contributionRepository.findAll();
        assertThat(listBeforeDeletion.size()).isEqualTo(3);

        contributionRepository.delete(1);
        contributionRepository.delete(2);
        contributionRepository.delete(3);
        List<Contribution> listAfterDeletion = contributionRepository.findAll();
        assertThat(listAfterDeletion.size()).isEqualTo(0);
    }

    @Test
    void testFindContribution() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);
        GuestRepository guestRepository = getGuestRepository();
        ParticipationRepository participationRepository = getParticipationRepository();
        ContributionRepository contributionRepository = getContributionRepository();

        partyRepository.create(new Party("Party", partyTime));
        guestRepository.create(new Guest("John"));
        participationRepository.create(new Participation(1L, 1L));
        contributionRepository.create(new Contribution("Fifa", 1L));
        contributionRepository.create(new Contribution("Red Dead Redemption 2", 1L));
        contributionRepository.create(new Contribution("GTA V", 1L));

        Optional<Contribution> foundContribution = contributionRepository.find(2);
        assertThat(foundContribution.get().getName()).isEqualTo("Red Dead Redemption 2");
        assertThat(foundContribution.get().getId()).isEqualTo(2L);
    }

    @Test
    void testFoundNoContribution() throws SQLException {
        ContributionRepository contributionRepository = getContributionRepository();

        Optional<Contribution> foundContribution = contributionRepository.find(2222L);

        assertThat(foundContribution).isEmpty();
    }

    @Test
    void testFindAllContributions() throws SQLException {
        PartyRepository partyRepository = getPartyRepository();
        LocalDateTime partyTime = LocalDateTime.of(2017, Month.FEBRUARY, 15, 12, 00, 00);
        GuestRepository guestRepository = getGuestRepository();
        ParticipationRepository participationRepository = getParticipationRepository();
        ContributionRepository contributionRepository = getContributionRepository();

        partyRepository.create(new Party("Party", partyTime));
        guestRepository.create(new Guest("John"));
        participationRepository.create((new Participation(1L, 1L)));
        for (int i = 1; i <= 10; i++) {
            contributionRepository.create(new Contribution(String.format("Contribution nr: %d", i), 1L));
        }
        List<Contribution> shouldBe10ElementList = contributionRepository.findAll();
        assertThat(shouldBe10ElementList.size()).isEqualTo(10);

        assertThat(shouldBe10ElementList)
                .extracting(Contribution::getName)
                .containsExactly("Contribution nr: 1", "Contribution nr: 2", "Contribution nr: 3", "Contribution nr: 4",
                        "Contribution nr: 5", "Contribution nr: 6", "Contribution nr: 7", "Contribution nr: 8",
                        "Contribution nr: 9", "Contribution nr: 10");
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

    private ContributionRepository getContributionRepository() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        return new ContributionRepository(dbConnectionProvider.getConnection());
    }
}
