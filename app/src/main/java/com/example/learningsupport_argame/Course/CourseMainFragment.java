package com.example.learningsupport_argame.Course;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.learningsupport_argame.Course.PopupWindow.PopupMenuActionItem;
import com.example.learningsupport_argame.Course.PopupWindow.PopupMenuAdapter;
import com.example.learningsupport_argame.Course.PopupWindow.PromptAdapter;
import com.example.learningsupport_argame.Course.list.CourseListActivity;
import com.example.learningsupport_argame.MonitorModel.MonitorActivity;
import com.example.learningsupport_argame.NavigationActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static android.view.View.TEXT_ALIGNMENT_CENTER;

public class CourseMainFragment extends Fragment {
    private View mView;
    private AppCompatActivity mActivity;
    public static final String CURRENT_EDIT_COURSE_NAME = "curent_edit_course";
    private final static String TAG = "CourseMainActivity";
    public static final int COURSE_TIME_REQUEST_CODE = 100;
    public static final int COURSE_ADD_REQUEST_CODE = 101;

    private String[] weeks = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private int[] background = new int[]{R.drawable.course_course1, R.drawable.course_course2,
            R.drawable.course_course3, R.drawable.course_course4,
            R.drawable.course_course5};
    private TextView[][] mCourseTVTable;
    private LinearLayout mWeekListLayout;
    private GridLayout mCourseListLayout;
    // 定义标题栏上的按钮
    private ImageButton mMenuMoreButton;
    private ImageButton mCourseAddButton;
    private Toolbar mToolbar;
    private ImageButton mNavigationButton;
    private TextView mCurrentWeekTV;
    private Button mEditWeekCommit;
    private Button mEditWeekCancel;
    private int week_current;

    private Handler mHandler;
    private boolean isDataLoaded = false;
    int mCourseTableItemHeight = 200;

    List<TextView> mCourseItemList = new ArrayList<>();

