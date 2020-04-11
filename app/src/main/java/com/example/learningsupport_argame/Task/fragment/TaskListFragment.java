package com.example.learningsupport_argame.Task.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.ARModel.PutModelActivity;
import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.Navi.Activity.SelectLocationPopWindow;
import com.example.learningsupport_argame.NavigationActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class TaskListFragment extends Fragment {
    private AppCompatActivity mActivity;
    public static String sCurrentListType;
    private static String TAG = "TaskListFragment";
    private static final int PUT_MODEL_ACCOMPLISH = 1;
    private VpAdapter mVpAdapter;
    private List<Fragment> mFragmentList;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButton mFloatingActionSearchButton;
    private BottomNavigationViewEx mNavigationView;
    private AlertDialog mCreateTaskDialog;
    // 第一次为insert如果用户返回则应修改信息
    private boolean hasInsertTask = false;
    private boolean isPermissionRequested = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) getActivity();
    }

    public static TaskListFragment getInstance(int activityType) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("task_list_activity_type", activityType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = LayoutInflater.from(mActivity).inflate(R.layout.tasklist_layout, container, false);
        mToolbar = view.findViewById(R.id.task_list_toolbar);
        mToolbar.setTitle("执行中");

        if (getArguments() != null) {
            int taskType = getArguments().getInt("task_list_activity_type", 1);
            if (taskType == 1) {
                mToolbar.setNavigationOnClickListener(v -> {
                    ((NavigationActivity) mActivity).openNavigation();
                });
            } else {
                mToolbar.setNavigationIcon(R.drawable.course_ic_return);
                mToolbar.setNavigationOnClickListener(v -> {
                    mActivity.finish();
                });
            }
        }

        mNavigationView = view.findViewById(R.id.task_list_navigation_view);

        mFloatingActionButton = view.findViewById(R.id.task_list_add_task);
        mFloatingActionSearchButton = view.findViewById(R.id.task_list_search);
        mFloatingActionSearchButton.setOnClickListener((v) -> mActivity.onSearchRequested());
        mViewPager = view.findViewById(R.id.task_list_vp_content);
        mFragmentList = new ArrayList<>(Arrays.asList(
                CurrentTaskFragment.getInstance(),
                TaskAcceptedListFragment.getInstance(),
                TaskCanAcceptListFragment.getInstance()
        ));
        mFloatingActionSearchButton.setVisibility(View.INVISIBLE);

        mViewPager.setCurrentItem(0);
        mVpAdapter = new VpAdapter(getChildFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mVpAdapter);
        initBNVE();
        initEvent();
        return view;
    }

    private void requestPermission() {
        if (!isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                    Manifest.permission.VIBRATE
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != mActivity.checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }
            if (!permissionsList.isEmpty()) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 0);
            }
        }
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
                        mToolbar.setTitle("执行中");
                        item.setIcon(R.drawable.task_list_icon_running_sellected);
                        mFloatingActionSearchButton.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.menu_task_accepted:
                        mFloatingActionSearchButton.setVisibility(View.VISIBLE);
                        mToolbar.setTitle("已接受的任务");
                        sCurrentListType = "已接受的任务";
                        position = 1;
                        item.setIcon(R.drawable.navigation_task);
                        break;
                    case R.id.menu_task_can_accept: {
                        mFloatingActionSearchButton.setVisibility(View.VISIBLE);
                        mToolbar.setTitle("任务列表");
                        sCurrentListType = "任务列表";
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
                if (UserLab.getCurrentUser().getHp() <= 0) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("血量过少")
                            .setMessage("当前血量过少，无法接受任务。\n您可以使用血量道具或等待血量恢复")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                Task task = new Task();
                hasInsertTask = false;
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.task_create_layout, null);//获取自定义布局
                CreateTaskViewAdapter taskViewAdapter = new CreateTaskViewAdapter(layout);
                mCreateTaskDialog = new AlertDialog.Builder(mActivity)
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
                    String[] taskReleaseTypeStr = new String[]{"个人任务", "好友任务", "社团任务", "全体任务"};
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

                    if (taskViewAdapter.getClub() != null) {
                        task.setReleaseClubId(taskViewAdapter.getClub().getId());
                    }

                    Log.d(TAG, "onClick: " + taskViewAdapter.getClub());
                    if (taskType == 2 && taskViewAdapter.getClub() == null) {
                        Toast.makeText(mActivity, "请选择要发布的社团", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (task.getTaskName().equals("")) {
                        Toast.makeText(mActivity, "请输入任务名称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (taskStartTimeArray[0] == null || taskStartTimeArray[1] == null || taskEndTimeArray[0] == null || taskEndTimeArray[1] == null) {
                        Toast.makeText(mActivity, "请输入任务日期和时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (task.getAccomplishTaskLocation().equals("")) {
                        Toast.makeText(mActivity, "请输入任务完成地点", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (task.getTaskContent().equals("")) {
                        Toast.makeText(mActivity, "请输入任务描述", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(mActivity, PutModelActivity.class);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        TextView mTaskClubSelectTV;
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
        int mClubIndex = -1;
        private List<RadioButton> mRadioButtonList = new ArrayList<>();
        private List<Club> mClubList = null;
        private View mSelectClubView = null;

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
            mTaskClubSelectTV = createTaskView.findViewById(R.id.task_club_select_layout);
            mTaskDescEditText = createTaskView.findViewById(R.id.task_create_enter_task_desc);
            mChooseTaskAR = createTaskView.findViewById(R.id.task_create_choose_ar);
            mTaskClubSelectTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRadioButtonList = new ArrayList<>();
                    mSelectClubView = LayoutInflater.from(mActivity).inflate(R.layout.task_release_select_club_layout, null, false);
                    LinearLayout linearLayout = mSelectClubView.findViewById(R.id.club_select_linear_layout);
                    new Thread(() -> {
                        if (mClubList == null)
                            mClubList = ClubLab.getParticipateClubList();
                        mActivity.runOnUiThread(() -> {
                            for (Club club : mClubList) {
                                View itemView = LayoutInflater.from(mActivity).inflate(R.layout.task_releas_club_select_item, null, false);
                                ImageView avatar = itemView.findViewById(R.id.club_image);
                                TextView name = itemView.findViewById(R.id.club_name);
                                TextView memberNum = itemView.findViewById(R.id.club_member_number);
                                RadioButton selectRadioButton = itemView.findViewById(R.id.club_select_radio_button);
                                mRadioButtonList.add(selectRadioButton);
                                selectRadioButton.setOnClickListener(v1 -> {
                                    for (RadioButton radioButton : mRadioButtonList) {
                                        if (selectRadioButton.equals(radioButton)) {
                                            mClubIndex = mRadioButtonList.indexOf(selectRadioButton);
                                            mTaskClubSelectTV.setText(mClubList.get(mClubIndex).getClubName());
                                            continue;
                                        }
                                        radioButton.setChecked(false);
                                    }
                                });
                                if (club.getCoverBitmap() != null)
                                    avatar.setImageBitmap(club.getCoverBitmap());
                                name.setText(club.getClubName());
                                memberNum.setText(club.getCurrentMemberNum() + "/" + club.getClubMaxMember());
                                linearLayout.addView(itemView);
                            }
                            new AlertDialog.Builder(mActivity)
                                    .setTitle("选择要发布的社团")
                                    .setView(mSelectClubView)
                                    .setPositiveButton("确认", null)
                                    .show();
                        });

                    }).start();
                }
            });
            // 设置任务时间默认为当前时间
            Calendar calendar = Calendar.getInstance();
            String currentTime = String.format("%d-%02d-%02d %d:%02d",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            mStartTimes[0] = mEndTimes[0] = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR),
                    (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));
            mStartTimes[1] = mEndTimes[1] = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            mTaskCreateTime = currentTime;
            mTaskStartTime.setText(currentTime);
            mTaskEndTime.setText(currentTime);

            mChooseTaskType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mTaskType = i;
                    switch (mTaskType) {
                        case 0:
                            // 对自己发布
                            mChooseTaskAR.setEnabled(false);
                            mTaskClubSelectTV.setVisibility(View.INVISIBLE);
                            break;
                        case 1:
                            // 对好友发布
                            mChooseTaskAR.setEnabled(true);
                            mTaskClubSelectTV.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            // 对社团发布
                            mChooseTaskAR.setEnabled(true);
                            mTaskClubSelectTV.setEnabled(true);
                            mTaskClubSelectTV.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            // 对全体发布
                            mTaskClubSelectTV.setVisibility(View.INVISIBLE);
                            mChooseTaskAR.setEnabled(true);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mChooseLocation.setOnClickListener(v -> {
                SelectLocationPopWindow slpw = new SelectLocationPopWindow(mActivity);
                slpw.setOnMarkerSet((address, latLng) -> {
                    mTaskLocation.setText(address);
                    mTaskAccomplishFullAddress = String.format("%s,%f,%f", address, latLng.latitude, latLng.longitude);
                    Toast.makeText(mActivity, address, Toast.LENGTH_SHORT).show();
                });
                slpw.showMapDialog();
            });

            mChooseTaskStartDate.setOnClickListener(v -> {
                //设置DateDialog为当前时间
                DatePickerDialog date = new DatePickerDialog(
                        mActivity, (view, year, month, dayOfMonth) -> {
                    mStartTimes[0] = String.format("%d-%02d-%02d", year, (month + 1), dayOfMonth);
                    mTaskStartTime.setText(mStartTimes[0] + " " + (mStartTimes[1] == null ? "" : mStartTimes[1]));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                date.setTitle("选择开始日期");
                date.show();
            });

            mChooseTaskStartTime.setOnClickListener(v -> {
                TimePickerDialog timePicker = new TimePickerDialog(
                        mActivity,
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
                        mActivity,
                        (view, year, month, dayOfMonth) -> {
                            mEndTimes[0] = String.format("%d-%02d-%02d", year, (month + 1), dayOfMonth);
                            Toast.makeText(mActivity, mEndTimes[0], Toast.LENGTH_SHORT).show();
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
                        mActivity,
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

        public Club getClub() {
            if (mClubIndex <= 0)
                return null;
            if (mClubList.size() <= mClubIndex)
                return null;
            return mClubList.get(mClubIndex);
        }
    }

}
