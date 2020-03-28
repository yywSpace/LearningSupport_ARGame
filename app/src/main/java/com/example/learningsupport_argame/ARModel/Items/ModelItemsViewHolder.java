package com.example.learningsupport_argame.ARModel.Items;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;

public class ModelItemsViewHolder extends RecyclerView.ViewHolder {
    private TextView mTextView;
    private ImageView mImageView;

    public ModelItemsViewHolder(@NonNull View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.ar_item_name);
        mImageView = itemView.findViewById(R.id.ar_item_image);
    }

    public void bind(ModelItem item) {
        if (item.getModelItemType() == ModelItemType.MODEL)
            mImageView.setImageResource(item.getImageRes());
        else if (item.getModelItemType() == ModelItemType.VIEW)
            mImageView.setImageResource(R.drawable.ar_item_view_icon);
        mTextView.setText(item.getItemName());
    }
}