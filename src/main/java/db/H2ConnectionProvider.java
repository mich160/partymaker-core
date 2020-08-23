package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionProvider implements DBConnectionProvider {
    private final String CONNECTION_URL = "jdbc:h2:~/test";
    private final String DB_USER = "sa";
    private final String DB_PASSWORD = "";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL, DB_USER, DB_PASSWORD);
    }
}
