package com.example.learningsupport_argame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.learningsupport_argame.fragmentPak.HaoyouFragment;
import com.example.learningsupport_argame.fragmentPak.ShetuanFragment;

public class DataGeneratorForTask {
    public static final int []mTabRes = new int[]{R.drawable.aixin,R.drawable.shetuan};//图标
    public static final int []mTabResPressed = new int[]{R.drawable.aixinselected,R.drawable.shetuanselected};
    public static final String []mTabTitle = new String[]{"执行中","所有任务"};//title

    public static Fragment[] getFragments(String from){//
        Fragment fragments[] = new Fragment[2];
        fragments[0] = new HaoyouFragment();//需要更换图标和设置它们的fragment
        fragments[1] = new ShetuanFragment();

        return fragments;
    }

    /**
     * 获取Tab 显示的内容
     * @param context
     * @param position
     * @return
     */
    public static View getTabView(Context context, int position){
        View view = LayoutInflater.from(context).inflate(R.layout.task_tab_layout,null);
        TextView tabText = (TextView) view.findViewById(R.id.tab_content_text_task);
        tabText.setText(DataGeneratorForTask.mTabTitle[position]);

        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image_task);
        tabIcon.setImageResource(DataGeneratorForTask.mTabRes[position]);
        tabIcon.getLayoutParams().height = 80;
        tabIcon.getLayoutParams().width = 80;

        return view;
    }
}
