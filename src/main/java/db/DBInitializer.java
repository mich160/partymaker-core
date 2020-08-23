package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {
    private final Connection connection;

    private DBInitializer(Connection connection)
    {
        this.connection = connection;
    }

    public void createTables() throws SQLException
    {
        try {
            Statement sqlStatement = connection.createStatement();
            connection.setAutoCommit(false);
            sqlStatement.addBatch("CREATE TABLE IF NOT EXISTS users(user_id serial primary key, name varchar, surname varchar)");
            sqlStatement.addBatch("CREATE TABLE IF NOT EXISTS things(thing_id serial primary key, name varchar)");
            int count[] = sqlStatement.executeBatch();
            connection.commit();
        } catch (SQLException e){
            throw new SQLException();
        }
    }
}
