package com.example.learningsupport_argame.UserManagement.shop;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.widget.Toolbar;
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
import com.example.learningsupport_argame.UserManagement.bag.UnityItem;
import com.example.learningsupport_argame.UserManagement.bag.UnityItemLab;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ShopActivity extends AppCompatActivity {
    private String TAG = "ShopActivity";
    private List<Item> mShopItemList;
    private List<Item> mTypeShopItemList;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mReturnLayout;
    private ShopListAdapter mShopListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity_layout);

        //初始化数据
        mShopItemList = new ArrayList<>();
        mShopItemList.addAll(RewardItemLab.get().getRewardItemList());
        List<ModelItem> shopItem = new ArrayList<>(ModelItemsLab.get().getItemList());
        shopItem.removeIf((modelItem -> UserLab.getCurrentUser().getModelItems().contains(modelItem)));
        mShopItemList.addAll(shopItem);
        List<UnityItem> unityModel = new ArrayList<>(UnityItemLab.get().getUnityItemList());
        unityModel.removeIf((modelItem -> UserLab.getCurrentUser().getUnityItems().contains(modelItem)));
        mShopItemList.addAll(unityModel);
        mTypeShopItemList = new ArrayList<>(mShopItemList);

        mReturnLayout = findViewById(R.id.shop_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mRecyclerView = findViewById(R.id.shop_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mShopListAdapter = new ShopListAdapter(mTypeShopItemList);
        mRecyclerView.setAdapter(mShopListAdapter);
        mSwipeRefreshLayout = findViewById(R.id.shop_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshModel();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        mToolbar = findViewById(R.id.shop_tool_bar);
        mToolbar.inflateMenu(R.menu.shop_menu);
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.shop_ar:
                    mTypeShopItemList.clear();
                    mTypeShopItemList.addAll(mShopItemList
                            .stream()
                            .filter(item1 -> item1 instanceof ModelItem)
                            .collect(Collectors.toList()));
                    break;
                case R.id.shop_unity:
                    mTypeShopItemList.clear();
                    mTypeShopItemList.addAll(mShopItemList
                            .stream()
                            .filter(item1 -> item1 instanceof UnityItem)
                            .collect(Collectors.toList()));
                    break;
                case R.id.shop_item:
                    mTypeShopItemList.clear();
                    mTypeShopItemList.addAll(mShopItemList
                            .stream()
                            .filter(item1 -> item1 instanceof RewardItem)
                            .collect(Collectors.toList()));
                    break;
                case R.id.shop_all:
                    mTypeShopItemList.clear();
                    mTypeShopItemList.addAll(mShopItemList);
                    break;
                case R.id.reverse_order:
                    mTypeShopItemList.sort((item1, item2) -> {
                        if (item1.getPrice() < item2.getPrice())
                            return 1;
                        else if (item1.getPrice() > item2.getPrice())
                            return -1;
                        return 0;
                    });
                    break;
                case R.id.natural_order:
                    mTypeShopItemList.sort((item1, item2) -> {
                        if (item1.getPrice() > item2.getPrice())
                            return 1;
                        else if (item1.getPrice() < item2.getPrice())
                            return -1;
                        return 0;
                    });
                    break;
            }
            Log.d(TAG, "setOnMenuItemClickListener: " + mTypeShopItemList.size());
            mShopListAdapter.notifyDataSetChanged();
            return true;
        });


    }

    void refreshModel() {
        mShopItemList.clear();
        mShopItemList.addAll(RewardItemLab.get().getRewardItemList());
        List<ModelItem> shopItem = new ArrayList<>(ModelItemsLab.get().getItemList());
        shopItem.removeIf((modelItem -> UserLab.getCurrentUser().getModelItems().contains(modelItem)));
        mShopItemList.addAll(shopItem);
        mShopItemList.addAll(UnityItemLab.get().getUnityItemList());
        mTypeShopItemList = new ArrayList<>(mShopItemList);
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
            mItemCoast.setText("$" + item.getPrice());
            if (item instanceof RewardItem) {
                RewardItem rewardItem = (RewardItem) item;
                mItemType.setText("道具");

                switch (rewardItem.getRewardItemType()) {
                    case ITEM_EXP_POTION:
                        mItemImage.setBackgroundResource(R.drawable.bag_exp_icon);
                        mItemBuyButton.setOnClickListener(v -> showBuyRewardItemDialog(rewardItem.getItemName(), 200));
                        break;
                    case ITEM_SPEED_POTION:
                        mItemImage.setBackgroundResource(R.drawable.bag_speed_icon);
                        mItemBuyButton.setOnClickListener(v ->
                                showBuyRewardItemDialog(rewardItem.getItemName(), item.getPrice()));
                        break;
                    case ITEM_HEALING_POTION:
                        mItemImage.setBackgroundResource(R.drawable.bag_heal_icon);
                        mItemBuyButton.setOnClickListener(v ->
                                showBuyRewardItemDialog(rewardItem.getItemName(), item.getPrice()));
                        break;
                    case ITEM_GOLD_POTION:
                        mItemImage.setBackgroundResource(R.drawable.bag_gold_icon);
                        mItemBuyButton.setOnClickListener(v ->
                                showBuyRewardItemDialog(rewardItem.getItemName(), item.getPrice()));
                        break;
                }
            } else if (item instanceof ModelItem) {
                ModelItem modelItem = (ModelItem) item;
                mItemType.setText("模型");
                mItemBuyButton.setOnClickListener(v ->
                        showBuyModelItemDialog(mItemBuyButton, modelItem, item.getPrice()));
                if (modelItem.getModelItemType().equals(ModelItemType.MODEL))
                    mItemImage.setBackgroundResource(modelItem.getImageRes());
                else
                    mItemImage.setBackgroundResource(R.drawable.ar_item_view_icon);
            } else if (item instanceof UnityItem) {
                UnityItem unityItem = (UnityItem) item;
                mItemType.setText("模型");
                mItemBuyButton.setOnClickListener(v ->
                        showBuyUnityModelDialog(mItemBuyButton, unityItem, item.getPrice()));
                mItemImage.setBackgroundResource(unityItem.getImgRec());
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

    void showBuyUnityModelDialog(Button button, UnityItem unityItem, int coast) {
        new AlertDialog.Builder(ShopActivity.this)
                .setTitle("购买" + unityItem.getItemName())
                .setMessage(String.format("是否要购买%s?\n你将花费%d个金币", unityItem.getItemName(), coast))
                .setPositiveButton("购买", (dialog, which) -> {
                    User user = UserLab.getCurrentUser();
                    if (user.getGold() < coast) {
                        Toast.makeText(this, String.format("当前金币数(%d)不足，无法购买", user.getGold()), Toast.LENGTH_LONG).show();
                        return;
                    }
                    UserLab.getCurrentUser()
                            .getUnityItems()
                            .add(unityItem);
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
