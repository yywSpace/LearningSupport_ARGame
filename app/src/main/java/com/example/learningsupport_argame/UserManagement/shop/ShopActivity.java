package com.example.learningsupport_argame.UserManagement.shop;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.bag.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


// TODO: 20-3-25  增加更多模型道具

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
        mReturnLayout = findViewById(R.id.shop_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mRecyclerView = findViewById(R.id.shop_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mShopListAdapter = new ShopListAdapter(mShopItemList);
        mRecyclerView.setAdapter(mShopListAdapter);
        mSwipeRefreshLayout = findViewById(R.id.shop_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshModel();
            mSwipeRefreshLayout.setRefreshing(false);
        });
        refreshModel();
    }

    void refreshModel() {
        mShopItemList.clear();
        mShopItemList.addAll(RewardItemLab.get().getRewardItemList());
        List<ModelItem> shopItem = new ArrayList<>(ModelItemsLab.get().getItemList());
        shopItem.removeIf((modelItem -> UserLab.getCurrentUser().getModelItems().contains(modelItem)));
        mShopItemList.addAll(shopItem);
        mShopListAdapter.notifyDataSetChanged();
    }

    class ShopListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Item> mShopItemList;

        ShopListAdapter(List<Item> shopItemList) {
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
        private Button mItemBuyButton;

        ShopListViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemImage = itemView.findViewById(R.id.shop_item_image);
            mItemName = itemView.findViewById(R.id.shop_item_title);
            mItemType = itemView.findViewById(R.id.shop_item_type);
            mItemCoast = itemView.findViewById(R.id.shop_item_coast);
            mItemDesc = itemView.findViewById(R.id.shop_item_desc);
            mItemBuyButton = itemView.findViewById(R.id.shop_item_use_button);
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
                        mItemBuyButton.setOnClickListener(v -> showBuyRewardItemDialog(rewardItem.getItemName(), 200));
                        break;
                    case ITEM_SPEED_POTION:
                        mItemCoast.setText("$500");
                        mItemImage.setBackgroundResource(R.drawable.bag_speed_icon);
                        mItemBuyButton.setOnClickListener(v ->
                                showBuyRewardItemDialog(rewardItem.getItemName(), 500));
                        break;
                    case ITEM_HEALING_POTION:
                        mItemCoast.setText("$100");
                        mItemImage.setBackgroundResource(R.drawable.bag_heal_icon);
                        mItemBuyButton.setOnClickListener(v ->
                                showBuyRewardItemDialog(rewardItem.getItemName(), 100));
                        break;
                    case ITEM_GOLD_POTION:
                        mItemCoast.setText("$100");
                        mItemImage.setBackgroundResource(R.drawable.bag_gold_icon);
                        mItemBuyButton.setOnClickListener(v ->
                                showBuyRewardItemDialog(rewardItem.getItemName(), 100));
                        break;
                }
            } else if (item instanceof ModelItem) {
                ModelItem modelItem = (ModelItem) item;
                mItemType.setText("模型");
                mItemBuyButton.setOnClickListener(v ->
                        showBuyModelItemDialog(mItemBuyButton, modelItem, 500));
                mItemCoast.setText("$500");
                if (modelItem.getModelItemType().equals(ModelItemType.MODEL))
                    mItemImage.setBackgroundResource(modelItem.getImageRes());
                else
                    mItemImage.setBackgroundResource(R.drawable.ar_item_view_icon);
            }
        }
    }

    void showBuyModelItemDialog(Button button, ModelItem modelItem, int coast) {
        new AlertDialog.Builder(ShopActivity.this)
                .setTitle("购买" + modelItem.getItemName())
                .setMessage(String.format("是否要购买%s?\n你将花费%d个金币", modelItem.getItemName(), coast))
                .setPositiveButton("购买", (dialog, which) -> {
                    User user = UserLab.getCurrentUser();
                    if (user.getGold() < coast) {
                        Toast.makeText(this, String.format("当前金币数(%d)不足，无法购买", user.getGold()), Toast.LENGTH_LONG).show();
                        return;
                    }
                    UserLab.getCurrentUser()
                            .getModelItems()
                            .add(modelItem);
                    user.setGold(user.getGold() - coast);
                    button.setEnabled(false);
                    button.setBackgroundColor(Color.parseColor("#d4d4d4"));
                    new Thread(() -> UserLab.updateUser(user)).start();
                    Toast.makeText(this, "购买道具成功，此次花费" + coast + "个金币", Toast.LENGTH_LONG).show();
                }).setNegativeButton("取消", null)
                .show();
    }

    void showBuyRewardItemDialog(String name, int coast) {
        AlertDialog alertDialog = new AlertDialog.Builder(ShopActivity.this)
                .setTitle("购买" + name)
                .setMessage(String.format("是否要购买%s?\n你将花费%d个金币", name, coast))
                .setPositiveButton("购买", (dialog, which) -> {
                    User user = UserLab.getCurrentUser();
                    if (user.getGold() < coast) {
                        Toast.makeText(this, String.format("当前金币数(%d)不足，无法购买", user.getGold()), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Optional<RewardItem> itemOptional = user.getRewardItems()
                            .stream()
                            .filter(rewardItem -> rewardItem.getItemName().equals(name))
                            .findFirst();
                    itemOptional.ifPresent(rewardItem -> rewardItem.setCount(rewardItem.getCount() + 1));
                    user.setGold(user.getGold() - coast);
                    new Thread(() -> UserLab.updateUser(user)).start();
                    Toast.makeText(this, "购买道具成功，此次花费" + coast + "个金币", Toast.LENGTH_LONG).show();
                }).setNegativeButton("取消", null)
                .create();
        alertDialog.show();
    }
}
