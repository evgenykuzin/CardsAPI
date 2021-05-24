import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import ru.sberbank.kuzin19190813.view.output_pojos.POJOInterface;
import ru.sberbank.kuzin19190813.winter_framework.constants.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class TestUtils {
    public static TestResponse executeRequest(String url, HttpMethod method, Set<Header> headers, JSONObject body) {
        RequestBuilder requestBuilder = null;
        switch (method) {
            case POST:
                requestBuilder = RequestBuilder.post();
                break;
            case GET:
                requestBuilder = RequestBuilder.get();
                break;
        }
        if (requestBuilder == null) throw new IllegalArgumentException("http method is not correct (" + method + ")");
        try {
            requestBuilder.setUri(URI.create(url));
            for (Header header : headers) {
                requestBuilder.addHeader(header.getName(), header.getValue());
            }
            if (body != null) {
                requestBuilder.setEntity(new StringEntity(body.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpUriRequest request = requestBuilder.build();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build(); CloseableHttpResponse response = httpClient.execute(request)) {
            int code = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity());
            return new TestResponse(content, code);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TestResponse executeRequest(String url, HttpMethod method, Set<Header> headers) {
        return executeRequest(url, method, headers, null);
    }

    public static String pojoToString(POJOInterface pojo) {
        return pojo.toJSONObject().toString();
    }

    public static String reformatParameters(String params) {
        return params.replaceAll(" ", "%20");
    }

    @Data
    @AllArgsConstructor
    public static class TestResponse {
        String content;
        int code;
    }

    public interface BeforeInitiator {
        void doBefore();
    }
}
