package com.example.wanandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.internal.FlowLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanandroid.Adapter.SearchHistoryAdapter;
import com.example.wanandroid.Api.WanApi;
import com.example.wanandroid.CustomView.InputBar;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.example.wanandroid.Utils.SpManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import okhttp3.Call;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private InputBar inputBar;
    private FlowLayout fl_hot_key;
    private RecyclerView rv_search_history;
    private SearchHistoryAdapter searchHistoryAdapter;
    private TextView tv_clear_all;

    private Context context;
    private List<String> searchHistoryList;
    private Set<String> searchHistorySet;
    private SpManager spManager;
    //热词的字体颜色组
    private static Integer[] colors = {Color.parseColor("#757575"), Color.parseColor("#242524"), Color.parseColor("#49617e"),
            Color.parseColor("#965e75"), Color.parseColor("#3b9a58"), Color.parseColor("#05596e"),
            Color.parseColor("#943e4f"), Color.parseColor("#0a5d17")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        spManager = new SpManager(context);
        initView();

        //获取历史搜索
        searchHistorySet = spManager.getSearchHistory();
        searchHistoryList = new ArrayList<>();
        if (searchHistorySet == null) {
            searchHistorySet = new HashSet<>();
        }
        searchHistoryList.addAll(searchHistorySet);

        searchHistoryAdapter = new SearchHistoryAdapter(context, searchHistoryList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_search_history.setLayoutManager(layoutManager);
        rv_search_history.setAdapter(searchHistoryAdapter);

        GetHotKey();
        initEvent();
    }

    private void initView() {
        inputBar = findViewById(R.id.inputBar);
        fl_hot_key = findViewById(R.id.fl_hot_key);
        rv_search_history = findViewById(R.id.rv_search_history);
        tv_clear_all = findViewById(R.id.tv_clear_all);
    }

    private void initEvent() {
        inputBar.setOnRightIconClickListener(new InputBar.OnRightIconClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(context, SearchResultActivity.class);
                intent.putExtra("keyword", inputBar.getContent());
                startActivity(intent);
                newSearchHistory(inputBar.getContent());
            }
        });

        //清除所有历史
        tv_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchHistorySet.clear();
                searchHistoryList.clear();
                spManager.clearSearchHistory();
                searchHistoryAdapter.notifyDataSetChanged();
            }
        });

        searchHistoryAdapter.setOnChildViewClickListener(new SearchHistoryAdapter.OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View view, int position) {
                searchHistoryList.remove(position);//从List中移除
                searchHistorySet.clear();//清空set
                searchHistorySet.addAll(searchHistoryList);//将移除后的list全部加入set中
                spManager.deleteSearchHistory(searchHistorySet);
                searchHistoryAdapter.notifyDataSetChanged();
            }
        });

        searchHistoryAdapter.setOnItemClickListener(new SearchHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context,SearchResultActivity.class);
                intent.putExtra("keyword",searchHistoryList.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * 新增一个搜索历史，加入set集合后，清空list集合，再将set集合全部加入list集合
     * 这样子可以保证不出现重复的历史记录
     *
     * @param history 搜索历史
     */
    private void newSearchHistory(String history) {
        searchHistorySet.add(history);
        searchHistoryList.clear();
        searchHistoryList.addAll(searchHistorySet);
        spManager.saveSearchHistory(history);
        searchHistoryAdapter.notifyDataSetChanged();
    }

    private void GetHotKey() {
        WanApi.GetHotKey(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(context, "获取搜索热词失败：网络错误", Toast.LENGTH_SHORT).show();
                L.e(TAG, "获取搜索热词列表：网络错误" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                L.d(TAG, "获取搜索热词回调：" + response);
                JsonObject object = new Gson().fromJson(response, JsonObject.class);
                if (object.get("errorCode").getAsInt() != 0) {
                    Toast.makeText(context, "获取搜索热词失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
                } else {
                    JsonArray array = object.getAsJsonArray("data");
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject itemObj = array.get(i).getAsJsonObject();
                        final String word = itemObj.get("name").getAsString();
                        Random random = new Random();

                        //新建标签
                        View view = LayoutInflater.from(context).inflate(R.layout.item_hot_key, null);
                        TextView textView = view.findViewById(R.id.tv_tag);
                        textView.setText(word);
                        textView.setTextColor(colors[random.nextInt(colors.length)]);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //搜索热词标签点击事件
                                Intent intent = new Intent(context, SearchResultActivity.class);
                                intent.putExtra("keyword", word);
                                startActivity(intent);
                                newSearchHistory(word);
                            }
                        });
                        fl_hot_key.addView(view);
                    }
                }
            }
        });
    }
}
