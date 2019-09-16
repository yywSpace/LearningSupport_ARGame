package com.example.learningsupport_argame.fragmentPak;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.adapter.MsgAdapter;
import com.example.learningsupport_argame.bean.Msg;
import com.example.learningsupport_argame.bean.MsgUtil;

import java.util.List;

public class HaoyouFragment extends Fragment {

    private ListView listView;

    private List<Msg> msgList;

    private MsgAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.haoyou_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


                listView = getActivity().findViewById(R.id.listview);


                //在这个fragment里面点击刷新按钮，则执行方法，运行服务器类的获取方法，获取得到一个list列表（这个list列表可以是PariofBean）然后将这个规范化的列表直接传递给adapter

                msgList = MsgUtil.getMsgList();//这里放置每一个人的用户数据，是以一个msg类保存在一个列表里//这个类打算后期不用！！！


                adapter = new MsgAdapter(msgList,getContext());//若如上这adapter也要做相应的修改


                listView.setAdapter(adapter);

            }

        }

