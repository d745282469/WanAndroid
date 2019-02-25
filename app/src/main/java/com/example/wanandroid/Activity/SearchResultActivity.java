package com.example.wanandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.wanandroid.Adapter.ArticleItem;
import com.example.wanandroid.Adapter.HomeArticleAdapter;
import com.example.wanandroid.Api.WanApi;
import com.example.wanandroid.CustomView.WebBar;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SearchResultActivity extends AppCompatActivity {
    private static final String TAG = "SearchResultActivity";
    private WebBar webBar;
    private RecyclerView rv_search_result;
    private HomeArticleAdapter homeArticleAdapter;
    private NestedScrollView sv_nested;

    private Context context;
    private List<ArticleItem> articleItemList;
    private int currentPage = 0;
    private String keyword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        context = this;

        initView();
        webBar.showRightIcon(false);
        webBar.setTitle("搜索结果");

        articleItemList = new ArrayList<>();
        homeArticleAdapter = new HomeArticleAdapter(articleItemList, context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_search_result.setAdapter(homeArticleAdapter);
        rv_search_result.setLayoutManager(layoutManager);

        //获取搜索关键词，由上一个活动传来
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        Search(keyword, currentPage);

        initEvent();
    }

    private void initView() {
        webBar = findViewById(R.id.web_bar);
        rv_search_result = findViewById(R.id.rv_search_result);
        sv_nested = findViewById(R.id.sv_nested);
    }

    private void initEvent() {
        sv_nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView view, int x, int y, int oldX, int oldY) {
                if (y == view.getChildAt(0).getMeasuredHeight() - view.getMeasuredHeight()) {
                    //滚动到了底部
                    Search(keyword, currentPage + 1);
                }
            }
        });
    }

    private void Search(String keyword, final int page) {
        WanApi.Search(keyword, page, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(context, "获取搜索结果失败：网络错误", Toast.LENGTH_SHORT).show();
                L.e(TAG, "获取搜索结果失败：网络错误" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                L.d(TAG, "获取搜索结果回调：" + response);
                JsonObject object = new Gson().fromJson(response, JsonObject.class);
                if (object.get("errorCode").getAsInt() != 0) {
                    Toast.makeText(context, "获取搜索结果失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
                } else {
                    JsonArray array = object.getAsJsonObject("data").getAsJsonArray("datas");
                    for (int i = 0; i < array.size(); i++) {
                        ArticleItem item = new ArticleItem();
                        JsonObject objItem = array.get(i).getAsJsonObject();

                        item.setApkLink(objItem.get("apkLink").getAsString());
                        item.setAuthor(objItem.get("author").getAsString());
                        item.setChapterId(objItem.get("chapterId").getAsInt());
                        item.setChapterName(objItem.get("chapterName").getAsString());
                        item.setCollect(objItem.get("collect").getAsBoolean());
                        item.setCourseId(objItem.get("courseId").getAsInt());
                        item.setDesc(objItem.get("desc").getAsString());
                        item.setEnvelopePic(objItem.get("envelopePic").getAsString());
                        item.setFresh(objItem.get("fresh").getAsBoolean());
                        item.setId(objItem.get("id").getAsInt());
                        item.setLink(objItem.get("link").getAsString());
                        item.setNickDate(objItem.get("niceDate").getAsString());
                        item.setOrigin(objItem.get("origin").getAsString());
                        item.setProjectLink(objItem.get("projectLink").getAsString());
                        item.setPublishTime(objItem.get("publishTime").getAsInt());
                        item.setSuperChapterId(objItem.get("superChapterId").getAsInt());
                        item.setSuperChapterName(objItem.get("superChapterName").getAsString());
                        item.setTitle(objItem.get("title").getAsString());
                        item.setType(objItem.get("type").getAsInt());
                        item.setUserId(objItem.get("userId").getAsInt());
                        item.setVisible(objItem.get("visible").getAsInt());
                        item.setZan(objItem.get("zan").getAsInt());
                        articleItemList.add(item);
                    }
                    homeArticleAdapter.notifyDataSetChanged();
                    currentPage = page;
                }
            }
        });
    }
}
