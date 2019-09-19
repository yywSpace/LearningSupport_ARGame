package com.example.learningsupport_argame.fragmentPak;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learningsupport_argame.R;

public class TestFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.test_layout, container, false);
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle=getArguments();
        TextView textView=(TextView) getView().findViewById(R.id.test);
        textView.setText(bundle.get("title").toString());
    }
}
