package ru.sberbank.kuzin19190813.view.input_pojos;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sberbank.kuzin19190813.view.output_pojos.POJOInterface;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.POJO;

@POJO
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCardPOJO implements POJOInterface {
    Long userId;
    String accountNumber;
    String cardNumber;
}
