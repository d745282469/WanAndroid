package com.example.wanandroid.Utils;

import android.util.Log;

/**
 * 日志工具
 * 2019年2月20日14:02:30
 */
public class L {
    private static final String TAG = "Dong-Debug";
    private static boolean isDebug = true;

    public static void isDebug (Boolean isDebug){
        L.isDebug = isDebug;
        Log.v("WanAndroid","日志开关："+(isDebug?"开":"关"));
    }

    public static void v(String tag,String msg){
        if (isDebug){
            Log.v(tag,msg);
        }
    }

    public static void d(String tag,String msg){
        if (isDebug){
            Log.d(tag,msg);
        }
    }

    public static void d(String msg){
        if (isDebug){
            Log.d(TAG,msg);
        }
    }

    public static void i(String tag,String msg){
        if (isDebug){
            Log.i(tag,msg);
        }
    }

    public static void w(String tag,String msg){
        if (isDebug){
            Log.w(tag,msg);
        }
    }

    public static void e(String tag,String msg){
        if (isDebug){
            Log.e(tag,msg);
        }
    }

}
