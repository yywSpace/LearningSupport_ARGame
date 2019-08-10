package com.example.learningsupport_argame.FeedbackModel;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningsupport_argame.MonitorModel.MonitorInfo;
import com.example.learningsupport_argame.MonitorModel.MonitorInfoLab;
import com.example.learningsupport_argame.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * todo 通过获取到的一周，或一月的数据适配折线图
 */
public class LineChartFragment extends Fragment {
    private static String TAG = LineChartFragment.class.getSimpleName();
    LineChart mLineChart;
    List<Entry> entries;
    LineDataSet dataSet;
    LineData lineData;
    private MonitorInfoLab mMonitorInfoLab;
    private List<MonitorInfo> mMonitorInfosWeek;
    private List<MonitorInfo> mMonitorInfosMonth;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMonitorInfoLab = MonitorInfoLab.get();
        Calendar c  = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy/MM/dd").format(c.getTime());

        mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(today);
        mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(today);


        entries = new ArrayList<>();
        for (int i = 0; i < 24; i++) {

            float val = (float) (Math.random() * 10);
            entries.add(new Entry(i, val));
        }

        dataSet = new LineDataSet(entries, "Label");
        dataSet.setLineWidth(1f);

        lineData = new LineData(dataSet);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_line_chart, container, false);
        mLineChart = view.findViewById(R.id.line_chart);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
        weekSetting();
        return view;
    }

    public static LineChartFragment newInstance() {
        LineChartFragment fragment = new LineChartFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    void weekSetting() {
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(7);
    }

    void monthSetting() {
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(30);
    }


}
