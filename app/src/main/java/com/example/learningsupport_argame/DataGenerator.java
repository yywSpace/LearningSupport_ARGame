package com.example.learningsupport_argame;

import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningsupport_argame.fragmentPak.FujinFragment;
import com.example.learningsupport_argame.fragmentPak.HaoyouAllFragment;
import com.example.learningsupport_argame.fragmentPak.HaoyouFragment;
import com.example.learningsupport_argame.fragmentPak.ShetuanFragment;

public class DataGenerator {

    //这个类和FriendList_Main配套使用为其提供data数据构造tab
    public static final int []mTabRes = new int[]{R.drawable.aixin,R.drawable.shetuan,R.drawable.fujin,R.drawable.haoyouall};//图标
    public static final int []mTabResPressed = new int[]{R.drawable.aixinselected,R.drawable.shetuanselected,R.drawable.fujinselected,R.drawable.haoyouallselected};
    public static final String []mTabTitle = new String[]{"我的好友","社团","附近","所有人"};//title

    public static Fragment[] getFragments(String from){//
        Fragment fragments[] = new Fragment[4];
        fragments[0] = new HaoyouFragment();
        fragments[1] = new ShetuanFragment();
        fragments[2] = new FujinFragment();
        fragments[3] = new HaoyouAllFragment();
        return fragments;
    }

    /**
     * 获取Tab 显示的内容
     * @param context
     * @param position
     * @return
     */
    public static View getTabView(Context context, int position){
        View view = LayoutInflater.from(context).inflate(R.layout.friendlist_tab_layout,null);
        TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
        tabText.setText(mTabTitle[position]);

        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
        tabIcon.setImageResource(DataGenerator.mTabRes[position]);
        tabIcon.getLayoutParams().height = 80;
        tabIcon.getLayoutParams().width = 80;

        return view;
    }
}

