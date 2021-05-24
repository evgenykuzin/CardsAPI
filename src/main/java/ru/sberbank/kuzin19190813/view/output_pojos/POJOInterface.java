package ru.sberbank.kuzin19190813.view.output_pojos;

import com.google.gson.Gson;
import org.json.JSONObject;

public interface POJOInterface {
    default JSONObject toJSONObject() {
        return new JSONObject(new Gson().toJson(this));
    }
}
