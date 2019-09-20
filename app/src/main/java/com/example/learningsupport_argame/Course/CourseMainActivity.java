package com.example.learningsupport_argame.Course;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
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

public class CourseMainActivity extends AppCompatActivity implements OnClickListener,TextView.OnLongClickListener {

    final static int requestCode1 = 1;
    int jieNum;
    String[] daysArray = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    String[] numArray;
    int[] background = new int[]{R.drawable.course_course1, R.drawable.course_course2,
            R.drawable.course_course3, R.drawable.course_course4,
            R.drawable.course_course5};
    TextView[][] textViews;
    LinearLayout dayslayout;
    GridLayout course_layout;

    private Context context;

    String kaixue_date = "";
    // 定义标题栏上的按钮
    private ImageButton menuBtn;

    private TextView tv_currentweek;

    private Calendar cal;

    private int year_current, month_current, day_current, week_current;
    String string_date;

    Button btn_editweek_commit;
    Button btn_editweek_cancel;

    int edited_week;
    int textview_height = 200;

    List<TextView> textview_list = new ArrayList<>();
    List<Course> courseList = new ArrayList<>();

    // 定义标题栏弹窗按钮
    private PopupMenuAdapter popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_table_layout);

        SQLiteStudioService.instance().start(this);

        LitePal.initialize(CourseMainActivity.this);

        dayslayout = findViewById(R.id.days_layout);
        course_layout = findViewById(R.id.course_layout);
        tv_currentweek = findViewById(R.id.tv_currentweek);

        string_date = getDate()[0];
        String[] str = getDate()[0].split("-");
        year_current = Integer.valueOf(str[0]);
        month_current = Integer.valueOf(str[1]);
        day_current = Integer.valueOf(str[2]);
        week_current = Integer.valueOf(getDate()[1]);

        init();
        initData();
        tableInit();

        getCurrentWeek();
        query_db();

        setClickListener();

        GetMonitorInfo m=new GetMonitorInfo();
        m.get();

    }
    public void setClickListener()
    {
        for (TextView textView : textview_list) {
            textView.setOnClickListener(this);
            textView.setOnLongClickListener(this);
        }

    }


    private void init() {
        //实例化标题栏按钮并设置监听
        menuBtn = findViewById(R.id.edit_btn);
        menuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show(v);
            }
        });


        //实例化标题栏弹窗
        popupMenu = new PopupMenuAdapter(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //给标题栏弹窗添加子类

        final PopupMenuActionItem editKaixueDate = new PopupMenuActionItem(this, "设置开学日期", R.drawable.course_ic_calendar);
        final PopupMenuActionItem addCourse = new PopupMenuActionItem(this, "手动添加课程", R.drawable.course_ic_pencil);
        final PopupMenuActionItem editCurrentWeek = new PopupMenuActionItem(this, "修改当前周", R.drawable.course_ic_edit_week);
        final PopupMenuActionItem editCourseTime = new PopupMenuActionItem(this, "设置课程时间", R.drawable.course_ic_time);

        popupMenu.addAction(editKaixueDate);
        popupMenu.addAction(editCurrentWeek);
        popupMenu.addAction(addCourse);
        popupMenu.addAction(editCourseTime);

        popupMenu.setItemOnClickListener(new PopupMenuAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(PopupMenuActionItem item, int position) {
                if (item.mTitle == "设置开学日期") {
                    //Toast.makeText( CourseMainActivity.this,"3", Toast.LENGTH_SHORT).show();

//                    getDate();
                    OnDateSetListener listener = new OnDateSetListener() {
                        public void onDateSet(DatePicker arg0, int year, int month, int day) {

                            //tv_currentweek.setText(year + "-" + (++month) + "-" + day);      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                            kaixue_date = year + "-" + (++month) + "-" + day;
                            SharedPreferences sharedPreferences = getSharedPreferences("kaixue_date", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("kaixue_date", kaixue_date);
                            editor.commit();

                           getCurrentWeek();
                            query_db();

                        }

                    };


                    DatePickerDialog dialog = new DatePickerDialog(CourseMainActivity.this, DatePickerDialog.BUTTON_POSITIVE, listener, year_current, month_current - 1, day_current);//主题在这里！后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月

                    //设置日期的范围

                    DatePicker datePicker = dialog.getDatePicker();

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

//                    getCurrentWeek();
//                    query_db();


                } else if (item.mTitle == "修改当前周") {

                    showPickerView();

                    int num;
                    switch (week_current) {
                        case 2:
                            num = 1;
                            break;//一
                        case 3:
                            num = 2;
                            break;
                        case 4:
                            num = 3;
                            break;
                        case 5:
                            num = 4;
                            break;
                        case 6:
                            num = 5;
                            break;
                        case 7:
                            num = 6;
                            break;//六
                        case 1:
                            num = 7;
                            break;//日
                        default:
                            num = -1;
                            break;
                    }

                    Date current_date;
                    int day = (edited_week) * 7 + num + 2;

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    current_date = null;
                    try {
                        current_date = dateFormat.parse(string_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long time = current_date.getTime();
                    long time1 = day * 24 * 60 * 60 * 1000;

                    long time2 = time - time1;


                    Date newDate = new Date(time2);

                    String str = dateFormat.format(newDate);

                    SharedPreferences sharedPreferences = getSharedPreferences("kaixue_date", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("kaixue_date", str);

                    editor.commit();


                    query_db();


                } else if (item.mTitle == "手动添加课程") {

                    SharedPreferences sharedPreferences = getSharedPreferences("kaixue_date", Context.MODE_PRIVATE);
                    if (sharedPreferences.getString("kaixue_date", "没有开学日期记录").equals("没有开学日期记录"))
                        Toast.makeText(CourseMainActivity.this, "请先添加开学日期或设置当前周", Toast.LENGTH_SHORT).show();
                    else {

                        Intent intent = new Intent(CourseMainActivity.this, AddCourseActivity.class);
                        startActivity(intent);
                        CourseMainActivity.this.finish();
                    }

                } else {

                    Intent intent = new Intent(CourseMainActivity.this, CourseTimeActivity.class);
                    startActivityForResult(intent, requestCode1);
                 //   CourseMainActivity.this.finish();

                }


            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case requestCode1:
//                if (resultCode == Activity.RESULT_OK) {
//                    jieNum = Integer.valueOf(data.getIntExtra("每日课程节数", -1));
//                    tv_currentweek.setText(jieNum);
//                    Toast.makeText(CourseMainActivity.this,String.valueOf(jieNum),Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                break;
//
//        }
//    }

    private void showPickerView() {
//      要展示的数据
        final List<String> listData = getData();

//      监听选中
        final OptionsPickerView pvOptions = new OptionsPickerBuilder(CourseMainActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
//               返回的是选中位置
//              展示选中数据
                tv_currentweek.setText("第" + listData.get(options1) + "周");
                edited_week = Integer.valueOf(listData.get(options1));
            }
        })
                .setLayoutRes(R.layout.course_pickerview_stair_layout, new CustomListener() {
                    @Override
                    public void customLayout(View v) {

                        btn_editweek_cancel = v.findViewById(R.id.course_pickerview_cancel_btn);
                        btn_editweek_commit = v.findViewById(R.id.course_pickerview_commit_btn);

                    }
                })
//                .setSelectOptions(0)//设置选择第一个
                .setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))
                .isDialog(true)
                .build();//创建

        btn_editweek_commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.returnData();
                pvOptions.dismiss();

            }
        });
        btn_editweek_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.dismiss();

            }
        });
