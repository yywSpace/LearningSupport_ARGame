package com.example.learningsupport_argame.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.bean.Msg;

import java.util.List;

public class MsgAdapter extends BaseAdapter {

    private List<Msg> mMsgList;

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    public MsgAdapter(List<Msg> msgList, Context context) {

        this.mMsgList = msgList;

        this.mLayoutInflater = LayoutInflater.from(context);

        this.mContext = context;

    }

    @Override

    public int getCount() {

        return mMsgList.size();

    }

    @Override

    public Msg getItem(int position) {

        return mMsgList.get(position);

    }

    @Override

    public long getItemId(int position) {

        return position;

    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null){

            convertView = mLayoutInflater.inflate(R.layout.item_haoyou_layout,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.imageView = convertView.findViewById(R.id.imageview);

//            viewHolder.titleTV = convertView.findViewById(R.id.title_tv);
//
//            viewHolder.contentTV = convertView.findViewById(R.id.content_tv);

             viewHolder.talkbutton=(ImageButton) convertView.findViewById(R.id.btn_talk);
            viewHolder.infobutton=(ImageButton) convertView.findViewById(R.id.btn_info);
            viewHolder.talkbutton.getBackground().setAlpha(0);
            viewHolder. infobutton.getBackground().setAlpha(0);

            convertView.setTag(viewHolder);

        }else{

            viewHolder = (ViewHolder) convertView.getTag();

        }

        Msg msg = mMsgList.get(position);

        viewHolder.imageView.setImageResource(msg.getImageResourceID());

//        viewHolder.titleTV.setText(msg.getTitle());
//
//        viewHolder.contentTV.setText(msg.getContent());

        viewHolder. talkbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    view.getBackground().setAlpha(70);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                   view.getBackground().setAlpha(0);
                    //这里处理事件。
                    Toast.makeText(mContext,"这里处理事件",Toast.LENGTH_SHORT).show();

                }
                // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                return false;

            }});



        viewHolder.infobutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    view.getBackground().setAlpha(70);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                     view .getBackground().setAlpha(0);
                    //这里处理事件。
                    Toast.makeText(mContext,"这里处理事件",Toast.LENGTH_SHORT).show();
                    return false;
                }
                return false;
            }
        });

        return convertView;

    }

    private static class ViewHolder{

        ImageView imageView;

        TextView titleTV;

        TextView contentTV;
        ImageButton talkbutton;
        ImageButton infobutton;

    }

}

