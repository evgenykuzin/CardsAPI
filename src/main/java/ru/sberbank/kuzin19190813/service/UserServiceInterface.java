package ru.sberbank.kuzin19190813.service;

import ru.sberbank.kuzin19190813.model.User;

import java.util.function.Consumer;

public interface UserServiceInterface {

    default User createUser() {
        return createUser(user -> {});
    }

    User createUser(Consumer<User> setter);

}
