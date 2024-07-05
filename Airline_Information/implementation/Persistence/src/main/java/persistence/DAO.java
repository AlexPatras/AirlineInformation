package persistence;

import java.sql.SQLException;

public interface DAO<T>  {
    void create(T entity) throws Exception;
    T read(int id) throws Exception;
    void update(T entity) throws Exception;
    void delete(int id) throws Exception;
}
