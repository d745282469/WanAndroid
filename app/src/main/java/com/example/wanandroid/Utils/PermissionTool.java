
package com.example.wanandroid.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时权限工具类
 * dong 2019年1月5日15:27:39
 */
public class PermissionTool {
    private Context context;
    private List<String> permission_list = new ArrayList<>();
    private static GetPermissionResultListener GetPermissionResultListener;

    /**
     * 申请一系列的权限的构造器
     *
     * @param context         上下文
     * @param permission_list 权限集合
     */
    public PermissionTool(Context context, List<String> permission_list) {
        this.context = context;
        this.permission_list = permission_list;
    }

    /**
     * 只申请某个权限的构造器
     *
     * @param context    上下文
     * @param permission 权限名
     */
    public PermissionTool(Context context, String permission) {
        this.context = context;
        permission_list.add(permission);
    }

    /**
     * 执行获取权限动作
     */
    private void getPermission() {
        for (int i = 0; i < permission_list.size(); i++) {
            //循环查是否已经获取到权限
            if (ContextCompat.checkSelfPermission(context, permission_list.get(i)) == PackageManager.PERMISSION_GRANTED) {
                //将已经获取到的权限剔除
                permission_list.remove(i);
            }
        }

        if (permission_list.size() <= 0) {
            //所有权限都已经获取到了
            GetPermissionResultListener.getPermissionSuccess();
        } else {
            //将list转成数组,此时list中的所有权限都是未获取的
            String[] permission_array = new String[permission_list.size()];
            for (int i = 0; i < permission_list.size(); i++) {
                permission_array[i] = permission_list.get(i);
            }
            //开始申请权限
            ActivityCompat.requestPermissions((Activity) context, permission_array, 1111);
        }
    }

    /**
     * 暴露给外部的监听接口,失败时将返回被拒绝的权限集合
     */
    public interface GetPermissionResultListener {
        void getPermissionSuccess();

        void getPermissionFail(List<String> permissions);
    }

    /**
     * 设置监听器，只有设置了监听器的才会执行获取权限的动作
     *
     * @param getPermissionResultListenerListener 监听器
     */
    public void setGetPermissionResultListener(GetPermissionResultListener getPermissionResultListenerListener) {
        GetPermissionResultListener = getPermissionResultListenerListener;
        getPermission();
    }

    /**
     * 回调会传到Activity中，所以需要在Activity的回调中调用该方法，将后续处理交给该类
     *
     * @param requestCode  在Activity的回调中的参数，原封不动的返回即可
     * @param permissions  在Activity的回调中的参数，原封不动的返回即可
     * @param grantResults 在Activity的回调中的参数，原封不动的返回即可
     */
    public static void onResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1111) {
            if (grantResults.length > 0) {
                List<String> permissions_fail = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //被拒绝的权限
                        permissions_fail.add(permissions[i]);
                    }
                }
                if (permissions_fail.size() > 0 && GetPermissionResultListener != null) {
                    //有一个权限没被允许都将走失败的分支
                    GetPermissionResultListener.getPermissionFail(permissions_fail);
                } else if (GetPermissionResultListener != null) {
                    GetPermissionResultListener.getPermissionSuccess();
                }
            }
        }
    }
}
