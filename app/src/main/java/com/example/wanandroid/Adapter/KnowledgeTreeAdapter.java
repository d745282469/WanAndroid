package com.example.wanandroid.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.internal.FlowLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wanandroid.R;

import java.util.List;

public class KnowledgeTreeAdapter extends RecyclerView.Adapter<KnowledgeTreeAdapter.ViewHolder> {
    private Context context;
    private List<KnowledgeTreeItem> itemList;
    private OnItemClickListener listener;

    public KnowledgeTreeAdapter(Context context, List<KnowledgeTreeItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_knowledge, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        KnowledgeTreeItem item = itemList.get(position);

        holder.tv_super_chapter_name.setText(Html.fromHtml(item.getSuperChapterName()));
        for (int i = 0; i < item.getChildChapterNameList().size(); i++) {
            View child = View.inflate(context, R.layout.item_knowledge_child_item, null);
            TextView textView = child.findViewById(R.id.tv_tag);
            SpannableString spannableString = new SpannableString(item.getChildChapterNameList().get(i));
            spannableString.setSpan(new UnderlineSpan(), 0, item.getChildChapterNameList().get(i).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setText(spannableString);
            holder.fl_child_list.addView(child);
        }

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_super_chapter_name;
        FlowLayout fl_child_list;
        LinearLayout ll_item;

        public ViewHolder(@NonNull View view) {
            super(view);
            tv_super_chapter_name = view.findViewById(R.id.tv_super_chapter_name);
            fl_child_list = view.findViewById(R.id.fl_child_list);
            ll_item = view.findViewById(R.id.ll_knowledge_item);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
