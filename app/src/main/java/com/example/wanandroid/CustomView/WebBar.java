package com.example.wanandroid.CustomView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wanandroid.R;

public class WebBar extends LinearLayout {
    private Context context;
    private View rootView;
    private LinearLayout ll_back, ll_right;
    private TextView tv_title;
    private ImageView iv_right;

    private OnRightIconClickListener onRightIconClickListener;

    public WebBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.curtom_web_bar, this);
        initView();
        initEvent();
    }

    private void initView() {
        ll_back = rootView.findViewById(R.id.ll_bar_back);
        ll_right = rootView.findViewById(R.id.ll_bar_right);
        tv_title = rootView.findViewById(R.id.tv_bar_title);
        iv_right = rootView.findViewById(R.id.iv_bar_right);
    }

    private void initEvent() {
        ll_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) getContext();
                activity.finish();
            }
        });

        ll_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRightIconClickListener != null) {
                    onRightIconClickListener.onClick();
                }
            }
        });
    }

    public void setOnRightIconClickListener(OnRightIconClickListener onRightIconClickListener) {
        this.onRightIconClickListener = onRightIconClickListener;
    }


    public interface OnRightIconClickListener {
        void onClick();
    }

    public void setTitle(String title){
        tv_title.setText(title);
    }

    public void showLeftIcon(boolean b){
        if (b){
            ll_back.setVisibility(VISIBLE);
        }else {
            ll_back.setVisibility(INVISIBLE);
        }
    }

    public void showRightIcon(boolean b){
        if (b){
            ll_right.setVisibility(VISIBLE);
        }else {
            ll_right.setVisibility(INVISIBLE);
        }
    }

    public void setRightIcon(Drawable drawable){
        iv_right.setImageDrawable(drawable);
    }

}
