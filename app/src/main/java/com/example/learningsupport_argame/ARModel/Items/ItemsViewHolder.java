package com.example.learningsupport_argame.ARModel.Items;


import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;

public class ItemsViewHolder extends RecyclerView.ViewHolder {
    private TextView mTextView;
    private ImageView mImageView;

    public ItemsViewHolder(@NonNull View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.ar_item_name);
        mImageView = itemView.findViewById(R.id.ar_item_image);
    }

    public void bind(Item item) {
        if (item.getItemType() == ItemType.MODEL)
            mImageView.setImageResource(R.drawable.ar_item_model_icon);
        else if (item.getItemType() == ItemType.VIEW)
            mImageView.setImageResource(R.drawable.ar_item_view_icon);
        mTextView.setText(item.getItemName());
    }
}