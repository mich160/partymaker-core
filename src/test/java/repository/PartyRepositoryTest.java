package repository;

import db.DBConnectionProvider;
import db.H2ConnectionProvider;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;
import java.sql.Statement;

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
}
