package com.example.learningsupport_argame.UserManagement.bag;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.ARModel.Items.ModelItem;
import com.example.learningsupport_argame.ARModel.Items.ModelItemType;
import com.example.learningsupport_argame.ARModel.Items.ModelItemsLab;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.TaskReward.RewardItem;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.learningsupport_argame.UserManagement.Login.LoginActivity.*;

/**
 * 两种类型道具，第一种为AR模型ModelItems，第二种奖励道具RewardItems
 */
public class UserBagActivity extends Activity {
    private String TAG = "UserBagActivity";
    private Toolbar mToolbar;
    private FrameLayout mReturnLayout;
    private RecyclerView mBagRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UserBagListAdapter mUserBagListAdapter;
    private List<Item> mItemList;
    private List<Item> mTypeItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_bag_activity_layout);
        mItemList = new ArrayList<>();
        mItemList.addAll(UserLab.getCurrentUser()
                .getRewardItems()
                .stream()
                .filter(rewardItem -> rewardItem.getCount() != 0)
                .collect(Collectors.toList()));
        mItemList.addAll(UserLab.getCurrentUser().getModelItems());
        mItemList.addAll(UserLab.getCurrentUser().getUnityItems());
        // 上面list为全部数据，下方type为显示的数据
        mTypeItemList = new ArrayList<>(mItemList);
        mToolbar = findViewById(R.id.bag_tool_bar);
        mToolbar.inflateMenu(R.menu.bag_menu);
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.bag_ar:
                    List<Item> modelItem = mItemList
                            .stream()
                            .filter(item1 -> item1 instanceof ModelItem)
                            .collect(Collectors.toList());
                    mTypeItemList.clear();
                    mTypeItemList.addAll(modelItem);
                    break;
                case R.id.bag_unity:
                    List<Item> unityItem = mItemList
                            .stream()
                            .filter(item1 -> item1 instanceof UnityItem)
                            .collect(Collectors.toList());
                    mTypeItemList.clear();
                    mTypeItemList.addAll(unityItem);
                    break;
                case R.id.bag_item:
                    List<Item> rewardItem = mItemList
                            .stream()
                            .filter(item1 -> item1 instanceof RewardItem)
                            .collect(Collectors.toList());
                    mTypeItemList.clear();
                    mTypeItemList.addAll(rewardItem);
                    break;
                case R.id.bag_all:
                    mTypeItemList.clear();
                    mTypeItemList.addAll(mItemList);
                    break;
                case R.id.reverse_order:
                    mTypeItemList.sort((item1, item2) -> {
                        if (item1.getCount() < item2.getCount())
                            return 1;
                        else if (item1.getCount() > item2.getCount())
                            return -1;
                        return 0;
                    });
                    break;
                case R.id.natural_order:
                    mTypeItemList.sort((item1, item2) -> {
                        if (item1.getCount() > item2.getCount())
                            return 1;
                        else if (item1.getCount() < item2.getCount())
                            return -1;
                        return 0;
                    });
                    break;
            }
            Log.d(TAG, "setOnMenuItemClickListener: " + mTypeItemList.size());
            mUserBagListAdapter.notifyDataSetChanged();
            return true;
        });
        mSwipeRefreshLayout = findViewById(R.id.bag_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            List<RewardItem> rewardItems = UserLab.getCurrentUser()
                    .getRewardItems()
                    .stream()
                    .filter(rewardItem -> rewardItem.getCount() != 0)
                    .collect(Collectors.toList());
            mItemList.clear();
            mItemList.addAll(rewardItems);
            mItemList.addAll(UserLab.getCurrentUser().getModelItems());
            mItemList.addAll(UserLab.getCurrentUser().getUnityItems());
            mTypeItemList.clear();
            mTypeItemList.addAll(mItemList);
            mUserBagListAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        });
        mReturnLayout = findViewById(R.id.bag_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mUserBagListAdapter = new UserBagListAdapter(mTypeItemList);
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
                mItemName.setText(item.getItemName());
                mItemDesc.setText(item.getItemDesc());
                mItemNumber.setText(item.getCount() + "个");

                if (item instanceof RewardItem) {
                    RewardItem rewardItem = (RewardItem) item;
                    mItemType.setText("道具");
                    mItemUseButton.setText("使用");
                    switch (rewardItem.getRewardItemType()) {
                        case ITEM_EXP_POTION:
                            mItemImage.setBackgroundResource(R.drawable.bag_exp_icon);
                            mItemUseButton.setOnClickListener(v -> showExpItemUseDialog());
                            break;
                        case ITEM_SPEED_POTION:
                            mItemImage.setBackgroundResource(R.drawable.bag_speed_icon);
                            mItemUseButton.setOnClickListener(v -> showSpeedItemUseDialog());
                            break;
                        case ITEM_HEALING_POTION:
                            mItemImage.setBackgroundResource(R.drawable.bag_heal_icon);
                            mItemUseButton.setOnClickListener(v -> showHealItemUseDialog());
                            break;
                        case ITEM_GOLD_POTION:
                            mItemImage.setBackgroundResource(R.drawable.bag_gold_icon);
                            mItemUseButton.setOnClickListener(v -> showGoldItemUseDialog());
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
                    ModelItem modelItem = (ModelItem) item;
                    mItemType.setText("AR模型");
                    mItemUseButton.setClickable(false);
                    mItemUseButton.setBackgroundColor(Color.parseColor("#d4d4d4"));
                    mItemUseButton.setText("使用");
                    if (modelItem.getModelItemType().equals(ModelItemType.MODEL))
                        mItemImage.setBackgroundResource(modelItem.getImageRes());
                    else
                        mItemImage.setBackgroundResource(R.drawable.ar_item_view_icon);
                } else if (item instanceof UnityItem) {
                    UnityItem unityItem = (UnityItem) item;
                    mItemUseButton.setBackgroundColor(Color.parseColor("#33b5e5"));
                    mItemType.setText("人物模型");
                    if (UserLab.getCurrentUser().getModName().equals(unityItem.getModName())) {
                        mItemUseButton.setClickable(false);
                        mItemUseButton.setText("已启用");
                    } else {
                        mItemUseButton.setClickable(true);
                        mItemUseButton.setText("启用");
                        mItemUseButton.setOnClickListener(v -> {
                            UserLab.getCurrentUser().setModName(unityItem.getModName());
                            mItemList.remove(item);
                            mItemList.add(0, item);
                            new Thread(() -> UserLab.updateUser(UserLab.getCurrentUser())).start();
                            notifyDataSetChanged();
                        });
                    }
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), unityItem.getImgRec());
                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    drawable.setCornerRadius(30);
                    mItemImage.setBackground(drawable);
                }
            }
        }


        void showHealItemUseDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(UserBagActivity.this)
                    .setTitle("治疗道具")
                    .setIcon(R.drawable.bag_heal_icon)
                    .setMessage("确定要使用道具吗？\n你将增加5点血量值。")
                    .setPositiveButton("确认", (dialog, which) -> {
                        User currentUser = UserLab.getCurrentUser();
                        RewardItem rewardItem = currentUser.getRewardItems()
                                .stream()
                                .filter(ri -> ri.getRewardItemType().equals(RewardItem.RewardItemType.ITEM_HEALING_POTION))
                                .findFirst().get();
                        rewardItem.setCount(rewardItem.getCount() - 1);
                        currentUser.setHp(Math.min(currentUser.getHp() + 5, User.BASIC_HP + currentUser.getLevel()));
                        mUserBagListAdapter.notifyDataSetChanged();
                        new Thread(() -> UserLab.updateUser(UserLab.getCurrentUser())).start();
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
                        User currentUser = UserLab.getCurrentUser();
                        if (currentUser.isNextTaskSpeedUp()) {
                            Toast.makeText(UserBagActivity.this, "下一次任务已经处于加速状态", Toast.LENGTH_SHORT).show();
                        } else {
                            currentUser.setNextTaskSpeedUp(true);
                            RewardItem rewardItem = currentUser.getRewardItems()
                                    .stream()
                                    .filter(ri -> ri.getRewardItemType().equals(RewardItem.RewardItemType.ITEM_SPEED_POTION))
                                    .findFirst().get();
                            rewardItem.setCount(rewardItem.getCount() - 1);
                            mUserBagListAdapter.notifyDataSetChanged();
                            new Thread(() -> UserLab.updateUser(UserLab.getCurrentUser())).start();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
        }

        void showGoldItemUseDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(UserBagActivity.this)
                    .setTitle("金币堆")
                    .setIcon(R.drawable.bag_speed_icon)
                    .setMessage("确定要使用道具吗？\n本次你将获得一百点黄金。")
                    .setPositiveButton("确认", (dialog, which) -> {
                        User currentUser = UserLab.getCurrentUser();
                        RewardItem rewardItem = currentUser.getRewardItems()
                                .stream()
                                .filter(ri -> ri.getRewardItemType().equals(RewardItem.RewardItemType.ITEM_GOLD_POTION))
                                .findFirst().get();
                        rewardItem.setCount(rewardItem.getCount() - 1);
                        currentUser.setGold(currentUser.getGold() + 100);
                        mUserBagListAdapter.notifyDataSetChanged();
                        new Thread(() -> UserLab.updateUser(UserLab.getCurrentUser())).start();
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
                        boolean isLevelUp = UserLab.getCurrentUser().levelUp(100);
                        if (isLevelUp)
                            Toast.makeText(UserBagActivity.this, "恭喜您的等级提升至" + UserLab.getCurrentUser().getLevel() + "级", Toast.LENGTH_SHORT).show();
                        RewardItem rewardItem = UserLab.getCurrentUser().getRewardItems()
                                .stream()
                                .filter(ri -> ri.getRewardItemType().equals(RewardItem.RewardItemType.ITEM_EXP_POTION))
                                .findFirst().get();
                        rewardItem.setCount(rewardItem.getCount() - 1);
                        mUserBagListAdapter.notifyDataSetChanged();
                        new Thread(() -> UserLab.updateUser(UserLab.getCurrentUser())).start();
                    })
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
        }

    }
}
