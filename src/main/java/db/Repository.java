package db;

import model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    public Optional<T> find(long id) throws SQLException;
    public List<T> findAll() throws SQLException;
    public T create(T obj) throws SQLException;
    //public T update(T obj) throws SQLException;
    public int delete(int id) throws SQLException;
}
