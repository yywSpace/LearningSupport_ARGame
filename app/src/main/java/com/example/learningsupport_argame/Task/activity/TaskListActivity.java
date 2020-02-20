package com.example.learningsupport_argame.Task.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.ARModel.PutModelActivity;
import com.example.learningsupport_argame.Navi.Activity.SelectLocationPopWindow;
import com.example.learningsupport_argame.NavigationController;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.MultiSelectionSpinner;
import com.example.learningsupport_argame.Task.fragment.TaskAcceptedListFragment;
import com.example.learningsupport_argame.Task.fragment.TaskCanAcceptListFragment;
import com.example.learningsupport_argame.Task.fragment.CurrentTaskFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.example.learningsupport_argame.Task.TaskLab.mAcceptedTaskList;

public class TaskListActivity extends AppCompatActivity {
    private static String TAG = "TaskListActivity";
    private static final int PUT_MODEL_ACCOMPLISH = 1;
    private VpAdapter mVpAdapter;
    private List<Fragment> mFragmentList;
    private ViewPager mViewPager;
    private FloatingActionButton mFloatingActionButton;
    private BottomNavigationViewEx mNavigationView;
    private AlertDialog mCreateTaskDialog;
    // 第一次为insert如果用户返回则应修改信息
    private boolean hasInsertTask = false;
//    private ImageButton mReturnButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasklist_navigation_layout);
        // 初始化导航栏信息
        new NavigationController(this, getWindow().getDecorView());
//        mReturnButton = findViewById(R.id.navigation_button);
//        mReturnButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


        mNavigationView = findViewById(R.id.task_list_navigation_view);

        mFloatingActionButton = findViewById(R.id.task_list_add_task);
        mViewPager = findViewById(R.id.task_list_vp_content);

        mFragmentList = new ArrayList<>(Arrays.asList(
                CurrentTaskFragment.getInstance(this),
                TaskAcceptedListFragment.getInstance(this),
                TaskCanAcceptListFragment.getInstance(this)
        ));

        mViewPager.setCurrentItem(0);

        mVpAdapter = new VpAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mVpAdapter);
        ActivityUtil.addActivity(this);
        initBNVE();
        initEvent();
    }

    /**
     * init BottomNavigationViewEx envent
     */
    private void initEvent() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            Menu menu = mNavigationView.getMenu();

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                menu.findItem(R.id.menu_task_running).setIcon(R.drawable.task_list_icon_running);
                menu.findItem(R.id.menu_task_accepted).setIcon(R.drawable.task_list_icon_accepted);
                menu.findItem(R.id.menu_task_can_accept).setIcon(R.drawable.task_list_icon_all);

                int position = -1;
                switch (item.getItemId()) {
                    case R.id.menu_task_running:
                        item.setIcon(R.drawable.task_list_icon_running_sellected);
                        break;
                    case R.id.menu_task_accepted:
                        position = 1;
                        item.setIcon(R.drawable.navigation_task);
                        break;
                    case R.id.menu_task_can_accept: {
                        position = 2;
                        item.setIcon(R.drawable.task_list_icon_all_sellected);
                        // 此处return false且在FloatingActionButton没有自定义点击事件时 会屏蔽点击事件
                        // return false;
                    }
                    default:
                        break;
                }

                if (previousPosition != position) {
                    mViewPager.setCurrentItem(position, false);
                    previousPosition = position;
                }

                return true;
            }
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 1 is center
                // 此段结合屏蔽FloatingActionButton点击事件的情况使用
                // 在viewPage滑动的时候 跳过最中间的page
