package com.example.learningsupport_argame.Course;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import com.example.learningsupport_argame.Course.FloatingDragActionBtn.DragFloatActionButton;

import com.example.learningsupport_argame.Course.ListView.ListViewActivity;
import com.example.learningsupport_argame.NavigationController;
import com.example.learningsupport_argame.R;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class CourseMainActivity extends AppCompatActivity {
    public final static String COURSE_INFO_PREF = "course_info_shared_preferences";
    private final static String TAG = "CourseMainActivity";
    final static int requestCode1 = 1;
    int mCoursesNumber;
    String[] weeks = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    int[] background = new int[]{R.drawable.course_course1, R.drawable.course_course2,
            R.drawable.course_course3, R.drawable.course_course4,
            R.drawable.course_course5};
    TextView[][] mCourseTVTable;
    LinearLayout mWeekListLayout;
    GridLayout mCourseListLayout;

    // 定义标题栏上的按钮
    private ImageButton mMenuMoreButton;
    private ImageButton mCourseAddButton;
    private TextView mCurrentWeekTV;

    private int week_current;
    String string_date;

    private Button mEditWeekCommit;
    private Button mEditWeekCancel;

    int mCourseTableItemHeight = 200;

    List<TextView> mCourseItemList = new ArrayList<>();
    List<Course> courseList = new ArrayList<>();

    // 定义标题栏弹窗按钮
    private PopupMenuAdapter mMorePopupMenu;

    public static int cHeight;

    TextView.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            TextView textView = (TextView) v;
            if (textView.getText() != "") {
                String start_tag = textView.getTag().toString().split(",")[0];
                String end_tag = textView.getTag().toString().split(",")[1];
                String zhou_tag = textView.getTag().toString().split(",")[2];
//            Toast.makeText(CourseMainActivity.this,zhou_tag,Toast.LENGTH_SHORT).show();

                int startJie = Integer.valueOf(start_tag);
                int endJie = Integer.valueOf(end_tag);
                int belongColumnJie = Integer.valueOf(zhou_tag);

                int belongWeek = -1;
                if ((week_current - 1) % 7 != 0)
                    belongWeek = (week_current - 1) % 7;
                else
                    belongWeek = 7;

                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String currentTimeStr = dateFormat.format(new Date());

                Toast.makeText(CourseMainActivity.this, "长按事件", Toast.LENGTH_SHORT).show();
                Toast.makeText(CourseMainActivity.this, currentTimeStr, Toast.LENGTH_SHORT).show();
                Date currentTime = null;
                try {
                    currentTime = dateFormat.parse(currentTimeStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(CourseMainActivity.this, "当前时间转化失败", Toast.LENGTH_SHORT).show();
                }

                SharedPreferences sharedPreferences = getSharedPreferences("course_time", MODE_PRIVATE);
                int courseTimeSpan = sharedPreferences.getInt("time_span", -1);

                for (int i = startJie; i <= endJie; i++) {
                    String courseBeginTimeStr = sharedPreferences.getString("第" + i + "节", "没有上课时间的记录，请先添加上课时间");
                    Date courseBeginTime = null;
                    try {
                        courseBeginTime = dateFormat.parse(courseBeginTimeStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(CourseMainActivity.this, "上课时间转化失败", Toast.LENGTH_SHORT).show();
                    }
                    long end = courseBeginTime.getTime() + courseTimeSpan * 60 * 1000;
                    Date courseEndTime = new Date(end);
                    String courseEndTimeStr = dateFormat.format(courseEndTime);

                    // Toast.makeText(CourseMainActivity.this, belongColumnJie+" "+belongWeek, Toast.LENGTH_SHORT).show();

                    if (((currentTime.after(courseBeginTime) || currentTime.equals(courseBeginTime))) && currentTime.before(courseEndTime) && belongColumnJie == belongWeek) {
                        //long monitorTimeSpan=courseEndTime.getTime()-currentTime.getTime();

                        //Toast.makeText(CourseMainActivity.this,courseEndTimeStr, Toast.LENGTH_SHORT).show();
                        enter_jiandu_dialog(courseEndTimeStr, currentTimeStr);
                        break;
                    }
                }
                return true;
            } else
                return false;
        }
    };
    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView course = (TextView) v;
            Toast.makeText(CourseMainActivity.this, course.getText().toString(), Toast.LENGTH_SHORT).show();
            int color = course.getSolidColor();
            //        if (textView.getText() == "" && color != R.drawable.course_course1 && color != R.drawable.course_course2 && color != R.drawable.course_course3 && color != R.drawable.course_course4 && color != R.drawable.course_course5 && color != R.drawable.course_shapetv) {
            //            // textView.setBackgroundResource(R.drawable.ic_tianjia);
            //            textView.setBackgroundResource(R.drawable.course_shapetv);
            //
            //            return;
            //        }
            //        if (textView.getText() == "" && textView.getSolidColor() == R.drawable.course_shapetv) {
            //            textView.setBackgroundResource(R.drawable.course_ic_add_edit);
            //
            //            //编辑课程界面
            //            Toast.makeText(CourseMainActivity.this, "进入编辑课程界面", Toast.LENGTH_SHORT).show();
            //        }

            if (course.getText() != "") {
                Toast.makeText(CourseMainActivity.this, "dialogdialoag", Toast.LENGTH_SHORT).show();
                courseInfoDialog(course);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_table_navigation_layout);
        // 添加侧边栏
        new NavigationController(this, getWindow().getDecorView(), NavigationController.NavigationItem.COURSE);

        SQLiteStudioService.instance().start(this);

        LitePal.initialize(CourseMainActivity.this);

        string_date = getDate()[0];
        week_current = Integer.valueOf(getDate()[1]);

        initView();
        initData();
        initCourseTable();
        setCurrentWeek();
        clearTable();
        query_db();

        GetMonitorInfo m = new GetMonitorInfo();
        m.get();
        String ss1 = "日使用时间" + m.getDayInfo()[0] + " 日专注时间" + m.getDayInfo()[1] + " 日使用次数" + m.getDayInfo()[2];
        String ss2 = "周使用时间" + m.getWeekInfo()[0] + " 周专注时间" + m.getWeekInfo()[1] + " 周使用次数" + m.getWeekInfo()[2];
//        String ss3="月使用时间"+m.getMonthInfo()[0]+" 月专注时间"+m.getMonthInfo()[1]+" 月使用次数"+m.getMonthInfo()[2];
        Toast.makeText(CourseMainActivity.this, ss1 + ss2, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        mWeekListLayout = findViewById(R.id.course_week_list);
        mCourseListLayout = findViewById(R.id.course_list_layout);
        mCurrentWeekTV = findViewById(R.id.course_current_week);

        //实例化标题栏弹窗
        mMorePopupMenu = new PopupMenuAdapter(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //实例化标题栏按钮并设置监听
        mMenuMoreButton = findViewById(R.id.course_menu_more_button);
        mMenuMoreButton.setOnClickListener(v -> mMorePopupMenu.show(v));
        mCourseAddButton = findViewById(R.id.course_menu_add_button);
        mCourseAddButton.setOnClickListener((v) -> {
            SharedPreferences sharedPreferences = getSharedPreferences(COURSE_INFO_PREF, Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences1 = getSharedPreferences(COURSE_INFO_PREF, Context.MODE_PRIVATE);
            if (sharedPreferences.getString("school_opens_date", "没有开学日期记录").equals("没有开学日期记录"))
                Toast.makeText(CourseMainActivity.this, "请先设置开学日期", Toast.LENGTH_SHORT).show();
            else if (sharedPreferences1.getInt("jie_num", -1) == -1)
                Toast.makeText(CourseMainActivity.this, "请先设置课程时间", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(CourseMainActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });
        mCurrentWeekTV.setOnClickListener(v -> {
            editCurrentWeek();
        });

        DragFloatActionButton dragFloatActionButton = findViewById(R.id.course_add_button);
        dragFloatActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "DragFloatActionButton:onClick");
                SharedPreferences sharedPreferences = getSharedPreferences(COURSE_INFO_PREF, Context.MODE_PRIVATE);
                SharedPreferences sharedPreferences1 = getSharedPreferences("course_time", Context.MODE_PRIVATE);
                if (sharedPreferences.getString("school_opens_date", "没有开学日期记录").equals("没有开学日期记录"))
                    Toast.makeText(CourseMainActivity.this, "请先设置开学日期", Toast.LENGTH_SHORT).show();
                else if (sharedPreferences1.getInt("jie_num", -1) == -1)
                    Toast.makeText(CourseMainActivity.this, "请先设置课程时间", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(CourseMainActivity.this, AddCourseActivity.class);
                    startActivity(intent);
                    CourseMainActivity.this.finish();
                }
            }
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
    private void initData() {
        //给标题栏弹窗添加子类
        final PopupMenuActionItem schoolOpensDate = new PopupMenuActionItem(this, "设置开学日期", R.drawable.course_ic_calendar);
        final PopupMenuActionItem courseTime = new PopupMenuActionItem(this, "设置课程时间", R.drawable.course_ic_time);
        final PopupMenuActionItem courseListTable = new PopupMenuActionItem(this, "查看课程列表", R.drawable.course_ic_edit_week);

        mMorePopupMenu.addAction(schoolOpensDate);
        mMorePopupMenu.addAction(courseTime);
        mMorePopupMenu.addAction(courseListTable);

        mMorePopupMenu.setItemOnClickListener(new PopupMenuAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(PopupMenuActionItem item, int position) {
                if (item.mTitle == "设置开学日期") {
                    // 获取当前时间
                    Calendar cTime = Calendar.getInstance();
                    //主题在这里！后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                    DatePickerDialog dialog = new DatePickerDialog(CourseMainActivity.this,
                            DatePickerDialog.BUTTON_POSITIVE,
                            (arg0, year, month, day) -> {
                                String schoolOpenData = year + "/" + (month + 1) + "/" + day;
                                SharedPreferences sharedPreferences = getSharedPreferences(COURSE_INFO_PREF, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("school_opens_date", schoolOpenData);
                                editor.commit();
                                setCurrentWeek();
                                clearTable();
                                query_db();
                            }, cTime.get(Calendar.YEAR), cTime.get(Calendar.MONTH), cTime.get(Calendar.DAY_OF_MONTH));

                    //设置日期的范围
//                    DatePicker datePicker = dialog.getDatePicker();
//                    datePicker.setMaxDate(new Date().getTime());//设置日期的上限日期
//                    datePicker.setMinDate(new Date().getTime());//设置日期的下限日期，其中是参数类型是long型，为日期的时间戳

                    dialog.setCanceledOnTouchOutside(true);

                    dialog.setTitle("");

//                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//
//                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });

                    dialog.show();
                } else if (item.mTitle == "查看课程列表") {
                    List<Course> list = LitePal.findAll(Course.class, true);
                    if (list.size() == 0) {
                        Toast.makeText(CourseMainActivity.this, "还未添加课程", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(CourseMainActivity.this, ListViewActivity.class);
                        intent.putExtra("flag", "CourseMainActivity");
                        startActivity(intent);
                        CourseMainActivity.this.finish();
                    }
                } else {
                    Intent intent = new Intent(CourseMainActivity.this, CourseTimeActivity.class);
                    startActivity(intent);
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
        final OptionsPickerView pvOptions = new OptionsPickerBuilder(CourseMainActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                // 返回的是选中位置
                // 展示选中数据
                mCurrentWeekTV.setText("第" + weekNumberList.get(options1) + "周");
                clearTable();
                query_db();
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
        mEditWeekCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.dismiss();
            }
        });
        mEditWeekCommit.setOnClickListener(new OnClickListener() {
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
    public void query_db() {
        SQLiteDatabase db = Connector.getDatabase();
        if (db != null) {
            List<Course> list = LitePal.findAll(Course.class, true);

            for (Course course : list) {
                int course_id = course.getId();
                String course_name = course.getCourse_name();
                String classroom = course.getClassroom();
                int start_zhou = course.getStart_zhoushu();
                int end_zhou = course.getEnd_zhoushu();
                String dan_shuang_week = course.getWeekstyle();
                String teacher = course.getTeacher();

                List<Jie> list_jie = new ArrayList<>();
                list_jie = course.getJie();
                for (Jie jie : list_jie) {
                    String zhou = jie.getZhou();
                    int start_jie = jie.getStart_jieshu();
                    int end_jie = jie.getEnd_jieshu();

                    int zhou_num = -1;
                    switch (zhou) {
                        case "周一":
                            zhou_num = 1;
                            break;
                        case "周二":
                            zhou_num = 2;
                            break;
                        case "周三":
                            zhou_num = 3;
                            break;
                        case "周四":
                            zhou_num = 4;
                            break;
                        case "周五":
                            zhou_num = 5;
                            break;
                        case "周六":
                            zhou_num = 6;
                            break;
                        case "周日":
                            zhou_num = 7;
                            break;
                        default:
                            Toast.makeText(CourseMainActivity.this, "周获取失败", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    //屏幕宽度
                    int width = dm.widthPixels;
                    //平均宽度
                    int aveWidth = (width - 100) / 7;

                    int jie_span = end_jie - start_jie + 1;
                    TextView textView = new TextView(CourseMainActivity.this);
                    textView.setWidth(aveWidth);
                    textView.setHeight(mCourseTableItemHeight * jie_span);
                    textView.setTextSize(15);
                    textView.isClickable();
                    textView.isLongClickable();
                    textView.setIncludeFontPadding(true);
                    textView.setPadding(1, 1, 1, 1);
                    textView.setGravity(Gravity.CENTER);
                    textView.setOnClickListener(mOnClickListener);
                    textView.setOnLongClickListener(mOnLongClickListener);
                    //Toast.makeText(CourseMainActivity.this, String.valueOf(textview_list.size()), Toast.LENGTH_SHORT).show();

                    for (TextView tv : mCourseItemList) {
                        if (tv.getTag() == start_jie + "," + end_jie + "," + zhou_num) {
                            //Toast.makeText(CourseMainActivity.this,tv.getText().toString(),Toast.LENGTH_LONG).show();
                            Log.d("KKKKKKKKKKK", "kkkkkkkkkk");
                            mCourseListLayout.removeView(tv);
                            mCourseItemList.remove(tv);

                        }
                    }
                    textView.setTag(start_jie + "," + end_jie + "," + zhou_num);

                    //GridLayout.spec(start, span) start:开始的行或列 span:合并的单元格个数
                    GridLayout.Spec rowSpec = GridLayout.spec(start_jie - 1, jie_span);
                    GridLayout.Spec columnSpec = GridLayout.spec(zhou_num);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                    params.setGravity(Gravity.TOP);

                    int week;

                    if (mCurrentWeekTV.getText().toString().equals("假期中")) {
                        Toast.makeText(CourseMainActivity.this, "当前没有课程要上", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Character.isDigit(mCurrentWeekTV.getText().toString().charAt(2)))
                            week = Integer.valueOf(mCurrentWeekTV.getText().toString().substring(1, 3));
                        else if (mCurrentWeekTV.getText().toString().equals("当前周")) {
                            Toast.makeText(CourseMainActivity.this, "请先设置开学日期或当前周", Toast.LENGTH_SHORT).show();
                            return;
                        } else
                            week = Integer.valueOf(mCurrentWeekTV.getText().toString().substring(1, 2));
                        if (start_zhou <= week && end_zhou >= week) {
                            //textView.setText(course_name + "\n" + "@" + mClassroom);
                            switch (dan_shuang_week) {
                                case "单周":
                                    if (week % 2 != 0) {
                                        Random random = new Random();
                                        int ran = random.nextInt(5);
                                        textView.setBackgroundResource(background[ran]);
                                        textView.setText(course_name + "\n" + "@" + classroom);
                                        mCourseListLayout.addView(textView, params);
                                        mCourseItemList.add(textView);
                                    }
                                    break;
                                case "双周":
                                    if (week % 2 == 0) {
                                        Random random = new Random();
                                        int ran = random.nextInt(5);
                                        textView.setBackgroundResource(background[ran]);
                                        textView.setText(course_name + "\n" + "@" + classroom);
                                        mCourseListLayout.addView(textView, params);
                                        mCourseItemList.add(textView);
                                    }
                                    break;
                                case "单双周":
                                    Random random = new Random();
                                    int ran = random.nextInt(5);
                                    textView.setBackgroundResource(background[ran]);
                                    textView.setText(course_name + "\n" + "@" + classroom);
                                    mCourseListLayout.addView(textView, params);
                                    mCourseItemList.add(textView);
                                    break;
                            }
                        }
                    }
                }
            }

        } else {
            Toast.makeText(CourseMainActivity.this, "当前没有课程信息", Toast.LENGTH_SHORT).show();
        }
    }

    //初始化课程表格
    public void initCourseTable() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        int screenWidth = dm.widthPixels;
        //平均宽度
        int aveWidth = (screenWidth - 100) / 7;
        //Text(CourseMainActivity.this, String.valueOf(aveWidth), Toast.LENGTH_SHORT).show();
        //屏幕高度
        // int height = dm.heightPixels;
        // int aveHeight = height / numArray.length;
        mCoursesNumber = getSharedPreferences("course_time", MODE_PRIVATE).getInt("jie_num", -1);
        if (mCoursesNumber == -1) {
            Toast.makeText(CourseMainActivity.this, "没有每日课程节数信息，请先添加", Toast.LENGTH_SHORT).show();
            mCoursesNumber = 12;
        }
        // 课程表
        mCourseTVTable = new TextView[mCoursesNumber + 1][8];

        // 初始化课表上方星期列表
        for (int i = 0; i < 8; i++) {
            TextView week = new TextView(CourseMainActivity.this);
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
        for (int courseNum = 1; courseNum <= mCoursesNumber; courseNum++) {
            for (int week = 0; week <= 7; week++) {
                TextView textView = new TextView(CourseMainActivity.this);
                textView.setGravity(Gravity.CENTER);
                textView.setHeight(mCourseTableItemHeight);
                textView.setTextSize(15);
                textView.setOnClickListener(mOnClickListener);
                textView.setOnLongClickListener(mOnLongClickListener);
                if (week == 0) {
                    textView.setWidth(100);
                    textView.setText(String.valueOf(courseNum));
                    mCourseListLayout.addView(textView);
                } else {
                    mCourseTVTable[courseNum][week] = new TextView(CourseMainActivity.this);
                    textView.setWidth(aveWidth);
                    textView.setId(Integer.valueOf((String.valueOf(courseNum) + week)));
                    textView.setText("");
                    mCourseListLayout.addView(textView);
                    mCourseItemList.add(textView);
                    mCourseTVTable[courseNum][week] = textView;
                }
            }
        }
        LinearLayout linearLayout = findViewById(R.id.course_top_height);
        linearLayout.measure(0, 0);
        cHeight = linearLayout.getMeasuredHeight();
        Log.d(TAG, String.valueOf(cHeight));

    }

    public void enter_jiandu_dialog(final String courseEndTimeStr, final String courseStartTimeStr) {
        PromptAdapter.Builder builder = new PromptAdapter.Builder(CourseMainActivity.this);
        builder.setTitle("提示");
        builder.setContent("确定要进入监督界面吗?");
        builder.setRight("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //跳转到监督界面
                //Toast.makeText(CourseMainActivity.this, "进入监督界面++"+courseStartTimeStr+"++"+courseEndTimeStr, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseMainActivity.this, MonitorActivity.class);

                /**

                 intent.putExtra("openActivity","CourseMainActivity");          **/

                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                long time = 0;
                try {
                    time = dateFormat.parse(courseEndTimeStr).getTime() - dateFormat.parse(courseStartTimeStr).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(CourseMainActivity.this, String.valueOf(time), Toast.LENGTH_SHORT).show();

                intent.putExtra("courseStartTimeStr", courseStartTimeStr);
                intent.putExtra("courseEndTimeStr", courseEndTimeStr);

                startActivity(intent);
                dialogInterface.dismiss();
            }

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
        View v = View.inflate(CourseMainActivity.this, R.layout.course_show_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CourseMainActivity.this);

        TextView course_classroom = v.findViewById(R.id.show_course_classroom);
        TextView course_number = v.findViewById(R.id.show_course_number);
        TextView course_teacher = v.findViewById(R.id.show_course_teacher);
        TextView course_week_number = v.findViewById(R.id.show_course_week_number);

        String course_name = "";
        String classroom = "";
        int start_week = -1;
        int end_week = -1;
        String single_or_biweekly = "";
        String teacher = "";
        if (textView.getTag() == null)
            return;
        String tag = textView.getTag().toString();
        String start_time = tag.split(",")[0];
        String end_time = tag.split(",")[1];
        String week_tag = tag.split(",")[2];
        int week_num = Integer.parseInt(week_tag);
        String weekStr = week_num > 0 && week_num < 8 ? weeks[week_num - 1] : "所在周几有误";

        String str = textView.getText().toString().split("@")[0].trim();
        //Toast.makeText(CourseMainActivity.this,str,Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = Connector.getDatabase();
        if (db != null) {
            List<Course> courseList = LitePal.where("course_name = ?", str).find(Course.class);
            if (courseList == null) {
                Toast.makeText(CourseMainActivity.this, "null", Toast.LENGTH_SHORT).show();
            } else {
                for (Course course : courseList) {
                    Toast.makeText(CourseMainActivity.this, start_time + end_time, Toast.LENGTH_SHORT).show();
                    course_name = course.getCourse_name();
                    classroom = course.getClassroom();
                    start_week = course.getStart_zhoushu();
                    end_week = course.getEnd_zhoushu();
                    single_or_biweekly = course.getWeekstyle();
                    teacher = course.getTeacher();
                }

            }
            builder.setTitle(course_name);
            builder.setView(v);
            builder.setPositiveButton("编辑", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(CourseMainActivity.this, AddCourseActivity.class);
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
        //没有则直接创建
        SharedPreferences sharedPreferences = getSharedPreferences(COURSE_INFO_PREF, MODE_PRIVATE);
        if (sharedPreferences == null) {
            Toast.makeText(CourseMainActivity.this, "SharedPreferences对象不存在", Toast.LENGTH_SHORT).show();
        } else {
            String schoolOpenDate = sharedPreferences.getString("school_opens_date", "");
            if (schoolOpenDate.equals("")) {
                Toast.makeText(CourseMainActivity.this, "请先添加开学日期并添加课程", Toast.LENGTH_SHORT).show();
            } else {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date schoolOpensDate = null;
                try {
                    schoolOpensDate = dateFormat.parse(schoolOpenDate);
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SQLiteStudioService.instance().stop();
    }
}
