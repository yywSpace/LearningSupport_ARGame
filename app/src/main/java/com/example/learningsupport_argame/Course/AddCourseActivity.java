package com.example.learningsupport_argame.Course;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.learningsupport_argame.Course.ListView.ListViewActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;


public class AddCourseActivity extends AppCompatActivity implements RadioButton.OnCheckedChangeListener, View.OnClickListener {
    private final String TAG = "AddCourseActivity";
    private ImageView mCourseAddReturn;
    private TextView mCourseAddSubmit;
    private Button mCourseAddNextBtn;
    private Button mCourseAddOtherTime;
    private LinearLayout mOtherCourseTimeLayout;

    private EditText mCourseNameEditText;
    private EditText mClassroomEditText;
    private TextView mCourseTimeTV;
    private EditText mCourseTeacherEditText;
    private TextView mCourseWeekStyleTV;
    private RadioGroup mWeekStyleRadioGroup;
    private RadioButton mWeekStyleRadioSingle;
    private RadioButton mWeekStyleRadioBiweekly;
    private RadioButton mWeekStyleRadioBoth;

    private Button mCourseTimeSubmitBtn;
    private Button mCourseTimeCancelBtn;
    private Button mWeekStyleSubmitBtn;
    private Button mWeekStyleCancelBtn;

    private Handler mHandler;

    String edittext_zhou;

    private String mCourseName, mClassroom, mCourseTeacher, mCourseWeekStyle;
    int mCourseStartWeek, mCourseEndWeek;
    List<CourseTime> mCourseTimeList = new ArrayList<>();

    private List<JsonBeanJie> options1Items_jieshu = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items_jieshu = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items_jieshu = new ArrayList<>();

    private List<JsonBeanWeek> options1Items_zhoushu = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items_zhoushu = new ArrayList<>();

