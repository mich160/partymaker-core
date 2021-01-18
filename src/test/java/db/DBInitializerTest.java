package db;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.*;

public class DBInitializerTest {
    @Test
    public void checkCreatesGuestTable() throws SQLException {
        Connection connection = createTables();

        long id = insertInto(connection, "INSERT INTO guests (name) VALUES ('jan')");
        PreparedStatement selectRow = connection.prepareStatement("SELECT * FROM guests WHERE guest_id = ?");
        selectRow.setLong(1, id);
        ResultSet resultRow = selectRow.executeQuery();
        resultRow.next();
        String name = resultRow.getString(2);

        Assert.assertEquals(name, "jan");
    }

    @Test
    void checkCreatesContributionsTable() throws SQLException {
        Connection connection = createTables();

        long id = insertInto(connection, "INSERT INTO contributions (name) VALUES ('zebula')");
        PreparedStatement selectRow = connection.prepareStatement("SELECT * FROM contributions WHERE contribution_id = ?");
        selectRow.setLong(1, id);
        ResultSet resultRow = selectRow.executeQuery();
        resultRow.next();
        String name = resultRow.getString(2);

        Assert.assertEquals(name, "zebula");
    }

    private long insertInto(Connection connection, String query) throws SQLException {
        Statement statement = connection.createStatement();
        int affectedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        if (affectedRows > 0) {
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    long id = resultSet.getLong(1);
                    connection.commit();
                    return id;
                }
            }
        }
        throw new RuntimeException("Should insert at least one row!");
    }

    private Connection createTables() throws SQLException {
        DBConnectionProvider dbConnectionProvider = new H2ConnectionProvider();
        Connection connection = dbConnectionProvider.getConnection();
        DBInitializer dbInitializer = new DBInitializer(connection);
        dbInitializer.createTables();
        return connection;
    }
}
