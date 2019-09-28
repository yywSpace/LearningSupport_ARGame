package com.example.learningsupport_argame.community.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.bean.PairInfoBean;
import com.example.learningsupport_argame.community.adapter.CoinBarAdapter;
import com.example.learningsupport_argame.community.adapter.ExpandCoinAdapter;
import com.example.learningsupport_argame.databinding.HaoyouallFragmentLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class HaoyouAllFragment extends Fragment {
    private HaoyouallFragmentLayoutBinding binding;
    private CoinBarAdapter coinBarAdapter;
    List<String> partentList=new ArrayList<>();
    List<List<PairInfoBean>> childList=new ArrayList<>();
    private Context con;
    private ExpandCoinAdapter expandCoinAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
       // View view = inflater.inflate(R.layout.haoyouall_fragment_layout, container, false);

        con =this.getContext();
         binding= DataBindingUtil.inflate( inflater,R.layout.haoyouall_fragment_layout,container, false);
        View view =  binding.getRoot();

        binding.setPresenter(this);

        binding.recycleList.setHasFixedSize(true);
        binding.recycleList.setLayoutManager(new LinearLayoutManager(con));
        coinBarAdapter=new CoinBarAdapter(con);
        binding.recycleList.setAdapter(coinBarAdapter);

        expandCoinAdapter=new ExpandCoinAdapter(con,partentList,childList);
        binding.expandedMenu.setAdapter(expandCoinAdapter);
        binding.expandedMenu.setGroupIndicator(null);

        getBar();
        getExpandData();

        binding.recycleList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (coinBarAdapter.getDataCount()>0){
                    float height=binding.recycleList.getHeight()/coinBarAdapter.getDataCount();
                    float x=0;
                    float y=0;
                    switch (event.getAction()){

                        case MotionEvent.ACTION_DOWN:
                            y=Math.abs(event.getY());
                            int downPos= (int) (y/height);
                            binding.expandedMenu.setSelectedGroup(downPos);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            y=Math.abs(event.getY());
                            int pos= (int) (y/height);

                            binding.expandedMenu.setSelectedGroup(pos);
//                            showToast(coinBarAdapter.getDataList().get(pos));
                            break;
                        case MotionEvent.ACTION_UP:


                            break;
                    }
                }
                return false;
            }
        });
        return view;
    }
    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void getExpandData(){

        char  a='A';
        for (int i=0;i<26;i++){
            partentList.add(a+"");
            List<PairInfoBean> pairInfoBeanList=new ArrayList<>();
            for (int j=0;j<10;j++){
                PairInfoBean pairInfoBean=new PairInfoBean();
                pairInfoBean.pairName=a+"/"+j;

                pairInfoBeanList.add(pairInfoBean);
            }
            childList.add(pairInfoBeanList);
            a++;

        }
        expandCoinAdapter.notifyDataSetChanged();

        for (int i=0;i<expandCoinAdapter.getGroupCount();i++){
            binding.expandedMenu.expandGroup(i);
        }
    }


    public void getBar(){
        List<String> list=new ArrayList<>();
        char  a='A';
        for (int i=0;i<26;i++){
            list.add(a+"");
            a++;
        }
        coinBarAdapter.setData(list);


    }




}
