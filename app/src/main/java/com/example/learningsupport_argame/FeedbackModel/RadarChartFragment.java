package com.example.learningsupport_argame.FeedbackModel;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class RadarChartFragment extends Fragment {
    private MonitorInfoLab mMonitorInfoLab;
    private List<MonitorInfo> mMonitorInfosDay;
    private List<MonitorInfo> mMonitorInfosWeek;
    private List<MonitorInfo> mMonitorInfosMonth;
    private RadarChart mRadarChart;
    private TextView mTimeSelector;
    String time;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMonitorInfoLab = MonitorInfoLab.get();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_radar_chart, container, false);

        mTimeSelector = view.findViewById(R.id.feedback_radar_chart_time_selector);
        // 设置时间选择器
        Calendar calendar = Calendar.getInstance();
        time = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        mTimeSelector.setText(new SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime()));
        mTimeSelector.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(),
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        time = String.format("%d/%02d/%02d", year, (monthOfYear + 1), dayOfMonth);

                        mTimeSelector.setText(year + "年" + (monthOfYear + 1)
                                + "月" + dayOfMonth + "日");
                        mMonitorInfosDay = mMonitorInfoLab.getMonitorInfoListDay(time);
                        mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(time);
                        mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(time);
                        setData();
                    },
                    Integer.parseInt(time.split("/")[0]) + 0,
                    Integer.parseInt(time.split("/")[1]) - 1,
                    Integer.parseInt(time.split("/")[2]) + 0)
                    .show();
        });
        mRadarChart = view.findViewById(R.id.radar_chart);
        Calendar c = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy/MM/dd").format(c.getTime());
        mMonitorInfosDay = mMonitorInfoLab.getMonitorInfoListDay(today);
        mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(today);
        mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(today);
        setData();
        radarSetting();
        return view;

    }

    private void setData() {
        RadarDataSet dataSetToday = getRadarEntries(mMonitorInfosDay, "Day", Color.BLUE);
        RadarDataSet dataSetWeek = getRadarEntries(mMonitorInfosWeek, "Week", Color.RED);
        RadarDataSet dataSetMonth = getRadarEntries(mMonitorInfosMonth, "Month", Color.GREEN);
        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(dataSetToday);
        sets.add(dataSetWeek);
        sets.add(dataSetMonth);
        RadarData radarData = new RadarData(sets);
        mRadarChart.setData(radarData);
        mRadarChart.invalidate();
    }

    private void radarSetting() {
        mRadarChart.setRotationEnabled(false);
        Description description = mRadarChart.getDescription();
        description.setEnabled(false);
        MarkerView mv = new RadarMarkerView(getContext(), R.layout.feedback_radar_markerview);
        mv.setChartView(mRadarChart); // For bounds control
        mRadarChart.setMarker(mv); // Set the marker to the chart

        Legend legend = mRadarChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        XAxis xAxis = mRadarChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private final String[] mActivities = new String[]{"使用时间", "息屏时间", "专注时间", "失神时间", "使用次数"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });

        YAxis yAxis = mRadarChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(1f);
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
            attentionTime += mi.getMonitorAttentionTime();
            useCount += mi.getMonitorPhoneUseCount();
            totalTime += mi.getTaskTotalTime();
            unUsePhoneTime += mi.getMonitorTaskScreenOffTime();
            inAttentionTime += mi.getMonitorScreenOnInattentionSpan();
        }
        // 使用时间 专注时间 使用次数
        radarEntries.add(new RadarEntry(usePhoneTime / totalTime));
        radarEntries.add(new RadarEntry(unUsePhoneTime / totalTime));
        radarEntries.add(new RadarEntry(attentionTime / totalTime));
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
