package com.example.wanandroid.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wanandroid.Api.*;

import com.example.wanandroid.CustomView.WebBar;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.example.wanandroid.Utils.SpManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText et_username,et_password;
    private TextView btn_login,btn_gotoReg;
    private WebBar webBar;

    private SpManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spManager = new SpManager(this);

        initView();
        initEvent();
    }

    private void initView(){
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_gotoReg = findViewById(R.id.btn_goto_reg);
        webBar = findViewById(R.id.web_bar);

        webBar.showRightIcon(false);
    }

    private void initEvent(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查输入是否完备
                if (et_username.getText().toString().length() < 1){
                    Toast.makeText(LoginActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_password.getText().toString().length() < 1){
                    Toast.makeText(LoginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                    return;
                }

                //执行登陆动作
                WanApi.Login(et_username.getText().toString(), et_password.getText().toString(), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(LoginActivity.this,"登陆失败：网络错误",Toast.LENGTH_SHORT).show();
                        L.e(TAG,"登陆失败："+e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.d(TAG,"登陆结果:"+response);
                        JsonObject object = new Gson().fromJson(response,JsonObject.class);
                        if (object.get("errorCode").getAsInt()!=0){
                            Toast.makeText(LoginActivity.this,"登陆失败："+object.get("errorMsg").getAsString(),Toast.LENGTH_SHORT).show();
                        }else {
                            JsonObject data = object.get("data").getAsJsonObject();
                            spManager.saveUserName(et_username.getText().toString());
                            spManager.saveStatus(1);
                            finish();
                        }
                    }
                });
            }
        });

        btn_gotoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegActivity.class);
                startActivity(intent);
            }
        });
    }
}
