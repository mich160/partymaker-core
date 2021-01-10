package repository;

import db.DBConnectionProvider;
import db.H2ConnectionProvider;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;
import java.sql.Statement;

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
}
