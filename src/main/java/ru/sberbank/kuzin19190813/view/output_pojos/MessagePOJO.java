package ru.sberbank.kuzin19190813.view.output_pojos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.sberbank.kuzin19190813.winter_framework.constants.DefaultMessage;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.POJO;

@POJO
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePOJO implements POJOInterface {
    String message;

    public MessagePOJO(DefaultMessage defaultMessage) {
        this.message = defaultMessage.name();
    }

    public static MessagePOJO ok() {
        return new MessagePOJO(DefaultMessage.OK);
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }
}
