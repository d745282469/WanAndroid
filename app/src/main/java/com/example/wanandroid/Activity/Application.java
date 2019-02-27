package com.example.wanandroid.Activity;

import com.example.wanandroid.Utils.L;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Application extends android.app.Application {
    private static final String TAG = "Application";
    @Override
    public void onCreate() {
        super.onCreate();

        //cookie持久化，不懂原理，cv编程
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();//其他okHttp配置也可以在这里设置
        OkHttpUtils.initClient(client);

        L.isDebug(true);
        List<Cookie> cookies = cookieJar.loadForRequest(HttpUrl.parse("http://www.wanandroid.com"));
        for (int i = 0,size = cookies.size(); i < size; i++){
            Cookie cookie = cookies.get(i);
            L.d(TAG,"WanAndroid cookie: expiresAt="+cookie.expiresAt()+",value="+cookie.value());
        }

    }
}
