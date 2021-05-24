package ru.sberbank.kuzin19190813.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@ToString(exclude = "user")
@Data
@Entity(name = "CARD")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "CARD", indexes = {
        @Index(columnList = "id", name = "card_id_hidx"),
        @Index(columnList = "account_number", name = "card_account_number_hidx"),
})
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "card_number")
    String cardNumber;

    @Column(name = "account_number")
    String accountNumber;

    @Column(nullable = false, columnDefinition = "int default 0")
    Double balance = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name="user_id")
    User user;
}
