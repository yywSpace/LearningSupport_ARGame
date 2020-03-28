package com.example.learningsupport_argame.Task.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learningsupport_argame.Task.TaskShowView;

public class CurrentTaskFragment extends Fragment {
    private String TAG = "CurrentTaskFragment";
    private TaskShowView mTaskShowView;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mTaskShowView = new TaskShowView(mActivity);
        return mTaskShowView.getView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 更新数据
        mTaskShowView.updateData();
    }


    public static CurrentTaskFragment getInstance() {
        CurrentTaskFragment fragment = new CurrentTaskFragment();
//        Bundle args = new Bundle();
//        args.putString(User.CURRENT_USER_ID, userId);
//        fragment.setArguments(args);
        return fragment;
    }
}
