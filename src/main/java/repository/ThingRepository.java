package repository;

import db.DBConnectionProvider;
import db.Repository;
import model.ThingBean;

import java.sql.*;
import java.util.ArrayList;

public class ThingRepository implements Repository<ThingBean> {
    private final Connection connection;

    public ThingRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ThingBean find(int thingID) throws SQLException {
        String SQL = "SELECT thing_id, name "
                + "FROM things "
                + "WHERE thing_id = ?";

        ThingBean obj = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {

            pstmt.setInt(1, thingID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.first())
            {
                obj = map(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    @Override
    public ArrayList<ThingBean> findAll() throws SQLException{
        String SQL = "SELECT thing_id, name FROM things";

        ArrayList<ThingBean> things = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(SQL);
             ResultSet rs = pstmt.executeQuery(SQL)) {
            while (rs.next())
            {
                things.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return things;
    }

    @Override
    public ThingBean create(ThingBean obj) throws SQLException{

        String SQL = "INSERT INTO things(name) "
                + "VALUES(?)";

        ThingBean objectToReturn = null;

        try
         {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, obj.getName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        objectToReturn = this.find(rs.getInt(1));
                    }
                }catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return objectToReturn;
    }

    @Override
    public int delete(int id) throws SQLException{
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

    private static ThingBean map(ResultSet rs) throws SQLException
    {
        ThingBean obj = new ThingBean();

        obj.setName(rs.getString("name"));
        return obj;
    }
}
