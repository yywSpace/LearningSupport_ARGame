package com.example.learningsupport_argame.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    public static final int TYPE_SEARCH = 0;
    public static final int TYPE_NORMAL = 1;

    private Context con;
    private List<String> mDatas;

    private LayoutInflater mLayoutInflater;

    public TestAdapter(Context context, List<String> datas) {
        mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
        con=context;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_SEARCH : TYPE_NORMAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case TYPE_NORMAL:
//
//                return new ViewHolder(mLayoutInflater.inflate(R.layout.item_haoyou_layout, parent, false));
//              case TYPE_SEARCH:
//
//               return new ViewHolder(mLayoutInflater.inflate(R.layout.special_layout, parent, false));
//        }
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_haoyou_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.infobutton.getBackground().setAlpha(0);
        holder.talkbutton.getBackground().setAlpha(0);
        holder. talkbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    view.getBackground().setAlpha(70);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    view.getBackground().setAlpha(0);
                    //这里处理事件。
                    Toast.makeText(con,"这里处理事件",Toast.LENGTH_SHORT).show();

                }
                // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                return false;

            }});



          holder.infobutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    view.getBackground().setAlpha(70);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    view .getBackground().setAlpha(0);
                    //这里处理事件。
                    Toast.makeText(con,"这里处理事件",Toast.LENGTH_SHORT).show();
                    return false;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageButton talkbutton;
        ImageButton infobutton;
        public ViewHolder(View itemView) {

            super(itemView);


            imageView =itemView.findViewById(R.id.imageview);
            talkbutton=(ImageButton) itemView.findViewById(R.id.btn_talk);
            infobutton=(ImageButton)itemView.findViewById(R.id.btn_info);


        }
    }

}
