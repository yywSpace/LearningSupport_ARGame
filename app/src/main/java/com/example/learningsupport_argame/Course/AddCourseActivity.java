package com.example.learningsupport_argame.Course;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.example.learningsupport_argame.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;


public class AddCourseActivity extends AppCompatActivity implements RadioButton.OnCheckedChangeListener {

    EditText add_coursename;
    EditText add_classroom;
    Button add_jieshu;
    EditText add_teacher;
    Button add_zhoushu;
    RadioGroup radioGroup_zhou;
    RadioButton radio_dan;
    RadioButton radio_shuang;
    RadioButton radio_danshuang;
    TextView add_textView;
    Button submit;
    Button cancel;
    Button submit_zhou;
    Button cancel_zhou;

    String coursename;
    String classroom;
    List<Jie> list_jie=new ArrayList<>();
    String teacher;
    int startzhou;
    int endzhou;
    String weekstyle;

    SQLiteDatabase db;
    View layout;

    int addtime_count=1;
    List<View> layout_inflater=new ArrayList<>();

    String edittext_zhou;

    List<Course> list_courseInfo=new ArrayList<>();

    private PromptAdapter.Builder builder;

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

    private static boolean isLoaded_jieshu= false;
    private static boolean isLoaded_zhoushu= false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_add_layout);

        LitePal.initialize(AddCourseActivity.this);

        add_coursename = findViewById(R.id.add_course);
        add_classroom = findViewById(R.id.add_classroom);
        add_jieshu = findViewById(R.id.add_jieshu);
        add_teacher = findViewById(R.id.add_teacher);
        add_zhoushu = findViewById(R.id.add_zhoushu);
        edittext_zhou="";

