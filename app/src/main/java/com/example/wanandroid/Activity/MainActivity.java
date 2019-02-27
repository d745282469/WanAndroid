package com.example.wanandroid.Activity;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanandroid.Api.WanApi;
import com.example.wanandroid.Fragment.GzhFragment;
import com.example.wanandroid.Fragment.HomeFragment;
import com.example.wanandroid.Fragment.KnowledgeFragment;
import com.example.wanandroid.Fragment.ProjectFragment;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.example.wanandroid.Utils.PermissionTool;
import com.example.wanandroid.Utils.SpManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SpManager spManager;
    private final int BACK_TIME = 2000;//两次回退间隔时间,ms
    private long firstBackTime = 0;

    private TextView tv_title, tv_slide_header_username, tv_text_home, tv_text_project,
            tv_text_knowledge, tv_text_gzh;
    private ImageView iv_icon_home, iv_icon_project,
            iv_icon_knowledge, iv_icon_gzh;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FrameLayout fl_content;
    private LinearLayout ll_bar_slide, ll_bar_search, ll_btn_home, ll_btn_project, ll_btn_knowledg, ll_btn_gzh;
    private BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        List<String> perms = new ArrayList<>();
        perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        PermissionTool tool = new PermissionTool(MainActivity.this, perms);
        tool.setGetPermissionResultListener(new PermissionTool.GetPermissionResultListener() {
            @Override
            public void getPermissionSuccess() {

            }

            @Override
            public void getPermissionFail(List<String> permissions) {
                Toast.makeText(MainActivity.this, "拒绝权限将导致某些功能无法正常使用", Toast.LENGTH_SHORT).show();
            }
        });

        spManager = new SpManager(this);

        initView();
        initEvent();

        replaceFragment(new HomeFragment());
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (spManager.getStatus() == 1) {
            navigationView.getMenu().getItem(3).setTitle("注销");
            tv_slide_header_username.setText(spManager.getUerName());
        }
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        ll_bar_slide = findViewById(R.id.ll_bar_slide);
        ll_bar_search = findViewById(R.id.ll_bar_search);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);
        tv_slide_header_username = headerView.findViewById(R.id.slide_header_userName);
        fl_content = findViewById(R.id.fl_content);
        ll_btn_home = findViewById(R.id.ll_btn_home);
        ll_btn_project = findViewById(R.id.ll_btn_project);
        ll_btn_knowledg = findViewById(R.id.ll_btn_knowledge);
        ll_btn_gzh = findViewById(R.id.ll_btn_gzh);
        iv_icon_home = findViewById(R.id.iv_icon_home);
        iv_icon_project = findViewById(R.id.iv_icon_project);
        iv_icon_knowledge = findViewById(R.id.iv_icon_knowledge);
        iv_icon_gzh = findViewById(R.id.iv_icon_gzh);
        tv_text_home = findViewById(R.id.tv_text_home);
        tv_text_project = findViewById(R.id.tv_text_project);
        tv_text_knowledge = findViewById(R.id.tv_text_knowledge);
        tv_text_gzh = findViewById(R.id.tv_text_gzh);
        bottom_nav = findViewById(R.id.bottom_nav);
    }

    private void initEvent() {
        ll_bar_slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_login:
                        if (spManager.getStatus() == 0) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            //注销
                            WanApi.Logout(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(MainActivity.this, "注销失败：网络错误", Toast.LENGTH_SHORT).show();
                                    L.e(TAG, "注销失败：" + e.toString());
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    L.d(TAG, "注销回调：" + response);
                                    JsonObject object = new Gson().fromJson(response, JsonObject.class);
                                    if (object.get("errorCode").getAsInt() != 0) {
                                        Toast.makeText(MainActivity.this, "注销失败：" + object.get("errorMsg").getAsString(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        //注销成功，在sp中标记状态为未登录
                                        spManager.saveStatus(0);
                                        tv_slide_header_username.setText(getResources().getString(R.string.nav_header_username_default));
                                        navigationView.getMenu().getItem(3).setTitle("登陆");
                                    }
                                }
                            });
                        }
                        break;
                    case R.id.nav_my_collect:
                        if (spManager.getStatus() == 0) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, MyCollectActivity.class));
                        }
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        break;
                    case R.id.nav_setting:
                        startActivity(new Intent(MainActivity.this,SettingActivity.class));
                        break;
                }
                return true;
            }
        });

        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_bottom_home:
                        tv_title.setText(getString(R.string.app_name));
                        replaceFragment(new HomeFragment());
                        return true;
                    case R.id.nav_bottom_project:
                        tv_title.setText("项目");
                        replaceFragment(new ProjectFragment());
                        return true;
                    case R.id.nav_bottom_knowledge:
                        tv_title.setText("知识体系");
                        replaceFragment(new KnowledgeFragment());
                        return true;
                    case R.id.nav_bottom_gzh:
                        tv_title.setText("公众号");
                        replaceFragment(new GzhFragment());
                        return true;
                }
                return false;
            }
        });

        ll_bar_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
//                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, fragment);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionTool.onResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondBackTime = System.currentTimeMillis();
            if (firstBackTime > 0) {
                if (secondBackTime - firstBackTime <= BACK_TIME) {
                    //两次回退间隔时间小于设置时间，退出应用
                    finish();
                    return true;
                } else {
                    Toast.makeText(MainActivity.this, "再次按下退出", Toast.LENGTH_SHORT).show();
                    firstBackTime = secondBackTime;
                    return false;
                }
            } else {
                Toast.makeText(MainActivity.this, "再次按下退出", Toast.LENGTH_SHORT).show();
                firstBackTime = secondBackTime;
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