    // 定义标题栏弹窗按钮
    private PopupMenuAdapter mMorePopupMenu;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.course_table_layout, container, false);
        week_current = Integer.parseInt(getDate()[1]);
        mHandler = new Handler();
        mView = view;
        initView(view);
        initEvent();
        initCourseTable(12, view);
        initPopupMenu();
        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            // 获取设置信息后初始化主页面
            CourseLab.getCourseSetting();
            CourseLab.sCourseList = CourseLab.getAllCourse(UserLab.getCurrentUser().getId());
            mHandler.post(() -> {
                if (CourseSetting.SCHOOL_OPEN_DATE == null) {
                    Toast.makeText(mActivity, "请先添加开学线日期并添加课程", Toast.LENGTH_SHORT).show();
                    return;
                }
                initCourseTable(CourseSetting.COURSE_NUMBER, mView);
                setCurrentWeek();
                clearTable();
                initTableData(CourseLab.sCourseList);
                isDataLoaded = true;
            });
        }).start();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (CourseLab.sCourseList != null) {
            if (CourseSetting.SCHOOL_OPEN_DATE == null) {
                Toast.makeText(mActivity, "请先添加开学线日期并添加课程", Toast.LENGTH_SHORT).show();
                return;
            }
            initCourseTable(CourseSetting.COURSE_NUMBER, mView);
            setCurrentWeek();
            clearTable();
            initTableData(CourseLab.sCourseList);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 其他
        if (resultCode != RESULT_OK) {
            if (requestCode == COURSE_TIME_REQUEST_CODE) {
                initCourseTable(CourseSetting.COURSE_NUMBER, mView);
            } else if (requestCode == COURSE_ADD_REQUEST_CODE) {
                clearTable();
                initTableData(CourseLab.sCourseList);
            }
        }
        // 如果修改了开学日期
        if (resultCode != CourseTimeActivity.SCHOOL_OPEN_DATE_RESULT_CODE) {
            if (requestCode == COURSE_TIME_REQUEST_CODE) {
                setCurrentWeek();
                clearTable();
                initTableData(CourseLab.sCourseList);
            }
        }
    }

    private void initView(View view) {
        mMenuMoreButton = view.findViewById(R.id.course_menu_more_button);
        mCourseAddButton = view.findViewById(R.id.course_menu_add_button);
        mNavigationButton = view.findViewById(R.id.navigation_button);
        mToolbar = view.findViewById(R.id.course_main_toolbar);
        mWeekListLayout = view.findViewById(R.id.course_week_list);
        mCourseListLayout = view.findViewById(R.id.course_list_layout);
        mCurrentWeekTV = view.findViewById(R.id.course_current_week);

        //实例化标题栏弹窗
        mMorePopupMenu = new PopupMenuAdapter(mActivity, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initEvent() {
        mMenuMoreButton.setOnClickListener(v -> mMorePopupMenu.show(v));

        mCourseAddButton.setOnClickListener((v) -> {
            if (!isDataLoaded) {
                Toast.makeText(mActivity, "正在加载数据请稍后重试", Toast.LENGTH_SHORT).show();
                return;
            }
            if (CourseSetting.SCHOOL_OPEN_DATE == null)
                Toast.makeText(mActivity, "请先设置开学日期", Toast.LENGTH_SHORT).show();
            else if (CourseSetting.COURSE_NUMBER == -1)
                Toast.makeText(mActivity, "请先设置课程时间", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(mActivity, AddCourseActivity.class);
                startActivityForResult(intent, COURSE_ADD_REQUEST_CODE);
            }
        });
        mNavigationButton.setOnClickListener((v) -> ((NavigationActivity) mActivity).openNavigation());
        mCurrentWeekTV.setOnClickListener(v -> {
            if (!isDataLoaded) {
                Toast.makeText(mActivity, "正在加载数据请稍后重试", Toast.LENGTH_SHORT).show();
                return;
            }
            editCurrentWeek();
        });
    }

    //清除课程格子
    public void clearTable() {
        for (TextView textView : mCourseItemList) {
            if (textView.getTag() != null)
                mCourseListLayout.removeView(textView);
        }
    }

    /**
     * 初始化数据
     */
    private void initPopupMenu() {
        //给标题栏弹窗添加子类
        final PopupMenuActionItem courseTime = new PopupMenuActionItem(mActivity, "设置课程时间", R.drawable.course_ic_time);
        final PopupMenuActionItem courseListTable = new PopupMenuActionItem(mActivity, "查看课程列表", R.drawable.course_ic_edit_week);

        mMorePopupMenu.addAction(courseTime);
        mMorePopupMenu.addAction(courseListTable);

        mMorePopupMenu.setItemOnClickListener(new PopupMenuAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(PopupMenuActionItem item, int position) {
                if (!isDataLoaded) {
                    Toast.makeText(mActivity, "正在加载数据请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (item.mTitle == "查看课程列表") {
                    if (CourseLab.sCourseList.size() == 0) {
                        Toast.makeText(mActivity, "还未添加课程", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(mActivity, CourseListActivity.class);
                        intent.putExtra("flag", "CourseMainActivity");
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(mActivity, CourseTimeActivity.class);
                    startActivityForResult(intent, COURSE_TIME_REQUEST_CODE);
                }
            }
        });
    }

    private void editCurrentWeek() {
        // 要展示的数据
        List<String> weekNumberList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            weekNumberList.add(String.valueOf(i));
        }
        // 监听选中
        final OptionsPickerView pvOptions = new OptionsPickerBuilder(mActivity, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                // 返回的是选中位置
                // 展示选中数据
                mCurrentWeekTV.setText("第" + weekNumberList.get(options1) + "周");
                clearTable();
                initTableData(CourseLab.sCourseList);
            }
        }).setLayoutRes(R.layout.course_pickerview_stair_layout, new CustomListener() {
            @Override
            public void customLayout(View v) {
                mEditWeekCancel = v.findViewById(R.id.course_picker_view_cancel_button);
                mEditWeekCommit = v.findViewById(R.id.course_picker_view_commit_button);
            }
        }).setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))
                .isDialog(true)
                .build();//创建
        mEditWeekCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.dismiss();
            }
        });
        mEditWeekCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.returnData();
                pvOptions.dismiss();

            }
        });

        // 把数据绑定到控件上面
        pvOptions.setPicker(weekNumberList);
        pvOptions.show();

        // for (TextView textView : textview_list) {
        //    mCourseListLayout.removeView(textView);
        // }
        //clearTable();
        //tableInit();
    }

    //从数据库提取数据
    public void initTableData(List<Course> courseList) {
        for (Course course : courseList) {
            int course_id = course.getId();
            String courseName = course.getName();
            String classroom = course.getClassroom();
            int startWeek = course.getStartWeek();
            int endWeek = course.getEndWeek();
            String weekStyle = course.getWeekStyle();
            String teacher = course.getTeacher();
            Log.d(TAG, "initTableData: " + courseName);
            List<CourseTime> courseTimeList = course.getTimes();
            for (CourseTime courseTime : courseTimeList) {
                if (courseTime.getWeek() == null) {
                    Log.d(TAG, "initTableData: " + course.getName());
                    Log.d(TAG, "initTableData: " + course.getTimes());
                }
                String weekStr = courseTime.getWeek().trim();
                int startTime = courseTime.getStartTime();
                int endTime = courseTime.getEndTime();
                int weekNum = -1;
                Log.d(TAG, "query_db: " + weekStr);
                switch (weekStr) {
                    case "周一":
                        weekNum = 1;
                        break;
                    case "周二":
                        weekNum = 2;
                        break;
                    case "周三":
                        weekNum = 3;
                        break;
                    case "周四":
                        weekNum = 4;
                        break;
                    case "周五":
                        weekNum = 5;
                        break;
                    case "周六":
                        weekNum = 6;
                        break;
                    case "周日":
                        weekNum = 7;
                        break;
                    default:
                        Toast.makeText(mActivity, "周获取失败", Toast.LENGTH_SHORT).show();
                        break;
                }

                DisplayMetrics dm = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                //屏幕宽度
                int width = dm.widthPixels;
                //平均宽度
                int aveWidth = (width - 100) / 7;

                int timeSpan = endTime - startTime + 1;
                TextView textView = new TextView(mActivity);
                textView.setWidth(aveWidth);
                textView.setHeight(mCourseTableItemHeight * timeSpan);
                textView.setTextSize(15);
                textView.isClickable();
                textView.isLongClickable();
                textView.setBackgroundResource(R.drawable.course_item_round_bg);
                textView.setIncludeFontPadding(true);
                textView.setPadding(1, 1, 1, 1);
                textView.setGravity(Gravity.CENTER);
                textView.setOnClickListener(mOnClickListener);
                textView.setOnLongClickListener(mOnLongClickListener);
                //Toast.makeText(mActivity, String.valueOf(textview_list.size()), Toast.LENGTH_SHORT).show();

                for (TextView tv : mCourseItemList) {
                    if (tv.getTag() == startTime + "," + endTime + "," + weekNum) {
                        //Toast.makeText(mActivity,tv.getText().toString(),Toast.LENGTH_LONG).show();
                        mCourseListLayout.removeView(tv);
                        mCourseItemList.remove(tv);
                    }
                }
                textView.setTag(startTime + "," + endTime + "," + weekNum);
                Log.d(TAG, "course time: " + startTime + "," + endTime + "," + weekNum);
                //GridLayout.spec(start, span) start:开始的行或列 span:合并的单元格个数
                GridLayout.Spec rowSpec = GridLayout.spec(startTime - 1, timeSpan);
                GridLayout.Spec columnSpec = GridLayout.spec(weekNum);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                params.setGravity(Gravity.TOP);

                int week;
                if (mCurrentWeekTV.getText().toString().equals("假期中")) {
                    Toast.makeText(mActivity, "当前没有课程要上", Toast.LENGTH_SHORT).show();
                } else {
                    if (Character.isDigit(mCurrentWeekTV.getText().toString().charAt(2)))
                        week = Integer.parseInt(mCurrentWeekTV.getText().toString().substring(1, 3));
                    else if (mCurrentWeekTV.getText().toString().equals("当前周")) {
                        if (CourseSetting.SCHOOL_OPEN_DATE == null)
                            return;
                        // 当前周为第一周
                        week = 1;
                    } else
                        week = Integer.parseInt(mCurrentWeekTV.getText().toString().substring(1, 2));
                    if (startWeek <= week && endWeek >= week) {
                        //textView.setText(course_name + "\n" + "@" + mClassroom);
                        switch (weekStyle) {
                            case "单周":
                                if (week % 2 != 0) {
                                    Random random = new Random();
                                    int ran = random.nextInt(5);
                                    textView.setBackgroundResource(background[ran]);
                                    textView.setText(courseName + "\n" + "@" + classroom);
                                    mCourseListLayout.addView(textView, params);
                                    mCourseItemList.add(textView);
                                }
                                break;
                            case "双周":
                                if (week % 2 == 0) {
                                    Random random = new Random();
                                    int ran = random.nextInt(5);
                                    textView.setBackgroundResource(background[ran]);
                                    textView.setText(courseName + "\n" + "@" + classroom);
                                    mCourseListLayout.addView(textView, params);
                                    mCourseItemList.add(textView);
                                }
                                break;
                            case "单双周":
                                Random random = new Random();
                                int ran = random.nextInt(5);
                                textView.setBackgroundResource(background[ran]);
                                textView.setText(courseName + "\n" + "@" + classroom);
                                mCourseListLayout.addView(textView, params);
                                mCourseItemList.add(textView);
                                break;
                        }
                    }
                }
            }
        }
    }

    //初始化课程表格
    public void initCourseTable(int coursesNumber, View view) {
        mCourseListLayout.removeAllViews();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        int screenWidth = dm.widthPixels;
        //平均宽度
        int aveWidth = (screenWidth - 100) / 7;
        //Text(mActivity, String.valueOf(aveWidth), Toast.LENGTH_SHORT).show();
        //屏幕高度
        // int height = dm.heightPixels;
        // int aveHeight = height / numArray.length;
        // 课程表
        mCourseTVTable = new TextView[coursesNumber + 1][8];

        // 初始化课表上方星期列表
        for (int i = 0; i < 8; i++) {
            TextView week = new TextView(mActivity);
            week.setHeight(100);
            week.setGravity(Gravity.CENTER);
            if (i == 0) {
                week.setText("");
                week.setWidth(100);
            } else {
                week.setWidth(aveWidth);
                week.setText(weeks[i - 1]);
            }
            mWeekListLayout.addView(week);
        }
        // 初始化课程表表项
        for (int courseNum = 1; courseNum <= coursesNumber; courseNum++) {
            for (int week = 0; week <= 7; week++) {
                TextView textView = new TextView(mActivity);
                textView.setGravity(Gravity.CENTER);
                textView.setHeight(mCourseTableItemHeight);
                textView.setTextSize(15);
                textView.setOnClickListener(mOnClickListener);
                textView.setOnLongClickListener(mOnLongClickListener);
                if (week == 0) {
                    textView.setWidth(100);
                    textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    textView.setText(String.valueOf(courseNum));
                    mCourseListLayout.addView(textView);
                } else {
                    textView.setBackgroundResource(R.drawable.course_item_round_bg);
                    mCourseTVTable[courseNum][week] = new TextView(mActivity);
                    textView.setWidth(aveWidth);
                    textView.setId(Integer.parseInt((String.valueOf(courseNum) + week)));
                    textView.setText("");
                    mCourseListLayout.addView(textView);
                    mCourseItemList.add(textView);
                    mCourseTVTable[courseNum][week] = textView;
                }
            }
        }
    }

    public void enter_jiandu_dialog(final String courseEndTimeStr, final String courseStartTimeStr) {
        PromptAdapter.Builder builder = new PromptAdapter.Builder(mActivity);
        builder.setTitle("提示");
        builder.setContent("确定要进入监督界面吗?");
        builder.setRight("确定", (dialogInterface, i) -> {
            //跳转到监督界面
            //Toast.makeText(mActivity, "进入监督界面++"+courseStartTimeStr+"++"+courseEndTimeStr, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mActivity, MonitorActivity.class);

            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            long time = 0;
            try {
                time = dateFormat.parse(courseEndTimeStr).getTime() - dateFormat.parse(courseStartTimeStr).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Toast.makeText(mActivity, String.valueOf(time), Toast.LENGTH_SHORT).show();

            intent.putExtra("courseStartTimeStr", courseStartTimeStr);
            intent.putExtra("courseEndTimeStr", courseEndTimeStr);

            startActivity(intent);
            dialogInterface.dismiss();
        });
        builder.setLeft("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        PromptAdapter dialog = builder.create();
        dialog.show();
    }


    public void courseInfoDialog(TextView textView) {
        View v = View.inflate(mActivity, R.layout.course_show_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        TextView course_classroom = v.findViewById(R.id.show_course_classroom);
        TextView course_number = v.findViewById(R.id.show_course_number);
        TextView course_teacher = v.findViewById(R.id.show_course_teacher);
        TextView course_week_number = v.findViewById(R.id.show_course_week_number);

        String classroom = "", single_or_biweekly = "", teacher = "";
        int start_week = -1, end_week = -1;
        if (textView.getTag() == null)
            return;
        String tag = textView.getTag().toString();
        String start_time = tag.split(",")[0];
        String end_time = tag.split(",")[1];
        String week_tag = tag.split(",")[2];
        int week_num = Integer.parseInt(week_tag);
        String weekStr = week_num > 0 && week_num < 8 ? weeks[week_num - 1] : "所在周几有误";
        // 根据课程名获取课程实例
        String course_name = textView.getText().toString().split("@")[0].trim();
        Optional<Course> courseOptional = CourseLab.sCourseList.stream().filter(course -> course.getName().equals(course_name)).findFirst();
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            classroom = course.getClassroom();
            start_week = course.getStartWeek();
            end_week = course.getEndWeek();
            single_or_biweekly = course.getWeekStyle();
            teacher = course.getTeacher();
        }

        builder.setTitle(course_name);
        builder.setView(v);
        builder.setPositiveButton("编辑", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mActivity, AddCourseActivity.class);
                courseOptional.ifPresent(course -> intent.putExtra(CURRENT_EDIT_COURSE_NAME, course.getName()));
                startActivity(intent);
            }
        });

        course_classroom.setText(classroom);
        course_number.setText(weekStr + " " + start_time + "－" + end_time + "节");
        course_teacher.setText(teacher);

        if (single_or_biweekly.equals("单周") || single_or_biweekly.equals("双周")) {
            String string = start_week + "-" + end_week + "周" + "(" + single_or_biweekly + ")";
            course_week_number.setText(string);
        } else if (single_or_biweekly.equals("单双周")) {
            course_week_number.setText(start_week + "-" + end_week + "周");
        }
        builder.create().show();
    }

    //获取当前日期
    private String[] getDate() {
        Calendar cal = Calendar.getInstance();
        int year_current = cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy", "year" + year_current);
        int month_current = cal.get(Calendar.MONTH) + 1;   //获取到的月份是从0开始计数
        int day_current = cal.get(Calendar.DAY_OF_MONTH);
        String date = year_current + "/" + month_current + "/" + day_current;
        String[] str = new String[2];
        str[0] = date;
        str[1] = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
        return str;
    }

    public void setCurrentWeek() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date schoolOpensDate = null;
        try {
            schoolOpensDate = dateFormat.parse(CourseSetting.SCHOOL_OPEN_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar currentDate = Calendar.getInstance();
        long currentTime = currentDate.getTime().getTime();
        long schoolOpensTime = schoolOpensDate.getTime();

        int currentWeek = currentDate.get(Calendar.DAY_OF_WEEK);

        int num = (currentWeek == 1 ? 7 : currentWeek - 1);
        long time = currentTime - schoolOpensTime;
        int day = (int) (time / 1000 / 60 / 60 / 24);
        if (time < 0)
            mCurrentWeekTV.setText("假期中");
        else {
            int k = (day - (7 - num + 1));
            if (k >= 1) {
                mCurrentWeekTV.setText("第" + ((k / 7) + 2) + "周");
            } else {
                mCurrentWeekTV.setText("当前周");
            }
        }
    }


    TextView.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            TextView textView = (TextView) v;
            if (textView.getText() != "") {
                String start_tag = textView.getTag().toString().split(",")[0];
                String end_tag = textView.getTag().toString().split(",")[1];
                String zhou_tag = textView.getTag().toString().split(",")[2];
                //            Toast.makeText(mActivity,zhou_tag,Toast.LENGTH_SHORT).show();

                int startJie = Integer.parseInt(start_tag);
                int endJie = Integer.parseInt(end_tag);
                int belongColumnJie = Integer.parseInt(zhou_tag);

                int belongWeek = -1;
                if ((week_current - 1) % 7 != 0)
                    belongWeek = (week_current - 1) % 7;
                else
                    belongWeek = 7;

                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String currentTimeStr = dateFormat.format(new Date());

                Toast.makeText(mActivity, currentTimeStr, Toast.LENGTH_SHORT).show();
                Date currentTime = null;
                try {
                    currentTime = dateFormat.parse(currentTimeStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "当前时间转化失败", Toast.LENGTH_SHORT).show();
                }

                int courseTimeSpan = CourseSetting.COURSE_TIME_SPAN;

                // TODO: 20-3-2 适配监督
                for (int i = startJie; i <= endJie; i++) {
                    String courseBeginTimeStr = CourseSetting.ALL_COURSE_START_TIME.get(i - 1);
                    //String courseBeginTimeStr = sharedPreferences.getString("第" + i + "节", "没有上课时间的记录，请先添加上课时间");
                    Date courseBeginTime = null;
                    try {
                        courseBeginTime = dateFormat.parse(courseBeginTimeStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(mActivity, "上课时间转化失败", Toast.LENGTH_SHORT).show();
                    }
                    long end = courseBeginTime.getTime() + courseTimeSpan * 60 * 1000;
                    Date courseEndTime = new Date(end);
                    String courseEndTimeStr = dateFormat.format(courseEndTime);

                    // Toast.makeText(mActivity, belongColumnJie+" "+belongWeek, Toast.LENGTH_SHORT).show();

                    if (((currentTime.after(courseBeginTime) || currentTime.equals(courseBeginTime))) && currentTime.before(courseEndTime) && belongColumnJie == belongWeek) {
                        //long monitorTimeSpan=courseEndTime.getTime()-currentTime.getTime();

                        //Toast.makeText(mActivity,courseEndTimeStr, Toast.LENGTH_SHORT).show();
                        enter_jiandu_dialog(courseEndTimeStr, currentTimeStr);
                        break;
                    }
                }
                return true;
            } else
                return false;
        }
    };
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView courseTv = (TextView) v;
            if (courseTv.getText() != "") {
                Toast.makeText(mActivity, "详细信息", Toast.LENGTH_SHORT).show();
                courseInfoDialog(courseTv);
            }
        }
    };

}
