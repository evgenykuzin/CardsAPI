package ru.sberbank.kuzin19190813.db.dao;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import org.hibernate.query.NativeQuery;
import org.jetbrains.annotations.NotNull;
import ru.sberbank.kuzin19190813.db.HibernateUtil;

import javax.persistence.Table;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class AbstractDAO<C, I extends Serializable> implements DAO<C, I> {
    private final Class<C> entityClass;

    public AbstractDAO(Class<C> entityClass) {
        this.entityClass = entityClass;
    }

    public String getTableName() {
        Table tableClass = entityClass.getAnnotation(Table.class);
        if (tableClass != null) return tableClass.name();
        else return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, entityClass.getSimpleName());
    }

    public void saveAll(@NotNull final Collection<C> objects) {
        executeVoid(session -> objects.forEach(session::save));
    }

    public void save(C object) {
        saveAll(Collections.singleton(object));
    }

    public C get(I id) {
        return executeAndGet(session -> session
                .get(entityClass, id));
    }

    public List<C> getAll() {
        return executeAndGet(session -> {
            CriteriaQuery<C> cq = session
                    .getCriteriaBuilder()
                    .createQuery(entityClass);
            CriteriaQuery<C> all = cq
                    .select(cq.from(entityClass));
            return session.createQuery(all).getResultList();
        });
    }

    public void updateAll(Collection<C> objects) {
        executeVoid(session -> objects.forEach(session::update));
    }

    public void update(C object) {
        updateAll(Collections.singleton(object));
    }

    public void deleteAll(Collection<C> objects) {
        executeVoid(session -> objects.forEach(session::delete));
    }

    public void delete(C object) {
        deleteAll(Collections.singleton(object));
    }

    public void saveOrUpdate(C object) {
        executeVoid(session -> session.saveOrUpdate(object));
    }

    public List<C> search(String tableName, String by, String value) {
        return executeAndGet(session -> {
            NativeQuery<C> query = session
                    .createNativeQuery(getSearchQueryString(tableName, by, value), entityClass);
            return query.getResultList();
        });
    }

    public List<C> search(String by, String value) {
        return search(getTableName(), by, value);
    }

    public C searchFirst(String by, String value) {
        return search(getTableName(), by, value)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public String getSearchQueryString(String tableName, String by, String value) {
        return "select * from " + tableName + " where " + String.format("%s = '%s';", by, value);
    }

    protected void executeVoid(Command command) {
        try {
            this.executeVoid(HibernateUtil.getSessionFactory(), command);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    protected <T> T executeAndGet(Getter<T> getter) {
        try {
            return this.executeAndGet(HibernateUtil.getSessionFactory(), getter);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    protected <T> T executeAndGetOrThrow(Getter<T> getter) throws SQLException {
        return this.executeAndGet(HibernateUtil.getSessionFactory(), getter);
    }

    protected void executeVoidOrThrow(Command command) throws SQLException {
        this.executeVoid(HibernateUtil.getSessionFactory(), command);
    }

}