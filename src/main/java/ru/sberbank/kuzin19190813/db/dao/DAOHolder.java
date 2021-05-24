package ru.sberbank.kuzin19190813.db.dao;

import ru.sberbank.kuzin19190813.db.dao.impl.CardDAO;
import ru.sberbank.kuzin19190813.db.dao.impl.UserDAO;

public class DAOHolder {
    public static CardDAO getCardDAO() {
        return Holder.CARD_DAO;
    }

    public static UserDAO getUserDAO() {
        return Holder.USER_DAO;
    }

    private static class Holder {
        public static CardDAO CARD_DAO = new CardDAO();
        public static UserDAO USER_DAO = new UserDAO();
    }
}
