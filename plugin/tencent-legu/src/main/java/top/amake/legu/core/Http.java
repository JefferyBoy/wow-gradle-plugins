package top.amake.legu.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @author mxlei
 * @date 2021/9/24
 */
public class Http {
    private static Http http;
    private final OkHttpClient client;
    private final Gson gson;
    private static final String MEDIA_TYPE = "application/x-www-form-urlencoded";

    private Http() {
        client = new OkHttpClient();
        gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    }

    public static synchronized Http getInstance() {
        if (http == null) {
            http = new Http();
        }
        return http;
    }

    public Gson getGson() {
        return gson;
    }

    public <T> T post(Map<String, Object> headers, Map<String, Object> params, Type typeOfT) {
        String body = post(headers, params);
        return gson.fromJson(body, typeOfT);
    }

    public <T> T post(Map<String, Object> headers, Map<String, Object> params, Class<T> clz) {
        String body = post(headers, params);
        return gson.fromJson(body, clz);
    }

    public String post(Map<String, Object> headers, Map<String, Object> params) {
        return post(headers, httpParamsConvert(params));
    }

    public String post(Map<String, Object> headers, String body) {
        RequestBody requestBody = RequestBody.create(
            MediaType.parse(MEDIA_TYPE), body);
        Request.Builder request = new Request.Builder()
            .post(requestBody);
        if (headers != null) {
            for (String key : headers.keySet()) {
                Object val = headers.get(key);
                if (val != null) {
                    request.header(key, String.valueOf(val));
                }
            }
        }
        try {
            ResponseBody responseBody = client.newCall(request.build())
                .execute().body();
            if (responseBody != null) {
                return responseBody.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String httpParamsConvert(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            if (i != 0) {
                sb.append("&");
            }
            i++;
            sb.append(key).append("=").append(params.get(key));
        }
        return sb.toString();
    }
}
