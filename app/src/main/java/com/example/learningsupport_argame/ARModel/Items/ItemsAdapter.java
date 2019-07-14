package com.example.learningsupport_argame.ARModel.Items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningsupport_argame.R;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolder> {
    private List<Item> mItems;
    private Context mContext;
    private OnMyItemClickListener mOnMyItemClickListener;

    public ItemsAdapter(Context context,List<Item> items) {
        mContext = context;
        mItems = items;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.armodel_popwindow_item, null, false);
        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder viewHolder, int i) {
        viewHolder.setOnMyItemClickListener(mOnMyItemClickListener);
        viewHolder.bind(mItems.get(i));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnMyItemClickListener(OnMyItemClickListener itemClickListener){
        mOnMyItemClickListener =itemClickListener;
    }
}