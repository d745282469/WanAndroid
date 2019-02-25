package com.example.wanandroid.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.wanandroid.Api.WanApi;
import com.example.wanandroid.CustomView.WebBar;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.example.wanandroid.Utils.SpManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    private WebBar webBar;
    private WebView webView;
    private SwipeRefreshLayout refreshLayout;

    private String title = null, url = null,author = null;
    private boolean collect = false;
    private int originId = -1,id = -1;
    private Context context;
    private SpManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        context = this;
        spManager = new SpManager(context);

        initView();
        initWebView();

        //获取标题、url、收藏、作者、文章id
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        author = intent.getStringExtra("author");
        originId = intent.getIntExtra("originId",-1);//站内文章的id
        id = intent.getIntExtra("id",-1);//从收藏列表跳转过来时携带的收藏列表中的id
        collect = intent.getBooleanExtra("collect", false);

        if (id == 0 || id == -1){
            collect = false;
        }else {
            collect = true;
        }
        changeCollect(collect);
        webView.loadUrl(url);

        initEvent();
    }

    private void initView() {
        webBar = findViewById(R.id.web_bar);
        webView = findViewById(R.id.web_view);
        refreshLayout = findViewById(R.id.refreshLayout);
    }

    private void initEvent(){
        webBar.setOnRightIconClickListener(new WebBar.OnRightIconClickListener() {
            @Override
            public void onClick() {
                if (spManager.getStatus() == 0){
                    startActivity(new Intent(context,LoginActivity.class));
                    return;
                }

                if (collect){
                    //取消收藏
                    if (originId != -1){
                        //该文章是站内文章
                        WanApi.CancelCollect_1(originId, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(context,"取消收藏失败：网络错误",Toast.LENGTH_SHORT).show();
                                L.e(TAG,"取消收藏失败："+e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                L.d(TAG,"取消收藏回调："+response);
                                JsonObject object = new Gson().fromJson(response,JsonObject.class);
                                if (object.get("errorCode").getAsInt() != 0){
                                    Toast.makeText(context,"取消收藏失败："+object.get("errorMsg").getAsString(),Toast.LENGTH_SHORT).show();
                                }else {
                                    changeCollect(false);
                                }
                            }
                        });
                    }else {
                        //从收藏列表跳入的已收藏的站外文章
                        WanApi.CanccelCollect_2(id, originId, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(context,"取消收藏失败：网络错误",Toast.LENGTH_SHORT).show();
                                L.e(TAG,"取消收藏失败："+e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                L.d(TAG,"取消收藏回调："+response);
                                JsonObject object = new Gson().fromJson(response,JsonObject.class);
                                if (object.get("errorCode").getAsInt() != 0){
                                    Toast.makeText(context,"取消收藏失败："+object.get("errorMsg").getAsString(),Toast.LENGTH_SHORT).show();
                                }else {
                                    changeCollect(false);
                                }
                            }
                        });
                    }
                }else {
                    //添加收藏
                    if (originId!=-1){
                        //站内文章
                        WanApi.CollectWanArticle(originId, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(context,"收藏站内文章失败：网络错误",Toast.LENGTH_SHORT).show();
                                L.e(TAG,"收藏站内文章失败："+e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                L.d(TAG,"收藏站内文章回调："+response);
                                JsonObject object = new Gson().fromJson(response,JsonObject.class);
                                if (object.get("errorCode").getAsInt()!= 0){
                                    Toast.makeText(context,"收藏站内文章失败："+object.get("errorMsg").getAsString(),Toast.LENGTH_SHORT).show();
                                }else {
                                    changeCollect(true);
                                }
                            }
                        });
                    }else {
                        //站外文章
                        Toast.makeText(context,"收藏站外文章功能暂无",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(url);
            }
        });
    }

    /**
     * 改变收藏状态
     * @param b 收藏/取消收藏
     */
    private void changeCollect(boolean b){
        collect = b;
        if (b){
            webBar.setRightIcon(context.getDrawable(R.drawable.home_article_like_act));
        }else {
            webBar.setRightIcon(context.getDrawable(R.drawable.home_article_like_def));
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        //拦截超链接事件，在该webView中打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                refreshLayout.setRefreshing(true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                refreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                webBar.setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
    }
}
