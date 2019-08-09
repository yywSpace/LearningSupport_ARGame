package com.example.learningsupport_argame.FeedbackModel;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;

public class RadarChartFragment extends Fragment {
    private MonitorInfoLab mMonitorInfoLab;
    List<MonitorInfo> mMonitorInfosToday;
    List<MonitorInfo> mMonitorInfosWeek;
    List<MonitorInfo> mMonitorInfosMonth;
    RadarData mRadarData;
    RadarChart radarChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMonitorInfoLab = MonitorInfoLab.get();
        mMonitorInfosToday = mMonitorInfoLab.getMonitorInfoListToday();
        mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek();
        mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth();
        RadarDataSet dataSetToday = getRadarEntries(mMonitorInfosToday, "Today", Color.BLUE);
        RadarDataSet dataSetWeek = getRadarEntries(mMonitorInfosWeek, "This Week", Color.DKGRAY);
        RadarDataSet dataSetMonth = getRadarEntries(mMonitorInfosMonth, "This Month", Color.MAGENTA);
        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(dataSetToday);
        sets.add(dataSetWeek);
        sets.add(dataSetMonth);
        mRadarData = new RadarData(sets);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_radar_chart, container, false);
        radarChart = view.findViewById(R.id.radar_chart);
        radarChart.setRotationEnabled(false);
        radarChart.setData(mRadarData);
        Description description = radarChart.getDescription();
        description.setEnabled(false);
        MarkerView mv = new RadarMarkerView(getContext(), R.layout.feedback_radar_markerview);
        mv.setChartView(radarChart); // For bounds control
        radarChart.setMarker(mv); // Set the marker to the chart

        Legend legend = radarChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private final String[] mActivities = new String[]{"使用时间", "未使用时间", "专注时间", "失神时间", "使用次数"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(1f);

        radarChart.invalidate();
        return view;

    }

    public static RadarChartFragment newInstance() {
        RadarChartFragment fragment = new RadarChartFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private RadarDataSet getRadarEntries(List<MonitorInfo> monitorInfos, String label, int fillColor) {
        List<RadarEntry> radarEntries = new ArrayList<>();
        float usePhoneTime = 0, unUsePhoneTime = 0, attentionTime = 0, inAttentionTime = 0, totalTime = 0, useCount = 0;

        for (MonitorInfo mi : monitorInfos) {
            usePhoneTime += mi.getMonitorTaskScreenOnTime();
            attentionTime += mi.getMonitorScreenOnAttentionSpan();
            useCount += mi.getMonitorPhoneUseCount();
            totalTime += mi.getTaskTotalTime();
            unUsePhoneTime += mi.getMonitorTaskScreenOffTime();
            inAttentionTime += mi.getMonitorScreenOnInattentionSpan();
        }
        // 使用时间 专注时间 使用次数
        radarEntries.add(new RadarEntry(usePhoneTime / totalTime));
        radarEntries.add(new RadarEntry(unUsePhoneTime / totalTime));
        radarEntries.add(new RadarEntry(attentionTime / usePhoneTime));
        radarEntries.add(new RadarEntry(inAttentionTime / usePhoneTime));
        radarEntries.add(new RadarEntry(useCount / 100));


        RadarDataSet dataSet = new RadarDataSet(radarEntries, label);
        dataSet.setColor(fillColor);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(fillColor);
        dataSet.setDrawHighlightCircleEnabled(true);
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setValueTextSize(0);
        return dataSet;
    }
}
