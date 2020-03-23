package com.example.learningsupport_argame.UserManagement.bag;

import android.app.Activity;
import android.content.Context;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.ARModel.Items.ModelItem;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.TaskReward.RewardItem;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

/**
 * 两种类型道具，第一种为AR模型ModelItems，第二种奖励道具RewardItems
 */
public class UserBagActivity extends Activity {
    private FrameLayout mReturnLayout;
    private RecyclerView mBagRecyclerView;
    private UserBagListAdapter mUserBagListAdapter;
    private List<Item> mItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_bag_activity_layout);
        mItemList = new ArrayList<>();
        mItemList.addAll(UserLab.getCurrentUser().getRewardItems());
//        mItemList.addAll(ModelItemsLab.get().getItemList());
        mReturnLayout = findViewById(R.id.bag_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mUserBagListAdapter = new UserBagListAdapter(mItemList);
        mBagRecyclerView = findViewById(R.id.bag_list_recycler_view);
        mBagRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBagRecyclerView.setAdapter(mUserBagListAdapter);
    }

    class UserBagListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Item> mItemList;

        public UserBagListAdapter(List<Item> itemList) {
            mItemList = itemList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserBagListViewHolder(LayoutInflater.from(getApplicationContext())
                            .inflate(R.layout.user_bag_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserBagListViewHolder) {
                ((UserBagListViewHolder) holder).bind(mItemList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }
    }

    // TODO: 20-3-23 适配各种模型图片
    class UserBagListViewHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImage;
        private TextView mItemName;
        private TextView mItemType;
        private TextView mItemNumber;
        private TextView mItemDesc;
        private TextView mItemUseButton;

        public UserBagListViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemImage = itemView.findViewById(R.id.bag_item_image);
            mItemName = itemView.findViewById(R.id.bag_item_title);
            mItemType = itemView.findViewById(R.id.bag_item_type);
            mItemNumber = itemView.findViewById(R.id.bag_item_number);
            mItemDesc = itemView.findViewById(R.id.bag_item_desc);
            mItemUseButton = itemView.findViewById(R.id.bag_item_use_button);
        }

        public void bind(Item item) {
            if (item instanceof RewardItem) {
                RewardItem rewardItem = (RewardItem) item;
                mItemType.setText("道具");
                mItemNumber.setText(rewardItem.getCount() + "个");
                switch (rewardItem.getRewardItemType()) {
                    case ITEM_EXP_POTION:
                        mItemName.setText("经验增加");
                        mItemImage.setBackgroundResource(R.drawable.bag_exp_icon);
                        mItemDesc.setText("可适量增加经验值(100)");
                        mItemUseButton.setOnClickListener(v -> showExpItemUseDialog());
                        break;
                    case ITEM_SPEED_POTION:
                        mItemName.setText("时间加速");
                        mItemImage.setBackgroundResource(R.drawable.bag_speed_icon);
                        mItemDesc.setText("可适当加速任务的执行(x1.3)");
                        mItemUseButton.setOnClickListener(v -> showSpeedItemUseDialog());
                        break;
                    case ITEM_HEALING_POTION:
                        mItemName.setText("治疗");
                        mItemImage.setBackgroundResource(R.drawable.bag_heal_icon);
                        mItemDesc.setText("治疗用户5点血量");
                        mItemUseButton.setOnClickListener(v -> showHealItemUseDialog());
                        break;
                }
                if (rewardItem.getCount() <= 0) {
                    mItemUseButton.setClickable(false);
                    mItemUseButton.setBackgroundColor(Color.parseColor("#d4d4d4"));
                } else {
                    mItemUseButton.setClickable(true);
                    mItemUseButton.setBackgroundColor(Color.parseColor("#33b5e5"));
                }
            } else if (item instanceof ModelItem) {
                mItemUseButton.setClickable(false);
                mItemUseButton.setBackgroundColor(Color.parseColor("#d4d4d4"));
            }
        }

        void showHealItemUseDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(UserBagActivity.this)
                    .setTitle("治疗道具")
                    .setIcon(R.drawable.bag_heal_icon)
                    .setMessage("确定要使用道具吗？\n你将增加5点血量值。")
                    .setPositiveButton("确认", (dialog, which) -> {

                    })
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
        }

        void showSpeedItemUseDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(UserBagActivity.this)
                    .setTitle("加速道具")
                    .setIcon(R.drawable.bag_speed_icon)
                    .setMessage("确定要使用道具吗？\n下一次任务你将获得加速效果。")
                    .setPositiveButton("确认", (dialog, which) -> {

                    })
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
        }

        void showExpItemUseDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(UserBagActivity.this)
                    .setTitle("经验道具")
                    .setIcon(R.drawable.bag_exp_icon)
                    .setMessage("确定要使用道具吗？\n你将增加100点经验值。")
                    .setPositiveButton("确认", (dialog, which) -> {

                    })
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
        }
    }
}
