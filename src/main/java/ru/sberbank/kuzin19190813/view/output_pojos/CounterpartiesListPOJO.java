package ru.sberbank.kuzin19190813.view.output_pojos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.sberbank.kuzin19190813.model.User;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.POJO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@POJO
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CounterpartiesListPOJO implements POJOInterface {
    List<CounterpartyPOJO> counterparties;

    public CounterpartiesListPOJO(Collection<User> userList) {
        counterparties = new ArrayList<>();
        userList.forEach(user -> {
            String name = user.getUserName();
            if (name == null) name = "";
            counterparties.add(new CounterpartyPOJO(
                    user.getId(),
                    name
            ));
        });
    }

    public CounterpartiesListPOJO(List<CounterpartyPOJO> counterpartyPOJOS) {
        this.counterparties = counterpartyPOJOS;
    }

    @POJO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CounterpartyPOJO implements POJOInterface{
        Long counterpartyId;
        String name;
    }
}
