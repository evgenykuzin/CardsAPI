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
public class ErrorPOJO implements POJOInterface {
    String error;

    public ErrorPOJO(DefaultMessage defaultMessage) {
        this.error = defaultMessage.name();
    }

    public static ErrorPOJO parameterError(String parameterName) {
        return new ErrorPOJO(String.format("parameter '%s' was missed or not correct", parameterName));
    }

    public static ErrorPOJO notFound(String what, Object by) {
        return new ErrorPOJO(String.format("%s by key %s not found", what, by.toString()));
    }

    public static ErrorPOJO invalidToken() {
        return new ErrorPOJO("invalid token");
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }
}
