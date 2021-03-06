package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {
    private final Connection connection;

    public DBInitializer(Connection connection) {
        this.connection = connection;
    }

    public void createTables() throws SQLException {
        try {
            Statement sqlStatement = connection.createStatement();
            connection.setAutoCommit(false);
            sqlStatement.addBatch("CREATE TABLE IF NOT EXISTS parties(party_id serial primary key, name varchar, datetime timestamp)");
            sqlStatement.addBatch("CREATE TABLE IF NOT EXISTS guests(guest_id serial primary key, name varchar)");
            sqlStatement.addBatch("CREATE TABLE IF NOT EXISTS participations(participation_id serial primary key, guest_id INTEGER " +
                    "REFERENCES guests (guest_id), party_id INTEGER REFERENCES parties (party_id))");
            sqlStatement.addBatch("CREATE TABLE IF NOT EXISTS contributions(contribution_id serial primary key, name varchar, participation_id INTEGER " +
                    "REFERENCES participations (participation_id))");
            int count[] = sqlStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new SQLException();
        }
    }
}
