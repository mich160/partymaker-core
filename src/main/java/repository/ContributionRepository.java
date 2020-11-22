package repository;

import db.Repository;
import model.Contribution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContributionRepository implements Repository<Contribution> {
    private final Connection connection;

    public ContributionRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Contribution> find(long contributionID) throws SQLException {
        String SQL = "SELECT contribution_id, name, participation_id "
                + "FROM contributions "
                + "WHERE contribution_id = ?";

        Contribution obj = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {

            pstmt.setLong(1, contributionID);
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
    public List<Contribution> findAll() throws SQLException {
        String SQL = "SELECT contribution_id, name, participation_id FROM contributions";

        List<Contribution> contributions = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                contributions.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return contributions;
    }

    @Override
    public Contribution create(Contribution obj) throws SQLException {

        String SQL = "INSERT INTO contributions(name, participation_id) "
                + "VALUES(?, ?)";

        Contribution objectToReturn = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, obj.getName());
            pstmt.setLong(2, obj.getParticipation_id());

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
        String SQL = "DELETE FROM contributions WHERE contribution_id = ?";

        int affectedrows = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {

            pstmt.setInt(1, id);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
        return affectedrows;
    }

    private static Contribution map(ResultSet rs) throws SQLException {
        Contribution obj = new Contribution();

        obj.setId(rs.getLong("contribution_id"));
        obj.setName(rs.getString("name"));
        obj.setParticipation_id(rs.getLong("participation_id"));
        return obj;
    }
}
