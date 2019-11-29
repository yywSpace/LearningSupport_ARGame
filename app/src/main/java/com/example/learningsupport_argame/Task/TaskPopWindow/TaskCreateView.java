package com.example.learningsupport_argame.Task.TaskPopWindow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.MultiSelectionSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskCreateView {
    private View mTaskCreateView;
    private AppCompatActivity mContext;
    private EditText mTaskNameEditText;
    private Spinner mChooseTaskType;
    private ImageView mChooseTaskStartDate;
    private ImageView mChooseTaskStartTime;
    private TextView mTaskStartTime;
    private ImageView mChooseTaskEndDate;
    private ImageView mChooseTaskEndTime;
    private TextView mTaskEndTime;
    private ImageView mChooseLocation;
    private TextView mTaskLocation;
    private FrameLayout mTaskTypeDynamicLayout;
    private EditText mTaskDescEditText;

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


    public View getView() {
        return mTaskCreateView;
    }

    public void setView(View view) {
        mTaskCreateView = view;
    }

    public TaskCreateView(AppCompatActivity context) {
        mContext = context;

        mTaskCreateView = mContext.getLayoutInflater().inflate(R.layout.task_create_layout, null, false);
        mTaskNameEditText = mTaskCreateView.findViewById(R.id.task_create_enter_name);
        mChooseTaskType = mTaskCreateView.findViewById(R.id.task_create_choose_type);
        mChooseTaskStartDate = mTaskCreateView.findViewById(R.id.task_create_set_start_date);
        mChooseTaskStartTime = mTaskCreateView.findViewById(R.id.task_create_set_start_time);
        mTaskStartTime = mTaskCreateView.findViewById(R.id.task_create_start_time);
        mChooseTaskEndDate = mTaskCreateView.findViewById(R.id.task_create_set_end_date);
        mChooseTaskEndTime = mTaskCreateView.findViewById(R.id.task_create_set_end_time);
        mTaskEndTime = mTaskCreateView.findViewById(R.id.task_create_end_time);
        mChooseLocation = mTaskCreateView.findViewById(R.id.task_create_set_location);
        mTaskLocation = mTaskCreateView.findViewById(R.id.task_create_location);
        mTaskTypeDynamicLayout = mTaskCreateView.findViewById(R.id.layout_task_type);
        mTaskDescEditText = mTaskCreateView.findViewById(R.id.task_create_enter_task_desc);

        // 设置任务时间默认为当前时间
        Calendar calendar = Calendar.getInstance();
        String currentTime = String.format("%s-%s-%s %s:%02d",
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
                        mTaskTypeDynamicLayout.removeAllViews();

                    case 3: // 使用AR发布
                        mTaskTypeDynamicLayout.removeAllViews();
                        break;
                    case 1:
                        // 对好友发布
                        // TODO: 19-11-3 适配数据
                        mTaskTypeDynamicLayout.removeAllViews();
                        MultiSelectionSpinner chooseFriends = new MultiSelectionSpinner(mContext);
//                        List<PairInfoBean> peopleList = new ArrayList<>();
//                        List<String> peopleListString = new ArrayList<>();
//                        char a = 'A';
//                        for (int j = 0; j < 10; j++) {
//                            PairInfoBean pairInfoBean = new PairInfoBean();
//                            pairInfoBean.pairName = a + "/" + j;
//                            peopleListString.add(pairInfoBean.pairName);
//                            peopleList.add(pairInfoBean);
//                            a++;
//                        }
//                        ArrayAdapter<PairInfoBean> adapter = new ArrayAdapter<PairInfoBean>(mContext, android.R.layout.simple_spinner_dropdown_item, peopleList);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        chooseFriends.setItems(peopleListString);
//                        mTaskTypeDynamicLayout.addView(chooseFriends);
//                        chooseFriends.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
//                            @Override
//                            public void selectedIndices(List<Integer> indices) {
//                                Toast.makeText(mContext, "22222", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void selectedStrings(List<String> strings) {
//
//                            }
//                        });
                        break;
                    case 2:
                        // 对社团发布
                        // TODO: 19-11-3 适配数据

                        mTaskTypeDynamicLayout.removeAllViews();
                        MultiSelectionSpinner chooseSociety = new MultiSelectionSpinner(mContext);

                        List<String> shetuanListString = new ArrayList<>();
                        char b = 'A';
                        for (int j = 0; j < 10; j++) {
                            shetuanListString.add(b + "/" + j);
                            b++;
                        }
                        chooseSociety.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                            @Override
                            public void selectedIndices(List<Integer> indices) {
                                Toast.makeText(mContext, "22222", Toast.LENGTH_SHORT).show();
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
            // TODO: 19-11-4 地图选点
            mTaskLocation.setText("地图选点");
        });

        mChooseTaskStartDate.setOnClickListener(v -> {
            //设置DateDialog为当前时间
            DatePickerDialog date = new DatePickerDialog(
                    mContext, (view, year, month, dayOfMonth) -> {
                mStartTimes[0] = year + "-" + (month + 1) + "-" + dayOfMonth;
                mTaskStartTime.setText(mStartTimes[0] + " " + (mStartTimes[1] == null ? "" : mStartTimes[1]));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            date.setTitle("选择开始日期");
            date.show();


        });

        mChooseTaskStartTime.setOnClickListener(v -> {

            TimePickerDialog timePicker = new TimePickerDialog(
                    mContext,
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
                    mContext,
                    (view, year, month, dayOfMonth) -> {
                        mEndTimes[0] = year + "-" + (month + 1) + "-" + dayOfMonth;
                        Toast.makeText(mContext, mEndTimes[0], Toast.LENGTH_SHORT).show();
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
                    mContext,
                    (view, hourOfDay, minute) -> {
                        mEndTimes[1] = String.format("%02d:%02d", hourOfDay, minute);
                        mTaskEndTime.setText((mEndTimes[0] == null ? "" : mEndTimes[0]) + " " + mEndTimes[1]);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), false);
            timePicker.setTitle("选择结束时间");
            timePicker.show();

        });
    }

    public void showTaskCreateDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(mTaskCreateView)
                .setTitle("创建任务")
                .setIcon(R.drawable.ziji)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] taskReleaseTypeStr = new String[]{"个人任务", "好友任务", "社团任务", "AR任务"};
                int taskType = mTaskType;
                String[] taskStartTimeArray = mStartTimes;
                String[] taskEndTimeArray = mEndTimes;

                // 保存数据
                Task task = new Task();
                task.setTaskName(mTaskNameEditText.getText().toString());
                task.setTaskType(taskReleaseTypeStr[taskType]);
                task.setTaskStartAt(mTaskStartTime.getText().toString());
                task.setTaskEndIn(mTaskEndTime.getText().toString());
                task.setAccomplishTaskLocation(mTaskLocation.getText().toString());
                task.setTaskContent(mTaskDescEditText.getText().toString());
                task.setTaskCreateTime(mTaskCreateTime);
                task.setUserId(UserLab.getCurrentUser().getId());
                task.setTaskStatus("未开始");

                if (task.getTaskName().equals("")) {
                    Toast.makeText(mContext, "请输入任务名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (taskStartTimeArray[0] == null || taskStartTimeArray[1] == null || taskEndTimeArray[0] == null || taskEndTimeArray[1] == null) {
                    Toast.makeText(mContext, "请输入任务日期和时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (task.getAccomplishTaskLocation().equals("")) {
                    Toast.makeText(mContext, "请输入任务完成地点", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (task.getTaskContent().equals("")) {
                    Toast.makeText(mContext, "请输入任务描述", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 如果为用AR发布，则传递数据到Unity,并设置模型
                if (taskType == 3) {
                    Toast.makeText(mContext, "AR放置模型", Toast.LENGTH_SHORT).show();
                }

                // 存储数据
                new Thread(() -> {
                    TaskLab.insertTask(task);
                }).start();
                // 如果任务信息没有错误，销毁对话框
                dialog.dismiss();
            }
        });
    }
}