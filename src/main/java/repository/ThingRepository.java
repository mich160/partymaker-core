package repository;

import db.Repository;
import model.Thing;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ThingRepository implements Repository<Thing> {
    private final Connection connection;

    public ThingRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Thing> find(long thingID) throws SQLException {
        String SQL = "SELECT thing_id, name "
                + "FROM things "
                + "WHERE thing_id = ?";

        Thing obj = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {

            pstmt.setLong(1, thingID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.first()) {
                obj = map(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(obj);
    }

    @Override
    public List<Thing> findAll() throws SQLException {
        String SQL = "SELECT thing_id, name FROM things";

        List<Thing> things = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                things.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return things;
    }

    @Override
    public Thing create(Thing obj) throws SQLException {

        String SQL = "INSERT INTO things(name) "
                + "VALUES(?)";

        Thing objectToReturn = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, obj.getName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        objectToReturn = this.find(rs.getInt(1)).get();
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
        String SQL = "DELETE FROM things WHERE thing_id = ?";

        int affectedrows = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {

            pstmt.setInt(1, id);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
        return affectedrows;
    }

    private static Thing map(ResultSet rs) throws SQLException {
        Thing obj = new Thing();

        obj.setName(rs.getString("name"));
        return obj;
    }
}
