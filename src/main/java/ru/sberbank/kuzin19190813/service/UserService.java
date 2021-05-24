package ru.sberbank.kuzin19190813.service;

import ru.sberbank.kuzin19190813.db.dao.DAOHolder;
import ru.sberbank.kuzin19190813.model.User;

import java.util.function.Consumer;

public class UserService implements UserServiceInterface {

    public User createUser() {
        return createUser(user -> {});
    }

    public User createUser(Consumer<User> setter) {
        User user = new User();
        setter.accept(user);
        DAOHolder.getUserDAO().save(user);
        return user;
    }

}
