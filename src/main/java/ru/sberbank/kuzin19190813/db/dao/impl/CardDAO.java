package ru.sberbank.kuzin19190813.db.dao.impl;

import ru.sberbank.kuzin19190813.db.dao.AbstractDAO;
import ru.sberbank.kuzin19190813.model.Card;

public class CardDAO extends AbstractDAO<Card, Long> {
    public CardDAO() {
        super(Card.class);
    }

    public Double updateBalanceByAccountNumber(String accountNumber, Double amount) {
        return executeAndGet(session -> {
            String query = getSearchQueryString(getTableName(), "account_number", accountNumber);
            Card card = session
                    .createNativeQuery(query, Card.class)
                    .getResultList().stream().findFirst().orElse(null);
            if (card == null) return null;
            Double balance = card.getBalance();
            double newBalance = balance + amount;
            if (newBalance < 0) throw new IllegalArgumentException("updated balance is less then zero");
            card.setBalance(newBalance);
            session.update(card);
            return newBalance;
        });
    }

    public Double getBalance(String accountNumber) {
        return searchFirst("account_number", accountNumber).getBalance();
    }
}
