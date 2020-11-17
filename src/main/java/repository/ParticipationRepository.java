package repository;

import db.Repository;
import model.Participation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipationRepository implements Repository<Participation>{
    private final Connection connection;

    public ParticipationRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Participation> find(long participationID) throws SQLException {
        String SQL = "SELECT participation_id, user_id, party_id "
                + "FROM participations "
                + "WHERE participation_id = ?";

        Participation createdParticipation = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {

            pstmt.setLong(1, participationID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.first()) {
                createdParticipation = map(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(createdParticipation);
    }

    @Override
    public List<Participation> findAll() throws SQLException {
        String SQL = "SELECT participation_id, user_id, party_id FROM participations";

        List<Participation> participations = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                participations.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return participations;
    }

    @Override
    public Participation create(Participation participation) throws SQLException {
        String SQL = "INSERT INTO participations(user_id, party_id) "
                + "VALUES(?, ?)";

        Participation objectToReturn = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setLong(1, participation.getUserID());
            pstmt.setLong(2, participation.getPartyID());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long participationID = rs.getInt(1);
                        objectToReturn = this.find(participationID).get();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return objectToReturn;
    }

    @Override
    public int delete(int id) throws SQLException {
        String SQL = "DELETE FROM participations WHERE participation_id = ?";

        int affectedrows = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {

            pstmt.setLong(1, id);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
        return affectedrows;
    }

    private static Participation map(ResultSet rs) throws SQLException {
        Participation participation = new Participation();

        participation.setId(rs.getLong("participation_id"));
        participation.setUserID(rs.getLong("user_id"));
        participation.setPartyID(rs.getLong("party_id"));
        return participation;
    }
}
