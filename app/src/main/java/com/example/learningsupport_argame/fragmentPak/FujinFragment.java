package com.example.learningsupport_argame.fragmentPak;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.other.CardConfig;
import com.example.learningsupport_argame.other.CardItemTouchHelperCallBack;
import com.example.learningsupport_argame.other.CardLayoutManager;
import com.example.learningsupport_argame.other.OnSwipeListener;

import java.util.ArrayList;
import java.util.List;


public class FujinFragment extends Fragment {
    private List<Integer> list = new ArrayList<>();
    private Context con;
    private Integer lastcard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fujin_fragment_layout, container, false);


//       talkbutton.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               Toast.makeText(con,"这里处理事件",Toast.LENGTH_SHORT).show();
//               talkbutton.getBackground().setAlpha(50);
//               try {
//                   Thread.sleep(50);
//               } catch (InterruptedException e) {
//                   e.printStackTrace();
//               }
//               talkbutton.getBackground().setAlpha(0);
//               Log.d("inlog", "进入事件处理 " );
//
//           }
//       });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
        initData();

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
        con=this.getContext();

    }

    private void initView() {

        final RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new MyAdapter());

        CardItemTouchHelperCallBack cardCallback = new CardItemTouchHelperCallBack(recyclerView.getAdapter(), list);

        cardCallback.setOnSwipedListener(new OnSwipeListener<Integer>() {



            @Override

            public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {

                MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;

                viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);

                if (direction == CardConfig.SWIPING_LEFT) {

                    myHolder.dislikeImageView.setAlpha(Math.abs(ratio));

                } else if (direction == CardConfig.SWIPING_RIGHT) {

                    myHolder.likeImageView.setAlpha(Math.abs(ratio));

                } else {

                    myHolder.dislikeImageView.setAlpha(0f);

                    myHolder.likeImageView.setAlpha(0f);


                }

            }



            @Override

            public void onSwiped(RecyclerView.ViewHolder viewHolder, Integer o, int direction) {

                MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;

                viewHolder.itemView.setAlpha(1f);

                myHolder.dislikeImageView.setAlpha(0f);

                myHolder.likeImageView.setAlpha(0f);
                lastcard=myHolder.avatarImageView.getId();

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

    private void getLastCard(){

//获得当前长度，总长-当前长既是位置，add到那个位置，在notify
    }





    private void initData() {

        list.add(R.drawable.img_avatar_01);

        list.add(R.drawable.img_avatar_02);

        list.add(R.drawable.img_avatar_03);

        list.add(R.drawable.img_avatar_04);

        list.add(R.drawable.img_avatar_05);

        list.add(R.drawable.img_avatar_06);

        list.add(R.drawable.img_avatar_07);//这里是头像图片

    }



    private class MyAdapter extends RecyclerView.Adapter {

        @Override

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_layout, parent, false);

            return new MyViewHolder(view);

        }



        @Override

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ImageView avatarImageView = ((MyViewHolder) holder).avatarImageView;

            avatarImageView.setImageResource(list.get(position));//设置头像

            //也请在这个放置每一个人的数据形象，可以将之绑定在list里面，后将list的元素换成一个用户类实体pairOfbean

        }



        @Override

        public int getItemCount() {

            return list.size();

        }



        class MyViewHolder extends RecyclerView.ViewHolder {



            ImageView avatarImageView;

            ImageView likeImageView;

            ImageView dislikeImageView;

            ImageButton talkbutton;
            ImageButton infobutton;



            MyViewHolder(View itemView) {

                super(itemView);

                avatarImageView = (ImageView) itemView.findViewById(R.id.iv_avatar);

                likeImageView = (ImageView) itemView.findViewById(R.id.iv_like);

                dislikeImageView = (ImageView) itemView.findViewById(R.id.iv_dislike);

                talkbutton=(ImageButton) itemView.findViewById(R.id.btn_talk);
                infobutton=(ImageButton) itemView.findViewById(R.id.btn_info);
                talkbutton.getBackground().setAlpha(0);
                infobutton.getBackground().setAlpha(0);
                talkbutton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                           talkbutton.getBackground().setAlpha(70);
                         }
                        if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                         talkbutton.getBackground().setAlpha(0);
                         //这里处理事件。
                            Toast.makeText(con,"这里处理事件",Toast.LENGTH_SHORT).show();

                }
               // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                return false;

            }});



                infobutton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                                infobutton.getBackground().setAlpha(70);
                            }
                            if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                                infobutton.getBackground().setAlpha(0);
                                //这里处理事件。
                                Toast.makeText(con,"这里处理事件",Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        return false;
                    }
                });





        }

    }}}












