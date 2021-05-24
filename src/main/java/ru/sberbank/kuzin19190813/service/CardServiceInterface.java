package ru.sberbank.kuzin19190813.service;

import ru.sberbank.kuzin19190813.exceptions.CardNotFoundException;
import ru.sberbank.kuzin19190813.exceptions.IllegalParameterException;
import ru.sberbank.kuzin19190813.model.Card;

import java.util.List;

public interface CardServiceInterface {
    void createCard(Long userId, String accountNumber, String cardNumber);

    List<Card> getCards(Long userId);

    void updateBalance(String accountNumber, Double amount) throws CardNotFoundException, IllegalParameterException;

    Double getCardBalance(String cardNumber);

}
