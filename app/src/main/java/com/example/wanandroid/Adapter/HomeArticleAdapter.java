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

import java.util.List;

public class HomeArticleAdapter extends RecyclerView.Adapter<HomeArticleAdapter.ViewHolder> {
    private List<ArticleItem> itemList;
    private Context context;

    public HomeArticleAdapter(List<ArticleItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ArticleItem item = itemList.get(position);
//        Glide.with(context).load(item.getImgUrl()).into(holder.iv_img);
        holder.tv_title.setText(Html.fromHtml(item.getTitle()));
        holder.tv_nice_date.setText(item.getNickDate());
        holder.tv_author.setText(item.getAuthor());
        holder.tv_super_chapter_name.setText(item.getSuperChapterName());
        holder.tv_chapter_name.setText(Html.fromHtml(item.getChapterName()));
        holder.tv_fresh.setVisibility(View.INVISIBLE);
        Glide.with(context).load(context.getDrawable(R.drawable.home_article_like_def)).into(holder.iv_collect);

        if (item.isFresh()) {
            holder.tv_fresh.setVisibility(View.VISIBLE);
        }
        if (item.isCollect()) {
            Glide.with(context).load(context.getDrawable(R.drawable.home_article_like_act)).into(holder.iv_collect);
        }

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("url", item.getLink());
                intent.putExtra("collect", item.isCollect());
                intent.putExtra("author", item.getAuthor());
                if (item.getOriginId() == 0) {
                    //没有originId，那么id就是文章的id
                    intent.putExtra("originId", item.getId());
                } else {
                    //有originId，那么文章id则为originId，id表示收藏id
                    intent.putExtra("originId", item.getOriginId());
                    intent.putExtra("id", item.getId());
                }
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
        TextView tv_title, tv_author, tv_super_chapter_name, tv_chapter_name, tv_nice_date,
                tv_fresh;
        ImageView iv_collect;
        LinearLayout ll_item;

        public ViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_article_title);
            tv_author = view.findViewById(R.id.tv_author);
            tv_super_chapter_name = view.findViewById(R.id.tv_super_chapter_name);
            tv_chapter_name = view.findViewById(R.id.tv_chapter_name);
            tv_nice_date = view.findViewById(R.id.tv_nice_date);
            iv_collect = view.findViewById(R.id.iv_collect);
            tv_fresh = view.findViewById(R.id.tv_fresh);
            ll_item = view.findViewById(R.id.ll_article_item);
        }
    }
}
