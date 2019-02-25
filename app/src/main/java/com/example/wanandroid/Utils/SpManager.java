package com.example.wanandroid.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * SharedPreferences 管理工具
 * 2019年2月20日13:53:19
 */
public class SpManager {
    private SharedPreferences userInfoSp;
    private Context context;
    private final String USER_NAME = "userName";
    private final String USER_STATUS = "userStatus";
    private final String SEARCH_HISTORY = "searchHistory";

    public SpManager(Context context) {
        this.context = context;
        userInfoSp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    public String getUerName() {
        return userInfoSp.getString(USER_NAME, null);
    }

    public int getStatus() {
        //0:未登录 1：已登录
        return userInfoSp.getInt(USER_STATUS, 0);
    }

    public void saveUserName(String userName) {
        userInfoSp.edit().putString(USER_NAME, userName).apply();
    }

    public void saveStatus(int status) {
        userInfoSp.edit().putInt(USER_STATUS, status).apply();
    }

    public void saveSearchHistory(String history) {
        Set<String> strings = userInfoSp.getStringSet(SEARCH_HISTORY, null);
        if (strings == null) {
            strings = new HashSet<>();
            strings.add(history);
        }
        strings.add(history);
        userInfoSp.edit().putStringSet(SEARCH_HISTORY, strings).apply();
    }

    public Set<String> getSearchHistory() {
        return userInfoSp.getStringSet(SEARCH_HISTORY, null);
    }

    public void clearSearchHistory(){
        Set<String> strings = new HashSet<>();
        userInfoSp.edit().putStringSet(SEARCH_HISTORY, strings).apply();
    }

    public void deleteSearchHistory(Set<String> strings){
        userInfoSp.edit().putStringSet(SEARCH_HISTORY,strings).apply();
    }
}
