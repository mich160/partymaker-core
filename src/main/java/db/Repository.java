package db;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Repository<T> {

    public T find(int id) throws SQLException;
    public ArrayList<T> findAll() throws SQLException;
    public T create(T obj) throws SQLException;
    //public T update(T obj) throws SQLException;
    public int delete(int id) throws SQLException;
}
