package repository;

import db.Repository;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements Repository<User> {
    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<User> find(long userID) throws SQLException {
        String SQL = "SELECT user_id, name, surname "
                + "FROM users "
                + "WHERE user_id = ?";

        User createdUser = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {

            pstmt.setLong(1, userID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.first()) {
                createdUser = map(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(createdUser);
    }

    @Override
    public List<User> findAll() throws SQLException {
        String SQL = "SELECT user_id, name, surname FROM users";

        List<User> users = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                users.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return users;
    }

    @Override
    public User create(User user) throws SQLException {
        String SQL = "INSERT INTO users(name, surname) "
                + "VALUES(?,?)";

        User objectToReturn = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getSurname());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userID = rs.getInt(1);
                        objectToReturn = this.find(userID).get();
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

    private static User map(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setSurname((rs.getString("surname")));
        return user;
    }
}
