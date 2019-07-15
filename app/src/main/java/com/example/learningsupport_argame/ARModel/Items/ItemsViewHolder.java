package com.example.learningsupport_argame.ARModel.Items;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.learningsupport_argame.R;

public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView mTextView;
    private OnModelItemClickListener mOnModelItemClickListener;
    private Item mItem;

    public ItemsViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mTextView = itemView.findViewById(R.id.armodel_item_name);

    }

    public void bind(Item item) {
        mTextView.setText(item.getItemName());
        mItem = item;
    }

    @Override
    public void onClick(View v) {
        if (mOnModelItemClickListener != null && mItem != null)
            mOnModelItemClickListener.onMyItemClick(mItem);
    }

    public void setOnModelItemClickListener(OnModelItemClickListener itemClickListener) {
        mOnModelItemClickListener = itemClickListener;
    }
}