//      把数据绑定到控件上面
        pvOptions.setPicker(listData);
        pvOptions.show();

        getCurrentWeek();
        query_db();


    }

    private List<String> getData() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add(String.valueOf(i));
        }
        return list;

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

//                Log.i("TAG", "Id=" + course_id);
//                Log.i("TAG", "课程名=" + course_name);
//                Log.i("TAG", "教室=" + classroom);
//                Log.i("TAG", "开始周=" + start_zhou);
//                Log.i("TAG", "结束周=" + end_zhou);
//                Log.i("TAG", "单双周=" + dan_shuang_week);
//                Log.i("TAG", "老师=" + teacher);

                List<Jie> list_jie = new ArrayList<>();
                list_jie = course.getJie();
                for (Jie jie : list_jie) {
                    String zhou = jie.getZhou();
                    int start_jie = jie.getStart_jieshu();
                    int end_jie = jie.getEnd_jieshu();
//                    Log.i("TAG", "节=" + zhou + " " + start_jie + end_jie);
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
                    textView.setHeight(textview_height * jie_span);
                    textView.setTextSize(15);
                    textView.isClickable();
                    textView.isLongClickable();
                    textView.setIncludeFontPadding(true);
                    textView.setPadding(1, 1, 1, 1);
//                    textViews[i][j].setId(Integer.valueOf((String.valueOf(i) + String.valueOf(j))));
                    textView.setGravity(Gravity.CENTER);

                    for (TextView tv : textview_list) {
                        if (tv.getTag() == start_jie + "," + end_jie + "," + zhou_num) {
                            course_layout.removeView(tv);
                            textview_list.remove(tv);
                        }
                    }
                    textView.setTag(start_jie + "," + end_jie + "," + zhou_num);

                    GridLayout.Spec rowSpec = GridLayout.spec(start_jie - 1, jie_span);
                    GridLayout.Spec columnSpec = GridLayout.spec(zhou_num);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                    params.setGravity(Gravity.TOP);

                    course_layout.addView(textView, params);
                    textview_list.add(textView);

                    int week = -1;

                    if (tv_currentweek.getText().toString().equals("假期中") == true) {

                        textView.setText(course_name + "\n" + "@" + classroom + "\n(非本周)");
                        textView.setBackgroundResource(R.drawable.course_shapetv);

                    } else {

                        if (Character.isDigit(tv_currentweek.getText().toString().charAt(2)) == true)
                            week = Integer.valueOf(tv_currentweek.getText().toString().substring(1, 3));
                        else if (tv_currentweek.getText().toString().equals("当前周")) {
                            Toast.makeText(CourseMainActivity.this, "请先设置开学日期或当前周", Toast.LENGTH_SHORT).show();
                            return;
                        } else
                            week = Integer.valueOf(tv_currentweek.getText().toString().substring(1, 2));
                        if (start_zhou <= week && end_zhou >= week) {
                            textView.setText(course_name + "\n" + "@" + classroom);
                            switch (dan_shuang_week) {
                                case "单周":
                                    if (week % 2 == 0) {
//                                    textView.setTextColor(getResources().getColor(R.color.colorGray));
//                                    textView.setBackgroundColor(getResources().getColor(R.color.background));
                                        textView.setBackgroundResource(R.drawable.course_shapetv);
                                        textView.setText(textView.getText() + "\n(非本周)");
                                    }
                                    break;
                                case "双周":
                                    if ((Integer.valueOf(tv_currentweek.getText().toString().substring(1, 2))) % 2 == 1) {
//                                    textView.setTextColor(getResources().getColor(R.color.colorGray));
//                                    textView.setBackgroundColor(getResources().getColor(R.color.background));
                                        textView.setBackgroundResource(R.drawable.course_shapetv);
                                        textView.setText(textView.getText() + "\n(非本周)");
                                    }
                                    break;
                                default:
                                    Random random = new Random();
                                    int ran = random.nextInt(5);
                                    textView.setBackgroundResource(background[ran]);
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

    //获取当前周


    //初始化课程表格

    public void tableInit() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        int width = dm.widthPixels;
        //平均宽度
        int aveWidth = (width - 100) / 7;
        Toast.makeText(CourseMainActivity.this, String.valueOf(aveWidth), Toast.LENGTH_SHORT).show();
        //屏幕高度
        int height = dm.heightPixels;
        //     int aveHeight = height / numArray.length;

        jieNum=getSharedPreferences("course_time",MODE_PRIVATE).getInt("jie_num",-1);
        if(jieNum==-1) {
            Toast.makeText(CourseMainActivity.this, "没有每日课程节数信息，请先添加", Toast.LENGTH_SHORT).show();
            jieNum = 12;
        }

        textViews = new TextView[jieNum + 1][8];
        // String[] numArray = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
        numArray=new String[jieNum+1];
        for (int i = 0; i <= jieNum; i++) {
            numArray[i] = String.valueOf(i);
        }
        for (int i = 0; i < 8; i++) {
            TextView textView = new TextView(CourseMainActivity.this);
            textView.setHeight(100);
            textView.setGravity(Gravity.CENTER);

            if (i == 0) {
                textView.setText("");
                textView.setWidth(100);
            } else {
                textView.setWidth(aveWidth);
                textView.setText(daysArray[i - 1]);

            }
            dayslayout.addView(textView);

        }
        for (int i = 1; i <= jieNum; i++) {
            for (int j = 0; j <= 7; j++) {

                if (j == 0) {
                    TextView textView = new TextView(CourseMainActivity.this);
                    textView.setGravity(Gravity.CENTER);
                    textView.setWidth(100);
                    textView.setTextSize(15);
                    textView.setHeight(textview_height);
                    textView.setText(numArray[i]);
                    course_layout.addView(textView);
                } else {
                    textViews[i][j] = new TextView(CourseMainActivity.this);
                    textViews[i][j].setWidth(aveWidth);
                    textViews[i][j].setHeight(textview_height);
                    textViews[i][j].setTextSize(15);
                    textViews[i][j].isClickable();
                    textViews[i][j].isLongClickable();
                    textViews[i][j].setId(Integer.valueOf((String.valueOf(i) + String.valueOf(j))));
                    textViews[i][j].setGravity(Gravity.CENTER);
                    course_layout.addView(textViews[i][j]);
                    textViews[i][j].setText("");
                    textview_list.add(textViews[i][j]);

                }

            }
        }
    }

    public void onClick(View view) {


        TextView textView = (TextView) view;
        int color = textView.getSolidColor();
        if (textView.getText() == "" && color != R.drawable.course_course1 && color != R.drawable.course_course2 && color != R.drawable.course_course3 && color != R.drawable.course_course4 && color != R.drawable.course_course5 && color != R.drawable.course_shapetv) {
            // textView.setBackgroundResource(R.drawable.ic_tianjia);
            textView.setBackgroundResource(R.drawable.course_shapetv);

            return;
        }
        if (textView.getText() == "" && textView.getSolidColor() == R.drawable.course_shapetv) {
            textView.setBackgroundResource(R.drawable.course_ic_add_edit);

            //编辑课程界面
            Toast.makeText(CourseMainActivity.this, "进入编辑课程界面", Toast.LENGTH_SHORT).show();
        }

        if (textView.getText() != "") {
            courseInfo_dialog(textView);
        }

    }

    public boolean onLongClick(View view) {
        TextView textView = (TextView) view;
        if (textView.getText() != "") {
            String start_tag = textView.getTag().toString().split(",")[0];
            String end_tag = textView.getTag().toString().split(",")[1];
            String zhou_tag=textView.getTag().toString().split(",")[2];

//            Toast.makeText(CourseMainActivity.this,zhou_tag,Toast.LENGTH_SHORT).show();

            int startJie = Integer.valueOf(start_tag);
            int endJie = Integer.valueOf(end_tag);
            int belongColumnJie=Integer.valueOf(zhou_tag);

            int belongWeek=-1;
            if((week_current-1)%7!=0)
                belongWeek=(week_current-1)%7;
            else
                belongWeek=7;

            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String currentTimeStr = dateFormat.format(new Date());
            Toast.makeText(CourseMainActivity.this,currentTimeStr,Toast.LENGTH_SHORT).show();
            Date currentTime = null;
            try {
                currentTime = dateFormat.parse(currentTimeStr);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(CourseMainActivity.this, "当前时间转化失败", Toast.LENGTH_SHORT).show();
            }

            SharedPreferences sharedPreferences = getSharedPreferences("course_time", MODE_PRIVATE);
            int courseTimeSpan = sharedPreferences.getInt("time_span",-1);

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
                String courseEndTimeStr=dateFormat.format(courseEndTime);

                Toast.makeText(CourseMainActivity.this, belongColumnJie+" "+belongWeek, Toast.LENGTH_SHORT).show();

                if (((currentTime.after(courseBeginTime) || currentTime.equals(courseBeginTime)) )&& currentTime.before(courseEndTime)&&belongColumnJie==belongWeek){
                    //long monitorTimeSpan=courseEndTime.getTime()-currentTime.getTime();
                   //Toast.makeText(CourseMainActivity.this,courseEndTimeStr, Toast.LENGTH_SHORT).show();
                    enter_jiandu_dialog(courseEndTimeStr);
                    break;
                }
            }
            return true;
        }
        else
            return false;
    }


    public void enter_jiandu_dialog(final String courseEndTimeStr){

               PromptAdapter.Builder builder = new PromptAdapter.Builder(CourseMainActivity.this);
               builder.setTitle("提示");
               builder.setContent("确定要进入监督界面吗?");
               builder.setRight("确定", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       //跳转到监督界面
                       Toast.makeText(CourseMainActivity.this, "进入监督界面", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(CourseMainActivity.this, MonitorActivity.class);
                       intent.putExtra("openActivity","CourseMainActivity");
                       intent.putExtra("courseEndTimeStr",courseEndTimeStr);
                       startActivity(intent);
//                       CourseMainActivity.this.finish();

                   }
               });
               builder.setLeft("取消", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

//                       Intent intent = new Intent(CourseMainActivity.this, CourseMainActivity.class);
//                       startActivity(intent);
                   }
               });
               PromptAdapter dialog=builder.create();
               dialog.show();
             // dialog.dismiss();
