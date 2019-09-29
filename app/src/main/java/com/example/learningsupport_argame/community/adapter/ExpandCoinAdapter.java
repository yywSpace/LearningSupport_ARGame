package com.example.learningsupport_argame.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningsupport_argame.bean.PairInfoBean;
import com.example.learningsupport_argame.R;

import java.util.List;

/**
 * create by zj on 2018/11/5
 */
public class ExpandCoinAdapter extends BaseExpandableListAdapter {
    private List<String> parentList;
    private List<List<PairInfoBean>> childList;
    private Context context;
    public ExpandCoinAdapter(Context context, List<String> parentList, List<List<PairInfoBean>> childList){
        this.context=context;
        this.parentList=parentList;
        this.childList=childList;
    }

    @Override
    public int getGroupCount() {
        return parentList==null?0:parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList==null?0:childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
       convertView= LayoutInflater.from(context).inflate(R.layout.item_coin_sel_parent,null);
        TextView textView=convertView.findViewById(R.id.tv_parent);
        textView.setText(parentList.get(groupPosition));
        textView.setTextSize(30);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(context).inflate(R.layout.item_coin_sel_child,null);
        TextView textView=convertView.findViewById(R.id.tv_coin_name);
        textView.setText(childList.get(groupPosition).get(childPosition).pairName);
        TextView textView1=convertView.findViewById(R.id.tv_coin_signature);
        textView1.setText("这里放TA的个性签名。");
        ImageView  imageView=convertView.findViewById(R.id.tv_coin_image);
        imageView.setImageResource(R.drawable.ic_launcher_foreground);
        imageView.getLayoutParams().width=200;
        imageView.getLayoutParams().height=200;

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    public static class  ViewHolderParent{


    }
}
