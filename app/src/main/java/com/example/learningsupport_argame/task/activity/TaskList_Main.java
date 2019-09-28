package com.example.learningsupport_argame.task.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.task.fragment.taskAllFragment;
import com.example.learningsupport_argame.task.fragment.taskingFragment;
import com.example.learningsupport_argame.community.ainmation.BottomSectorMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class TaskList_Main extends AppCompatActivity {
    private BottomNavigationViewEx bnve;
    private VpAdapter adapter;
    private List<Fragment> fragments;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;
    private boolean fabOpeded=false;
    private TextView textView;
    private Context con;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasklist_layout);

        initView();
        initData();
        initBNVE();
        initEvent();

    }

    /**
     * init BottomNavigationViewEx envent
     */
    private void initEvent() {
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            Menu menu =bnve.getMenu();

            MenuItem lastitem=menu.findItem(R.id.menu_main);

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int position = -1;
                switch (item.getItemId()) {
                    case R.id.menu_main:
                        position = 0;
                        if(lastitem!=null){
                            lastitem.setIcon(R.drawable.renwuall);
                        }

                        item.setIcon(R.drawable.renwuselected);
                        lastitem=item;


                        break;
                    case R.id.menu_me:
                        position = 1;
                        if(lastitem!=null){
                            lastitem.setIcon(R.drawable.renwu);
                        }

                        item.setIcon(R.drawable.renwuallselected);
                        lastitem=item;
                        break;
//                    case R.id.menu_empty: {
//                        position = 1;
//                        //此处return false且在FloatingActionButton没有自定义点击事件时 会屏蔽点击事件
//                       // return false;
//                   }
                    default:
                        break;
                }

                if (previousPosition != position) {
                  viewPager.setCurrentItem(position, false);
                    previousPosition = position;
                }

                return true;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//
//                if (0 == position) {
//                    floatingActionButton.setImageResource(R.drawable.tianjia);
//
//                }
//                if(2 == position) {
//                    floatingActionButton.setImageResource(R.drawable.tianjia);
//
//                }
               // 1 is center 此段结合屏蔽FloatingActionButton点击事件的情况使用
                  //在viewPage滑动的时候 跳过最中间的page也
                if (position >= 1) {
                    position++;
                }
                bnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
/**
 * fab 点击事件结合OnNavigationItemSelectedListener中return false使用
 */
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(!fabOpeded){
//                    openMenu(view);
//                }else{
//                    closeMenu(view);
//
//                }
//                Toast.makeText(TaskList_Main.this, "Center", Toast.LENGTH_SHORT).show();
//            }
//        });
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                closeMenu(floatingActionButton);
//            }
//        });
        class itemListener1 implements View.OnClickListener{

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
            }
        }
        class itemListener2 implements View.OnClickListener{

            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.create_task_layout, null);//获取自定义布局
                builder.setView(layout);
                builder.setTitle("创建个人任务");
                builder.setIcon(R.drawable.ziji);
                builder.setPositiveButton("确定",null);
                builder.setNegativeButton("取消",null);
                builder.show();


            }
        }
         new BottomSectorMenuView.Converter(floatingActionButton,this)
                .setToggleDuration(500, 800)
                .setAnchorRotationAngle(135f)
                 .addMenuItem(R.drawable.shetuan, "To社团",new itemListener1())
                 .addMenuItem(R.drawable.haoyou, "To好友",new itemListener1())
                 .addMenuItem(R.drawable.ar, "AR发布",new itemListener1())
                 .addMenuItem(R.drawable.ziji, "To自己",new itemListener2())
//                 .addMenuItem(R.drawable.aixin, "手写",new itemListener1())
                 .apply()  ;





    }

//    private void closeMenu(View view) {
//
//        ObjectAnimator animator =ObjectAnimator.ofFloat(view,"rotation",-135,20,0);
//        animator.setDuration(500);
//        animator.start();
////        textView.setVisibility(View.VISIBLE);
//        AlphaAnimation alphaAnimation =new AlphaAnimation(0,0.7f);
//        alphaAnimation.setDuration(500);
//        alphaAnimation.setFillAfter(true);
//        textView.startAnimation(alphaAnimation);
//        textView.setVisibility(View.GONE);
//        fabOpeded=false;
//
//    }
//
//    private void openMenu(View view) {
//
//        ObjectAnimator animator =ObjectAnimator.ofFloat(view,"rotation",0,-155,-135);
//        animator.setDuration(500);
//        animator.start();
//        textView.setVisibility(View.VISIBLE);
//        AlphaAnimation alphaAnimation =new AlphaAnimation(0,0.7f);
//        alphaAnimation.setDuration(500);
//        alphaAnimation.setFillAfter(true);
//        textView.startAnimation(alphaAnimation);
//        fabOpeded=true;
//
//
//    }


    private void initView() {
        con=this;
        floatingActionButton = findViewById(R.id.fab);
        viewPager = findViewById(R.id.vp);
        bnve = findViewById(R.id.bnve);
//        textView=findViewById(R.id.cloud);
    }

    /**
     * create fragments
     */
    private void initData() {
        fragments = new ArrayList<>(3);
//        TestFragment homeFragment = new TestFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("title", "首页");
//        homeFragment.setArguments(bundle);
//
//        TestFragment orderFragment = new TestFragment();
//        bundle = new Bundle();
//        bundle.putString("title", "订单");
//        orderFragment.setArguments(bundle);
//
////        taskAllFragment meFragment = new taskAllFragment();
//        TestFragment meFragment = new TestFragment();
//        bundle = new Bundle();
//        bundle.putString("title", "我的");
//        meFragment.setArguments(bundle);

//

        taskingFragment f1=new taskingFragment();
        taskAllFragment f2=new taskAllFragment();
        fragments.add(f1);
        fragments.add(f2);
//
    }

    /**
     * init BottomNavigationViewEx
     */
    private void initBNVE() {

        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);

        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

    }

    /**
     * view pager adapter
     */
    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;
        private  FragmentManager fragmentManager;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
            this.fragmentManager=fm;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {

           return  data.get(position);
        }
    }
}

