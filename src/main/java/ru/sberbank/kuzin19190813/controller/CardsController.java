package ru.sberbank.kuzin19190813.controller;

import com.sun.net.httpserver.Headers;
import ru.sberbank.kuzin19190813.exceptions.CardNotFoundException;
import ru.sberbank.kuzin19190813.exceptions.IllegalParameterException;
import ru.sberbank.kuzin19190813.model.Card;
import ru.sberbank.kuzin19190813.service.CardService;
import ru.sberbank.kuzin19190813.view.input_pojos.IncrementBalancePOJO;
import ru.sberbank.kuzin19190813.view.input_pojos.CreateCardPOJO;
import ru.sberbank.kuzin19190813.view.output_pojos.BalancePOJO;
import ru.sberbank.kuzin19190813.view.output_pojos.CardsListPOJO;
import ru.sberbank.kuzin19190813.view.output_pojos.ErrorPOJO;
import ru.sberbank.kuzin19190813.winter_framework.constants.HttpMethod;
import ru.sberbank.kuzin19190813.winter_framework.models.ResponseEntity;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.Controller;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.RestMapping;

import java.util.List;
import java.util.Map;

import static ru.sberbank.kuzin19190813.winter_framework.models.ResponseEntity.getErrorResponse;

@Controller(parentPath = "cards")
public class CardsController {

    @RestMapping(path = "create-card", method = HttpMethod.POST)
    public ResponseEntity createCard(CreateCardPOJO createCardPOJO) {
        new CardService().createCard(createCardPOJO.getUserId(), createCardPOJO.getAccountNumber(), createCardPOJO.getCardNumber());
        return ResponseEntity.getOkResponse();
    }

    public static native int get();

    @RestMapping(path = "list", method = HttpMethod.GET)
    public ResponseEntity cardsList(Map<String, String> map) {
        Long userId;
        try {
            userId = Long.valueOf(map.get("userId"));
        } catch (NumberFormatException | NullPointerException e) {
            return getErrorResponse("parameter 'userId' was missed or not correct", 400);
        }
        List<Card> cards = new CardService().getCards(userId);
        if (cards == null) return getErrorResponse(ErrorPOJO.notFound("user", userId), 404);
        return ResponseEntity.builder()
                .content(new CardsListPOJO(cards).toJSONObject())
                .statusCode(200)
                .build();
    }

    @RestMapping(path = "update-balance", method = HttpMethod.POST)
    public ResponseEntity updateBalance(IncrementBalancePOJO incrementBalancePOJO) {
        if (incrementBalancePOJO == null) return getErrorResponse(new ErrorPOJO("invalid request data"), 400);
        String accountNumber = incrementBalancePOJO.getAccountNumber();
        if (accountNumber == null || accountNumber.isEmpty()) {
            return getErrorResponse("parameter 'account_number' was missed or not correct", 400);
        }
        try {
            new CardService().updateBalance(accountNumber, incrementBalancePOJO.getAmount());
        } catch (CardNotFoundException e) {
            return getErrorResponse(ErrorPOJO.notFound("card", accountNumber), 404);
        } catch (IllegalParameterException e) {
            return getErrorResponse(ErrorPOJO.parameterError("amount"), 400);
        } catch (IllegalArgumentException e) {
            return getErrorResponse(new ErrorPOJO(e.getMessage()), 400);
        }
        return ResponseEntity.getOkResponse();
    }

    @RestMapping(path = "balance", method = HttpMethod.GET)
    public ResponseEntity balance(Map<String, String> map) {
        String accountNumber = map.get("accountNumber");
        Double balance = new CardService().getCardBalance(accountNumber);
        return ResponseEntity.builder()
                .content(new BalancePOJO(accountNumber, balance).toJSONObject())
                .statusCode(200)
                .build();
    }


}
