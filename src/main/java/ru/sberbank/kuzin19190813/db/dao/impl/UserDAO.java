package ru.sberbank.kuzin19190813.db.dao.impl;

import org.hibernate.Hibernate;
import ru.sberbank.kuzin19190813.db.dao.AbstractDAO;
import ru.sberbank.kuzin19190813.model.User;

import java.util.function.Function;

public class UserDAO extends AbstractDAO<User, Long> {
    public UserDAO() {
        super(User.class);
    }

    public <T> User getWith(Long id, Function<User, T> fieldInitFunction) {
        return executeAndGet(session -> {
            User user = session
                    .get(getEntityClass(), id);
            if (user != null) {
                Hibernate.initialize(fieldInitFunction.apply(user));
            }
            return user;
        });
    }

    public User getWithCounterparties(Long id) {
        return getWith(id, User::getCounterparties);
    }

    public User getWithCounterpartyOf(Long id) {
        return getWith(id, User::getCounterpartyOf);
    }

    public User getWithCards(Long id) {
        return getWith(id, User::getCards);
    }
}
