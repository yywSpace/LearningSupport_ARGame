package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;

public class UserDynamicFragment extends Fragment {
    private String mCurrentUserId;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCurrentUserId = getArguments().getString(User.CURRENT_USER_ID);

        View view = inflater.inflate(R.layout.user_management_friend_fragment_user_dynamic, container, false);
        mRecyclerView = view.findViewById(R.id.user_management_dynamic_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        return view;
    }

    public static UserDynamicFragment getInstance(String userId) {
        UserDynamicFragment fragment = new UserDynamicFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

}
