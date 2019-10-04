package com.example.learningsupport_argame.tempararyfile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.bean.PairInfoBean;
import com.example.learningsupport_argame.community.adapter.FriendtAdapter;
import com.example.learningsupport_argame.task.data.TaskInfo;

import java.util.ArrayList;
import java.util.List;

public class tasklist_fragment extends Fragment {
    @Override
    public void onStart() {
        super.onStart();
        Context con= getContext();
        ListView listView = getActivity().findViewById(R.id.list_task_redo);
        TaskInfo t1=new TaskInfo();
        TaskInfo.FormatTask f1= t1.new FormatTask();
        f1.set("任务1","2019-10-32","金明综合楼8号楼",null,"hello",null);
        t1.unzip(f1);
        TaskInfo t2=new TaskInfo();
        TaskInfo.FormatTask f2=t2.new FormatTask();
        f2.set("任务2","2019-9-32","金明综合楼10号楼",null,"goodbye",null);
        t2.unzip(f2);
        TaskInfo t3=new TaskInfo();
        TaskInfo.FormatTask f3= t3.new FormatTask();
        f3.set("任务3","2019-10-32","金明综合楼9号楼",null,"嘤嘤嘤",null);
        t3.unzip(f3);
        List<TaskInfo> list =new ArrayList<>();
        list.add(t1);
        list.add(t2);
        list.add(t3);


        AbbTaskAdapter adapter =new AbbTaskAdapter(list,con);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.task_card_layout, null);//获取自定义布局
                builder.setView(new tasklist_fragment.MyViewHolder(layout).finalview());
                builder.setTitle("任务详情");

                builder.setPositiveButton("确定", null);
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View view = inflater.inflate(R.layout.tasklist_redo_layout, container, false);
        return view;
    }
    class MyViewHolder {




        ImageView likeImageView;

        ImageView dislikeImageView;
        ImageButton tishi;

        ImageButton startrwbtn;
        ImageButton ditubtn;
        TextClock textClock;
        ListView listView;
        List<PairInfoBean> peopleList = new ArrayList<PairInfoBean>();
        View view;

        MyViewHolder(View itemView) {



//                avatarImageView = (ImageView) itemView.findViewById(R.id.iv_avatar_task);

            likeImageView = (ImageView) itemView.findViewById(R.id.iv_like_task);

            dislikeImageView = (ImageView) itemView.findViewById(R.id.iv_dislike_task);
            startrwbtn=(ImageButton)itemView.findViewById(R.id.btn_renwukaishi);
            ditubtn =(ImageButton)itemView.findViewById(R.id.btn_ditu);

            textClock = (TextClock) itemView.findViewById(R.id.date_window);
            listView = (ListView) itemView.findViewById(R.id.list_chenyuan);

            tishi=(ImageButton)itemView.findViewById(R.id.wenhao);
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
                        new  AlertDialog.Builder(getContext())
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
                        Toast.makeText(getContext(), "这里处mm理事件", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getContext(), "这里处理事件", Toast.LENGTH_SHORT).show();

                    }
                    // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            this.view =itemView;
//                talkbutton.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                            talkbutton.getBackground().setAlpha(70);
//                        }
//                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                            talkbutton.getBackground().setAlpha(0);
//                            //这里处理事件。
//                            Toast.makeText(con, "这里处理事件", Toast.LENGTH_SHORT).show();
//
//                        }
//                        // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
//                        return false;
//
//                    }
//                });
//
//
//                infobutton.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                            infobutton.getBackground().setAlpha(70);
//                        }
//                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                            infobutton.getBackground().setAlpha(0);
//                            //这里处理事件。
//                            Toast.makeText(con, "这里处理事件", Toast.LENGTH_SHORT).show();
//                            return false;
//                        }
//                        return false;
//                    }
//                });
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

        private View finalview(){
            return view;
        }
    }
}
