package Util;


import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    interface HttpCallbackListener{
        void onFinish();
        void onError();
    }

    public static void sendOKHttpRequest(String url, Callback callback){
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(800, TimeUnit.MILLISECONDS)
                .build();
        Request request=new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
