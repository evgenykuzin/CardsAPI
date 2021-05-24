package ru.sberbank.kuzin19190813.util;

import com.sun.net.httpserver.Headers;
import ru.sberbank.kuzin19190813.db.dao.impl.TokenDAO;

public class Authorization {
    public static boolean checkToken(Headers headers) {
        return new TokenDAO().contains(headers.getFirst("Token"));
    }
}
