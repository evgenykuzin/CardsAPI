package ru.sberbank.kuzin19190813.db.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface DAO<C, I extends Serializable> {
    void saveAll(Collection<C> objects);

    void save(C object);

    C get(I id);

    List<C> getAll();

    void updateAll(Collection<C> objects);

    void update(C object);

    void deleteAll(Collection<C> objects);

    void delete(C object);

    void saveOrUpdate(C object);

    List<C> search(String tableName, String by, String value);

    default void executeVoid(SessionFactory sessionFactory, Command command) throws SQLException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            command.execute(session);
            session.getTransaction().commit();
        }
    }

    default <T> T executeAndGet(SessionFactory sessionFactory, Getter<T> getter) throws SQLException {
        T t = null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            t = getter.get(session);
            session.getTransaction().commit();
        }
        return t;
    }

    interface Command {
        void execute(Session session) throws SQLException;
    }

    interface Getter<T> {
        T get(Session session) throws SQLException;
    }
}