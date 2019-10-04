package com.example.learningsupport_argame.task.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.community.adapter.FriendtAdapter;
import com.example.learningsupport_argame.bean.PairInfoBean;
import com.example.learningsupport_argame.community.ainmation.CardConfig;
import com.example.learningsupport_argame.community.ainmation.CardItemTouchHelperCallBack;
import com.example.learningsupport_argame.community.ainmation.CardLayoutManager;
import com.example.learningsupport_argame.community.ainmation.OnSwipeListener;

import java.util.ArrayList;
import java.util.List;

public class taskingFragment extends Fragment {

    private Context con;
    private List<Integer> list = new ArrayList<>();
    private Integer lastcard;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_now_fragment_layout, container, false);



        return view;
    }



        @Override
    public void onStart() {
        super.onStart();
        initView();
        initData();
        Log.d("KK","ing on start");

//        talkbutton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
//                    talkbutton.getBackground().setAlpha(50);
//                }
//                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
//                    talkbutton.getBackground().setAlpha(0);
//                    //这里处理事件。
//                    Toast.makeText(con,"这里处理事件",Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = this.getContext();

    }

    private void initView() {

        final RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerViewforTasking);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new taskingFragment.MyAdapter());

        CardItemTouchHelperCallBack cardCallback = new CardItemTouchHelperCallBack(recyclerView.getAdapter(), list);

        cardCallback.setOnSwipedListener(new OnSwipeListener<Integer>() {


            @Override

            public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {

                taskingFragment.MyAdapter.MyViewHolder myHolder = (taskingFragment.MyAdapter.MyViewHolder) viewHolder;

                viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);

                if (direction == CardConfig.SWIPING_LEFT) {

//                    myHolder.dislikeImageView.setAlpha(Math.abs(ratio));

                } else if (direction == CardConfig.SWIPING_RIGHT) {

//                    myHolder.likeImageView.setAlpha(Math.abs(ratio));

                } else {

//                    myHolder.dislikeImageView.setAlpha(0f);
//
//                    myHolder.likeImageView.setAlpha(0f);


                }

            }


            @Override

            public void onSwiped(RecyclerView.ViewHolder viewHolder, Integer o, int direction) {

                taskingFragment.MyAdapter.MyViewHolder myHolder = (taskingFragment.MyAdapter.MyViewHolder) viewHolder;

//                viewHolder.itemView.setAlpha(1f);
//
//                myHolder.dislikeImageView.setAlpha(0f);
//
//                myHolder.likeImageView.setAlpha(0f);
                //   lastcard=myHolder.avatarImageView.getId();

                Toast.makeText(con, direction == CardConfig.SWIPED_LEFT ? "swiped left" : "swiped right", Toast.LENGTH_SHORT).show();

            }


            @Override

            public void onSwipedClear() {

                Toast.makeText(con, "data clear", Toast.LENGTH_SHORT).show();

                recyclerView.postDelayed(new Runnable() {

                    @Override

                    public void run() {

                        initData();

                        recyclerView.getAdapter().notifyDataSetChanged();

                    }

                }, 3000L);

            }


        });

        final ItemTouchHelper touchHelper = new ItemTouchHelper(cardCallback);

        final CardLayoutManager cardLayoutManager = new CardLayoutManager(recyclerView, touchHelper);

        recyclerView.setLayoutManager(cardLayoutManager);

        touchHelper.attachToRecyclerView(recyclerView);

    }

    private void getLastCard() {

//获得当前长度，总长-当前长既是位置，add到那个位置，在notify
    }


    private void initData() {

        list.add(R.drawable.img_avatar_01);

//        list.add(R.drawable.img_avatar_02);
//
//        list.add(R.drawable.img_avatar_03);
//
//        list.add(R.drawable.img_avatar_04);
//
//        list.add(R.drawable.img_avatar_05);
//
//        list.add(R.drawable.img_avatar_06);
//
//        list.add(R.drawable.img_avatar_07);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("KK","ing on resume");
    }

    private class MyAdapter extends RecyclerView.Adapter {

        @Override

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_layout, parent, false);

            return new taskingFragment.MyAdapter.MyViewHolder(view);

        }


        @Override

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

//            ImageView avatarImageView = ((taskingFragment.MyAdapter.MyViewHolder) holder).avatarImageView;
//
//            avatarImageView.setImageResource(list.get(position));

        }


        @Override

        public int getItemCount() {

            return list.size();

        }


        class MyViewHolder extends RecyclerView.ViewHolder {


//

            ImageView likeImageView;
            ImageView dislikeImageView;
            ImageButton startrwbtn;
            ImageButton ditubtn;
            ImageButton tishi;
            TextClock textClock;
            ListView listView;

            List<PairInfoBean> peopleList = new ArrayList<PairInfoBean>();


            MyViewHolder(View itemView) {

                super(itemView);


//                likeImageView = (ImageView) itemView.findViewById(R.id.iv_like_task);
//
//                dislikeImageView = (ImageView) itemView.findViewById(R.id.iv_dislike_task);

//
                  startrwbtn=(ImageButton)itemView.findViewById(R.id.btn_renwukaishi);
                  ditubtn =(ImageButton)itemView.findViewById(R.id.btn_ditu);

                textClock = (TextClock) itemView.findViewById(R.id.date_window);
                listView = (ListView) itemView.findViewById(R.id.list_chenyuan);
                tishi =(ImageButton)itemView.findViewById(R.id.wenhao);


                InitialData();
                FriendtAdapter adapter=new FriendtAdapter(peopleList,getContext());
                listView.setAdapter(adapter);
                textClock.setFormat12Hour("1998-08-01 12:00:00");
//                talkbutton.getBackground().setAlpha(0);
//                infobutton.getBackground().setAlpha(0);
                startrwbtn.getBackground().setAlpha(0);
                ditubtn.getBackground().setAlpha(0);
                tishi.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                           tishi.getBackground().setAlpha(70);
                        }
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            tishi.getBackground().setAlpha(0);
                            //这里处理事件。
                         //   Toast.makeText(con, "弹出提示框", Toast.LENGTH_SHORT).show();
                            new  AlertDialog.Builder(con)
                                    .setTitle("任务类型提示" )
                                    .setMessage("CheckBox选择框\n\t\t\t\t\t为红色则表示自建任务\n\t\t\t\t\t为绿色则表示社团任务\n\t\t\t\t\t为蓝色则表示好友指定任务\n\t\t\t\t\t为紫色则表示主动的接受任务")
                                    .setNegativeButton("取消",null)
                                    .show();


                        }
                        return false;
                    }
                });
                ditubtn.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            ditubtn.getBackground().setAlpha(70);
                        }
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            ditubtn.getBackground().setAlpha(0);
                            //这里处理事件。
                            Toast.makeText(con, "这里处nn理事件", Toast.LENGTH_SHORT).show();

                        }
                        // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
               startrwbtn.setOnTouchListener(new View.OnTouchListener() {
                   @Override
                   public boolean onTouch(View view, MotionEvent motionEvent) {
                       if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            startrwbtn.getBackground().setAlpha(70);
                        }
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            startrwbtn.getBackground().setAlpha(0);
                            //这里处理事件。
                            Toast.makeText(con, "这里处理事件", Toast.LENGTH_SHORT).show();

                        }
                        // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                        return false;
                       }
               });
//
//
            }

            private void InitialData() {
                char a = 'A';

                for (int j = 0; j < 10; j++) {
                    PairInfoBean pairInfoBean = new PairInfoBean();
                    pairInfoBean.pairName = a + "/" + j;

                    peopleList.add(pairInfoBean);
                    a++;
                }

            }
        }
    }
}