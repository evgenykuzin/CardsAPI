package ru.sberbank.kuzin19190813.service;

import ru.sberbank.kuzin19190813.db.dao.DAOHolder;
import ru.sberbank.kuzin19190813.exceptions.CardNotFoundException;
import ru.sberbank.kuzin19190813.exceptions.IllegalParameterException;
import ru.sberbank.kuzin19190813.model.Card;
import ru.sberbank.kuzin19190813.model.User;

import java.util.List;

public class CardService implements CardServiceInterface {
    @Override
    public void createCard(Long userId, String accountNumber, String cardNumber) {
        Card card = new Card();
        card.setAccountNumber(accountNumber);
        card.setCardNumber(cardNumber);
        User user = new User();
        user.setId(userId);
        card.setUser(user);
        DAOHolder.getCardDAO().save(card);
    }

    public List<Card> getCards(Long userId) {
        User user = DAOHolder.getUserDAO().get(userId);
        if (user == null) return null;
        return user.getCards();
    }

    public void updateBalance(String accountNumber, Double amount) throws CardNotFoundException, IllegalParameterException {
        if (amount == null) throw new IllegalParameterException("amount is null or less then zero");
        Double updatedBalance = DAOHolder.getCardDAO()
                .updateBalanceByAccountNumber(accountNumber, amount);
        if (updatedBalance == null) throw new CardNotFoundException();
    }

    public Double getCardBalance(String accountNumber) {
        return DAOHolder.getCardDAO().getBalance(accountNumber);
    }
}
