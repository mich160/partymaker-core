package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresqlConnectionProvider implements DBConnectionProvider {
    private final String CONNECTION_URL = "jdbc:postgresql://localhost:8012/event_base";
    private final String DB_USER = "postgres";
    private final String DB_PASSWORD = "password";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL, DB_USER, DB_PASSWORD);
    }
}
