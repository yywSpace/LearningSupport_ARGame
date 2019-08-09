package com.example.learningsupport_argame.FeedbackModel;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningsupport_argame.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LineChartFragment extends Fragment {
    private static String TAG = LineChartFragment.class.getSimpleName();
    List<Entry> entries;
    PointF[] data;
    LineDataSet dataSet;
    LineData lineData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entries = new ArrayList<>();
        data = new PointF[]{new PointF(1, 1), new PointF(2, 2), new PointF(3, 3), new PointF(4, 4), new PointF(5, 5), new PointF(6, 6)};

        for (PointF point : data) {
            entries.add(new Entry(point.x, point.y));
        }

        dataSet = new LineDataSet(entries, "Label");
        lineData = new LineData(dataSet);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_line_chart, container, false);
        LineChart chart = view.findViewById(R.id.line_chart);
        chart.setData(lineData);
        chart.invalidate();

        return view;
    }

    public static LineChartFragment newInstance() {
        LineChartFragment fragment = new LineChartFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

}
