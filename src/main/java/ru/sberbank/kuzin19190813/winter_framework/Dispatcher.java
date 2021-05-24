package ru.sberbank.kuzin19190813.winter_framework;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;
import ru.sberbank.kuzin19190813.util.Authorization;
import ru.sberbank.kuzin19190813.view.output_pojos.ErrorPOJO;
import ru.sberbank.kuzin19190813.winter_framework.constants.HttpMethod;
import ru.sberbank.kuzin19190813.winter_framework.exceptions.IllegalResultType;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.Controller;
import ru.sberbank.kuzin19190813.winter_framework.models.ResponseEntity;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.POJO;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.RestMapping;
import ru.sberbank.kuzin19190813.winter_framework.util.AnnotationManager;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hibernate.internal.util.io.StreamCopier.BUFFER_SIZE;
import static ru.sberbank.kuzin19190813.winter_framework.models.ResponseEntity.getErrorResponse;
import static ru.sberbank.kuzin19190813.winter_framework.util.JSONParser.readRequestBody;
import static ru.sberbank.kuzin19190813.winter_framework.util.URLParser.parseParameters;

public class Dispatcher {

    public static void dispatch(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        if (path.lastIndexOf("/") != path.length() - 1) path += "/";
        ResponseEntity responseEntity = defaultErrorResponse("unknown error");
        HttpMethod requestHttpMethod = HttpMethod.valueOf(HttpMethod.class, httpExchange.getRequestMethod());
        Headers headers = httpExchange.getRequestHeaders();
        switch (requestHttpMethod) {
            case GET:
                responseEntity = get(path, parseParameters(httpExchange.getRequestURI().getQuery()), headers);
                break;
            case POST:
                responseEntity = post(path, readRequestBody(httpExchange.getRequestBody()), headers);
                break;
        }
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(responseEntity.getStatusCode(), responseEntity.getContent().toString().length());
            try (ByteArrayInputStream bis = new ByteArrayInputStream(responseEntity.getContent().toString().getBytes(StandardCharsets.UTF_8))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int count;
                while ((count = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpExchange.close();
    }

    public static <T> ResponseEntity execute(String path, HttpMethod httpMethod, T requestData, Headers headers) {
        try {
            if (!Authorization.checkToken(headers)) {
                return ResponseEntity.getInvalidTokenResponse();
            }
            List<Class<?>> classes = AnnotationManager.findClassesByAnnotation(WinterConfigurationHolder.getConfiguration().getControllersPackageToScan(), Controller.class);
            for (Class<?> clazz : classes) {
                Controller controller = clazz.getAnnotation(Controller.class);
                if (controller == null) continue;
                String parentPath = controller.parentPath();
                if (!path.contains(parentPath)) continue;
                Method method = findRestMethod(path, httpMethod, clazz, parentPath);
                if (method == null) continue;
                Parameter[] methodParameters = method.getParameters();
                if (methodParameters.length <= 0) {
                    return invoke(method, clazz);
                }
                Object data = null;
                for (Parameter parameter : methodParameters) {
                    Class<?> type = parameter.getType();
                    if (type.isInstance(requestData)) {
                        data = requestData;
                    } else if (type.getAnnotation(POJO.class) != null) {
                        try {
                            data = new Gson().fromJson(requestData.toString(), type);
                        } catch (Throwable t) {
                            return getErrorResponse(new ErrorPOJO("invalid request data: " + t.toString()), 400);
                        }
                    }
                }
                return invoke(method, clazz, data);
            }
            return defaultNotFoundResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultErrorResponse(e.getCause().getMessage());
        }
    }

    public static ResponseEntity get(String path, Map<String, String> params, Headers headers) {
        return execute(path, HttpMethod.GET, params, headers);
    }

    public static ResponseEntity post(String path, JSONObject jsonObject, Headers headers) {
        return execute(path, HttpMethod.POST, jsonObject, headers);
    }

    private static Method findRestMethod(String path, HttpMethod httpMethod, Class<?> controllerClass, String parentPath) {
        return AnnotationManager.findMethodByAnnotation(controllerClass, RestMapping.class, annotation -> {
            RestMapping restMapping = annotation instanceof RestMapping ? ((RestMapping) annotation) : null;
            if (restMapping == null) return false;
            String restMappingPath = restMapping.path();
            String fullMethodPath;
            if (parentPath == null || parentPath.isEmpty()) {
                fullMethodPath = String.format("/%s/", restMappingPath);
            } else {
                fullMethodPath = String.format("/%s/%s/", parentPath, restMapping.path());
            }
            return fullMethodPath.equals(path) && restMapping.method().equals(httpMethod);
        });
    }

    private static ResponseEntity defaultErrorResponse(String message) {
        return ResponseEntity.getErrorResponse(message, 500);
    }

    private static ResponseEntity defaultNotFoundResponse() {
        return ResponseEntity.getErrorResponse("404 not found", 404);
    }

    private static ResponseEntity invoke(Method method, Class<?> clazz, Object... args) throws IllegalAccessException, InstantiationException, InvocationTargetException, IllegalResultType {
        Object result = method.invoke(clazz.newInstance(), args);
        if (result instanceof ResponseEntity) {
            return (ResponseEntity) result;
        } else {
            throw new IllegalResultType("must be ResponseEntity");
        }
    }

}
