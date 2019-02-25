package com.example.wanandroid.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wanandroid.Activity.KnowledgeTreeDetailActivity;
import com.example.wanandroid.Adapter.KnowledgeTreeAdapter;
import com.example.wanandroid.Adapter.KnowledgeTreeItem;
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

public class KnowledgeFragment extends Fragment {
    private static final String TAG = "KnowledgeFragment";
    private View rootView;
    private RecyclerView rv_knowledge;
    private KnowledgeTreeAdapter knowledgeTreeAdapter;

    private List<KnowledgeTreeItem> knowledgeTreeItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_knowledge,container,false);

        initView();

        knowledgeTreeItemList = new ArrayList<>();
        knowledgeTreeAdapter = new KnowledgeTreeAdapter(getContext(),knowledgeTreeItemList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_knowledge.setAdapter(knowledgeTreeAdapter);
        rv_knowledge.setLayoutManager(layoutManager);

        //获取体系树
        WanApi.GetKnowledgeTree(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getContext(), "获取知识体系树列表失败：网络错误", Toast.LENGTH_SHORT).show();
                L.e(TAG, "获取知识体系树列表失败：网络错误" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                L.d(TAG, "获取知识体系树列表回调：" + response);
                JsonObject object = new Gson().fromJson(response, JsonObject.class);
                if (object.get("errorCode").getAsInt() != 0) {
                    Toast.makeText(getContext(), "获取知识体系树列表失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
                }else {
                    JsonArray array = object.getAsJsonArray("data");
                    for (int i = 0; i < array.size(); i++){
                        KnowledgeTreeItem item = new KnowledgeTreeItem();
                        JsonObject superObj = array.get(i).getAsJsonObject();
                        JsonArray childArray = superObj.getAsJsonArray("children");
                        //获取父级标题以及id
                        item.setSuperChapterName(superObj.get("name").getAsString());
                        item.setSuperChapterId(superObj.get("id").getAsInt());

                        //循环获取子级标题以及id
                        ArrayList<CharSequence> childChapterNameList = new ArrayList<>();
                        ArrayList<Integer> childChapterIdList = new ArrayList<>();
                        for (int j = 0; j < childArray.size(); j++){
                            JsonObject childItemObj = childArray.get(j).getAsJsonObject();
                            childChapterIdList.add(childItemObj.get("id").getAsInt());
                            childChapterNameList.add(childItemObj.get("name").getAsString());
                        }
                        item.setChildChapterIdList(childChapterIdList);
                        item.setChildChapterNameList(childChapterNameList);
                        knowledgeTreeItemList.add(item);
                    }
                    knowledgeTreeAdapter.notifyDataSetChanged();
                }
            }
        });

        initEvent();

        return rootView;
    }

    private void initView(){
        rv_knowledge = rootView.findViewById(R.id.rv_knowledge);
    }

    private void initEvent(){
        knowledgeTreeAdapter.setOnItemClickListener(new KnowledgeTreeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                L.d(TAG,"click");
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList("idList", knowledgeTreeItemList.get(position).getChildChapterIdList());
                bundle.putCharSequenceArrayList("nameList",knowledgeTreeItemList.get(position).getChildChapterNameList());
                bundle.putString("superName",knowledgeTreeItemList.get(position).getSuperChapterName());
                Intent intent = new Intent(getContext(), KnowledgeTreeDetailActivity.class);
                intent.putExtra("data",bundle);
                startActivity(intent);
            }
        });
    }
}