    private Thread thread_jieshu;
    private Thread thread_zhoushu;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    private static boolean isLoaded_jieshu = false;
    private static boolean isLoaded_zhoushu = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_add_layout);
        mCourseAddSubmit = findViewById(R.id.course_add_commit);
        mCourseAddReturn = findViewById(R.id.course_add_return);
        mCourseAddNextBtn = findViewById(R.id.course_add_next);
        mCourseAddOtherTime = findViewById(R.id.course_button_add_other_time);
        mOtherCourseTimeLayout = findViewById(R.id.course_other_time_layout);
        mCourseNameEditText = findViewById(R.id.course_name_edit_text);
        mClassroomEditText = findViewById(R.id.course_classroom_edit_text);
        mCourseTimeTV = findViewById(R.id.course_time_text_view);
        mCourseTeacherEditText = findViewById(R.id.course_teacher_edit_text);
        mCourseWeekStyleTV = findViewById(R.id.course_week_style_text_view);
        mHandler = new Handler();
        CourseTime courseTime = new CourseTime();
        courseTime.setTimeTextView(mCourseTimeTV);
        mCourseTimeList.add(courseTime);
        initEvent();
        edittext_zhou = "";
        mHandler_jieshu.sendEmptyMessage(MSG_LOAD_DATA);
        mHandler_zhoushu.sendEmptyMessage(MSG_LOAD_DATA);
    }

    void initEvent() {
        mCourseAddReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCourseActivity.this, CourseMainActivity.class);
                startActivity(intent);
                AddCourseActivity.this.finish();
            }
        });

        mCourseAddSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInfoCompleted()) {
                    NCourse currentCourse = new NCourse(UserLab.getCurrentUser().getId(), mCourseName, mClassroom,
                            mCourseTimeList, mCourseTeacher, mCourseStartWeek, mCourseEndWeek, mCourseWeekStyle);
                    runOnUiThread(() -> new Thread(() -> {
                        List<NCourse> allCourses = CourseLab.getAllCourse(currentCourse.getUserId());
                        boolean isMixed = judgeMixCourse(currentCourse, allCourses);
                        if (!isMixed) {
                            CourseLab.insertCourse(currentCourse);
                            Looper.prepare();
                            Toast.makeText(AddCourseActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            finish();
                            Looper.loop();
                        }
                    }).start());
                }
            }
        });
        mCourseAddOtherTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Optional<CourseTime> currentTime = mCourseTimeList
                        .stream()
                        .filter((courseTime) -> courseTime.getTimeTextView().getText().equals(""))
                        .findFirst();
                // 如果时间列表中有未初始化的时间则让用户初始化
                if (currentTime.isPresent()) {
                    Toast.makeText(AddCourseActivity.this, "请填写完当前时段", Toast.LENGTH_SHORT).show();
                    return;
                }
                TextView otherTime = (TextView) LayoutInflater.from(AddCourseActivity.this).inflate(R.layout.course_other_time_text_view, null);
                otherTime.setOnClickListener(timeView -> showCourseTimePickerViewN((TextView) timeView));
                // 长按删除
                otherTime.setOnLongClickListener(timeView -> {
                    mOtherCourseTimeLayout.removeView(timeView);
                    mCourseTimeList.removeIf(courseTime -> courseTime.getTimeTextView().equals(timeView));
                    return true;
                });
                mOtherCourseTimeLayout.addView(otherTime);
                // 在添加时先设置与此时间关联的TextView，在后续点击事件中根据此TextView找到CourseTime并设置数据
                CourseTime courseTime = new CourseTime();
                courseTime.setTimeTextView(otherTime);
                mCourseTimeList.add(courseTime);
            }
        });
        mCourseAddNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInfoCompleted()) {
                    final PromptAdapter.Builder builder = new PromptAdapter.Builder(AddCourseActivity.this);
                    builder.setTitle("提示");
                    builder.setContent("是否保存当前添加的课程");
                    builder.setRight("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            NCourse currentCourse = new NCourse(UserLab.getCurrentUser().getId(), mCourseName, mClassroom,
                                    mCourseTimeList, mCourseTeacher, mCourseStartWeek, mCourseEndWeek, mCourseWeekStyle);
                            runOnUiThread(() -> new Thread(() -> {
                                List<NCourse> allCourses = CourseLab.getAllCourse(currentCourse.getUserId());
                                boolean isMixed = judgeMixCourse(currentCourse, allCourses);
                                if (!isMixed) {
                                    CourseLab.insertCourse(currentCourse);
                                    Looper.prepare();
                                    Toast.makeText(AddCourseActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                    mHandler.post(AddCourseActivity.this::clearContent);
                                    Looper.loop();
                                }
                            }).start());
                        }
                    });
                    builder.setLeft("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clearContent();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }
            }
        });

        mCourseTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoaded_jieshu) {
                    showCourseTimePickerViewN((TextView) v);
                } else {
                    Toast.makeText(AddCourseActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCourseWeekStyleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoaded_zhoushu) {
                    showWeekStylePickerView();
                } else {
                    Toast.makeText(AddCourseActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
//        TextView textView = (TextView) v;
//        if (isLoaded_jieshu) {
//            showCourseTimePickerView(textView);
//        } else {
//            Toast.makeText(AddCourseActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
//        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler_jieshu = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread_jieshu == null) {//如果已创建就不再重新创建子线程了
                        thread_jieshu = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析数据
                                initJsonData_jieshu();
                            }
                        });
                        thread_jieshu.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded_jieshu = true;
                    break;
                case MSG_LOAD_FAILED:
                    Toast.makeText(AddCourseActivity.this, "Parse Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler mHandler_zhoushu = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread_zhoushu == null) {//如果已创建就不再重新创建子线程了
                        thread_zhoushu = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析数据
                                initJsonData_zhoushu();
                            }
                        });
                        thread_zhoushu.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded_zhoushu = true;
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(AddCourseActivity.this, "Parse Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void initJsonData_jieshu() {//解析数据

        String JsonData = new GetJsonDataUtil().getJson(this, "time.json");//获取assets目录下的json文件数据

        ArrayList<JsonBeanJie> jsonBean = parseData_jieshu(JsonData);//用Gson 转成实体

        /**
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items_jieshu = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {
            ArrayList<String> startjieList = new ArrayList<>();
            ArrayList<ArrayList<String>> endjieList = new ArrayList<>();

            for (int c = 0; c < jsonBean.get(i).getJieList().size(); c++) {
                String startjieName = jsonBean.get(i).getJieList().get(c).getName();
                startjieList.add(startjieName);
                ArrayList<String> endjie_List = new ArrayList<>();
                endjie_List.addAll(jsonBean.get(i).getJieList().get(c).getEndjie());
                endjieList.add(endjie_List);
            }
            options2Items_jieshu.add(startjieList);
            options3Items_jieshu.add(endjieList);
        }
        mHandler_jieshu.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    private void initJsonData_zhoushu() {//解析数据

        String JsonData = new GetJsonDataUtil().getJson(this, "zhoushu.json");//获取assets目录下的json文件数据

        ArrayList<JsonBeanWeek> jsonBean = parseData_zhoushu(JsonData);//用Gson 转成实体

        /**
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items_zhoushu = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {
            ArrayList<String> endzhou_list = new ArrayList<>();

            for (int c = 0; c < jsonBean.get(i).getEndzhou().size(); c++) {
                String endzhou = jsonBean.get(i).getEndzhou().get(c);
                endzhou_list.add(endzhou);
            }
            options2Items_zhoushu.add(endzhou_list);
        }
        mHandler_zhoushu.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }


    public ArrayList<JsonBeanJie> parseData_jieshu(String result) {//Gson 解析
        ArrayList<JsonBeanJie> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBeanJie entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBeanJie.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler_jieshu.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    public ArrayList<JsonBeanWeek> parseData_zhoushu(String result) {//Gson 解析
        ArrayList<JsonBeanWeek> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBeanWeek entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBeanWeek.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler_zhoushu.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    private void showCourseTimePickerViewN(final TextView textView) {
        final OptionsPickerView pvCustomOptions = new OptionsPickerBuilder(AddCourseActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) { //返回的分别是三个级别的选中位置
                // 星期
                String opt1week = options1Items_jieshu.size() > 0 ?
                        options1Items_jieshu.get(options1).getPickerViewText() : "";
                // 开始节数
                String opt2startTime = options2Items_jieshu.size() > 0
                        && options2Items_jieshu.get(options1).size() > 0 ?
                        options2Items_jieshu.get(options1).get(options2) : "";
                // 结束节数
                String opt3endTime = options2Items_jieshu.size() > 0
                        && options3Items_jieshu.get(options1).size() > 0
                        && options3Items_jieshu.get(options1).get(options2).size() > 0 ?
                        options3Items_jieshu.get(options1).get(options2).get(options3) : "";

                // 截取开始和结束时间
                String startTime = Character.isDigit(opt2startTime.charAt(2)) ?
                        opt2startTime.substring(1, 3) : opt2startTime.substring(1, 2);
                String endTime = Character.isDigit(opt3endTime.charAt(3)) ?
                        opt3endTime.substring(2, 4) : opt3endTime.substring(2, 3);

                SharedPreferences sharedPreferences = getSharedPreferences(CourseMainActivity.COURSE_INFO_PREF, MODE_PRIVATE);
                int courseNum = sharedPreferences.getInt("jie_num", -1);
                if (Integer.parseInt(endTime) > courseNum) {
                    Toast.makeText(AddCourseActivity.this, "所选节数与设置的每日节数不符", Toast.LENGTH_SHORT).show();
                    textView.setText("");
                } else {
                    String tx = opt1week + "  " + startTime + "－" + endTime + "节";
                    boolean isMixed = mCourseTimeList.stream()
                            .filter((courseTime -> !courseTime.getTimeTextView().getText().toString().equals("")))
                            .anyMatch(courseTime -> {
                                // 如果与刚设置时间冲突
                                return !(Integer.parseInt(startTime) > courseTime.getEndTime() || Integer.parseInt(endTime) < courseTime.getStartTime()) &&
                                        opt1week.equals(courseTime.getWeek());
                            });
                    if (isMixed) {
                        Toast.makeText(AddCourseActivity.this, "所选日期与上述日期冲突", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    textView.setText(tx);
                    CourseTime courseTime = mCourseTimeList.stream().filter((ct) -> ct.getTimeTextView().equals(textView)).findFirst().get();
                    courseTime.setWeek(opt1week);
                    courseTime.setStartTime(Integer.parseInt(startTime));
                    courseTime.setEndTime(Integer.parseInt(endTime));
                }
            }
        }).setLayoutRes(R.layout.course_pickerview_jie, new CustomListener() {
            @Override
            public void customLayout(View v) {
                //自定义布局中的控件初始化及事件处理
                mCourseTimeSubmitBtn = v.findViewById(R.id.course_select_time_submit);
                mCourseTimeCancelBtn = v.findViewById(R.id.course_select_time_cancel);
            }
        }).setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .isDialog(true)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))
                .build();

        mCourseTimeSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvCustomOptions.returnData();
                pvCustomOptions.dismiss();
            }
        });
        mCourseTimeCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvCustomOptions.dismiss();
            }
        });
        pvCustomOptions.setPicker(options1Items_jieshu, options2Items_jieshu, options3Items_jieshu);//三级选择器
        pvCustomOptions.show();
    }

    private void showWeekStylePickerView() {
        // 注意：自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针
        final OptionsPickerView weekSetOptions = new OptionsPickerBuilder(AddCourseActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) { //返回的分别是三个级别的选中位置
                String opt1tx = options1Items_zhoushu.size() > 0 ?
                        options1Items_zhoushu.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items_zhoushu.size() > 0
                        && options2Items_zhoushu.get(options1).size() > 0 ?
                        options2Items_zhoushu.get(options1).get(options2) : "";

                String str1 = "";
                if (Character.isDigit(opt1tx.charAt(2))) {
                    str1 = opt1tx.substring(1, 3);
                } else {
                    str1 = opt1tx.substring(1, 2);
                }

                String str2 = "";
                if (Character.isDigit(opt2tx.charAt(3))) {
                    str2 = opt2tx.substring(2, 4);
                } else {
                    str2 = opt2tx.substring(2, 3);
                }

                String str_zhou = str1 + "－" + str2 + "周";

                if (edittext_zhou == "(单周)")
                    mCourseWeekStyle = "单周";
                else if (edittext_zhou == "(双周)")
                    mCourseWeekStyle = "双周";
                else if (edittext_zhou == "")
                    mCourseWeekStyle = "单双周";

                edittext_zhou = str_zhou + edittext_zhou;
                mCourseWeekStyleTV.setText(edittext_zhou);

                mCourseStartWeek = Integer.parseInt(str1);
                mCourseEndWeek = Integer.parseInt(str2);
                edittext_zhou = "";
            }
        })
                .setLayoutRes(R.layout.course_pickerview_week_layout, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        //自定义布局中的控件初始化及事件处理
                        mWeekStyleSubmitBtn = v.findViewById(R.id.course_select_week_style_commit);
                        mWeekStyleCancelBtn = v.findViewById(R.id.course_select_week_style_cancel);
                        mWeekStyleRadioGroup = v.findViewById(R.id.course_week_style_radio_group);
                        mWeekStyleRadioSingle = v.findViewById(R.id.course_week_style_single);
                        mWeekStyleRadioBiweekly = v.findViewById(R.id.course_week_style_biweekly);
                        mWeekStyleRadioBoth = v.findViewById(R.id.course_week_style_both);

                        RadioButton.OnCheckedChangeListener listener = new RadioButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                if (buttonView.getId() == R.id.course_week_style_single && buttonView.isChecked())
                                    edittext_zhou = "(单周)";
                                if (buttonView.getId() == R.id.course_week_style_biweekly && buttonView.isChecked())
                                    edittext_zhou = "(双周)";
                                if (buttonView.getId() == R.id.course_week_style_both && buttonView.isChecked())
                                    edittext_zhou = "";
                            }
                        };
                        mWeekStyleRadioSingle.setOnCheckedChangeListener(listener);
                        mWeekStyleRadioBiweekly.setOnCheckedChangeListener(listener);
                        mWeekStyleRadioBoth.setOnCheckedChangeListener(listener);
                    }
                })
                .setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .isDialog(true)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))
                .build();


        mWeekStyleSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekSetOptions.returnData();
                weekSetOptions.dismiss();
            }
        });
        mWeekStyleCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekSetOptions.dismiss();
            }
        });

        weekSetOptions.setPicker(options1Items_zhoushu, options2Items_zhoushu);//三级选择器
        weekSetOptions.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler_jieshu != null) {
            mHandler_jieshu.removeCallbacksAndMessages(null);
        }
        if (mHandler_zhoushu != null)
            mHandler_zhoushu.removeCallbacksAndMessages(null);
        SQLiteStudioService.instance().stop();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        edittext_zhou = "";
        if (buttonView == mWeekStyleRadioSingle && isChecked) {
            edittext_zhou = "(单周)";

        } else if ((buttonView == mWeekStyleRadioBiweekly && isChecked)) {
            edittext_zhou = "(双周)";
        } else if (buttonView == mWeekStyleRadioBoth && isChecked) {
            edittext_zhou = "";
        }
    }

    public boolean isInfoCompleted() {
        mCourseName = mCourseNameEditText.getText().toString();
        mClassroom = mClassroomEditText.getText().toString();
        mCourseTeacher = mCourseTeacherEditText.getText().toString();
        // 找到列表中未赋值的TextView
        Optional<CourseTime> timeOptional = mCourseTimeList.stream().filter(courseTime -> courseTime.getTimeTextView().getText().toString().equals(""))
                .findFirst();
        // 如果有未赋值的其他课程时间或者有信息未填写则返回false
        if (timeOptional.isPresent() || mCourseName.equals("") || mClassroom.equals("") ||
                mCourseWeekStyleTV.getText().toString().equals("") || mCourseTeacher.equals("")) {
            Toast.makeText(AddCourseActivity.this, "请填写完整课程信息", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void clearContent() {
        mCourseNameEditText.setText("");
        mClassroomEditText.setText("");
        mCourseTimeTV.setText("");
        mCourseTeacherEditText.setText("");
        mCourseWeekStyleTV.setText("");
        mCourseTimeList.clear();
        mOtherCourseTimeLayout.removeAllViews();
        mOtherCourseTimeLayout.addView(mCourseTimeTV);
        mCourseName = "";
        mClassroom = "";
        mCourseStartWeek = -1;
        mCourseEndWeek = -1;
        mCourseWeekStyle = "";
    }

//    public void clear_all_onClick(View view) {
//        LitePal.deleteAll(Course.class);
////        mCourseNameEditText.setText("");
////        mClassroomEditText.setText("");
////        mCourseTimeTV.setText("");
////        mCourseTeacherEditText.setText("");
////        mCourseWeekStyleTV.setText("");
//        Toast.makeText(AddCourseActivity.this, "课程已全部清空", Toast.LENGTH_SHORT).show();
//    }


    public boolean judgeMixCourse(NCourse currentCourse, List<NCourse> allCourses) {
        // 获取所有课程
        boolean haveSameName = false;
        NCourse mixedCourse = null;
        boolean isMix = false;
        for (NCourse oldCourse : allCourses) {
            // 判断课程名是否相同
            if (oldCourse.getName().equals(currentCourse.getName())) {
                Looper.prepare();
                Toast.makeText(AddCourseActivity.this, "数据库中已有同名课程", Toast.LENGTH_SHORT).show();
                Looper.loop();

                final PromptAdapter.Builder builder = new PromptAdapter.Builder(AddCourseActivity.this);
                builder.setTitle("提示");
                builder.setContent("数据库中已有同名课程记录");
                builder.setRight("查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(AddCourseActivity.this, ListViewActivity.class);
                        intent.putExtra("flag", "AddCourseActivity");
                        startActivity(intent);
                        dialogInterface.dismiss();
                    }
                });
                builder.setLeft("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
//                            Intent intent = new Intent(AddCourseActivity.this, AddCourseActivity.class);
//                            startActivity(intent);
                    }
                });
                builder.create().show();
                haveSameName = true;
                break;
            }
            // 判断每节课程的时间是否混淆
            isMixLabel:
            for (CourseTime currentTime : currentCourse.getTimes()) {
                for (CourseTime oldTime : oldCourse.getTimes()) {
                    // 如果在同一周的相同节数有其他课程
                    if ((!(currentTime.getStartTime() > oldTime.getEndTime() || currentTime.getEndTime() < oldTime.getStartTime())) && currentTime.getWeek().equals(oldTime.getWeek())) {
                        // 判断是否相互混淆
                        List<Integer> currentCWeekList = getCourseWeekList(currentCourse.getStartWeek(), currentCourse.getEndWeek(), currentCourse.getWeekStyle());
                        List oldCWeekList = getCourseWeekList(oldCourse.getStartWeek(), oldCourse.getEndWeek(), oldCourse.getWeekStyle());
                        for (int i = 0; i < currentCWeekList.size(); i++)
                            for (int j = 0; j < oldCWeekList.size(); j++)
                                if (currentCWeekList.get(i) == oldCWeekList.get(j)) {
                                    isMix = true;
                                    mixedCourse = oldCourse;
                                    break isMixLabel;
                                }
                        Log.d(TAG, "judgeMixCourse: " + "混淆: " + isMix);

                    }
                }
            }
            if (isMix) {
                PromptAdapter.Builder builder = new PromptAdapter.Builder(AddCourseActivity.this);
                builder.setTitle("提示");
                builder.setContent("该课程所在时段与已有课程:  " + mixedCourse.getName() + " 冲突");
                builder.setRight("查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddCourseActivity.this, ListViewActivity.class);
                        intent.putExtra("flag", "AddCourseActivity");
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                builder.setLeft("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        Intent intent=new Intent(AddCourseActivity.this,ListViewActivity.class);
//                        startActivity(intent);
                    }
                });
                Looper.prepare();
                builder.create().show();
                Looper.loop();
            }
        }
        return haveSameName || isMix;
    }

    /**
     * 根据当前课程周类型获取包含此课程周的列表
     *
     * @param startWeek 课程开始周
     * @param endWeek   课程结束周
     * @param weekStyle 周类型
     * @return
     */
    public List<Integer> getCourseWeekList(int startWeek, int endWeek, String weekStyle) {
        List<Integer> weekList = new ArrayList<>();
        if (weekStyle.equals("单周")) {
            for (int i = startWeek; i <= endWeek; i = i + 1) {
                if (i % 2 != 0) {
                    weekList.add(i);
                }
            }
        }
        if (weekStyle.equals("双周")) {
            for (int i = startWeek; i <= endWeek; i = i + 1) {
                if (i % 2 == 0) {
                    weekList.add(i);
                }
            }
        }
        if (weekStyle.equals("单双周")) {
            for (int i = startWeek; i <= endWeek; i = i + 1) {
                weekList.add(i);
            }
        }
        return weekList;
    }
}