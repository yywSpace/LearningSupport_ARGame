package com.example.learningsupport_argame.ARModel.Items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;

import java.util.List;

public class ModelItemsAdapter extends RecyclerView.Adapter<ModelItemsViewHolder> {
    private List<ModelItem> mItems;
    private Context mContext;
    private OnModelItemClickListener mOnModelItemClickListener;

    public ModelItemsAdapter(Context context, List<ModelItem> items) {
        mContext = context;
        mItems = items;
    }

    @NonNull
    @Override
    public ModelItemsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ar_recycle_item, null, false);
        return new ModelItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelItemsViewHolder viewHolder, int i) {
        viewHolder.bind(mItems.get(i));
        viewHolder.itemView.setOnClickListener(v -> {
            if (mOnModelItemClickListener != null)
                mOnModelItemClickListener.onMyItemClick(mItems.get(i));
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnModelItemClickListener(OnModelItemClickListener itemClickListener) {
        mOnModelItemClickListener = itemClickListener;
    }
}