//                if (position >= 1)
//                    position++;

                mNavigationView.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        /**
         * fab 点击事件结合OnNavigationItemSelectedListener中return false使用
         */
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = new Task();
                hasInsertTask = false;
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.task_create_layout, null);//获取自定义布局
                CreateTaskViewAdapter taskViewAdapter = new CreateTaskViewAdapter(layout);
                mCreateTaskDialog = new AlertDialog.Builder(TaskListActivity.this)
                        .setView(taskViewAdapter.getView())
                        .setTitle("创建任务")
                        .setIcon(R.drawable.ziji)
                        .setNegativeButton("取消", (dialog, which) -> {
                            if (hasInsertTask)
                                new Thread(() -> {
                                    TaskLab.deleteReleasedTask(task);
                                }).start();
                        })
                        .setPositiveButton("确定", null)
                        .create();
                mCreateTaskDialog.show();
                mCreateTaskDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String[] taskReleaseTypeStr = new String[]{"个人任务", "好友任务", "社团任务"};
                    int taskType = taskViewAdapter.mTaskType;
                    String[] taskStartTimeArray = taskViewAdapter.mStartTimes;
                    String[] taskEndTimeArray = taskViewAdapter.mEndTimes;

                    // 保存数据
                    task.setTaskName(taskViewAdapter.mTaskNameEditText.getText().toString());
                    task.setTaskType(taskReleaseTypeStr[taskType]);
                    task.setTaskStartAt(taskViewAdapter.mTaskStartTime.getText().toString());
                    task.setTaskEndIn(taskViewAdapter.mTaskEndTime.getText().toString());
                    task.setAccomplishTaskLocation(taskViewAdapter.mTaskAccomplishFullAddress);
                    task.setTaskContent(taskViewAdapter.mTaskDescEditText.getText().toString());
                    task.setTaskCreateTime(taskViewAdapter.mTaskCreateTime);
                    task.setUserId(UserLab.getCurrentUser().getId());
                    task.setTaskStatus("未开始");

                    if (task.getTaskName().equals("")) {
                        Toast.makeText(TaskListActivity.this, "请输入任务名称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (taskStartTimeArray[0] == null || taskStartTimeArray[1] == null || taskEndTimeArray[0] == null || taskEndTimeArray[1] == null) {
                        Toast.makeText(TaskListActivity.this, "请输入任务日期和时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (task.getAccomplishTaskLocation().equals("")) {
                        Toast.makeText(TaskListActivity.this, "请输入任务完成地点", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (task.getTaskContent().equals("")) {
                        Toast.makeText(TaskListActivity.this, "请输入任务描述", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 存储数据
                    new Thread(() -> {
                        // 如果没有加入数据库就插入，否则更新
                        if (!hasInsertTask) {
                            TaskLab.insertTask(task);
                            // 如果为自己发布的任务直接添加进参与者列表
                            TaskLab.acceptTask(task);
                            hasInsertTask = true;
                        } else {
                            TaskLab.updateTask(task);
                        }
                        // 更新已接受列表
                        TaskLab.getAcceptedTask(UserLab.getCurrentUser().getId() + "");
                    }).start();

                    // 如果为用AR发布，放置模型
                    if (taskViewAdapter.mChooseTaskAR.isChecked()) {
                        Intent intent = new Intent(TaskListActivity.this, PutModelActivity.class);
                        intent.putExtra("task", task);
                        startActivityForResult(intent, PUT_MODEL_ACCOMPLISH);
                    } else {
                        mCreateTaskDialog.dismiss();
                    }

                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PUT_MODEL_ACCOMPLISH)
            if (resultCode == RESULT_OK) {
                mCreateTaskDialog.dismiss();
            }
    }

    /**
     * init BottomNavigationViewEx
     */
    private void initBNVE() {
        mNavigationView.enableAnimation(false);
        mNavigationView.enableShiftingMode(false);
        mNavigationView.enableItemShiftingMode(false);
    }


    /**
     * view pager mVpAdapter
     */
    class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        public VpAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            mFragmentList = fragmentList;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

    }


    class CreateTaskViewAdapter {
        private View mCreateTaskView;
        CheckBox mChooseTaskAR;
        EditText mTaskNameEditText;
        Spinner mChooseTaskType;
        ImageView mChooseTaskStartDate;
        ImageView mChooseTaskStartTime;
        TextView mTaskStartTime;
        ImageView mChooseTaskEndDate;
        ImageView mChooseTaskEndTime;
        TextView mTaskEndTime;
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
        String[] mStartTimes = new String[2];
        String[] mEndTimes = new String[2];
        String mTaskCreateTime;
        String mTaskAccomplishFullAddress = "";


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
            mChooseTaskAR = createTaskView.findViewById(R.id.task_create_choose_ar);

            // 设置任务时间默认为当前时间
            Calendar calendar = Calendar.getInstance();
            String currentTime = String.format("%s-%02s-%02s %s:%02d",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            mStartTimes[0] = mEndTimes[0] = calendar.get(Calendar.YEAR) + "-" +
                    (calendar.get(Calendar.MONTH) + 1) + "-" +
                    calendar.get(Calendar.DAY_OF_MONTH);
            mStartTimes[1] = mEndTimes[1] = calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                    String.format("%02d", calendar.get(Calendar.MINUTE));
            mTaskCreateTime = currentTime;
            mTaskStartTime.setText(currentTime);
            mTaskEndTime.setText(currentTime);

            mChooseTaskType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mTaskType = i;
                    switch (mTaskType) {
                        case 0: // 对自己发布
                            mChooseTaskAR.setEnabled(false);
                            mTaskTypeDynamicLayout.removeAllViews();
                            break;
                        case 1:
                            // 对好友发布
                            // TODO: 19-11-3 适配数据
                            mChooseTaskAR.setEnabled(true);
                            mTaskTypeDynamicLayout.removeAllViews();
                            MultiSelectionSpinner chooseFriends = new MultiSelectionSpinner(TaskListActivity.this);
//                            List<PairInfoBean> peopleList = new ArrayList<>();
//                            List<String> peopleListString = new ArrayList<>();
//                            char a = 'A';
//                            for (int j = 0; j < 10; j++) {
//                                PairInfoBean pairInfoBean = new PairInfoBean();
//                                pairInfoBean.pairName = a + "/" + j;
//                                peopleListString.add(pairInfoBean.pairName);
//                                peopleList.add(pairInfoBean);
//                                a++;
//                            }
//                            ArrayAdapter<PairInfoBean> adapter = new ArrayAdapter<PairInfoBean>(TaskListActivity.this, android.R.layout.simple_spinner_dropdown_item, peopleList);
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                            chooseFriends.setItems(peopleListString);
//                            mTaskTypeDynamicLayout.addView(chooseFriends);
//                            chooseFriends.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
//                                @Override
//                                public void selectedIndices(List<Integer> indices) {
//                                    Toast.makeText(TaskListActivity.this, "22222", Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void selectedStrings(List<String> strings) {
//
//                                }
//                            });
                            break;
                        case 2:
                            // 对社团发布
                            // TODO: 19-11-3 适配数据
                            mChooseTaskAR.setEnabled(true);
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
                SelectLocationPopWindow slpw = new SelectLocationPopWindow(TaskListActivity.this);
                slpw.setOnMarkerSet((address, latLng) -> {
                    mTaskLocation.setText(address);
                    mTaskAccomplishFullAddress = String.format("%s,%f,%f", address, latLng.latitude, latLng.longitude);
                    Toast.makeText(TaskListActivity.this, address, Toast.LENGTH_SHORT).show();
                });
                slpw.showMapDialog();
            });

            mChooseTaskStartDate.setOnClickListener(v -> {
                //设置DateDialog为当前时间
                DatePickerDialog date = new DatePickerDialog(
                        TaskListActivity.this, (view, year, month, dayOfMonth) -> {
                    mStartTimes[0] = String.format("%s-%02s-%02s %s:%02d", year, (month + 1), dayOfMonth);
                    mTaskStartTime.setText(mStartTimes[0] + " " + (mStartTimes[1] == null ? "" : mStartTimes[1]));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                date.setTitle("选择开始日期");
                date.show();
            });

            mChooseTaskStartTime.setOnClickListener(v -> {
                TimePickerDialog timePicker = new TimePickerDialog(
                        TaskListActivity.this,
                        (view, hourOfDay, minute) -> {
                            mStartTimes[1] = String.format("%02d:%02d", hourOfDay, minute);
                            mTaskStartTime.setText((mStartTimes[0] == null ? "" : mStartTimes[0]) + " " + mStartTimes[1]);
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false);
                timePicker.setTitle("选择开始时间");
                timePicker.show();

            });

            mChooseTaskEndDate.setOnClickListener(v -> {
                //设置DateDialog为当前时间
                DatePickerDialog datePicker = new DatePickerDialog(
                        TaskListActivity.this,
                        (view, year, month, dayOfMonth) -> {
                            mEndTimes[0] =  String.format("%s-%02s-%02s %s:%02d", year, (month + 1), dayOfMonth);
                            Toast.makeText(TaskListActivity.this, mEndTimes[0], Toast.LENGTH_SHORT).show();
                            mTaskEndTime.setText(mEndTimes[0] + " " + (mEndTimes[1] == null ? "" : mEndTimes[1]));
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("选择结束日期");
                datePicker.show();
            });
            mChooseTaskEndTime.setOnClickListener(v -> {
                TimePickerDialog timePicker = new TimePickerDialog(
                        TaskListActivity.this,
                        (view, hourOfDay, minute) -> {
                            mEndTimes[1] = String.format("%02d:%02d", hourOfDay, minute);
                            mTaskEndTime.setText((mEndTimes[0] == null ? "" : mEndTimes[0]) + " " + mEndTimes[1]);
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false);
                timePicker.setTitle("选择结束时间");
                timePicker.show();

            });
            mCreateTaskView = createTaskView;
        }
    }
}

