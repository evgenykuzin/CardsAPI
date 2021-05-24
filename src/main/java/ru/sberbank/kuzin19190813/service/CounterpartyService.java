package ru.sberbank.kuzin19190813.service;

import org.hibernate.exception.ConstraintViolationException;
import ru.sberbank.kuzin19190813.db.dao.DAOHolder;
import ru.sberbank.kuzin19190813.exceptions.*;
import ru.sberbank.kuzin19190813.model.User;

import javax.persistence.PersistenceException;
import java.util.List;

public class CounterpartyService implements CounterpartyServiceInterface {

    public void addCounterparty(Long userId, Long counterpartyId) throws UserNotFoundException, EntityAlreadyExistsException {
        User user = DAOHolder.getUserDAO().getWithCounterparties(userId);
        User counterparty = DAOHolder.getUserDAO().getWithCounterpartyOf(counterpartyId);
        if (user == null || counterparty == null) {
            throw new UserNotFoundException("user or counterparty not found");
        }
        user.getCounterparties().add(counterparty);
        counterparty.getCounterpartyOf().add(user);
        try {
            DAOHolder.getUserDAO().update(user);
        } catch (PersistenceException e) {
            if (!e.getCause().getClass().equals(ConstraintViolationException.class)) {
                e.printStackTrace();
            } else {
                throw new EntityAlreadyExistsException("counterparty already exists");
            }
        }
    }

    public List<User> getCounterparties(Long userId) throws UserNotFoundException {
        User user = DAOHolder.getUserDAO().getWithCounterparties(userId);
        if (user == null) throw new UserNotFoundException();
        return user.getCounterparties();
    }

    public void sendPaymentToCounterparty(Long userId, Long counterpartyId, String fromAccountNumber, String toAccountNumber, Double amount) throws UserNotFoundException, CardNotFoundException, IllegalParameterException, IllegalAccountNumberException {
        User user = DAOHolder.getUserDAO().getWithCounterparties(userId);
        if (user == null) throw new UserNotFoundException();
        User counterparty = DAOHolder.getUserDAO().get(counterpartyId);
        if (counterparty == null) throw new UserNotFoundException("counterparty not found");
        if (user.getCounterparties().stream().noneMatch(c -> c.getId().equals(counterparty.getId())))
            throw new UserNotFoundException(String.format("user with id %s is not counterparty of user with id %s", counterpartyId, userId));
        if (counterparty.getCards().stream().noneMatch(card -> card.getAccountNumber().equals(toAccountNumber))) {
            throw new IllegalAccountNumberException(String.format("counterparty has no access to this 'to' account number (%s)", toAccountNumber));
        }
        if (user.getCards().stream().noneMatch(card -> card.getAccountNumber().equals(fromAccountNumber))) {
            throw new IllegalAccountNumberException(String.format("user has no access to this 'from' account number (%s)", fromAccountNumber));
        }
        new CardService().updateBalance(fromAccountNumber, -1*amount);
        new CardService().updateBalance(toAccountNumber, amount);
    }
}
