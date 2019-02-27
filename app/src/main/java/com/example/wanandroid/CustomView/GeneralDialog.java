package com.example.wanandroid.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.wanandroid.R;

public class GeneralDialog extends Dialog {
    private Context context;
    private String content,title;
    private OnSureListener listener;

    private TextView tv_title,tv_content,btn_cancel,btn_sure;

    public GeneralDialog(Context context, String content) {
        super(context, R.style.dialog);
        this.context = context;
        this.content = content;
        this.title = "提示";
    }

    public GeneralDialog(Context context, String content, String title) {
        super(context,R.style.dialog);
        this.context = context;
        this.content = content;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_general);

        initView();
        initEvent();
    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_sure = findViewById(R.id.btn_sure);

        tv_title.setText(title);
        tv_content.setText(content);
    }

    private void initEvent(){
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralDialog.this.dismiss();
            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    listener.onSure();
                }
            }
        });
    }

    public void setOnSureListener(OnSureListener listener) {
        this.listener = listener;
    }

    public interface OnSureListener{
        void onSure();
    }
}
