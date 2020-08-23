package db;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnectionProviderTest {

    @Test
    void testH2Connection() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        testDBConnection(dbConnectionProvider);
    }

    @Test
    void testPostgresConnection() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new PostgresqlConnectionProvider();
        testDBConnection(dbConnectionProvider);
    }

    private void testDBConnection(DBConnectionProvider dbConnectionProvider) throws SQLException {
        Connection connection = dbConnectionProvider.getConnection();

        PreparedStatement testQuery = connection.prepareStatement("SELECT 'string' FROM DUAL");
        ResultSet resultSet = testQuery.executeQuery();

        String string = resultSet.getString(1);
        Assertions.assertEquals(string, "string");
    }
}
