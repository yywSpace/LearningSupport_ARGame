package com.example.learningsupport_argame.Course;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

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
import com.example.learningsupport_argame.Course.ListView.ListViewActivity;
import com.example.learningsupport_argame.R;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CourseTimeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvJieNum;
    TextView tvTimeSpan;
    LinearLayout courseTimeLayout;
    int jieNum;
    int flag = 0;

    Button btnJieNumCancel;
    Button btnJieNumCommit;
    Button btnTimeStartCommit;
    Button btnTimeStartCancel;
    Button btnTimeSpanCancel;
    Button btnTimeSpanCommit;

    TextView[] textView1;
    TextView[] textViews;
    TextView textView;

    ImageView ivReturnCourseTable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_time_layout);

        LitePal.initialize(CourseTimeActivity.this);

        tvJieNum = findViewById(R.id.tvJieNum);
        ivReturnCourseTable = findViewById(R.id.course_return_coursetable);
        tvTimeSpan = findViewById(R.id.course_time_span);
        courseTimeLayout = findViewById(R.id.course_time_linearlayout);


//        Intent intent=new Intent(CourseTimeActivity.this,CourseMainActivity.class);
//        intent.putExtra("每日课程节数",tvJieNum.getText().toString());
//        setResult(RESULT_OK,intent);
        //finish();

    }

    public void dialog(final int jieNum) {

        PromptAdapter.Builder builder = new PromptAdapter.Builder(CourseTimeActivity.this);
        builder.setTitle("提示");
        builder.setContent("修改课程节数需要清空课程信息，是否确定修改");
        builder.setRight("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                dialogInterface.dismiss();
                tvJieNum.setText(String.valueOf(jieNum));
                LitePal.deleteAll(Course.class);
                showEveryJie();


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


    public void tvJieNum_onClick(View view) {
        showPickerViewJieNum();

    }

    public void showEveryJie() {

        new Handler(CourseTimeActivity.this.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                // Toast.makeText(CourseTimeActivity.this,String.valueOf(jieNum),Toast.LENGTH_SHORT).show();
//                    textView1=new TextView[jieNum+1];
                textViews = new TextView[jieNum + 1];
//                    textView=new TextView(CourseTimeActivity.this);
//                    textView.setText("你好");
//                    linearLayout.addView(textView);
//                    courseTimeLayout.addView(linearLayout);

                courseTimeLayout.removeAllViews();
                for (int i = 1; i <= jieNum; i++) {

//                        textView1[i]=new TextView(CourseTimeActivity.this);
                    textViews[i] = new TextView(CourseTimeActivity.this);
//                        textView1[i].setText("第" + String.valueOf(i) + "节");
                    textViews[i].setHint("未填写");
                    textViews[i].setId(i);
                    textViews[i].setClickable(true);
                    textViews[i].setTextColor(getResources().getColor(R.color.colorBlack));
                    textViews[i].setTextSize(18);
//                        textView1[i].setTextColor(getResources().getColor(R.color.colorBlack));

//                        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//                        layoutParams.setMargins(15,10,10,10);
//                        final LinearLayout linearLayout=new LinearLayout(CourseTimeActivity.this);
//                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                        linearLayout.setLayoutParams(layoutParams);


                    LayoutInflater inflater = (LayoutInflater) CourseTimeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.course_every_time_layout, null);
                    LinearLayout everyTimeLayout = layout.findViewById(R.id.course_every_time_linearlayout);

                    TextView textView = layout.findViewById(R.id.tvEveryTime);
                    textView.setText("第" + i + "节:  ");

                    everyTimeLayout.addView(textViews[i]);

                    courseTimeLayout.addView(layout);
                }
                for (int i = 1; i <= jieNum; i++) {
                    textViews[i].setOnClickListener(CourseTimeActivity.this);
                }
            }
        });

    }

    private void showPickerViewJieNum() {
//      要展示的数据
        final List<String> listJieNum = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            listJieNum.add(String.valueOf(i));
        }
//      监听选中
        final OptionsPickerView pvOptions = new OptionsPickerBuilder(CourseTimeActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
//               返回的是选中位置
//              展示选中数据
                // tvJieNum.setText(listJieNum.get(options1));
                jieNum = Integer.valueOf(listJieNum.get(options1));

                ///////////////////////
                SharedPreferences sharedPreferences = getSharedPreferences(CourseMainActivity.COURSE_INFO_PREF, Context.MODE_PRIVATE);
                int jNum = sharedPreferences.getInt("jie_num", -1);
                if (jNum != -1) {
                    if (jieNum != jNum) {
                        dialog(jieNum);
                    } else {
                        tvJieNum.setText(String.valueOf(jieNum));
                        showEveryJie();
                    }
                } else {

                    //////////////////////////
                    tvJieNum.setText(String.valueOf(jieNum));
                    showEveryJie();
                }

            }
        })
                .setLayoutRes(R.layout.course_pickerview_stair_layout, new CustomListener() {
                    @Override
                    public void customLayout(View v) {

                        TextView textView = v.findViewById(R.id.course_picker_title);
                        textView.setText("每日课程节数");

                        btnJieNumCancel = v.findViewById(R.id.course_picker_view_cancel_button);
                        btnJieNumCommit = v.findViewById(R.id.course_picker_view_commit_button);

                    }
                })
