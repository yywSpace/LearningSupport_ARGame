package com.example.learningsupport_argame.community.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.community.adapter.MsgAdapter;
import com.example.learningsupport_argame.community.data.MagUtil_shetuan;
import com.example.learningsupport_argame.community.data.Msg;

import java.util.List;

public class ShetuanFragment extends Fragment {

    private ListView listView;

    private List<Msg> msgList;

    private MsgAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.shetuan_fragment_layout, container, false);
        return view;

    }
    @Override
    public void onStart() {
        super.onStart();


        listView = getActivity().findViewById(R.id.listview);

        msgList = new MagUtil_shetuan().getMsgList();//现在用到的是一个空的构造方法，以后请用一个uid作为参数创建实例

        adapter = new MsgAdapter(msgList,getContext());

        listView.setAdapter(adapter);

    }
}
