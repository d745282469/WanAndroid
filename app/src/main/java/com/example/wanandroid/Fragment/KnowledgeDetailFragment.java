package com.example.wanandroid.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wanandroid.Adapter.ArticleItem;
import com.example.wanandroid.Adapter.HomeArticleAdapter;
import com.example.wanandroid.Api.WanApi;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class KnowledgeDetailFragment extends Fragment {
    private static final String TAG = "KnowledgeDetailFragment";
    private View rootView;
    private RecyclerView rv_project;
    private HomeArticleAdapter homeArticleAdapter;
    private NestedScrollView sv_nested;
    private FloatingActionButton fbtn_back_top;

    private List<ArticleItem> articleItemList;
    private int currentPage = 0;
    private Context context;
    private int chapterId = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Bundle bundle = getArguments();
        if (bundle != null) {
            chapterId = bundle.getInt("id");
            L.d(TAG,"要加载的知识分类id="+chapterId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_project_detail, container, false);

        initView();
        initEvent();

        articleItemList = new ArrayList<>();
        homeArticleAdapter = new HomeArticleAdapter( articleItemList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_project.setLayoutManager(layoutManager);
        rv_project.setAdapter(homeArticleAdapter);

        GetKnowledgeArticle(chapterId, currentPage);

        return rootView;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rv_project = rootView.findViewById(R.id.rv_project);
        sv_nested = rootView.findViewById(R.id.sv_nested);
        fbtn_back_top = rootView.findViewById(R.id.fbtn_back_top);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        sv_nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView view, int x, int y, int oldX, int oldY) {
                if (y == view.getChildAt(0).getMeasuredHeight() - view.getMeasuredHeight()) {
                    //滑动到底部
                    GetKnowledgeArticle(chapterId, currentPage + 1);
                }
            }
        });

        fbtn_back_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv_nested.smoothScrollTo(0,0);
            }
        });
    }

    /**
     * 获取某个项目分类下的文章
     *
     * @param id   项目分类id
     * @param page 页码
     */
    private void GetKnowledgeArticle(int id, final int page) {
        WanApi.GetKnowledgeArticle(id, page, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(context, "获取知识体系文章失败：网络错误", Toast.LENGTH_SHORT).show();
                L.e(TAG, "获取知识体系文章失败：网络错误" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                L.d(TAG, "获取知识体系文章回调：" + response);
                JsonObject object = new Gson().fromJson(response, JsonObject.class);
                if (object.get("errorCode").getAsInt() != 0) {
                    Toast.makeText(context, "获取知识体系文章失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
