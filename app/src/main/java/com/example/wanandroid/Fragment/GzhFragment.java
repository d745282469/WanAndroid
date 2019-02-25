package com.example.wanandroid.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wanandroid.Adapter.ProjectFragAdapter;
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

public class GzhFragment extends Fragment {
    private static final String TAG = "GzhFragment";
    private View rootView;
    private ViewPager vp_project;
    private TabLayout tl_tab;

    private List<String> titleList;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_project, container, false);

        initView();

        titleList = new ArrayList<>();

        WanApi.GetGzhList(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getContext(), "获取公众号列表失败：网络错误", Toast.LENGTH_SHORT).show();
                L.e(TAG, "获取公众号列表失败：网络错误" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                L.d(TAG, "获取公众号列表回调：" + response);
                JsonObject object = new Gson().fromJson(response, JsonObject.class);
                if (object.get("errorCode").getAsInt() != 0) {
                    Toast.makeText(getContext(), "获取公众号列表失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
                }else {
                    JsonArray array = object.getAsJsonArray("data");
                    List<Fragment> fragments = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++){
                        JsonObject objItem = array.get(i).getAsJsonObject();
//                        viewList.add(inflater.inflate(R.layout.item_project_article, null));
                        tl_tab.addTab(tl_tab.newTab());
                        titleList.add(String.valueOf(Html.fromHtml(objItem.get("name").getAsString())));

                        //设置fragment，并传递数据
                        Bundle bundle = new Bundle();
                        bundle.putInt("id",objItem.get("id").getAsInt());
                        GzhDetailFragment fragment = new GzhDetailFragment();
                        fragment.setArguments(bundle);
                        fragments.add(fragment);
                    }
                    L.d(TAG,"碎片列表长度："+getFragmentManager().getFragments().size());
                    ProjectFragAdapter adapter = new ProjectFragAdapter(getFragmentManager(),fragments,titleList);
                    vp_project.setAdapter(adapter);
                    tl_tab.setupWithViewPager(vp_project);
                }
            }
        });

        return rootView;
    }

    private void initView() {
        vp_project = rootView.findViewById(R.id.vp_project);
        tl_tab = rootView.findViewById(R.id.tl_tab);
    }

    @Override
    public void onDestroy() {
        L.d(TAG,"onDestroy");
        super.onDestroy();
    }
}
