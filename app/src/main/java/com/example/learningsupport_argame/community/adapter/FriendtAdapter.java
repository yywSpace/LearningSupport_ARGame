package com.example.learningsupport_argame.community.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.bean.PairInfoBean;

import java.util.List;

public class FriendtAdapter extends BaseAdapter{

    private List<PairInfoBean> list;
    private Context mcon;

    public FriendtAdapter(List<PairInfoBean> list, Context mcon) {
        this.list = list;
        this.mcon = mcon;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater =LayoutInflater.from(this.mcon);
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_coin_sel_child,null);
            holder= new ViewHolder();

            holder.thename=(TextView)convertView.findViewById(R.id.tv_coin_name);

            convertView.setTag(holder);

        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.thename.setText(list.get(i).getPairName());


//        if(i%2==1){
//            convertView.setBackgroundColor(Color.rgb(105,105,105));
//
//        }
//        else{
//            convertView.setBackgroundColor(Color.rgb(151,255,255));
//        }






        return convertView;
    }
    static class ViewHolder{
        ImageView imageView;
        TextView thename;
        TextView thevalue;
    }


}
