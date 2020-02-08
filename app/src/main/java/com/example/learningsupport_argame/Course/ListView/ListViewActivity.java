package com.example.learningsupport_argame.Course.ListView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.learningsupport_argame.Course.AddCourseActivity;
import com.example.learningsupport_argame.Course.Course;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.Course.CourseTimeActivity;
import com.example.learningsupport_argame.Course.Jie;
import com.example.learningsupport_argame.Course.PromptAdapter;
import com.example.learningsupport_argame.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    private List<MyModel> data = new ArrayList<>();
    private Context mContext;
    MyListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_listview_main_activity);
        LitePal.initialize(ListViewActivity.this);

        myListView=findViewById(R.id.my_list);

        //return_addcourse=findViewById(R.id.return_addcourse);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);//通知栏所需颜色

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mContext = this;
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        initData();
        final MyAdapter myAdapter = new MyAdapter(data, this);
        ListView myListView = (ListView) findViewById(R.id.my_list);
        myListView.setAdapter(myAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Toast.makeText(mContext, ((MyModel) myAdapter.getItem(position)).getGroupName(), Toast.LENGTH_SHORT).show();
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


        SQLiteDatabase db = Connector.getDatabase();
        if (db != null) {
            List<Course> list = LitePal.findAll(Course.class, true);

            for (Course course : list) {

                String str="";
                String course_name = course.getCourse_name();
                String classroom = course.getClassroom();
                str+=classroom+" ; ";
                int start_zhou = course.getStart_zhoushu();
                int end_zhou = course.getEnd_zhoushu();
                String dan_shuang_week = course.getWeekstyle();
                String teacher = course.getTeacher();


                List<Jie> list_jie=course.getJie();
                for (Jie jie : list_jie) {
                    String zhou = jie.getZhou();
                    int start_jie = jie.getStart_jieshu();
                    int end_jie = jie.getEnd_jieshu();

                    str+=zhou+" "+start_jie+"－"+end_jie+"节"+" ";
                }
                str.trim();
                if(dan_shuang_week.equals("单双周"))
                    str+="; "+teacher+" ; "+start_zhou+"－"+end_zhou+"周";
                else
                    str+="; "+teacher+" ; "+start_zhou+"－"+end_zhou+"周"+"("+dan_shuang_week+")";
                MyModel model= new MyModel( course_name, str);
                data.add(model);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void return_addCourse_onClick(View view){
//        if(getIntent()==null)
//            getIntent().get
//            Toast.makeText(ListViewActivity.this,,Toast.LENGTH_SHORT).show();
//        if(getIntent()==null) {
//            Intent intent = new Intent(ListViewActivity.this, CourseMainActivity.class);
//            startActivity(intent);
//        }
        String str=getIntent().getStringExtra("flag");
        Toast.makeText(ListViewActivity.this,str,Toast.LENGTH_SHORT).show();
        if(str.equals("CourseMainActivity")){
            Intent intent=new Intent(ListViewActivity.this,CourseMainActivity.class);
            startActivity(intent);
        }
        ListViewActivity.this.finish();

    }

    public void dialog(){

        PromptAdapter.Builder builder = new PromptAdapter.Builder(ListViewActivity.this);
        builder.setTitle("提示");
        builder.setContent("确定要删除所有课程信息吗");
        builder.setRight("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                LitePal.deleteAll(Course.class);
                myListView.setAdapter(null);
                Toast.makeText(ListViewActivity.this, "课程已全部清空", Toast.LENGTH_SHORT).show();


            }

        });
        builder.setLeft("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        PromptAdapter dialog=builder.create();
        dialog.show();


    }

    public void delete_allcourse_onClick(View view)
    {

        //LitePal.deleteAll(Course.class);
        dialog();


//        for(int i=0;i<data.size();i++)
//            data.remove(i);
//        notify();


    }
}

