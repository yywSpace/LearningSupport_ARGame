package com.example.learningsupport_argame;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningsupport_argame.Fragment.FujinFragment;
import com.example.learningsupport_argame.Fragment.HaoyouAllFragment;
import com.example.learningsupport_argame.Fragment.HaoyouFragment;
import com.example.learningsupport_argame.Fragment.ShetuanFragment;

public class DataGenerator {
    public static final int []mTabRes = new int[]{R.drawable.aixin,R.drawable.shetuan,R.drawable.fujin,R.drawable.haoyouall};//图标
    public static final int []mTabResPressed = new int[]{R.drawable.aixinselected,R.drawable.shetuanselected,R.drawable.fujinselected,R.drawable.haoyouallselected};
    public static final String []mTabTitle = new String[]{"我的好友","社团","附件","所有人"};//title

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
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
        tabIcon.setImageResource(DataGenerator.mTabRes[position]);
        TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
        tabText.setText(mTabTitle[position]);
        return view;
    }
}

