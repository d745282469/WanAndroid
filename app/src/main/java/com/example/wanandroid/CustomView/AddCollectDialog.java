package com.example.wanandroid.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanandroid.Api.WanApi;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.L;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class AddCollectDialog extends Dialog {
    private static final String TAG = "AddCollectDialog";
    private Context context;
    private OnSureClickListener onSureClickListener;

    private EditText et_title, et_author, et_link;
    private TextView btn_cancel, btn_sure;

    public AddCollectDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_add_collect);
        initView();
        initEvent();
    }

    private void initView() {
        et_title = findViewById(R.id.et_title);
        et_author = findViewById(R.id.et_author);
        et_link = findViewById(R.id.et_link);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_sure = findViewById(R.id.btn_sure);
    }

    private void initEvent() {
        et_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    et_title.setBackground(context.getDrawable(R.drawable.tv_border_rect_green));
                } else {
                    et_title.setBackground(context.getDrawable(R.drawable.tv_border_rect_gray));
                }
            }
        });

        et_author.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    et_author.setBackground(context.getDrawable(R.drawable.tv_border_rect_green));
                } else {
                    et_author.setBackground(context.getDrawable(R.drawable.tv_border_rect_gray));
                }
            }
        });

        et_link.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    et_link.setBackground(context.getDrawable(R.drawable.tv_border_rect_green));
                } else {
                    et_link.setBackground(context.getDrawable(R.drawable.tv_border_rect_gray));
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCollectDialog.this.dismiss();
            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查输入是否完备
                if (et_title.getText().toString().length() <= 0){
                    Toast.makeText(context,"请输入标题",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_author.getText().toString().length() <= 0){
                    Toast.makeText(context,"请输入作者",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_link.getText().toString().length() <= 0){
                    Toast.makeText(context,"请输入链接",Toast.LENGTH_SHORT).show();
                    return;
                }
                //执行收藏动作
                String title = et_title.getText().toString();
                String author = et_title.getText().toString();
                String link = et_link.getText().toString();

               if (onSureClickListener!=null){
                   onSureClickListener.onSureClick(title,author,link);
               }
            }
        });
    }

    public void setOnSureClickListener(OnSureClickListener onSureClickListener) {
        this.onSureClickListener = onSureClickListener;
    }

    public interface OnSureClickListener {
        void onSureClick(String s1, String s2, String s3);
    }
}
