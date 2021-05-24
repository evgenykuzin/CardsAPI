package ru.sberbank.kuzin19190813.view.output_pojos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.sberbank.kuzin19190813.model.Card;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.POJO;

import java.util.List;
import java.util.stream.Collectors;

@POJO
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardsListPOJO implements POJOInterface {
    List<CardPOJO> cards;

    public CardsListPOJO(List<Card> cardEntities) {
        this.cards = cardEntities.stream()
                .map(card -> new CardPOJO(
                        card.getId(),
                        card.getAccountNumber(),
                        card.getCardNumber(),
                        card.getBalance(),
                        card.getUser().getId()
                ))
                .collect(Collectors.toList());
    }

    @POJO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CardPOJO implements POJOInterface {
        Long id;
        String accountNumber;
        String cardNumber;
        Double balance;
        Long userId;
    }
}
