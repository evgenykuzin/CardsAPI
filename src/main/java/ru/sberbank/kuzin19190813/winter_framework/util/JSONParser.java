package ru.sberbank.kuzin19190813.winter_framework.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JSONParser {
    public static JSONObject readRequestBody(InputStream is) {
        JSONObject result = new JSONObject();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            result = new JSONObject(sb.toString());
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