//        Intent intent = getIntent();
        mHandler_jieshu.sendEmptyMessage(MSG_LOAD_DATA);
        mHandler_zhoushu.sendEmptyMessage(MSG_LOAD_DATA);


    }

    public void add_jieshu_onClick(View view) {
        if (isLoaded_jieshu) {
            showPickerView_jieshu();
        } else {
            Toast.makeText(AddCourseActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
        }
    }
    public void add_zhoushu_onClick(View view)
    {
        if (isLoaded_zhoushu) {
            showPickerView_zhoushu();
        } else {
            Toast.makeText(AddCourseActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
        }

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


    private void showPickerView_jieshu() {
        // 注意：自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针

        final OptionsPickerView pvCustomOptions= new OptionsPickerBuilder(AddCourseActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) { //返回的分别是三个级别的选中位置
                String opt1tx = options1Items_jieshu.size() > 0 ?
                        options1Items_jieshu.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items_jieshu.size() > 0
                        && options2Items_jieshu.get(options1).size() > 0 ?
                        options2Items_jieshu.get(options1).get(options2) : "";

                String opt3tx = options2Items_jieshu.size() > 0
                        && options3Items_jieshu.get(options1).size() > 0
                        && options3Items_jieshu.get(options1).get(options2).size() > 0 ?
                        options3Items_jieshu.get(options1).get(options2).get(options3) : "";

                String str2="";
                if(Character.isDigit(opt2tx.charAt(2))==true) {
                    str2 = opt2tx.substring(1, 3);
                }
                else
                {
                    str2=opt2tx.substring(1,2);
                }
                String str3="";
                if(Character.isDigit(opt3tx.charAt(3))==true) {
                     str3 = opt3tx.substring(2, 4);
                }
                else
                {
                    str3=opt3tx.substring(2,3);
                }

               add_textView =findViewById(addtime_count);
                String tx=opt1tx+"  "+str2+"－"+str3+"节";


                if(addtime_count!=1)
                {
                       add_textView.setText(tx);
                       //layout=;
                      layout=add_textView.getRootView().getRootView();
                }
                else
                {
                    add_jieshu.setText(tx);
                }
                Jie jie=new Jie();
                jie.setZhou(opt1tx);
                jie.setStart_jieshu(Integer.parseInt(str2));
                jie.setEnd_jieshu(Integer.parseInt(str3));
                list_jie.add(jie);

            }
        })
                .setLayoutRes(R.layout.course_pickerview_jie, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        //自定义布局中的控件初始化及事件处理
                        submit =  v.findViewById(R.id.iv_finish);
                        cancel =  v.findViewById(R.id.iv_cancel);

                    }
                })

                .setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .isDialog(true)
                .setCyclic(true,true,true)
                .setTextColorCenter(Color.rgb(205,104,57))
                .setDividerColor(Color.alpha(Color.BLACK))

                .build();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvCustomOptions.returnData();
                pvCustomOptions.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvCustomOptions.dismiss();

            }
        });

        pvCustomOptions.setPicker(options1Items_jieshu, options2Items_jieshu, options3Items_jieshu);//三级选择器
        pvCustomOptions.show();

    }
    private void showPickerView_zhoushu() {
        // 注意：自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针


        final OptionsPickerView pvCustomOptions_zhoushu= new OptionsPickerBuilder(AddCourseActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) { //返回的分别是三个级别的选中位置
                String opt1tx = options1Items_zhoushu.size() > 0 ?
                        options1Items_zhoushu.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items_zhoushu.size() > 0
                        && options2Items_zhoushu.get(options1).size() > 0 ?
                        options2Items_zhoushu.get(options1).get(options2) : "";

                String str1 = "";
                if (Character.isDigit(opt1tx.charAt(2)) == true) {
                    str1 = opt1tx.substring(1, 3);
                } else {
                    str1 = opt1tx.substring(1, 2);
                }

                String str2 = "";
                if (Character.isDigit(opt2tx.charAt(3)) == true) {
                    str2 = opt2tx.substring(2, 4);
                } else {
                    str2 = opt2tx.substring(2, 3);
                }

                String str_zhou = str1 + "－" + str2 + "周";

                if (edittext_zhou == "(单周)")
                    weekstyle = "单周";
                else if (edittext_zhou == "(双周)")
                    weekstyle = "双周";
                else if (edittext_zhou == "")
                    weekstyle = "单双周";

                edittext_zhou = str_zhou + edittext_zhou;
                add_zhoushu.setText(edittext_zhou);

                startzhou = Integer.parseInt(str1);
                endzhou = Integer.parseInt(str2);

            }

        })
                .setLayoutRes(R.layout.course_pickerview_week_layout, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        //自定义布局中的控件初始化及事件处理
                        submit_zhou=v.findViewById(R.id.zhou_finish);
                        cancel_zhou=v.findViewById(R.id.zhou_cancel);

                        radioGroup_zhou=v.findViewById(R.id.radiogroup_zhou);
                        radio_dan=v.findViewById(R.id.radio_danzhou);
                        radio_shuang=v.findViewById(R.id.radio_shuangzhou);
                        radio_danshuang=v.findViewById(R.id.radio_danshuang);

                        radio_dan.setOnCheckedChangeListener(AddCourseActivity.this);
                        radio_shuang.setOnCheckedChangeListener(AddCourseActivity.this);
                        radio_danshuang.setOnCheckedChangeListener(AddCourseActivity.this);

                        add_zhoushu.setText(edittext_zhou);


                    }
                })

                .setDividerColor(Color.BLACK)
               .setContentTextSize(18)
                .isDialog(true)
                .setCyclic(true,true,true)
                .setTextColorCenter(Color.rgb(205,104,57))
                .setDividerColor(Color.alpha(Color.BLACK))

                .build();

        submit_zhou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvCustomOptions_zhoushu.returnData();

                pvCustomOptions_zhoushu.dismiss();
            }
        });
        cancel_zhou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvCustomOptions_zhoushu.dismiss();

            }
        });

        pvCustomOptions_zhoushu.setPicker(options1Items_zhoushu, options2Items_zhoushu);//三级选择器
        pvCustomOptions_zhoushu.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler_jieshu != null) {
            mHandler_jieshu.removeCallbacksAndMessages(null);
        }
        if(mHandler_zhoushu!=null)
            mHandler_zhoushu.removeCallbacksAndMessages(null);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        edittext_zhou="";

        if(buttonView==radio_dan&&isChecked)
        {
            edittext_zhou="(单周)";

        }
        else if((buttonView==radio_shuang&&isChecked))
        {
            edittext_zhou="(双周)";
        }
        else if(buttonView==radio_danshuang&&isChecked)
        {
            edittext_zhou="";
        }
    }
    public void return_coursetable_onClick(View view)
    {
//        builder = new PromptAdapter.Builder(AddCourseActivity.this);
//        builder.setTitle("提示");
//        builder.setContent("确定要放弃添加此课程吗?");
//        builder.setRight("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent=new Intent(AddCourseActivity.this, CourseMainActivity.class);
                startActivity(intent);
                AddCourseActivity.this.finish();

//            }
//        });
//        builder.setLeft("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                coursename=add_coursename.getText().toString();
//                classroom=add_classroom.getText().toString();
//                teacher=add_teacher.getText().toString();
//
//                if(add_coursename.getText().toString().equals("")||add_classroom.getText().toString().equals("")||add_jieshu.getText().toString().equals("")||
//                        add_zhoushu.getText().toString().equals("")||add_teacher.getText().toString().equals(""))
//                    Toast.makeText(AddCourseActivity.this,"请填写完整课程信息",Toast.LENGTH_SHORT).show();
//                else {
//
//                    new_course(coursename, classroom, list_jie, teacher, startzhou, endzhou, weekstyle);
//                    Toast.makeText(AddCourseActivity.this,"课程保存成功",Toast.LENGTH_SHORT).show();
//
//                    Intent intent = new Intent(AddCourseActivity.this, CourseMainActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        builder.create().show();

    }

    public void add_commit_onClick(View view)
    {
//        LitePal.initialize(AddCourseActivity.this);

        coursename=add_coursename.getText().toString();
        classroom=add_classroom.getText().toString();
        teacher=add_teacher.getText().toString();

        if(add_coursename.getText().toString().equals("")||add_classroom.getText().toString().equals("")||add_jieshu.getText().toString().equals("")||
                add_zhoushu.getText().toString().equals("")||add_teacher.getText().toString().equals(""))
            Toast.makeText(AddCourseActivity.this,"请填写完整课程信息",Toast.LENGTH_SHORT).show();
        else {
            if (db == null)
                db = Connector.getDatabase();

            new_course(coursename, classroom, list_jie, teacher, startzhou, endzhou, weekstyle);

            PromptAdapter.Builder builder = new PromptAdapter.Builder(AddCourseActivity.this);
            builder.setTitle("提示");
            builder.setContent("课程已全部存入数据库");
            builder.setRight("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(AddCourseActivity.this, AddCourseActivity.class);
                    startActivity(intent);

                }
            });
            builder.setLeft("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

//                    Intent intent = new Intent(AddCourseActivity.this, CourseMainActivity.class);
//                    startActivity(intent);
                }
            });
            builder.create().show();

        }


    }
    public void add_next_onClick(View view)
    {
//        LitePal.initialize(AddCourseActivity.this);

        addtime_count=1;

        if(db==null) {

            db = Connector.getDatabase();
        }

        coursename=add_coursename.getText().toString();
        classroom=add_classroom.getText().toString();
        teacher=add_teacher.getText().toString();


        if(add_coursename.getText().toString().equals("")||add_classroom.getText().toString().equals("")||add_jieshu.getText().toString().equals("")||
        add_zhoushu.getText().toString().equals("")||add_teacher.getText().toString().equals(""))
            Toast.makeText(AddCourseActivity.this,"请填写完整课程信息",Toast.LENGTH_SHORT).show();
        else
        {

            new_course(coursename, classroom, list_jie, teacher, startzhou, endzhou, weekstyle);

            add_coursename.setText("");
            add_classroom.setText("");
            add_jieshu.setText("");
            add_teacher.setText("");
            add_zhoushu.setText("");
            list_jie.clear();

            LinearLayout qitatime_layout = findViewById(R.id.qitatime_layout);

            for (View view1 : layout_inflater) {
                qitatime_layout.removeView(view1);
            }
        }

    }
    public void clear_all_onClick(View view)
    {
        LitePal.deleteAll(Course.class);
//        add_coursename.setText("");
//        add_classroom.setText("");
//        add_jieshu.setText("");
//        add_teacher.setText("");
//        add_zhoushu.setText("");
        Toast.makeText(AddCourseActivity.this,"课程已全部清空",Toast.LENGTH_SHORT).show();
    }

    public void add_qitatime_onClick(View view)
    {
        if(layout==null) {
//            TextView textView = layout.findViewWithTag("qitatime");
//             || textView.getText().toString().equals("")
            if(add_jieshu.getText().toString().equals(""))
                Toast.makeText(AddCourseActivity.this, "请填写完当前时段", Toast.LENGTH_SHORT).show();
            else{
               // TextView textView = layout.findViewWithTag("qitatime");
                LinearLayout qitatime_layout = findViewById(R.id.qitatime_layout);
                LayoutInflater inflater = (LayoutInflater) AddCourseActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layoutView = inflater.inflate(R.layout.course_othertime_layout, null);
                layout_inflater.add(layoutView);
                addtime_count++;
                qitatime_layout.addView(layoutView);
                TextView textView = layoutView.findViewWithTag("qitatime");
                textView.setId(addtime_count);

            }
        }
        else {
            TextView textView = layout.findViewWithTag("qitatime");
            if(textView.getText().toString().equals(""))
                Toast.makeText(AddCourseActivity.this, "请填写完当前时段", Toast.LENGTH_SHORT).show();
            else {
                LinearLayout qitatime_layout = findViewById(R.id.qitatime_layout);
                LayoutInflater inflater = (LayoutInflater) AddCourseActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layoutView = inflater.inflate(R.layout.course_othertime_layout, null);
                layout_inflater.add(layoutView);
                addtime_count++;
                qitatime_layout.addView(layoutView);
                TextView textView1= layoutView.findViewWithTag("qitatime");
                textView1.setId(addtime_count);
            }
        }

    }

    public void new_course(String coursename,String classroom,List<Jie> list_jie,String teacher,int startzhou,int endzhou,String weekstyle)
    {

        LitePal.saveAll(list_jie);
        Course course=new Course();
        course.setCourse_name(coursename);
        course.setClassroom(classroom);
       course.setJie(list_jie);
        course.setTeacher(teacher);
        course.setStart_zhoushu(startzhou);
        course.setEnd_zhoushu(endzhou);
        course.setWeekstyle(weekstyle);
        course.saveThrows();
        if (course.save()) {

            Toast.makeText(AddCourseActivity.this, "课程保存成功", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(AddCourseActivity.this, "课程已经存在", Toast.LENGTH_SHORT).show();

        }
        list_courseInfo.add(course);
    }
}
