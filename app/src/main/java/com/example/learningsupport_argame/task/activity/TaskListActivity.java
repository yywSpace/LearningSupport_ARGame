package com.example.learningsupport_argame.task.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.NavigationController;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.bean.PairInfoBean;
import com.example.learningsupport_argame.tempararyfile.MultiSelectionSpinner;
import com.example.learningsupport_argame.tempararyfile.TaskListFragment;
import com.example.learningsupport_argame.tempararyfile.CurrentTaskFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {
    private String mCurrentUserId;
    private BottomNavigationViewEx bnve;
    private VpAdapter adapter;
    private List<Fragment> fragments;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasklist_navigation_layout);
        // 初始化导航栏信息
        new NavigationController(this, getWindow().getDecorView());
        mCurrentUserId = getIntent().getStringExtra(User.CURRENT_USER_ID);
        if (mCurrentUserId == null) {
            mCurrentUserId = "4";
        }

        ActivityUtil.addActivity(this);
        initView();
        initData();
        initBNVE();
        initEvent();

    }

    /**
     * init BottomNavigationViewEx envent
     */
    private void initEvent() {
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            Menu menu = bnve.getMenu();
            MenuItem lastItem = menu.findItem(R.id.menu_main);

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int position = -1;
                switch (item.getItemId()) {
                    case R.id.menu_main:
                        position = 0;
                        if (lastItem != null)
                            lastItem.setIcon(R.drawable.renwuall);
                        item.setIcon(R.drawable.renwuselected);
                        lastItem = item;
                        break;
                    case R.id.menu_me:
                        position = 1;
                        if (lastItem != null)
                            lastItem.setIcon(R.drawable.renwu);

                        item.setIcon(R.drawable.renwuallselected);
                        lastItem = item;
                        break;
//                    case R.id.menu_empty: {
//                        position = 1;
//                        //此处return false且在FloatingActionButton没有自定义点击事件时 会屏蔽点击事件
//                       // return false;
//                   }
                    default:
                        break;
                }

                if (previousPosition != position) {
                    viewPager.setCurrentItem(position, false);
                    previousPosition = position;
                }

                return true;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 1 is center
                // 此段结合屏蔽FloatingActionButton点击事件的情况使用
                // 在viewPage滑动的时候 跳过最中间的page
                if (position >= 1)
                    position++;

                bnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /**
         * fab 点击事件结合OnNavigationItemSelectedListener中return false使用
         */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(TaskListActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.task_create_layout, null);//获取自定义布局
                CreateTaskViewAdapter taskViewAdapter = new CreateTaskViewAdapter(layout);

                builder.setView(taskViewAdapter.getView());
                builder.setTitle("创建任务");
                builder.setIcon(R.drawable.ziji);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 保存数据
                        String taskName = taskViewAdapter.mTaskNameEditText.getText().toString();
                        int taskType = taskViewAdapter.mTaskType;
                        String taskStartTime = taskViewAdapter.mTaskStartTime.getText().toString();
                        String taskEndTime = taskViewAdapter.mTaskEndTime.getText().toString();
                        String taskAccomplishLoc = taskViewAdapter.mTaskLocation.getText().toString();
                        String taskDesc = taskViewAdapter.mTaskDescEditText.getText().toString();

                        Toast.makeText(TaskListActivity.this, "taskStartTime: " + taskStartTime, Toast.LENGTH_SHORT).show();

                        // 如果为用AR发布，则设置模型
                        if (taskType == 3) {
                            Toast.makeText(TaskListActivity.this, "AR放置模型", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                Toast.makeText(TaskListActivity.this, "Center", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        floatingActionButton = findViewById(R.id.fab);
        viewPager = findViewById(R.id.vp);
        bnve = findViewById(R.id.bnve);
    }

    /**
     * create fragments
     */
    private void initData() {
        fragments = new ArrayList<>(3);
        CurrentTaskFragment currentTaskFragment = CurrentTaskFragment.getInstance(mCurrentUserId);
        TaskListFragment taskListFragment = TaskListFragment.getInstance(mCurrentUserId);
        fragments.add(currentTaskFragment);
        fragments.add(taskListFragment);

    }

    /**
     * init BottomNavigationViewEx
     */
    private void initBNVE() {

        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);

        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

    }

    /**
     * view pager adapter
     */
    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;
        private FragmentManager fragmentManager;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
            this.fragmentManager = fm;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {

            return data.get(position);
        }
    }


    class CreateTaskViewAdapter {
        private View mCreateTaskView;
        EditText mTaskNameEditText;
        Spinner mChooseTaskType;
        ImageView mChooseTaskStartDate;
        ImageView mChooseTaskStartTime;
        TextClock mTaskStartTime;
        ImageView mChooseTaskEndDate;
        ImageView mChooseTaskEndTime;
        TextClock mTaskEndTime;
        ImageView mChooseLocation;
        TextView mTaskLocation;
        FrameLayout mTaskTypeDynamicLayout;
        EditText mTaskDescEditText;
        /**
         * 0:自己
         * 1:好友
         * 2:社团
         * 3:AR
         */
        int mTaskType = 0;


        public View getView() {
            return mCreateTaskView;
        }

        CreateTaskViewAdapter(View createTaskView) {
            mTaskNameEditText = createTaskView.findViewById(R.id.task_create_enter_name);
            mChooseTaskType = createTaskView.findViewById(R.id.task_create_choose_type);
            mChooseTaskStartDate = createTaskView.findViewById(R.id.task_create_set_start_date);
            mChooseTaskStartTime = createTaskView.findViewById(R.id.task_create_set_start_time);
            mTaskStartTime = createTaskView.findViewById(R.id.task_create_start_time);
            mChooseTaskEndDate = createTaskView.findViewById(R.id.task_create_set_end_date);
            mChooseTaskEndTime = createTaskView.findViewById(R.id.task_create_set_end_time);
            mTaskEndTime = createTaskView.findViewById(R.id.task_create_end_time);
            mChooseLocation = createTaskView.findViewById(R.id.task_create_set_location);
            mTaskLocation = createTaskView.findViewById(R.id.task_create_location);
            mTaskTypeDynamicLayout = createTaskView.findViewById(R.id.layout_task_type);
            mTaskDescEditText = createTaskView.findViewById(R.id.task_create_enter_task_desc);

            mChooseTaskType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mTaskType = i;
                    switch (mTaskType) {
                        case 0: // 对自己发布
                            mTaskTypeDynamicLayout.removeAllViews();

                        case 3: // 使用AR发布
                            mTaskTypeDynamicLayout.removeAllViews();
                            break;
                        case 1:
                            // 对好友发布
                            // TODO: 19-11-3 适配数据
                            mTaskTypeDynamicLayout.removeAllViews();
                            MultiSelectionSpinner chooseFriends = new MultiSelectionSpinner(TaskListActivity.this);
                            List<PairInfoBean> peopleList = new ArrayList<>();
                            List<String> peopleListString = new ArrayList<>();
                            char a = 'A';
                            for (int j = 0; j < 10; j++) {
                                PairInfoBean pairInfoBean = new PairInfoBean();
                                pairInfoBean.pairName = a + "/" + j;
                                peopleListString.add(pairInfoBean.pairName);
                                peopleList.add(pairInfoBean);
                                a++;
                            }
                            ArrayAdapter<PairInfoBean> adapter = new ArrayAdapter<PairInfoBean>(TaskListActivity.this, android.R.layout.simple_spinner_dropdown_item, peopleList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            chooseFriends.setItems(peopleListString);
                            mTaskTypeDynamicLayout.addView(chooseFriends);
                            chooseFriends.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                @Override
                                public void selectedIndices(List<Integer> indices) {
                                    Toast.makeText(TaskListActivity.this, "22222", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void selectedStrings(List<String> strings) {

                                }
                            });
                            break;
                        case 2:
                            // 对社团发布
                            // TODO: 19-11-3 适配数据

                            mTaskTypeDynamicLayout.removeAllViews();
                            MultiSelectionSpinner chooseSociety = new MultiSelectionSpinner(TaskListActivity.this);

                            List<String> shetuanListString = new ArrayList<>();
                            char b = 'A';
                            for (int j = 0; j < 10; j++) {
                                shetuanListString.add(b + "/" + j);
                                b++;
                            }
                            chooseSociety.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                @Override
                                public void selectedIndices(List<Integer> indices) {
                                    Toast.makeText(TaskListActivity.this, "22222", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void selectedStrings(List<String> strings) {

                                }
                            });
                            chooseSociety.setItems(shetuanListString);
                            mTaskTypeDynamicLayout.addView(chooseSociety);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mChooseLocation.setOnClickListener(v -> {
                Toast.makeText(TaskListActivity.this, "地图选点", Toast.LENGTH_SHORT).show();
            });

            mChooseTaskStartDate.setOnClickListener(v -> {
                //设置DateDialog为当前时间
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog date = new DatePickerDialog(
                        TaskListActivity.this, (view, year, month, dayOfMonth) -> {
                    Toast.makeText(TaskListActivity.this, year + "-" + month + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
                    mTaskStartTime.setFormat24Hour(year + "-" + month + "-" + dayOfMonth);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                date.setTitle("选择开始日期");
                date.show();
            });

            mChooseTaskStartTime.setOnClickListener(v -> {

                TimePickerDialog.OnTimeSetListener timeListener = (view, hourOfDay, minute) -> {
                    Toast.makeText(TaskListActivity.this, "hourOfDay:" + hourOfDay + ",minute:" + minute, Toast.LENGTH_SHORT).show();
                };
                TimePickerDialog timePicker = new TimePickerDialog(TaskListActivity.this, timeListener, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                timePicker.setTitle("选择开始时间");
                timePicker.show();
            });

            mChooseTaskEndDate.setOnClickListener(v -> {
                DatePickerDialog.OnDateSetListener startDateListener = (view, year, monthOfYear, dayOfMonth) -> {
                    mTaskEndTime.setFormat24Hour(year + "-" + monthOfYear + "-" + dayOfMonth);
                    Toast.makeText(TaskListActivity.this, year + "-" + monthOfYear + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
                };
                DatePickerDialog datePicker = new DatePickerDialog(TaskListActivity.this, startDateListener, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
                datePicker.setTitle("选择结束日期");
                datePicker.show();
            });
            mChooseTaskEndTime.setOnClickListener(v -> {
                TimePickerDialog.OnTimeSetListener timeListener = (view, hourOfDay, minute) -> {

                    Toast.makeText(TaskListActivity.this, "hourOfDay:" + hourOfDay + ",minute:" + minute, Toast.LENGTH_SHORT).show();
                };
                TimePickerDialog timePicker = new TimePickerDialog(TaskListActivity.this, timeListener, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                timePicker.setTitle("选择结束时间");
                timePicker.show();
            });
            mCreateTaskView = createTaskView;
        }
    }
}

