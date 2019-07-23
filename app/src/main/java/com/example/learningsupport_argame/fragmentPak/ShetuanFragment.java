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
import com.example.learningsupport_argame.util.MsgUtil;

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

        msgList = com.example.learningsupport_argame.util.MagUtil_shetuan.getMsgList();

        adapter = new MsgAdapter(msgList,getContext());

        listView.setAdapter(adapter);

    }
}
