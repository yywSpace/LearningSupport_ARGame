package com.example.learningsupport_argame.Task.TaskPopWindow;


import android.graphics.Point;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskListPopWindow {
    private View mTaskListView;
    private AppCompatActivity mContext;
    private ViewPager mTaskViewPager;
    private TabLayout mTaskTabLayout;
    private List<View> mViewList;
    private List<String> mTitleList;
    private PopupWindow mPopupWindow;
    private ImageView mReturnImageView;
    private FloatingActionButton mAddFloatingActionButton;
    TaskCreateView mTaskCreateView;
    private Handler mHandler;

    public TaskListPopWindow(AppCompatActivity context) {
        mContext = context;
        mHandler = new Handler();
        mTaskListView = LayoutInflater.from(mContext).inflate(R.layout.task_list_pop_window_layout, null, false);
        mReturnImageView = mTaskListView.findViewById(R.id.task_list_pop_window_return);
        mReturnImageView.setOnClickListener(v -> mPopupWindow.dismiss());
        mAddFloatingActionButton = mTaskListView.findViewById(R.id.task_list_pop_window_add_task);
        mAddFloatingActionButton.setOnClickListener(v -> {
            mTaskCreateView = new TaskCreateView(mContext);
            mTaskCreateView.showTaskCreateDialog();
        });
        mTaskViewPager = mTaskListView.findViewById(R.id.task_list_pop_window_vp_content);
        mTaskViewPager.setOffscreenPageLimit(2);
        mTaskTabLayout = mTaskListView.findViewById(R.id.task_list_pop_window_tabs);
        CurrentTaskView currentTaskView = new CurrentTaskView(mContext);
        TaskListView taskListView = new TaskListView("4", mContext);
        mViewList = new ArrayList<>(Arrays.asList(
                currentTaskView.getView(),
                taskListView.getView()
        ));
        mTitleList = new ArrayList<>(Arrays.asList("当前任务", "所有任务"));

        mTaskViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mViewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitleList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = mViewList.get(position);
                container.addView(view);
                if (position == 0) {
                    new Thread(() -> {
                        List<Task> tasks = TaskLab.getAllTask("4");

                        mContext.runOnUiThread(() -> {
                            mHandler.post(() -> {
                                currentTaskView.initCurrentTaskData(tasks.get(0));
                            });
                        });
                    }).start();
                }
                return view;
            }
        });


        mTaskTabLayout.setupWithViewPager(mTaskViewPager);

        // 获取屏幕宽高，以防止pop window 充满真个个屏幕
        Point point = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(point);
        mPopupWindow = new PopupWindow(mTaskListView, point.x - 100, point.y - 100);
        mPopupWindow.setFocusable(false);//设置pw中的控件能够获取焦点
        mPopupWindow.setOutsideTouchable(false); //设置可以通过点击mPopupWindow外部关闭mPopupWindow
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mTaskCreateView.setView(null);
                mTaskCreateView = null;
            }
        });
        mPopupWindow.update();//刷新mPopupWindow
        mPopupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

}