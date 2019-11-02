package com.example.learningsupport_argame.community.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.community.adapter.TestAdapter;

import java.util.ArrayList;
import java.util.List;

public class HaoyouFragment extends Fragment {

//    private ListView listView;
//
//    private List<Msg> msgList;
//
//    private MsgAdapter adapter;


    private LinearLayout ll_search;  //外层的搜索框控件
    private SwipeRefreshLayout refreshLayout;
    private int mLlSearchHeight; // 搜索框的高度

    private int mScrollY;  //recyclerview 滑动的距离

    private boolean isShow = true;  //搜索框是否显示
    private RecyclerView recycler;

    private boolean isAnimmating;//是否正在进行动画
    private Context con;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        View view = inflater.inflate(R.layout.haoyou_fragment_layout, container, false);
        View view = inflater.inflate(R.layout.haoyou_layout_motified ,container, false);


        con = this.getContext();


        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        ll_search = (LinearLayout) view.findViewById(R.id.ll_search);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


//                listView = getActivity().findViewById(R.id.listview);
//
//
//                //在这个fragment里面点击刷新按钮，则执行方法，运行服务器类的获取方法，获取得到一个list列表（这个list列表可以是PariofBean）然后将这个规范化的列表直接传递给adapter
//
//                msgList = MsgUtil.getMsgList();//这里放置每一个人的用户数据，是以一个msg类保存在一个列表里//这个类打算后期不用！！！
//
//
//                adapter = new MsgAdapter(msgList,getContext());//若如上这adapter也要做相应的修改
//
//
//                listView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reflash();

            }
        });


        // refreshLayout.stopNestedScroll();
        List<String> list = new ArrayList<>();
        list.add("你好啊");
        list.add("哭丧脸");
        list.add("啦啦啦");
        list.add("哭丧脸");
        list.add("啦啦啦");
        list.add("哭丧脸");
        list.add("啦啦啦");
        TestAdapter adapter = new TestAdapter(con, list);
        recycler.setLayoutManager(new LinearLayoutManager(con));
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dy是垂直滚动距离，手指上滑动的时候为正，手指下滑的时候为负

                //需要获取llSearch 的高度作为 判断条件   所以布局文件中  llSearch的 visiable属性  不能设置为 gone
                // 设置为  gone之后，llSearch 不进行渲染  获取不到高度
                if (mLlSearchHeight == 0) {
                    mLlSearchHeight = ll_search.getHeight();
                }
                //记录滑动的距离
                mScrollY += dy;

                if (mScrollY <= 0) {
                    ll_search.setVisibility(View.INVISIBLE);
                } else {
                    ll_search.setVisibility(View.VISIBLE);
                }

                if (isAnimmating || (mScrollY <= mLlSearchHeight)) {
                    return;
                }

                if (dy < 0) {

                    if (isShow) {
                        return;
                    }

                    ObjectAnimator animator = ObjectAnimator.ofFloat(ll_search, "translationY", -mLlSearchHeight, 0);
                    animator.setDuration(300);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isShow = true;
                            isAnimmating = false;
                            animation.removeAllListeners();
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            isAnimmating = true;

                        }
                    });
                    animator.start();
                } else {
                    if (!isShow) {
                        return;
                    }
                    ObjectAnimator animator = ObjectAnimator.ofFloat(ll_search, "translationY", 0, -mLlSearchHeight);
                    animator.setDuration(300);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isShow = false;
                            isAnimmating = false;
                            animation.removeAllListeners();
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            isAnimmating = true;

                        }
                    });
                    animator.start();
                }
            }
        });


    }

    private void reflash() {

        Toast.makeText(con, "处理事件", Toast.LENGTH_SHORT).show();
        if (refreshLayout.isRefreshing() == true) ;
        {
            refreshLayout.setRefreshing(false);
        }

    }
}

