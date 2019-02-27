package com.example.wanandroid.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wanandroid.Activity.WebViewActivity;
import com.example.wanandroid.R;
import com.example.wanandroid.Utils.SpManager;

import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;

public class ProjectArticleAdapter extends RecyclerView.Adapter<ProjectArticleAdapter.ViewHolder> {
    private Context context;
    private List<ArticleItem> itemList;

    public ProjectArticleAdapter(Context context, List<ArticleItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project_article,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ArticleItem item = itemList.get(position);
        SpManager spManager = new SpManager(context);
        if (spManager.getNoImg()){
            Glide.with(context).load(R.drawable.page_no_img).into(holder.iv_img);
        }else {
            Glide.with(context).load(item.getEnvelopePic()).into(holder.iv_img);
        }
        holder.tv_title.setText(Html.fromHtml(item.getTitle()));
        holder.tv_author.setText(item.getAuthor());
        holder.tv_date.setText(item.getNickDate());
        holder.tv_chapter_name.setText(item.getChapterName());
        holder.tv_desc.setText(item.getDesc());
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("title",item.getTitle());
                intent.putExtra("url",item.getLink());
                intent.putExtra("collect",item.isCollect());
                intent.putExtra("author",item.getAuthor());
                intent.putExtra("originId",item.getId());
                Activity activity = (Activity) context;
                activity.startActivity(intent);
            }
        });

        holder.iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new BGAPhotoPreviewActivity.IntentBuilder(context)
                        .previewPhoto(item.getEnvelopePic())
                        .currentPosition(0)
                        .build();
                Activity activity = (Activity) context;
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_img;
        TextView tv_title,tv_author,tv_date,tv_chapter_name,tv_desc;
        LinearLayout ll_item;

        public ViewHolder(@NonNull View view) {
            super(view);
            iv_img = view.findViewById(R.id.iv_project_img);
            tv_title = view.findViewById(R.id.tv_project_title);
            tv_author = view.findViewById(R.id.tv_project_author);
            tv_date = view.findViewById(R.id.tv_project_date);
            tv_chapter_name = view.findViewById(R.id.tv_project_chapter_name);
            tv_desc = view.findViewById(R.id.tv_project_desc);
            ll_item = view.findViewById(R.id.ll_article_item);
        }
    }
}
