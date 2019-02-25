package com.example.wanandroid.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wanandroid.R;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private Context context;
    private List<String> itemList;
    private OnItemClickListener onItemClickListener;
    private OnChildViewClickListener onChildViewClickListener;

    public SearchHistoryAdapter(Context context, List<String> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_history, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_title.setText(itemList.get(position));

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, position);
                }
            }
        });

        holder.iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onChildViewClickListener != null) {
                    onChildViewClickListener.onChildViewClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView iv_clear;
        LinearLayout ll_item;

        public ViewHolder(@NonNull View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_search_history_title);
            iv_clear = view.findViewById(R.id.iv_search_history_clear);
            ll_item = view.findViewById(R.id.ll_search_history_item);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnChildViewClickListener(OnChildViewClickListener onChildViewClickListener) {
        this.onChildViewClickListener = onChildViewClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnChildViewClickListener {
        void onChildViewClick(View view, int position);
    }
}
