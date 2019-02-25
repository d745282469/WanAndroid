package com.example.wanandroid.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanandroid.Api.WanApi;
import com.example.wanandroid.CustomView.WebBar;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class RegActivity extends AppCompatActivity {
    private static final String TAG = "RegActivity";

    private EditText et_username,et_password,et_repassword;
    private TextView btn_reg;
    private WebBar webBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        initView();
        initEvent();
    }

    private void initView(){
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_repassword = findViewById(R.id.et_repassword);
        btn_reg = findViewById(R.id.btn_reg);
        webBar = findViewById(R.id.web_bar);

        webBar.showRightIcon(false);
    }

    private void initEvent(){
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查是否输入完备
                if (et_username.getText().toString().length() < 1){
                    Toast.makeText(RegActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_password.getText().toString().length() < 1){
                    Toast.makeText(RegActivity.this,"请输入至少6位密码",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (et_repassword.getText().toString().length() < 1){
                    Toast.makeText(RegActivity.this,"请确认密码",Toast.LENGTH_SHORT).show();
                    return;
                }else if (!et_password.getText().toString().equals(et_repassword.getText().toString())){
                    Toast.makeText(RegActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }

                //执行注册动作
                WanApi.Reg(et_username.getText().toString(), et_password.getText().toString(), et_repassword.getText().toString(), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(RegActivity.this,"注册失败：网络错误"+e.toString(),Toast.LENGTH_SHORT).show();
                        L.e(TAG,"注册失败"+e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.d(TAG,"注册回调："+response);
                        JsonObject object = new Gson().fromJson(response,JsonObject.class);
                        if (object.get("errorCode").getAsInt()!=0){
                            Toast.makeText(RegActivity.this,"注册失败："+object.get("errorMsg").getAsString(),Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(RegActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }
}
