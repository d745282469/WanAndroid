package com.example.wanandroid.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.example.wanandroid.Adapter.ProjectFragAdapter;
import com.example.wanandroid.CustomView.WebBar;
import com.example.wanandroid.Fragment.KnowledgeDetailFragment;
import com.example.wanandroid.R;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeTreeDetailActivity extends AppCompatActivity {
    private static final String TAG = "KnowledgeTreeDetailAct";
    private WebBar webBar;
    private TabLayout tl_tab;
    private ViewPager vp_knowledge_detail;

    private List<Integer> idList;
    private List<CharSequence> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_tree_detail);
        initView();

        Bundle bundle = getIntent().getBundleExtra("data");
        if (bundle != null) {
            webBar.setTitle(bundle.getString("superName"));
            idList = bundle.getIntegerArrayList("idList");
            nameList = bundle.getCharSequenceArrayList("nameList");
        }

        List<Fragment> fragments = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < nameList.size(); i++){
            tl_tab.addTab(tl_tab.newTab());
            tl_tab.getTabAt(i).setText(nameList.get(i));
            titleList.add(nameList.get(i).toString());//类型不同，所以遍历然后新建一个list

            Bundle bundle1 = new Bundle();
            bundle1.putInt("id",idList.get(i));
            KnowledgeDetailFragment fragment = new KnowledgeDetailFragment();
            fragment.setArguments(bundle1);
            fragments.add(fragment);
        }
        ProjectFragAdapter fragAdapter = new ProjectFragAdapter(getSupportFragmentManager(),fragments,titleList);
        vp_knowledge_detail.setAdapter(fragAdapter);
        tl_tab.setupWithViewPager(vp_knowledge_detail);
    }

    private void initView() {
        webBar = findViewById(R.id.web_bar);
        webBar.showRightIcon(false);
        tl_tab = findViewById(R.id.tl_tab);
        vp_knowledge_detail = findViewById(R.id.vp_knowledge_detail);
    }

    private void initEvent(){

    }
}
