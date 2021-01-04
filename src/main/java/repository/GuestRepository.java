package repository;

import db.Repository;
import model.Guest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuestRepository implements Repository<Guest> {
    private final Connection connection;

    public GuestRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Guest> find(long guestID) throws SQLException {
        String SQL = "SELECT guest_id, name "
                + "FROM guests "
                + "WHERE guest_id = ?";

        Guest createdGuest = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {

            pstmt.setLong(1, guestID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.first()) {
                createdGuest = map(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(createdGuest);
    }

    @Override
    public List<Guest> findAll() throws SQLException {
        String SQL = "SELECT guest_id, name FROM guests";

        List<Guest> guests = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                guests.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return guests;
    }

    @Override
    public Guest create(Guest guest) throws SQLException {
        String SQL = "INSERT INTO guests(name) "
                + "VALUES(?)";

        Guest objectToReturn = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, guest.getName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long guestID = rs.getInt(1);
                        objectToReturn = this.find(guestID).get();
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
        String SQL = "DELETE FROM guests WHERE guest_id = ?";

        int affectedrows = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {

            pstmt.setLong(1, id);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
        return affectedrows;
    }

    private static Guest map(ResultSet rs) throws SQLException {
        Guest guest = new Guest();

        guest.setId(rs.getLong("guest_id"));
        guest.setName(rs.getString("name"));
        return guest;
    }
}
