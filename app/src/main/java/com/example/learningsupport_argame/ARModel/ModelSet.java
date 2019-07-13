package com.example.learningsupport_argame.ARModel;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningsupport_argame.R;

import java.util.ArrayList;
import java.util.List;

public class ModelSet extends AppCompatActivity {
    private PopupWindow mItemsPopupWindow;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mItemsRecyclerView;
    private List<Item> mItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.armodel_activity_set_model);
        mItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mItems.add(new Item("Item" + i, "Item desc"));
        }

        mFloatingActionButton = findViewById(R.id.item_show_button);
        View contentView = LayoutInflater.from(this).inflate(R.layout.armodel_popwindow_items, null, false);
        mItemsRecyclerView = contentView.findViewById(R.id.popwindow_items_recycler_view);
        mItemsRecyclerView.setAdapter(new ItemsAdapter(mItems));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mItemsRecyclerView.setLayoutManager(llm);

        mItemsPopupWindow = new PopupWindow(contentView,
                getResources().getDisplayMetrics().widthPixels-(dp2px(this,50) + 147), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mFloatingActionButton.setOnClickListener(v -> {
            mItemsPopupWindow.showAsDropDown(
                    mFloatingActionButton,
                    -(getResources().getDisplayMetrics().widthPixels-(dp2px(this,50) + 147)),
                    -(mFloatingActionButton.getHeight() / 2 + dp2px(ModelSet.this, 50) / 2));
        });
    }

    // dp转像素
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    class ItemsViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.item_name);

        }

        public void bind(Item item) {
            mTextView.setText(item.getItemName());
        }
    }

    class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolder> {
        private List<Item> mItems;

        public ItemsAdapter(List<Item> items) {
            mItems = items;
        }

        @NonNull
        @Override
        public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(ModelSet.this).inflate(R.layout.armodel_popwindow_item, null, false);
            return new ItemsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemsViewHolder viewHolder, int i) {
            viewHolder.bind(mItems.get(i));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }
}

