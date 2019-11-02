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

public class FriendItemAdapter extends RecyclerView.Adapter<FriendItemAdapter.FriendItemViewHolder> {

    public static final int TYPE_SEARCH = 0;
    public static final int TYPE_NORMAL = 1;

    private Context mContext;
    private List<String> mDatas;

    private LayoutInflater mLayoutInflater;

    public FriendItemAdapter(Context context, List<String> datas) {
        mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_SEARCH : TYPE_NORMAL;
    }

    @Override
    public FriendItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case TYPE_NORMAL:
//
//                return new FriendItemViewHolder(mLayoutInflater.inflate(R.layout.item_haoyou_layout, parent, false));
//              case TYPE_SEARCH:
//
//               return new FriendItemViewHolder(mLayoutInflater.inflate(R.layout.special_layout, parent, false));
//        }
        return new FriendItemViewHolder(mLayoutInflater.inflate(R.layout.item_haoyou_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(FriendItemViewHolder holder, int position) {
        holder.infoButton.getBackground().setAlpha(0);
        holder.infoButton.getBackground().setAlpha(0);
        holder.infoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.getBackground().setAlpha(70);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.getBackground().setAlpha(0);
                    //这里处理事件。
                    Toast.makeText(mContext, "这里处理事件", Toast.LENGTH_SHORT).show();

                }
                // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                return false;

            }
        });


        holder.infoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.getBackground().setAlpha(70);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.getBackground().setAlpha(0);
                    //这里处理事件。
                    Toast.makeText(mContext, "这里处理事件", Toast.LENGTH_SHORT).show();
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

    public class FriendItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton talkButton;
        ImageButton infoButton;

        public FriendItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            talkButton = itemView.findViewById(R.id.btn_talk);
            infoButton =  itemView.findViewById(R.id.btn_info);
        }
    }

}
