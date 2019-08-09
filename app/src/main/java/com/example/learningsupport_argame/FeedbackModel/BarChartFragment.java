package com.example.learningsupport_argame.FeedbackModel;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningsupport_argame.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class BarChartFragment extends Fragment {
    private static String TAG = BarChartFragment.class.getSimpleName();
    List<BarEntry> mBarEntries;
    BarDataSet mBarDataSet;
    BarData mBarData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        float start = 1f;
        float count = 5;
        float range = 10;

        mBarEntries = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));
            mBarEntries.add(new BarEntry(i, val));
        }

        mBarDataSet = new BarDataSet(mBarEntries, "Label");
        mBarData = new BarData(mBarDataSet);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_bar_chart, container, false);
        BarChart barChart  = view.findViewById(R.id.bar_chart);
        barChart.setData(mBarData);
        barChart.invalidate();
        return view;
    }

    public static BarChartFragment newInstance() {
        BarChartFragment fragment = new BarChartFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

}
