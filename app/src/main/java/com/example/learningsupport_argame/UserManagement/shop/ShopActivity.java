package com.example.learningsupport_argame.UserManagement.shop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.ARModel.Items.ModelItem;
import com.example.learningsupport_argame.ARModel.Items.ModelItemType;
import com.example.learningsupport_argame.ARModel.Items.ModelItemsLab;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.TaskReward.RewardItem;
import com.example.learningsupport_argame.Task.TaskReward.RewardItemLab;
import com.example.learningsupport_argame.UserManagement.bag.Item;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    private List<Item> mShopItemList;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mReturnLayout;
    private ShopListAdapter mShopListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity_layout);
        mShopItemList = new ArrayList<>();
        mShopItemList.addAll(RewardItemLab.get().getRewardItemList());
        mShopItemList.addAll(ModelItemsLab.get().getItemList());
        mReturnLayout = findViewById(R.id.shop_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mRecyclerView = findViewById(R.id.shop_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mShopListAdapter = new ShopListAdapter(mShopItemList);
        mRecyclerView.setAdapter(mShopListAdapter);
        mSwipeRefreshLayout = findViewById(R.id.shop_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mShopItemList.clear();
            mShopItemList.addAll(RewardItemLab.get().getRewardItemList());
            mShopItemList.addAll(ModelItemsLab.get().getItemList());
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    class ShopListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Item> mShopItemList;

        public ShopListAdapter(List<Item> shopItemList) {
            mShopItemList = shopItemList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ShopListViewHolder(LayoutInflater.from(ShopActivity.this).inflate(R.layout.shop_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((ShopListViewHolder) holder).bind(mShopItemList.get(position));
        }

        @Override
        public int getItemCount() {
            return mShopItemList.size();
        }
    }

    class ShopListViewHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImage;
        private TextView mItemName;
        private TextView mItemType;
        private TextView mItemCoast;
        private TextView mItemDesc;
        private TextView mItemUseButton;

        public ShopListViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemImage = itemView.findViewById(R.id.shop_item_image);
            mItemName = itemView.findViewById(R.id.shop_item_title);
            mItemType = itemView.findViewById(R.id.shop_item_type);
            mItemCoast = itemView.findViewById(R.id.shop_item_coast);
            mItemDesc = itemView.findViewById(R.id.shop_item_desc);
            mItemUseButton = itemView.findViewById(R.id.shop_item_use_button);
        }

        void bind(Item item) {
            mItemName.setText(item.getItemName());
            mItemDesc.setText(item.getItemDesc());
            if (item instanceof RewardItem) {
                RewardItem rewardItem = (RewardItem) item;
                mItemType.setText("道具");
                switch (rewardItem.getRewardItemType()) {
                    case ITEM_EXP_POTION:
                        mItemCoast.setText("$200");
                        mItemImage.setBackgroundResource(R.drawable.bag_exp_icon);
                        break;
                    case ITEM_SPEED_POTION:
                        mItemCoast.setText("$1000");
                        mItemImage.setBackgroundResource(R.drawable.bag_speed_icon);
                        break;
                    case ITEM_HEALING_POTION:
                        mItemCoast.setText("$100");
                        mItemImage.setBackgroundResource(R.drawable.bag_heal_icon);
                        break;
                    case ITEM_GOLD_POTION:
                        mItemCoast.setText("$100");
                        mItemImage.setBackgroundResource(R.drawable.bag_gold_icon);
                        break;
                }
            } else if (item instanceof ModelItem) {
                ModelItem modelItem = (ModelItem) item;
                mItemCoast.setText("$500");
                mItemUseButton.setClickable(false);
                mItemUseButton.setBackgroundColor(Color.parseColor("#d4d4d4"));
                if (modelItem.getModelItemType().equals(ModelItemType.MODEL))
                    mItemImage.setBackgroundResource(R.drawable.ar_item_model_icon);
                else
                    mItemImage.setBackgroundResource(R.drawable.ar_item_view_icon);
            }
        }
    }
}
