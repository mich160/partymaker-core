package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface DBConnectionProvider {
    Connection getConnection() throws SQLException;
}
