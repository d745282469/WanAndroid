package com.example.wanandroid.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wanandroid.Activity.WebViewActivity;
import com.example.wanandroid.Adapter.ArticleItem;
import com.example.wanandroid.Adapter.BannerItem;
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
import java.util.Objects;

import okhttp3.Call;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private View rootView;
    private ViewPager vp_banner;
    private TextView tv_banner_title;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rv_article;
    private HomeArticleAdapter homeArticleAdapter;
    private FloatingActionButton fbtn_back_top;
    private NestedScrollView sv_nested;

    private List<View> viewList;
    private List<BannerItem> bannerItemList;
    private List<ArticleItem> articleItemList;
    private Thread bannerThread;
    private final int BANNER_TIME = 2000;//轮播图时间，ms
    private boolean isScrooling = false;//是否手动滑动中
    private int currentPage = 0;
    private boolean bannerSwitch = true;//轮播图开关

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        initEvent();

        viewList = new ArrayList<>();
        bannerItemList = new ArrayList<>();
        articleItemList = new ArrayList<>();

        //首页文章列表初始化
        homeArticleAdapter = new HomeArticleAdapter(articleItemList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_article.setAdapter(homeArticleAdapter);
        rv_article.setLayoutManager(linearLayoutManager);

        //获取首页轮播图
        GetBanner();

        //获取文章列表
        GetArticleList(currentPage);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        vp_banner = rootView.findViewById(R.id.vp_banner);
        tv_banner_title = rootView.findViewById(R.id.tv_banner_title);
        refreshLayout = rootView.findViewById(R.id.refreshLayout);
        rv_article = rootView.findViewById(R.id.rv_article);
        fbtn_back_top = rootView.findViewById(R.id.fbtn_back_top);
        sv_nested = rootView.findViewById(R.id.sv_nested);

        vp_banner.setOffscreenPageLimit(3);
        vp_banner.setPageMargin(40);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        //解决滑动冲突
        vp_banner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        refreshLayout.setEnabled(false);
                        isScrooling = true;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        refreshLayout.setEnabled(true);
                        isScrooling = false;
                        break;
                }
                return false;
            }
        });

        //轮播图根据滑动来改变标题
        vp_banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_banner_title.setText(bannerItemList.get(position).getTitle());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //返回顶部
        fbtn_back_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv_nested.smoothScrollTo(0, 0);
            }
        });

        sv_nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView view, int x, int y, int oldX, int oldY) {
                if (y == view.getChildAt(0).getMeasuredHeight() - view.getMeasuredHeight()) {
                    //滚动到了底部
                    GetArticleList(currentPage + 1);
                }
            }
        });

        vp_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("title", bannerItemList.get(vp_banner.getCurrentItem()).getTitle());
                intent.putExtra("url", bannerItemList.get(vp_banner.getCurrentItem()).getWebUrl());
                intent.putExtra("collect", false);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetBanner();
                GetArticleList(0);
                bannerSwitch = false;
            }
        });
    }

    /**
     * 轮播图控制
     */
    private void BannerSlide() {
        if (bannerThread == null) {
            bannerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (bannerSwitch) {
                        try {
                            Thread.sleep(BANNER_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!isScrooling && getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                L.d(TAG,"当前index="+vp_banner.getCurrentItem()+"，总长度="+ bannerItemList.size());
                                    if (vp_banner.getCurrentItem() != bannerItemList.size() - 1) {
                                        vp_banner.setCurrentItem(vp_banner.getCurrentItem() + 1, true);
                                    } else if (vp_banner.getCurrentItem() == bannerItemList.size() - 1) {
                                        vp_banner.setCurrentItem(0, true);
                                    }
                                }
                            });
                        }
                    }
                }
            });
            bannerThread.start();
        }
    }

    /**
     * 获取首页轮播图
     */
    private void GetBanner() {
        refreshLayout.setRefreshing(true);
        WanApi.GetBanner(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "首页轮播图加载失败：网络错误", Toast.LENGTH_SHORT).show();
                L.e(TAG, "首页轮播图加载失败：网络错误" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                refreshLayout.setRefreshing(false);
                L.d(TAG, "首页轮播回调：" + response);
                JsonObject object = new Gson().fromJson(response, JsonObject.class);
                if (object.get("errorCode").getAsInt() != 0) {
                    Toast.makeText(getContext(), "首页轮播图加载失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
                } else {
                    JsonArray array = object.getAsJsonArray("data");
                    bannerItemList.clear();
                    viewList.clear();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject item = array.get(i).getAsJsonObject();

                        BannerItem bannerItem = new BannerItem();
                        bannerItem.setImgUrl(item.get("imagePath").getAsString());
                        bannerItem.setTitle(item.get("title").getAsString());
                        bannerItem.setWebUrl(item.get("url").getAsString());
                        bannerItemList.add(bannerItem);

                        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.item_home_banner, null);
                        ImageView imageView = view.findViewById(R.id.iv_img);
                        Glide.with(Objects.requireNonNull(getContext())).load(item.get("imagePath").getAsString()).into(imageView);
                        tv_banner_title.setText(array.get(0).getAsJsonObject().get("title").getAsString());

                        viewList.add(view);
                    }

                    PagerAdapter pagerAdapter = new PagerAdapter() {
                        @Override
                        public int getCount() {
                            return viewList.size();
                        }

                        @Override
                        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                            return view == object;
                        }

                        @Override
                        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                            container.removeView(viewList.get(position));
                        }

                        @NonNull
                        @Override
                        public Object instantiateItem(@NonNull ViewGroup container, int position) {
                            container.addView(viewList.get(position));
                            return viewList.get(position);
                        }
                    };
                    vp_banner.setAdapter(pagerAdapter);
                    bannerSwitch = true;
                    BannerSlide();
                }
            }
        });
    }

    /**
     * 获取首页文章列表
     *
     * @param page 页码，从0开始
     */
    private void GetArticleList(final int page) {
        if (page == 0) {
            articleItemList.clear();
        }
        refreshLayout.setRefreshing(true);
        WanApi.GetHomeArticleList(page, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "获取首页文章列表失败：网络错误", Toast.LENGTH_SHORT).show();
                L.e(TAG, "获取首页文章列表失败：" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                refreshLayout.setRefreshing(false);
                L.d(TAG, "获取首页文章列表回调：" + response);
                JsonObject object = new Gson().fromJson(response, JsonObject.class);
                if (object.get("errorCode").getAsInt() != 0) {
                    Toast.makeText(getContext(), "获取首页文章列表失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
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
