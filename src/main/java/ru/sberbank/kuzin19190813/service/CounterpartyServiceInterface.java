package ru.sberbank.kuzin19190813.service;

import ru.sberbank.kuzin19190813.exceptions.*;
import ru.sberbank.kuzin19190813.model.User;

import java.util.List;

public interface CounterpartyServiceInterface {
    void addCounterparty(Long userId, Long counterpartyId) throws UserNotFoundException, EntityAlreadyExistsException;

    List<User> getCounterparties(Long userId) throws UserNotFoundException;

    void sendPaymentToCounterparty(Long userId, Long counterpartyId, String fromAccountNumber, String toAccountNumber, Double amount) throws UserNotFoundException, CardNotFoundException, IllegalParameterException, IllegalAccountNumberException;
}
