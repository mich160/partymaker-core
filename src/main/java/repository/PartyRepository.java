package repository;

import db.Repository;
import model.Party;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PartyRepository implements Repository<Party> {
    private final Connection connection;

    public PartyRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Party> find(long partyID) throws SQLException {
        String SQL = "SELECT party_id, name, datetime "
                + "FROM parties "
                + "WHERE party_id = ?";

        Party createdParty = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {

            pstmt.setLong(1, partyID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.first()) {
                createdParty = map(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(createdParty);
    }

    @Override
    public List<Party> findAll() throws SQLException {
        String SQL = "SELECT party_id, name, datetime FROM parties";

        List<Party> parties = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                parties.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return parties;
    }

    @Override
    public Party create(Party party) throws SQLException {
        String SQL = "INSERT INTO parties(name, datetime) "
                + "VALUES(?, ?)";

        Party objectToReturn = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, party.getName());
            pstmt.setObject(2, party.getDate());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long partyID = rs.getInt(1);
                        objectToReturn = this.find(partyID).get();
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
        String SQL = "DELETE FROM parties WHERE party_id = ?";

        int affectedrows = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {

            pstmt.setLong(1, id);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
        return affectedrows;
    }

    private static Party map(ResultSet rs) throws SQLException {
        Party party = new Party();

        party.setId(rs.getLong("party_id"));
        party.setName(rs.getString("name"));
        party.setDate(rs.getObject(3, LocalDateTime.class));
        return party;
    }
}
