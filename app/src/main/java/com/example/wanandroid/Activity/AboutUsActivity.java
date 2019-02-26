package com.example.wanandroid.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.example.wanandroid.CustomView.WebBar;
import com.example.wanandroid.R;

public class AboutUsActivity extends AppCompatActivity {
    private static final String TAG = "AboutUsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        TextView textView = findViewById(R.id.tv_text1);
        textView.setText(Html.fromHtml(getString(R.string.about_us_text_1)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        WebBar webBar = findViewById(R.id.web_bar);
        webBar.showRightIcon(false);
        webBar.setTitle("关于我们");
    }
}
