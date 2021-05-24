package ru.sberbank.kuzin19190813.controller;

import ru.sberbank.kuzin19190813.exceptions.*;
import ru.sberbank.kuzin19190813.model.User;
import ru.sberbank.kuzin19190813.service.CounterpartyService;
import ru.sberbank.kuzin19190813.view.input_pojos.AddCounterpartyPOJO;
import ru.sberbank.kuzin19190813.view.input_pojos.PaymentPOJO;
import ru.sberbank.kuzin19190813.view.output_pojos.CounterpartiesListPOJO;
import ru.sberbank.kuzin19190813.view.output_pojos.ErrorPOJO;
import ru.sberbank.kuzin19190813.view.output_pojos.MessagePOJO;
import ru.sberbank.kuzin19190813.winter_framework.constants.HttpMethod;
import ru.sberbank.kuzin19190813.winter_framework.models.ResponseEntity;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.Controller;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.RestMapping;

import java.util.List;
import java.util.Map;

@Controller(parentPath = "counterparty")
public class CounterpartyController {

    @RestMapping(path = "add", method = HttpMethod.POST)
    public ResponseEntity addCounterparty(AddCounterpartyPOJO addCounterPartyPOJO) {
        if (addCounterPartyPOJO == null) return ResponseEntity.getErrorResponse("invalid request data", 400);
        Long userId = addCounterPartyPOJO.getUserId();
        Long counterpartyId = addCounterPartyPOJO.getCounterpartyId();
        if (userId == null || counterpartyId == null) return ResponseEntity.getErrorResponse(ErrorPOJO.parameterError("userId or counterpartyId"), 400);
        try {
            new CounterpartyService().addCounterparty(userId, counterpartyId);
        } catch (UserNotFoundException e) {
            return ResponseEntity.getErrorResponse(ErrorPOJO.notFound("user or counterparty",
                    String.format(" %s and %s ", userId, counterpartyId)), 400);
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.getMessageResponse(new MessagePOJO("counterparty already exists"), 200);
        }
        return ResponseEntity.getOkResponse();
    }

    @RestMapping(path = "list", method = HttpMethod.GET)
    public ResponseEntity getCounterparties(Map<String, String> params) {
        String userIdStr = params.get("userId");
        if (userIdStr == null) return ResponseEntity.getErrorResponse("parameter userId is missed", 400);
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
            List<User> counterparties = new CounterpartyService().getCounterparties(userId);
            return ResponseEntity.builder()
                    .content(new CounterpartiesListPOJO(counterparties).toJSONObject())
                    .statusCode(200)
                    .build();
        } catch (NumberFormatException e) {
            return ResponseEntity.getErrorResponse(ErrorPOJO.parameterError("userId"), 400);
        } catch (UserNotFoundException e) {
            return ResponseEntity.getErrorResponse(ErrorPOJO.notFound("user", userId), 404);
        }
    }

    @RestMapping(path = "send-payment", method = HttpMethod.POST)
    public ResponseEntity sendPayment(PaymentPOJO paymentPOJO) {
        Long userId = paymentPOJO.getUserId();
        Long counterpartyId = paymentPOJO.getCounterpartyId();
        String toAccountNumber = paymentPOJO.getToAccountNumber();
        String fromAccountNumber = paymentPOJO.getFromAccountNumber();
        Double amount = paymentPOJO.getAmount();
        try {
            new CounterpartyService().sendPaymentToCounterparty(userId, counterpartyId, fromAccountNumber, toAccountNumber, amount);
        } catch (UserNotFoundException | CardNotFoundException e) {
            return ResponseEntity.getErrorResponse(e.getMessage(), 404);
        } catch (IllegalParameterException | IllegalAccountNumberException e) {
            return ResponseEntity.getErrorResponse(e.getMessage(), 400);
        }
        return ResponseEntity.getOkResponse();
    }

}
