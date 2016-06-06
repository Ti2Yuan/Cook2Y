package org.crazyit.cook2y.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.crazyit.cook2y.Cook2YApplication;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenti on 2016/4/22.
 */
public class HttpUtil {

    private static final OkHttpClient okHttpClient = new OkHttpClient();

    public static boolean isWIFI = true;

    static {
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(30,TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(30,TimeUnit.SECONDS);
    }

    /**
     * 访问网络，但不是异步操作
     * @param request
     * @return
     */
    public static Response execute(Request request){
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 开启异步线程访问网络
     * @param request
     * @param responCallback
     */
    public static void enqueue(Request request, Callback responCallback){
        okHttpClient.newCall(request).enqueue(responCallback);
    }

    /**
     * 查询手机WIFI状态
     * @return
     */
    public static boolean readWIFIState(){
        ConnectivityManager connectivityManager = (ConnectivityManager) Cook2YApplication.AppContext.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected()){
            isWIFI = ((connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI));
            return true;
        }else {
            return false;
        }
    }
}
