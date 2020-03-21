package com.example.learningsupport_argame.Course.ListView;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.Course.CourseLab;
import com.example.learningsupport_argame.Course.CourseTime;
import com.example.learningsupport_argame.Course.Course;
import com.example.learningsupport_argame.Course.PopupWindow.PromptAdapter;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    private List<MyModel> data = new ArrayList<>();
    private MyListView myListView;
    private MyAdapter myAdapter;
    private ImageView mReturnImageView;
    private ImageView mClearImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_listview_main_activity);
        mReturnImageView = findViewById(R.id.course_list_return);
        mClearImageView = findViewById(R.id.course_list_clear_all);
        mClearImageView.setOnClickListener(v -> removeAllCourseLDialog());
        mReturnImageView.setOnClickListener(v -> finish());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        initData();
        myListView = findViewById(R.id.my_list);
        myAdapter = new MyAdapter(data, this);
        myListView.setAdapter(myAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Toast.makeText(ListViewActivity.this, ((MyModel) myAdapter.getItem(position)).getGroupName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initData() {
        new Thread(() -> {
            CourseLab.getAllCourse(UserLab.getCurrentUser().getId());
            for (Course course : CourseLab.sCourseList) {
                String str = "";
                String courseName = course.getName();
                String classroom = course.getClassroom();
                str += classroom + " ; ";
                int startWeek = course.getStartWeek();
                int endWeek = course.getEndWeek();
                String weekStyle = course.getWeekStyle();
                String teacher = course.getTeacher();

                List<CourseTime> courseTimeList = course.getTimes();
                for (CourseTime courseTime : courseTimeList) {
                    String week = courseTime.getWeek();
                    int startTime = courseTime.getStartTime();
                    int endTime = courseTime.getEndTime();
                    str += week + " " + startTime + "－" + endTime + "节" + " ";
                }
                if (weekStyle.equals("单双周"))
                    str += "; " + teacher + " ; " + startWeek + "－" + endWeek + "周";
                else
                    str += "; " + teacher + " ; " + startWeek + "－" + endWeek + "周" + "(" + weekStyle + ")";
                MyModel model = new MyModel(courseName, str);
                data.add(model);
                runOnUiThread(() -> {
                    myAdapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    public void removeAllCourseLDialog() {
        PromptAdapter.Builder builder = new PromptAdapter.Builder(ListViewActivity.this);
        builder.setTitle("提示");
        builder.setContent("确定要删除所有课程信息吗");
        builder.setRight("确定", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            data.clear();
            myAdapter.notifyDataSetChanged();
            new Thread(CourseLab::deleteAllCourse).start();
            Toast.makeText(ListViewActivity.this, "课程已全部清空", Toast.LENGTH_SHORT).show();
        });
        builder.setLeft("取消", (dialogInterface, i) -> dialogInterface.dismiss());
        PromptAdapter dialog = builder.create();
        dialog.show();
    }
}

