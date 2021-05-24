package ru.sberbank.kuzin19190813.winter_framework.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.JSONObject;
import ru.sberbank.kuzin19190813.view.output_pojos.ErrorPOJO;
import ru.sberbank.kuzin19190813.view.output_pojos.MessagePOJO;

import java.util.Map;

@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ResponseEntity {
    JSONObject content;
    Map<String, String> headers;
    int statusCode;

    public static ResponseEntity getErrorResponse(ErrorPOJO errorPOJO, int code) {
        return ResponseEntity.builder()
                .content(errorPOJO.toJSONObject())
                .statusCode(code)
                .build();
    }

    public static ResponseEntity getErrorResponse(String message, int code) {
        return getErrorResponse(new ErrorPOJO(message), code);
    }

    public static ResponseEntity getMessageResponse(MessagePOJO messagePOJO, int code) {
        return ResponseEntity.builder()
                .content(messagePOJO.toJSONObject())
                .statusCode(code)
                .build();
    }

    public static ResponseEntity getMessageResponse(String message, int code) {
        return getMessageResponse(new MessagePOJO(message), code);
    }

    public static ResponseEntity getOkResponse() {
        return getMessageResponse(MessagePOJO.ok(), 200);
    }

    public static ResponseEntity getInvalidTokenResponse() {
        return getErrorResponse(ErrorPOJO.invalidToken(), 403);
    }
}