//                .setSelectOptions(0)//设置选择第一个

                .setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))

//
                .isDialog(true)
                .build();//创建

        btnJieNumCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pvOptions.returnData();
                pvOptions.dismiss();

            }
        });
        btnJieNumCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.dismiss();

            }
        });
//      把数据绑定到控件上面
        pvOptions.setPicker(listJieNum);
//      展示
        pvOptions.show();

    }

    public void showTimePicker(final View view) {
        //      要展示的数据
        final List<String> listStartMinute = new ArrayList<>();
        final List<String> listStartSecond = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            if (i <= 9)
                listStartMinute.add("0" + String.valueOf(i));
            else
                listStartMinute.add(String.valueOf(i));
        }
        for (int i = 0; i <= 59; i++) {
            if (i <= 9)
                listStartSecond.add("0" + String.valueOf(i));
            else
                listStartSecond.add(String.valueOf(i));
        }
//      监听选中
        final OptionsPickerView pvOptions = new OptionsPickerBuilder(CourseTimeActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
//               返回的是选中位置
//              展示选中数据
                TextView textView = (TextView) view;
                textView.setText(listStartMinute.get(options1) + ":" + listStartSecond.get(option2));
            }
        })
                .setLayoutRes(R.layout.course_pickerview_stair_layout, new CustomListener() {
                    @Override
                    public void customLayout(View v) {

                        TextView textView = v.findViewById(R.id.course_picker_title);
                        textView.setText("课程开始时间");

                        btnTimeStartCancel = v.findViewById(R.id.course_picker_view_cancel_button);
                        btnTimeStartCommit = v.findViewById(R.id.course_picker_view_commit_button);

                    }
                })
//                .setSelectOptions(0)//设置选择第一个

                .setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))

//
                .isDialog(true)
                .build();//创建

        btnTimeStartCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.returnData();
                pvOptions.dismiss();

            }
        });
        btnTimeStartCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.dismiss();

            }
        });
//      把数据绑定到控件上面
        // pvOptions.setPicker(listStartMinute,listStartSecond);
        pvOptions.setNPicker(listStartMinute, listStartSecond, null);
//      展示
        pvOptions.show();
    }

    public void onClick(View view) {
        showTimePicker(view);
    }

    public void return_coursetable_onClick(View view) {
        Intent intent = new Intent(CourseTimeActivity.this, CourseMainActivity.class);
        startActivity(intent);
        CourseTimeActivity.this.finish();
    }

    public void time_commit_onClick(View view) {
        int flag = 0;
        for (int i = 1; i <= jieNum; i++) {
            TextView tv = findViewById(i);
            if (tv.getText().toString().equals("")) {
                flag = 1;
                break;
            }
        }
        if (tvJieNum.getText().toString().equals("") || tvTimeSpan.getText().toString().equals("") || flag == 1)
            Toast.makeText(CourseTimeActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
        else {
            SharedPreferences sharedPreferences = getSharedPreferences(CourseMainActivity.COURSE_INFO_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("jie_num", jieNum);
            editor.putInt("time_span", Integer.valueOf(tvTimeSpan.getText().toString()));
            for (int i = 1; i <= jieNum; i++) {
                TextView textView = findViewById(i);
                editor.putString("第" + i + "节", textView.getText().toString());
            }
            editor.commit();
            Toast.makeText(CourseTimeActivity.this, "时间设置成功", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CourseTimeActivity.this, CourseMainActivity.class);
            startActivity(intent);
//            intent.putExtra("每日课程节数",tvJieNum.getText().toString());
//            setResult(RESULT_OK,intent);
//            CourseTimeActivity.this.finish();
        }
    }

    public void tvTime_span_onClick(View view) {
        //      要展示的数据
        final List<String> listTimeStart = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            listTimeStart.add(String.valueOf(i));
        }
//      监听选中
        final OptionsPickerView pvOptions = new OptionsPickerBuilder(CourseTimeActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
//               返回的是选中位置
//              展示选中数据
                tvTimeSpan.setText(listTimeStart.get(options1));
            }
        })
                .setLayoutRes(R.layout.course_pickerview_stair_layout, new CustomListener() {
                    @Override
                    public void customLayout(View v) {

                        TextView textView = v.findViewById(R.id.course_picker_title);
                        textView.setText("每节课的时间跨度(分)");

                        btnTimeSpanCancel = v.findViewById(R.id.course_picker_view_cancel_button);
                        btnTimeSpanCommit = v.findViewById(R.id.course_picker_view_commit_button);

                    }
                })
//                .setSelectOptions(0)//设置选择第一个
                .setDividerColor(Color.BLACK)
                .setContentTextSize(18)
                .setCyclic(true, true, true)
                .setTextColorCenter(Color.rgb(205, 104, 57))
                .setDividerColor(Color.alpha(Color.BLACK))
                .build();//创建

        btnTimeSpanCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.returnData();
                pvOptions.dismiss();

            }
        });
        btnTimeSpanCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.dismiss();

            }
        });
//      把数据绑定到控件上面
        pvOptions.setPicker(listTimeStart);
//      展示
        pvOptions.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //finish();
    }
}
