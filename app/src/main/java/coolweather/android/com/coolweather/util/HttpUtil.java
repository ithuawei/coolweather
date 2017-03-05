package coolweather.android.com.coolweather.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 用于请求网络数据,并回调会结果
 */
public class HttpUtil {
    public static void sendOkhttpRequest(String adressUrl, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(adressUrl).build();

        client.newCall(request).enqueue(callback);
    }
}
