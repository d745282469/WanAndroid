package com.example.wanandroid.CustomView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wanandroid.R;

public class InputBar extends LinearLayout {
    private Context context;
    private View rootView;
    private LinearLayout ll_back, ll_right;
    private ImageView iv_right;
    private EditText et_content;

    private OnRightIconClickListener onRightIconClickListener;

    public InputBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.curtom_input_bar, this);
        initView();
        initEvent();
    }

    private void initView() {
        ll_back = rootView.findViewById(R.id.ll_bar_back);
        ll_right = rootView.findViewById(R.id.ll_bar_right);
        iv_right = rootView.findViewById(R.id.iv_bar_right);
        et_content = rootView.findViewById(R.id.et_bar_content);
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
        et_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH ||actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    if (onRightIconClickListener!=null){
                        onRightIconClickListener.onClick();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void setOnRightIconClickListener(OnRightIconClickListener onRightIconClickListener) {
        this.onRightIconClickListener = onRightIconClickListener;
    }


    public interface OnRightIconClickListener {
        void onClick();
    }

    public void showLeftIcon(boolean b) {
        if (b) {
            ll_back.setVisibility(VISIBLE);
        } else {
            ll_back.setVisibility(INVISIBLE);
        }
    }

    public void showRightIcon(boolean b) {
        if (b) {
            ll_right.setVisibility(VISIBLE);
        } else {
            ll_right.setVisibility(INVISIBLE);
        }
    }

    public void setRightIcon(Drawable drawable) {
        iv_right.setImageDrawable(drawable);
    }

    public void setContentHint(String s) {
        et_content.setHint(s);
    }

    public String getContent() {
        return et_content.getText().toString();
    }
}
