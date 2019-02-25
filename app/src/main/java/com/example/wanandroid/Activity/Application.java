package com.example.wanandroid.Activity;

import com.example.wanandroid.Utils.L;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import okhttp3.OkHttpClient;

public class Application extends android.app.Application {
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
    }
}
