package com.example.learningsupport_argame.Course;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.learningsupport_argame.Course.PopupWindow.PromptAdapter;
import com.example.learningsupport_argame.R;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CourseTimeActivity extends AppCompatActivity {
    public final static String TAG = "CourseTimeActivity";
    public final static int SCHOOL_OPEN_DATE_RESULT_CODE = 200;
    private ImageView mCourseTimeReturn;
    private TextView mCourseNumberTV;
    private TextView mSchoolOpenDateTV;
    private TextView mCourseTimeSpanTV;
    private TextView mCourseSettingSubmit;
    private LinearLayout mCourseTimeListTV;
    private int mCourseNum;

    private Button mCourseNumCancel;
    private Button mCourseNumCommit;
    private Button mCourseTimeStartCommit;
    private Button mCourseTimeStartCancel;
    private Button mCourseTimeSpanCancel;
    private Button mCourseTimeSpanCommit;

    // 索引即为节数
    TextView[] mCourseStartTimes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_time_layout);
        initView();
        initData();
        initEvent();
    }

    void initView() {
        mSchoolOpenDateTV = findViewById(R.id.course_school_open_date);
        mCourseNumberTV = findViewById(R.id.course_number);
        mCourseTimeReturn = findViewById(R.id.course_time_return);
        mCourseTimeSpanTV = findViewById(R.id.course_time_span);
        mCourseTimeListTV = findViewById(R.id.course_start_time_list);
        mCourseSettingSubmit = findViewById(R.id.course_time_commit);
    }

    void initData() {
        if (CourseSetting.SCHOOL_OPEN_DATE != null)
            mSchoolOpenDateTV.setText(CourseSetting.SCHOOL_OPEN_DATE);
        if (CourseSetting.COURSE_NUMBER != 0)
            mCourseNumberTV.setText(CourseSetting.COURSE_NUMBER + "");
        if (CourseSetting.COURSE_TIME_SPAN != 0)
            mCourseTimeSpanTV.setText(CourseSetting.COURSE_TIME_SPAN + "");
        mCourseNum = CourseSetting.COURSE_NUMBER;
        mCourseStartTimes = new TextView[mCourseNum];
        for (int i = 0; i < CourseSetting.ALL_COURSE_START_TIME.size(); i++) {
            // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
            mCourseStartTimes[i] = new TextView(CourseTimeActivity.this);
            mCourseStartTimes[i].setText(CourseSetting.ALL_COURSE_START_TIME.get(i).trim());
            mCourseStartTimes[i].setTextColor(getResources().getColor(R.color.colorBlack));
            mCourseStartTimes[i].setTextSize(18);
            mCourseStartTimes[i].setOnClickListener(this::showTimePicker);
            View layout = LayoutInflater.from(CourseTimeActivity.this).inflate(R.layout.course_every_time_layout, null);
            LinearLayout everyTimeLayout = layout.findViewById(R.id.course_every_time_linear_layout);
            TextView textView = layout.findViewById(R.id.course_time_label);
            textView.setText("第" + (i + 1) + "节:  ");
            everyTimeLayout.addView(mCourseStartTimes[i]);
            mCourseTimeListTV.addView(layout);
        }
    }

    void initEvent() {
        mSchoolOpenDateTV.setOnClickListener(v -> {
            // 获取当前时间
            Calendar cTime = Calendar.getInstance();
            //主题在这里！后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
            DatePickerDialog dialog = new DatePickerDialog(CourseTimeActivity.this,
                    DatePickerDialog.BUTTON_POSITIVE,
                    (arg0, year, month, day) -> {
                        CourseSetting.SCHOOL_OPEN_DATE = year + "/" + (month + 1) + "/" + day;
                        setResult(SCHOOL_OPEN_DATE_RESULT_CODE);
                        mSchoolOpenDateTV.setText(CourseSetting.SCHOOL_OPEN_DATE);
                    }, cTime.get(Calendar.YEAR), cTime.get(Calendar.MONTH), cTime.get(Calendar.DAY_OF_MONTH));

            dialog.setCanceledOnTouchOutside(true);
            dialog.setTitle("");
            dialog.show();
        });
        mCourseSettingSubmit.setOnClickListener(v -> {
            for (int i = 0; i < mCourseNum; i++) {
                if (mCourseStartTimes[i].getText().toString().equals("")) {
                    Toast.makeText(CourseTimeActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "请填写完整信息: ");
                    return;
                }
            }
            if (mCourseNumberTV.getText().toString().equals("") || mCourseTimeSpanTV.getText().toString().equals("")) {
                Toast.makeText(CourseTimeActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            int courseSpan = Integer.parseInt(mCourseTimeSpanTV.getText().toString());
            List<String> startTimes = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

            List<Date> dateList = new ArrayList<>();
            for (int i = 0; i < mCourseNum; i++) {
                String time = mCourseStartTimes[i].getText().toString();
                try {
                    dateList.add(dateFormat.parse(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startTimes.add(i, time.trim());
            }
            for (int i = 1; i < mCourseNum; i++) {
                int m = (int) ((dateList.get(i).getTime() - dateList.get(i - 1).getTime()) / 1000 / 60);
                if (m < courseSpan) {
                    Toast.makeText(CourseTimeActivity.this,
                            String.format("第%d,%d节课上课时间间隔小于所设置间隔", i, i + 1),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            CourseSetting.ALL_COURSE_START_TIME.clear();
            CourseSetting.ALL_COURSE_START_TIME.addAll(startTimes);
            CourseSetting.COURSE_NUMBER = mCourseNum;
            CourseSetting.COURSE_TIME_SPAN = courseSpan;
            new Thread(CourseLab::insertCourseSetting).start();
            Toast.makeText(CourseTimeActivity.this, "时间设置成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
        mCourseNumberTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerViewCourseNum();
            }
        });
        mCourseTimeReturn.setOnClickListener(v -> finish());

        mCourseTimeSpanTV.setOnClickListener(v -> {
            // 要展示的数据
            final List<String> timeSpanList = new ArrayList<>();
            for (int i = 1; i <= 60; i++) {
                timeSpanList.add(String.valueOf(i));
            }
            // 监听选中
            final OptionsPickerView pvOptions = new OptionsPickerBuilder(CourseTimeActivity.this, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    // 返回的是选中位置
                    // 展示选中数据
                    mCourseTimeSpanTV.setText(timeSpanList.get(options1));
                }
            }).setLayoutRes(R.layout.course_pickerview_stair_layout, new CustomListener() {
                @Override
                public void customLayout(View v) {
                    TextView textView = v.findViewById(R.id.course_picker_title);
                    textView.setText("每节课的时间跨度(分)");
                    mCourseTimeSpanCancel = v.findViewById(R.id.course_picker_view_cancel_button);
                    mCourseTimeSpanCommit = v.findViewById(R.id.course_picker_view_commit_button);
                }
            }).setDividerColor(Color.BLACK)
                    .setContentTextSize(18)
                    .setCyclic(true, true, true)
                    .setTextColorCenter(Color.rgb(205, 104, 57))
                    .setDividerColor(Color.alpha(Color.BLACK))
                    .build();//创建

            mCourseTimeSpanCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pvOptions.returnData();
                    pvOptions.dismiss();
                }
            });
            mCourseTimeSpanCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pvOptions.dismiss();
                }
            });
            // 把数据绑定到控件上面
            pvOptions.setPicker(timeSpanList);
            // 展示
            pvOptions.show();
        });
    }

    public void showClearDataDialog(final int courseNum) {
        PromptAdapter.Builder builder = new PromptAdapter.Builder(CourseTimeActivity.this);
        builder.setTitle("提示");
        builder.setContent("修改课程节数需要清空课程信息，是否确定修改");
        builder.setRight("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mCourseNumberTV.setText(String.valueOf(courseNum));
                CourseLab.sCourseList.clear();
                runOnUiThread(() -> new Thread(CourseLab::deleteAllCourse).start());
                showEveryCourse();
            }
        });
        builder.setLeft("取消", (dialogInterface, i) -> dialogInterface.dismiss());
        PromptAdapter dialog = builder.create();
        dialog.show();
    }

    public void showEveryCourse() {
        new Handler(CourseTimeActivity.this.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                mCourseStartTimes = new TextView[mCourseNum];
                mCourseTimeListTV.removeAllViews();
                for (int i = 0; i < mCourseNum; i++) {
                    mCourseStartTimes[i] = new TextView(CourseTimeActivity.this);
                    mCourseStartTimes[i].setHint("未填写");
                    mCourseStartTimes[i].setTextColor(getResources().getColor(R.color.colorBlack));
                    mCourseStartTimes[i].setTextSize(18);
                    mCourseStartTimes[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showTimePicker(v);
                        }
                    });
                    View layout = LayoutInflater.from(CourseTimeActivity.this).inflate(R.layout.course_every_time_layout, null);
                    LinearLayout everyTimeLayout = layout.findViewById(R.id.course_every_time_linear_layout);
                    TextView textView = layout.findViewById(R.id.course_time_label);
                    textView.setText("第" + (i + 1) + "节:  ");
                    everyTimeLayout.addView(mCourseStartTimes[i]);
                    mCourseTimeListTV.addView(layout);
                }
            }
        });
    }

    private void showPickerViewCourseNum() {
        // 要展示的数据
        final List<String> courseNumberList = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            courseNumberList.add(String.valueOf(i));
        }
        // 监听选中
        final OptionsPickerView pvOptions = new OptionsPickerBuilder(CourseTimeActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                // 返回的是选中位置
                // 展示选中数据
                mCourseNum = Integer.parseInt(courseNumberList.get(options1));
                int courseNumber = CourseSetting.COURSE_NUMBER;
                if (courseNumber != -1) {
                    if (mCourseNum != courseNumber) {
                        // 更改课程数时提示将清除信息
                        showClearDataDialog(mCourseNum);
                    } else {
                        mCourseNumberTV.setText(String.valueOf(mCourseNum));
                        showEveryCourse();
                    }
                } else {
                    mCourseNumberTV.setText(String.valueOf(mCourseNum));
                    showEveryCourse();
                }
            }
        }).setLayoutRes(R.layout.course_pickerview_stair_layout, new CustomListener() {
            @Override
            public void customLayout(View v) {
                TextView textView = v.findViewById(R.id.course_picker_title);
                textView.setText("每日课程节数");

                mCourseNumCancel = v.findViewById(R.id.course_picker_view_cancel_button);
                mCourseNumCommit = v.findViewById(R.id.course_picker_view_commit_button);
            }
        })
                .setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))
                .isDialog(true)
                .build();//创建

        mCourseNumCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.returnData();
                pvOptions.dismiss();
            }
        });
        mCourseNumCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.dismiss();
            }
        });
        // 把数据绑定到控件上面
        pvOptions.setPicker(courseNumberList);
        // 展示
        pvOptions.show();
    }

    public void showTimePicker(final View view) {
        // 要展示的数据
        final List<String> listStartMinute = new ArrayList<>();
        final List<String> listStartSecond = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            if (i <= 9)
                listStartMinute.add("0" + i);
            else
                listStartMinute.add(String.valueOf(i));
        }
        for (int i = 0; i <= 59; i++) {
            if (i <= 9)
                listStartSecond.add("0" + i);
            else
                listStartSecond.add(String.valueOf(i));
        }
        // 监听选中
        final OptionsPickerView pvOptions = new OptionsPickerBuilder(CourseTimeActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                // 返回的是选中位置
                // 展示选中数据
                TextView textView = (TextView) view;
                textView.setText(listStartMinute.get(options1).trim() + ":" + listStartSecond.get(option2).trim());
            }
        })
                .setLayoutRes(R.layout.course_pickerview_stair_layout, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView textView = v.findViewById(R.id.course_picker_title);
                        textView.setText("课程开始时间");
                        mCourseTimeStartCancel = v.findViewById(R.id.course_picker_view_cancel_button);
                        mCourseTimeStartCommit = v.findViewById(R.id.course_picker_view_commit_button);
                    }
                })
                .setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))
                .isDialog(true)
                .build();//创建

        mCourseTimeStartCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.returnData();
                pvOptions.dismiss();
            }
        });
        mCourseTimeStartCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.dismiss();
            }
        });
        // 把数据绑定到控件上面
        // pvOptions.setPicker(listStartMinute,listStartSecond);
        pvOptions.setNPicker(listStartMinute, listStartSecond, null);
        // 展示
        pvOptions.show();
    }
}
