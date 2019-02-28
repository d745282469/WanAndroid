package com.example.wanandroid.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanandroid.CustomView.GeneralDialog;
import com.example.wanandroid.CustomView.WebBar;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.SpManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import java.io.File;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    private WebBar webBar;
    private LinearLayout ll_clear_cache,ll_no_img_mod, ll_check_version;
    private TextView tv_cache_size,tv_version;
    private CheckBox cb_no_img;

    private Context context;
    private final String CLEAR_CACHE_MSG = "你正在执行清除缓存动作，清除缓存后，所有数据将重新从服务器加载";
    private SpManager spManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        context = this;
        spManager = new SpManager(context);

        initView();
        setCaCheSize();
        initEvent();

        //检测版本号
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(),0);
            tv_version.setText("v"+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        webBar = findViewById(R.id.web_bar);
        tv_cache_size = findViewById(R.id.tv_cache_size);
        ll_clear_cache = findViewById(R.id.ll_clear_cache);
        ll_no_img_mod = findViewById(R.id.ll_no_img_mod);
        cb_no_img = findViewById(R.id.cb_no_img);
        ll_check_version = findViewById(R.id.ll_check_version);
        tv_version = findViewById(R.id.tv_version);

        webBar.showRightIcon(false);
        webBar.setTitle("设置");
        cb_no_img.setChecked(spManager.getNoImg());
    }

    private void initEvent() {
        ll_clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GeneralDialog dialog = new GeneralDialog(context, CLEAR_CACHE_MSG);
                dialog.show();
                dialog.setOnSureListener(new GeneralDialog.OnSureListener() {
                    @Override
                    public void onSure() {
                        if (deleteDir(context.getCacheDir())) {
                            dialog.dismiss();
                            Toast.makeText(context, "清除缓存成功", Toast.LENGTH_SHORT).show();
                            setCaCheSize();
                        } else {
                            Toast.makeText(context, "清除缓存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        ll_no_img_mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_no_img.isChecked()){
                    cb_no_img.setChecked(false);
                    spManager.setNoImg(false);
                }else {
                    cb_no_img.setChecked(true);
                    spManager.setNoImg(true);
                }
            }
        });

        cb_no_img.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                spManager.setNoImg(b);
            }
        });

        ll_check_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Beta.checkUpgrade();
            }
        });
    }

    /**
     * 获取某个文件的大小
     *
     * @param file 文件
     * @return 文件大小
     */
    private long getFileSize(File file) {
        long size = 0;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                size = size + getFileSize(files[i]);
            } else {
                size = size + files[i].length();
            }
        }
        return size;
    }

    /**
     * 清除某个文件以及下面的所有文件夹
     *
     * @param file 文件
     * @return 成功/失败
     */
    private boolean deleteDir(File file) {
        if (file != null) {
            if (file.isDirectory()){
                File[] files = file.listFiles();
                for (File file1 : files) {
                    boolean b = deleteDir(file1);
                    if (!b) {
                        return false;
                    }
                }
            }
            return file.delete();
        } else {
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCaCheSize() {
        //获取应用缓存大小
        File file = new File(this.getCacheDir().getPath());
        long size = getFileSize(file);
        int dimen = 0;
        while (size > 1000) {
            size = size / 1000;
            dimen++;
        }
        switch (dimen) {
            case 0:
                tv_cache_size.setText(size + " B");
                break;
            case 1:
                tv_cache_size.setText(size + " K");
                break;
            case 2:
                tv_cache_size.setText(size + " M");
                break;
        }
    }

}
