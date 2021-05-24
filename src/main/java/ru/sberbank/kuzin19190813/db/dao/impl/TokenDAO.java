package ru.sberbank.kuzin19190813.db.dao.impl;

import ru.sberbank.kuzin19190813.db.dao.AbstractDAO;
import ru.sberbank.kuzin19190813.model.Token;

public class TokenDAO extends AbstractDAO<Token, String> {
    public TokenDAO() {
        super(Token.class);
    }

    public boolean contains(String tokenString) {
        return getAll().stream().anyMatch(token -> token.getToken().equals(tokenString));
    }
}
