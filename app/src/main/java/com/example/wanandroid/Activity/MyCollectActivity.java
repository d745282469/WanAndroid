package com.example.wanandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.wanandroid.Adapter.ArticleItem;
import com.example.wanandroid.Adapter.HomeArticleAdapter;
import com.example.wanandroid.Api.WanApi;
import com.example.wanandroid.CustomView.AddCollectDialog;
import com.example.wanandroid.CustomView.WebBar;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.example.wanandroid.Utils.SpManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MyCollectActivity extends AppCompatActivity {
    private static final String TAG = "MyCollectActivity";
    private WebBar webBar;
    private RecyclerView rv_my_collect;
    private HomeArticleAdapter homeArticleAdapter;
    private NestedScrollView sv_nesed;
    private SwipeRefreshLayout refreshLayout;

    private List<ArticleItem> articleItemList;
    private Context context;
    private int currentPage = 0;
    private SpManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        context = this;
        spManager = new SpManager(context);
        initView();

        if (spManager.getStatus() == 0) {
            startActivity(new Intent(context, LoginActivity.class));
            return;
        }

        articleItemList = new ArrayList<>();
        homeArticleAdapter = new HomeArticleAdapter(articleItemList, context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_my_collect.setAdapter(homeArticleAdapter);
        rv_my_collect.setLayoutManager(layoutManager);

        GetCollectList(0);
        initEvent();
    }

    private void initView() {
        webBar = findViewById(R.id.web_bar);
        rv_my_collect = findViewById(R.id.rv_my_collect);
        sv_nesed = findViewById(R.id.sv_nested);
        refreshLayout = findViewById(R.id.refreshLayout);

        webBar.setRightIcon(getDrawable(R.drawable.collect_add_collect));
        webBar.setTitle("我的收藏");
    }

    private void initEvent() {
        sv_nesed.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView view, int x, int y, int oldX, int oldY) {
                if (y == view.getChildAt(0).getMeasuredHeight() - view.getMeasuredHeight()) {
                    //滚动到了底部
                    GetCollectList(currentPage + 1);
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetCollectList(0);
            }
        });

        webBar.setOnRightIconClickListener(new WebBar.OnRightIconClickListener() {
            @Override
            public void onClick() {
                final AddCollectDialog dialog = new AddCollectDialog(context);
                dialog.show();
                dialog.setOnSureClickListener(new AddCollectDialog.OnSureClickListener() {
                    @Override
                    public void onSureClick(String s1, String s2, String s3) {
                        WanApi.CollectArticle(s1, s2, s3, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(context,"收藏站外文章失败：网络错误",Toast.LENGTH_SHORT).show();
                                L.e(TAG,"收藏站外文章失败："+e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                L.d(TAG,"收藏站外文章回调："+response);
                                JsonObject object = new Gson().fromJson(response,JsonObject.class);
                                if (object.get("errorCode").getAsInt()!=0){
                                    Toast.makeText(context,"收藏站外文章失败："+object.get("errorMsg").getAsString(),Toast.LENGTH_SHORT).show();
                                }else {
                                    dialog.dismiss();
                                    GetCollectList(0);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void GetCollectList(final int page) {
        if (page == 0) {
            articleItemList.clear();
        }
        refreshLayout.setRefreshing(true);
        WanApi.GetMyCollectList(page, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(context, "获取收藏列表失败：网络错误", Toast.LENGTH_SHORT).show();
                L.e(TAG, "获取收藏列表失败：" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                refreshLayout.setRefreshing(false);
                L.d(TAG, "获取收藏列表回调：" + response);
                JsonObject object = new Gson().fromJson(response, JsonObject.class);
                if (object.get("errorCode").getAsInt() != 0) {
                    Toast.makeText(context, "获取收藏列表失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
                } else {
                    JsonArray array = object.getAsJsonObject("data").getAsJsonArray("datas");
                    for (int i = 0; i < array.size(); i++) {
                        ArticleItem item = new ArticleItem();
                        JsonObject objItem = array.get(i).getAsJsonObject();

                        item.setAuthor(objItem.get("author").getAsString());
                        item.setChapterId(objItem.get("chapterId").getAsInt());
                        item.setChapterName(objItem.get("chapterName").getAsString());
                        item.setCourseId(objItem.get("courseId").getAsInt());
                        item.setDesc(objItem.get("desc").getAsString());
                        item.setEnvelopePic(objItem.get("envelopePic").getAsString());
                        item.setId(objItem.get("id").getAsInt());
                        item.setOriginId(objItem.get("originId").getAsInt());
                        item.setLink(objItem.get("link").getAsString());
                        item.setNickDate(objItem.get("niceDate").getAsString());
                        item.setOrigin(objItem.get("origin").getAsString());
                        item.setPublishTime(objItem.get("publishTime").getAsInt());
                        item.setTitle(objItem.get("title").getAsString());
                        item.setUserId(objItem.get("userId").getAsInt());
                        item.setVisible(objItem.get("visible").getAsInt());
                        item.setZan(objItem.get("zan").getAsInt());
                        item.setCollect(true);
                        articleItemList.add(item);
                    }
                    homeArticleAdapter.notifyDataSetChanged();
                    currentPage = page;
                }
            }
        });
    }
}