//               CourseMainActivity.this.finish();


    }


    public void courseInfo_dialog(TextView textView) {
        View v = View.inflate(CourseMainActivity.this, R.layout.course_show_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CourseMainActivity.this);

        TextView show_classroom=v.findViewById(R.id.show_classroom);
        TextView show_jieshu=v.findViewById(R.id.show_jieshu);
        TextView show_teacher=v.findViewById(R.id.show_teacher);
        TextView show_zhoushu=v.findViewById(R.id.show_zhoushu);

        String course_name="";
        String classroom="";
        int start_zhou=-1;
        int end_zhou=-1;
        String dan_shuang_week="";
        String teacher="";

        int flag=0;
        String start_tag = textView.getTag().toString().split(",")[0];
        String end_tag = textView.getTag().toString().split(",")[1];
        String zhou_tag=textView.getTag().toString().split(",")[2];

        String strZhou="";
        switch(zhou_tag)
        {
            case "1":
                strZhou="周一";
                break;
            case "2":
                strZhou="周二";
                break;
            case "3":
                strZhou="周三";
                break;
            case "4":
                strZhou="周四";
                break;
            case "5":
                strZhou="周五";
                break;
            case "6":
                strZhou="周六";
                break;
            case "7":
                strZhou="周日";
                break;
            default:
                strZhou="所在周几有误";
                break;
        }

        String str=textView.getText().toString().split("@")[0].trim();
        Toast.makeText(CourseMainActivity.this,str,Toast.LENGTH_SHORT).show();

       SQLiteDatabase db = Connector.getDatabase();
       if(db!=null) {

           List<Course> courseList = LitePal.where("course_name = ?",str).find(Course.class);

           if (courseList == null) {
                Toast.makeText(CourseMainActivity.this, "null", Toast.LENGTH_SHORT).show();
           }
           else{
           for (Course course : courseList) {

                       Toast.makeText(CourseMainActivity.this, start_tag+end_tag, Toast.LENGTH_SHORT).show();
                        course_name = course.getCourse_name();
                        classroom = course.getClassroom();
                        start_zhou = course.getStart_zhoushu();
                        end_zhou = course.getEnd_zhoushu();
                        dan_shuang_week = course.getWeekstyle();
                        teacher = course.getTeacher();

//                       Log.i("TAG", "课程名=" + course_name);
//                       Log.i("TAG", "教室=" + classroom);
//                       Log.i("TAG", "开始周=" + start_zhou);
//                       Log.i("TAG", "结束周=" + end_zhou);
//                       Log.i("TAG", "单双周=" + dan_shuang_week);
//                       Log.i("TAG", "老师=" + teacher);

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
               show_classroom.setText(classroom);
               show_jieshu.setText(strZhou+" "+start_tag + "－" + end_tag + "节");
               show_teacher.setText(teacher);

               if (dan_shuang_week.equals("单周") || dan_shuang_week.equals("双周")) {
                   String string=String.valueOf(start_zhou) + "-" + String.valueOf(end_zhou) + "周" + "(" + dan_shuang_week + ")";
                   show_zhoushu.setText(string);
               }
               else if (dan_shuang_week.equals("单双周")) {
                  show_zhoushu.setText(String.valueOf(start_zhou) + "-" + String.valueOf(end_zhou) + "周");
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

        String date = year_current + "-" + month_current + "-" + day_current;
        String[] str = new String[2];
        str[0] = date;
        str[1] = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));

        return str;

    }

    public void getCurrentWeek() {
        //没有则直接创建
        SharedPreferences sharedPreferences = getSharedPreferences("kaixue_date", context.MODE_PRIVATE);
        if (sharedPreferences == null) {
            Toast.makeText(CourseMainActivity.this, "sharedference对象不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            String kaixue_date = sharedPreferences.getString("kaixue_date", "没有开学日期记录");


            if (kaixue_date == "没有开学日期记录") {
                Toast.makeText(CourseMainActivity.this, "请先添加开学日期并添加课程", Toast.LENGTH_SHORT).show();
                return;

            } else {

                int week_num = sharedPreferences.getInt("week_num", 0);

                String[] str = getDate();
                String current_date = str[0];
                DateFormat dateFormat = DateFormat.getDateInstance();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                Date date_kaixue = null;
                try {
                    date_kaixue = dateFormat.parse(kaixue_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(CourseMainActivity.this, "当前周数获取失败(日期转化失败)", Toast.LENGTH_SHORT).show();
                }
                Date date_current = null;
                try {
                    date_current = dateFormat.parse(current_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(CourseMainActivity.this, "当前周数获取失败(日期转化失败)", Toast.LENGTH_SHORT).show();
                }

                long time_kaixue = date_kaixue.getTime();
                long time_current = date_current.getTime();

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(time_kaixue));

                int kaixue_week = cal.get(Calendar.DAY_OF_WEEK);

     //           long time = Math.abs(time_current - time_kaixue);
                  long time = time_current - time_kaixue;

                int day = (int) (time / 1000 / 60 / 60 / 24);

                if (time < 0)
                    tv_currentweek.setText("假期中");
                else {

                    int current_week = Integer.valueOf(str[1]);

                    int num = -1;
                    switch (kaixue_week) {
                        case 2:
                            num = 1;
                            break;//一
                        case 3:
                            num = 2;
                            break;
                        case 4:
                            num = 3;
                            break;
                        case 5:
                            num = 4;
                            break;
                        case 6:
                            num = 5;
                            break;
                        case 7:
                            num = 6;
                            break;//六
                        case 1:
                            num = 7;
                            break;//日
                        default:
                            num = -1;
                            break;
                    }
                    Toast.makeText(CourseMainActivity.this, String.valueOf(day), Toast.LENGTH_SHORT).show();
                    int k = (day - (7 - num + 1));
//                        int value=(k/7)+2;
                    if (k >= 1) {
                        tv_currentweek.setText("第" + ((k / 7) + 2) + "周");
                    } else if (k < 1) {
                        tv_currentweek.setText("第1周");
                    }
                    query_db();

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







