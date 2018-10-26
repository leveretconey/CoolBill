package Share;


import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    interface HttpCallbackListener{
        void onFinish();
        void onError();
    }

    public static void sendOKHttpRequestGet(String url, HashMap<String,String> headers
            , Callback callback){
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
        Request.Builder reqBuilder=new Request.Builder()
                .get()
                .url(url);
        for (HashMap.Entry<String,String> entry :headers.entrySet() ){
            reqBuilder.addHeader(entry.getKey(),entry.getValue());
        }
        client.newCall(reqBuilder.build()).enqueue(callback);
    }

    public static void sendOKHttpRequestPOST(String url,String body, Callback callback){
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(800, TimeUnit.MILLISECONDS)
                .build();
        RequestBody requestBody=RequestBody.create(null,body);
        Request request=new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
