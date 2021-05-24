package ru.sberbank.kuzin19190813.view.input_pojos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.sberbank.kuzin19190813.view.output_pojos.POJOInterface;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.POJO;

@POJO
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncrementBalancePOJO implements POJOInterface {
    String accountNumber;
    Double amount;
}
