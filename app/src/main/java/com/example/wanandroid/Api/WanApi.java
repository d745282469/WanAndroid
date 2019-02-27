package com.example.wanandroid.Api;

import com.example.wanandroid.Utils.L;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class WanApi {
    private final static String baseUrl = "http://www.wanandroid.com";

    /**
     * 登陆
     * 对于cookie，会持久化存储，具体如何实现的我也不知道，cv编程，在Application中配置的
     *
     * @param userName       用户名
     * @param password       密码
     * @param stringCallback 回调
     */
    public static void Login(String userName, String password, StringCallback stringCallback) {
        String loginUrl = "/user/login";
        OkHttpUtils.post()
                .url(baseUrl + loginUrl)
                .addParams("username", userName)
                .addParams("password", password)
                .build()
                .execute(stringCallback);
    }

    /**
     * 注册
     *
     * @param userName       用户名
     * @param password       密码
     * @param rePassword     确认密码
     * @param stringCallback 回调
     */
    public static void Reg(String userName, String password, String rePassword, StringCallback stringCallback) {
        String regUrl = "/user/register";
        OkHttpUtils.post()
                .url(baseUrl + regUrl)
                .addParams("username", userName)
                .addParams("password", password)
                .addParams("repassword", rePassword)
                .build()
                .execute(stringCallback);
    }

    /**
     * 退出登陆
     * 理论上会自动清理cookie
     *
     * @param stringCallback 回调
     */
    public static void Logout(StringCallback stringCallback) {
        String logoutUrl = "/user/logout/json";
        OkHttpUtils.get()
                .url(baseUrl + logoutUrl)
                .build()
                .execute(stringCallback);
    }

    /**
     * 首页轮播图
     *
     * @param stringCallback 回调
     */
    public static void GetBanner(StringCallback stringCallback) {
        String bannerUrl = "/banner/json";
        OkHttpUtils.get()
                .url(baseUrl + bannerUrl)
                .build()
                .execute(stringCallback);
    }

    /**
     * 首页文章列表
     *
     * @param page           页码，从0开始
     * @param stringCallback 回调
     */
    public static void GetHomeArticleList(int page, StringCallback stringCallback) {
        String homeArticleUrl = "/article/list/" + page + "/json";
        OkHttpUtils.get()
                .url(baseUrl + homeArticleUrl)
                .build()
                .execute(stringCallback);
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        L.d(client.cookieJar().loadForRequest(HttpUrl.parse(baseUrl)).toString());
    }

    /**
     * 获取某个项目分类下的所有项目文章
     *
     * @param id             项目分类id
     * @param page           页码，从1开始
     * @param stringCallback 回调
     */
    public static void GetProjectList(int id, int page, StringCallback stringCallback) {
        String url = "/project/list/" + page + "/json?cid=" + id;
        OkHttpUtils.get()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 获取项目分类列表
     *
     * @param stringCallback 回调
     */
    public static void GetProjectTree(StringCallback stringCallback) {
        String url = "/project/tree/json";
        OkHttpUtils.get()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 获取知识体系树列表
     *
     * @param stringCallback 回调
     */
    public static void GetKnowledgeTree(StringCallback stringCallback) {
        String url = "/tree/json";
        OkHttpUtils.get()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 获取某个知识体系分类下的所有文章
     *
     * @param page           页码，0开始
     * @param id             分类的id
     * @param stringCallback 回调
     */
    public static void GetKnowledgeArticle(int id, int page, StringCallback stringCallback) {
        String url = "/article/list/" + page + "/json?cid=" + id;
        OkHttpUtils.get()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 获取公众号列表
     *
     * @param stringCallback 回调
     */
    public static void GetGzhList(StringCallback stringCallback) {
        String url = "/wxarticle/chapters/json";
        OkHttpUtils.get()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 获取公众号下面的文章
     *
     * @param id             公众号id
     * @param page           页码
     * @param stringCallback 回调
     */
    public static void GetGzhArticle(int id, int page, StringCallback stringCallback) {
        String url = "/wxarticle/list/" + id + "/" + page + "/json";
        OkHttpUtils.get()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 获取搜索热词
     *
     * @param stringCallback 回调
     */
    public static void GetHotKey(StringCallback stringCallback) {
        String url = "/hotkey/json";
        OkHttpUtils.get()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 搜索
     *
     * @param keyword        关键词
     * @param page           页码，从0开始
     * @param stringCallback 回调
     */
    public static void Search(String keyword, int page, StringCallback stringCallback) {
        String url = "/article/query/" + page + "/json";
        OkHttpUtils.post()
                .url(baseUrl + url)
                .addParams("k", keyword)
                .build()
                .execute(stringCallback);
    }

    /**
     * 收藏站外文章
     *
     * @param title          标题
     * @param author         作者
     * @param link           文章链接
     * @param stringCallback 回调
     */
    public static void CollectArticle(String title, String author, String link, StringCallback stringCallback) {
        String url = "/lg/collect/add/json";
        OkHttpUtils.post()
                .url(baseUrl + url)
                .addParams("title", title)
                .addParams("author", author)
                .addParams("link", link)
                .build()
                .execute(stringCallback);
    }

    /**
     * 收藏站内文章
     *
     * @param id             站内文章id
     * @param stringCallback 回调
     */
    public static void CollectWanArticle(int id, StringCallback stringCallback) {
        String url = "/lg/collect/" + id + "/json";
        OkHttpUtils.post()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 获取收藏文章列表
     *
     * @param page           页码，从0开始
     * @param stringCallback 回调
     */
    public static void GetMyCollectList(int page, StringCallback stringCallback) {
        String url = "/lg/collect/list/" + page + "/json";
        OkHttpUtils.get()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        L.d(client.cookieJar().loadForRequest(HttpUrl.parse(baseUrl)).toString());
    }

    /**
     * 在文章列表处、即文章界面取消收藏
     *
     * @param id             文章的id
     * @param stringCallback 回调
     */
    public static void CancelCollect_1(int id, StringCallback stringCallback) {
        String url = "/lg/uncollect_originId/" + id + "/json";
        OkHttpUtils.post()
                .url(baseUrl + url)
                .build()
                .execute(stringCallback);
    }

    /**
     * 在我的收藏处取消收藏，该处可能存在自己收藏的站外文章
     * 所以需要传两个参数
     *
     * @param id             该收藏的id
     * @param originId       如果是站内文章则为站内文章的id，如果是站外文章则为-1
     * @param stringCallback 回调
     */
    public static void CanccelCollect_2(int id, int originId, StringCallback stringCallback) {
        String url = "/lg/uncollect/" + id + "/json";
        OkHttpUtils.post()
                .url(baseUrl + url)
                .addParams("originId", String.valueOf(originId))
                .build()
                .execute(stringCallback);
    }
}
