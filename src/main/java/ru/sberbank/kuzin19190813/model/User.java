package ru.sberbank.kuzin19190813.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@ToString(exclude = {"counterparties", "counterpartyOf"})
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "SBERUSER")
public class User {
    private static final String counterpartiesTableName = "COUNTERPARTIES";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_name")
    String userName;

    String token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Card> cards;

    @ManyToMany
    @JoinTable(
            name = counterpartiesTableName,
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="counterparty_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"counterparty_id"})}
    )
    List<User> counterparties;

    @ManyToMany
    @JoinTable(name = counterpartiesTableName,
            joinColumns=@JoinColumn(name="counterparty_id"),
            inverseJoinColumns=@JoinColumn(name="user_id")
    )
    private List<User> counterpartyOf;

}
