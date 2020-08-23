package repository;

import db.DBConnectionProvider;
import db.Repository;
import model.UserBean;

import java.sql.*;
import java.util.ArrayList;

public class UserRepository implements Repository<UserBean> {
    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UserBean find(int userID) throws SQLException {
        String SQL = "SELECT user_id, name, surname "
                + "FROM users "
                + "WHERE user_id = ?";

        UserBean obj = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {

            pstmt.setInt(1, userID);
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
    public ArrayList<UserBean> findAll() throws SQLException{
        String SQL = "SELECT user_id, name, surname FROM users";

        ArrayList<UserBean> users = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(SQL);
             ResultSet rs = pstmt.executeQuery(SQL)) {
            while (rs.next())
            {
                users.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return users;
    }

    @Override
    public UserBean create(UserBean obj) throws SQLException{
        String SQL = "INSERT INTO users(name, surname) "
                + "VALUES(?,?)";

        UserBean objectToReturn = null;

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, obj.getName());
            pstmt.setString(2, obj.getSurname());

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
        String SQL = "DELETE FROM users WHERE user_id = ?";

        int affectedrows = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {

            pstmt.setInt(1, id);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
        return affectedrows;
    }

    private static UserBean map(ResultSet rs) throws SQLException
    {
        UserBean obj = new UserBean();

        obj.setName(rs.getString("name"));
        obj.setSurname((rs.getString("surname")));
        return obj;
    }
}
