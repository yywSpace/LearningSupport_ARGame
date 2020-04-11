package com.example.learningsupport_argame.FeedbackModel;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learningsupport_argame.MonitorModel.MonitorInfo;
import com.example.learningsupport_argame.MonitorModel.MonitorInfoLab;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * todo 适配一天内的数据
 */
public class LineChartFragment extends Fragment {
    private static String TAG = LineChartFragment.class.getSimpleName();
    private LineChart mLineChart;
    private MonitorInfoLab mMonitorInfoLab;
    private List<MonitorInfo> mMonitorInfosDay;
    private List<MonitorInfo> mMonitorInfosWeek;
    private List<MonitorInfo> mMonitorInfosMonth;

    private RadioButton mDayButton;
    private RadioButton mWeekButton;
    private RadioButton mMonthButton;
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
        Calendar c = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());

        View view = inflater.inflate(R.layout.feedback_line_chart, container, false);

        mLineChart = view.findViewById(R.id.line_chart);

        Description description = mLineChart.getDescription();
        description.setEnabled(false);
        daySetting();

        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            MonitorInfoLab.getAllMonitorInfo(UserLab.getCurrentUser().getId());
            mMonitorInfosDay = mMonitorInfoLab.getMonitorInfoListDay(today);
            mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(today);
            mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(today);
            setDayData(mMonitorInfosDay);
        }).start();
        // 设置时间选择器
        Calendar calendar = Calendar.getInstance();
        time = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        mTimeSelector = view.findViewById(R.id.feedback_line_chart_time_selector);
        mTimeSelector.setText(new SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime()));
        mTimeSelector.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(),
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        mTimeSelector.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                        time = String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                        if (mDayButton.isChecked()) {
                            mLineChart.animateX(1000);
                            mMonitorInfosDay = mMonitorInfoLab.getMonitorInfoListDay(time);
                            setDayData(mMonitorInfosDay);
                            daySetting();
                        } else if (mWeekButton.isChecked()) {
                            mLineChart.animateX(500);
                            mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(time);
                            setData(mMonitorInfosWeek, 7);
                            weekSetting();
                        } else if (mMonthButton.isChecked()) {
                            mLineChart.animateX(1000);
                            mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(time);
                            setData(mMonitorInfosMonth, 31);
                            monthSetting();
                        }
                    },
                    Integer.parseInt(time.split("-")[0]),//year
                    Integer.parseInt(time.split("-")[1]) - 1,//month
                    Integer.parseInt(time.split("-")[2]))//day
                    .show();
        });

        mDayButton = view.findViewById(R.id.feedback_line_chart_day);
        mDayButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setDayData(mMonitorInfosDay);
                daySetting();
            }
        });
        mWeekButton = view.findViewById(R.id.feedback_line_chart_week);
        mWeekButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setData(mMonitorInfosWeek, 7);
                weekSetting();
            }
        });
        mMonthButton = view.findViewById(R.id.feedback_line_chart_month);
        mMonthButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                monthSetting();
                setData(mMonitorInfosMonth, 31);
            }
        });
        return view;
    }


    private int getWeekNumber(String dataStr) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dataStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);

    }

    void setDayData(List<MonitorInfo> monitorInfos) {
        List<Entry> entries1 = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Entry> entries3 = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            entries1.add(new Entry(i, 0));
            entries2.add(new Entry(i, 0));
            entries3.add(new Entry(i, 0));
        }
        for (int j = 0; j < monitorInfos.size(); j++) {
            MonitorInfo monitorInfo = monitorInfos.get(j);
            Log.d(TAG, "setDayData: " + monitorInfo.getId());
            String beginTime = monitorInfo.getTaskBeginTime().substring(11);
            String endTime = monitorInfo.getTaskEndTime().substring(11);
            int hourBegin = Integer.parseInt(beginTime.split(":")[0]);
            int hourEnd = Integer.parseInt(endTime.split(":")[0]);
            for (int i = hourBegin; i <= hourEnd; i++) {
                entries1.set(i, new Entry(i, (float) (monitorInfo.getTaskDelayTime() / 60.0)));
                entries2.set(i, new Entry(i, (float) (monitorInfo.getTaskTotalTime() / 60.0)));
                entries3.set(i, new Entry(i, monitorInfo.getMonitorAttentionTime() / 60));
            }
        }
        LineDataSet dataSet1 = new LineDataSet(entries1, "任务推迟时间");
        dataSet1.setColor(Color.RED);

        LineDataSet dataSet2 = new LineDataSet(entries2, "任务持续时间");
        dataSet2.setColor(Color.BLUE);
        LineDataSet dataSet3 = new LineDataSet(entries3, "专注时间");
        dataSet3.setColor(Color.BLACK);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(dataSet1);
        sets.add(dataSet2);
        sets.add(dataSet3);
        LineData lineData = new LineData(sets);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    /**
     * 通过cnt来区分week(cnt = 7),month(cnt = 31)
     *
     * @param monitorInfos
     * @param cnt
     */
    private void setData(List<MonitorInfo> monitorInfos, int cnt) {
        long[] hours = new long[25];
        List<Entry> entries1 = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Entry> entries3 = new ArrayList<>();
        for (int i = 1; i <= cnt; i++) {
            float totalTaskTime = 0, attentionTime = 0, taskDelayTime = 0;
            for (int j = 0; j < monitorInfos.size(); j++) {
                MonitorInfo monitorInfo = monitorInfos.get(j);
                if (cnt == 24) {
//                    String beginTime = monitorInfo.getTaskBeginTime().substring(11);
//                    int hourBegin = Integer.parseInt(beginTime.split(":")[0]);
//                    int minuteBegin = Integer.parseInt(beginTime.split(":")[1]);
//                    String endTime = monitorInfo.getTaskEndTime().substring(11);
//                    int hourEnd = Integer.parseInt(endTime.split(":")[0]);
//                    int minuteEnd = Integer.parseInt(endTime.split(":")[1]);
//
//                    if (hourEnd > hourBegin) { // 如果不在同一小时
//                        // 第一小时时间等于60-其开始分钟数
//                        hours[hourBegin] = (60 - minuteBegin) * 60;
//                        for (int t = hourBegin + 1; t <= hourEnd; t++) {
//                            hours[t] = 3600;
//                        }
//                        hours[hourEnd] = minuteEnd * 60;
//                    }
//
//                    Log.d(TAG, "beginTime-endTime: " + beginTime + "-" + endTime);
                } else if (cnt == 7) { //星期的数据
                    if (getWeekNumber(monitorInfo.getTaskBeginTime()) == i) {
                        totalTaskTime += monitorInfo.getTaskTotalTime();
                        attentionTime += monitorInfo.getMonitorAttentionTime();
                        taskDelayTime += monitorInfo.getTaskDelayTime();
                    }
                } else if (cnt >= 28) {// 月的数据
                    if (Integer.parseInt(monitorInfo.getTaskBeginTime().substring(8, 10).trim()) == i) {
                        totalTaskTime += monitorInfo.getTaskTotalTime();
                        attentionTime += monitorInfo.getMonitorAttentionTime();
                        taskDelayTime += monitorInfo.getTaskDelayTime();
                    }
                }
            }
            entries1.add(new Entry(i - 1, taskDelayTime / 60));
            entries2.add(new Entry(i - 1, totalTaskTime / 60));
            entries3.add(new Entry(i - 1, attentionTime / 60));

        }

        LineDataSet dataSet1 = new LineDataSet(entries1, "手机使用次数");
        dataSet1.setColor(Color.RED);

        LineDataSet dataSet2 = new LineDataSet(entries2, "任务持续时间");
        dataSet2.setColor(Color.BLUE);
        LineDataSet dataSet3 = new LineDataSet(entries3, "专注时间");
        dataSet3.setColor(Color.BLACK);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(dataSet1);
        sets.add(dataSet2);
        sets.add(dataSet3);
        LineData lineData = new LineData(sets);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    public static LineChartFragment newInstance() {
        LineChartFragment fragment = new LineChartFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void daySetting() {
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setAxisMaximum(24);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter((value, axis) -> (int) value + "");
        mLineChart.getAxisRight().setEnabled(false);
        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
    }

    void weekSetting() {
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(6);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            String[] weeks = "日 一 二 三 四 五 六".split(" ");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return weeks[(int) value % weeks.length];
            }
        });

        mLineChart.getAxisRight().setEnabled(false);
        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
    }

    void monthSetting() {
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(30);
        xAxis.setValueFormatter((value, axis) -> (int) (value + 1) + "");

        mLineChart.getAxisRight().setEnabled(false);
        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
    }
}
