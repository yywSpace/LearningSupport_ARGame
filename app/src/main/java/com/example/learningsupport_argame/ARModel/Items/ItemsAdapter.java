package com.example.learningsupport_argame.ARModel.Items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolder> {
    private List<Item> mItems;
    private Context mContext;
    private OnModelItemClickListener mOnModelItemClickListener;

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
        viewHolder.setOnModelItemClickListener(mOnModelItemClickListener);
        viewHolder.bind(mItems.get(i));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnModelItemClickListener(OnModelItemClickListener itemClickListener){
        mOnModelItemClickListener =itemClickListener;
    }
}