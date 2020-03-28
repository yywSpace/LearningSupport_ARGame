package com.example.learningsupport_argame.FeedbackModel;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class BarChartFragment extends Fragment {
    private static String TAG = BarChartFragment.class.getSimpleName();
    private BarChart mBarChart;
    private MonitorInfoLab mMonitorInfoLab;
    private List<MonitorInfo> mMonitorInfosDay;
    private List<MonitorInfo> mMonitorInfosWeek;
    private List<MonitorInfo> mMonitorInfosMonth;
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
        View view = inflater.inflate(R.layout.feedback_bar_chart, container, false);
        mTimeSelector = view.findViewById(R.id.feedback_bar_chart_time_selector);
        // 设置时间选择器
        Calendar calendar = Calendar.getInstance();
        time = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        mTimeSelector.setText(new SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime()));
        mTimeSelector.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(),
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        time = String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);

                        mTimeSelector.setText(year + "年" + (monthOfYear + 1)
                                + "月" + dayOfMonth + "日");
                        mMonitorInfosDay = mMonitorInfoLab.getMonitorInfoListDay(time);
                        mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(time);
                        mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(time);
                        setData();
                    },
                    Integer.parseInt(time.split("-")[0]),
                    Integer.parseInt(time.split("-")[1]) - 1,
                    Integer.parseInt(time.split("-")[2]))
                    .show();
        });
        mBarChart = view.findViewById(R.id.bar_chart);
        mBarChart.getDescription().setEnabled(false);

        // 获取数据
        Calendar c = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        new Thread(()->{
            MonitorInfoLab.getAllMonitorInfo(UserLab.getCurrentUser().getId());
            mMonitorInfosDay = mMonitorInfoLab.getMonitorInfoListDay(today);
            mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(today);
            mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(today);
            setData();

        }).start();
        return view;
    }

    public static BarChartFragment newInstance() {
        BarChartFragment fragment = new BarChartFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    // 总时间（task） 专注 , 次数
    void setData() {
        int groupCount = 3;
        float groupSpace = 0.1f;
        float barSpace = 0.05f; // x4 DataSet
        float barWidth = 0.25f; // x4 DataSet
        //关键： (0.25 + 0.05) * 3 + 0.1 = 1.00 -> interval per "group" 一定要等于1,乘以2是表示每组有两个数据

        ArrayList<BarEntry> valuesTotalTime = new ArrayList<>();
        ArrayList<BarEntry> valuesAttentionTime = new ArrayList<>();
        ArrayList<BarEntry> valuesDelayTime = new ArrayList<>();

        getBarEntry(valuesTotalTime, valuesAttentionTime, valuesDelayTime);

        BarDataSet setTotalTime, setAttentionTime, setUseCount;

        if (mBarChart.getData() != null && mBarChart.getData().getDataSetCount() > 0) {
            setTotalTime = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            setAttentionTime = (BarDataSet) mBarChart.getData().getDataSetByIndex(1);
            setUseCount = (BarDataSet) mBarChart.getData().getDataSetByIndex(2);
            setTotalTime.setValues(valuesTotalTime);
            setAttentionTime.setValues(valuesAttentionTime);
            setUseCount.setValues(valuesDelayTime);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();

        } else {
            // create 4 DataSets
            setTotalTime = new BarDataSet(valuesTotalTime, "任务持续时间");
            setTotalTime.setColor(Color.rgb(104, 241, 175));
            setAttentionTime = new BarDataSet(valuesAttentionTime, "专注时间");
            setAttentionTime.setColor(Color.rgb(164, 228, 251));
            setUseCount = new BarDataSet(valuesDelayTime, "推迟时间");
            setUseCount.setColor(Color.rgb(242, 247, 158));

            BarData data = new BarData(setTotalTime, setAttentionTime, setUseCount);
            data.setBarWidth(barWidth);
            data.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return (int) value + "分钟";
                }
            });
            mBarChart.setData(data);
        }

        barChartSetting(groupCount, groupSpace, barSpace);
        mBarChart.invalidate();
    }

    void getBarEntry(ArrayList<BarEntry> valuesTotalTime, ArrayList<BarEntry> valuesAttentionTime, ArrayList<BarEntry> valuesDelayTime) {
        float taskTotalTime, attentionTime, taskDelayTime;
        // mMonitorInfosDay mMonitorInfosWeek mMonitorInfosMonth
        taskTotalTime = 0;
        attentionTime = 0;
        taskDelayTime = 0;
        for (MonitorInfo mi : mMonitorInfosDay) {
            taskTotalTime += mi.getTaskTotalTime();
            attentionTime += mi.getMonitorAttentionTime();
            taskDelayTime += mi.getTaskDelayTime();
        }
        valuesTotalTime.add(new BarEntry(0, taskTotalTime / 60));
        valuesAttentionTime.add(new BarEntry(1, attentionTime / 60));
        valuesDelayTime.add(new BarEntry(2, taskDelayTime));
        taskTotalTime = 0;
        attentionTime = 0;
        taskDelayTime = 0;
        for (MonitorInfo mi : mMonitorInfosWeek) {
            taskTotalTime += mi.getTaskTotalTime();
            attentionTime += mi.getMonitorAttentionTime();
            taskDelayTime += mi.getTaskDelayTime();
        }
        valuesTotalTime.add(new BarEntry(0, taskTotalTime / 60));
        valuesAttentionTime.add(new BarEntry(1, attentionTime / 60));
        valuesDelayTime.add(new BarEntry(2, taskDelayTime));
        taskTotalTime = 0;
        attentionTime = 0;
        taskDelayTime = 0;
        for (MonitorInfo mi : mMonitorInfosMonth) {
            taskTotalTime += mi.getTaskTotalTime();
            attentionTime += mi.getMonitorAttentionTime();
            taskDelayTime += mi.getTaskDelayTime();
        }
        valuesTotalTime.add(new BarEntry(0, taskTotalTime / 60));
        valuesAttentionTime.add(new BarEntry(1, attentionTime / 60));
        valuesDelayTime.add(new BarEntry(2, taskDelayTime));


    }

    void barChartSetting(int groupCount, float groupSpace, float barSpace) {

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(groupCount);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            String[] xLabel = "Day Week Month".split(" ");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value == .5f)
                    return xLabel[0];
                if (value == 1.5f)
                    return xLabel[1];
                if (value == 2.5f)
                    return xLabel[2];
                return " ";

            }
        });
        xAxis.setDrawGridLines(false);
        xAxis.addLimitLine(new LimitLine(0));
        xAxis.addLimitLine(new LimitLine(1));
        xAxis.addLimitLine(new LimitLine(2));
        xAxis.addLimitLine(new LimitLine(3));
        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setDrawGridLines(false);//不设置Y轴网格
        yAxis.setAxisMinimum(0);
        mBarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴

        mBarChart.groupBars(0, groupSpace, barSpace);
    }
}
