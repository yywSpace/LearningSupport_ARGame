package com.example.learningsupport_argame.FeedbackModel;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.learningsupport_argame.MonitorModel.MonitorInfo;
import com.example.learningsupport_argame.MonitorModel.MonitorInfoLab;
import com.example.learningsupport_argame.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * todo 通过获取到的一周，或一月的数据适配折线图
 */
public class LineChartFragment extends Fragment {
    private static String TAG = LineChartFragment.class.getSimpleName();
    private LineChart mLineChart;
    private MonitorInfoLab mMonitorInfoLab;
    private List<MonitorInfo> mMonitorInfosWeek;
    private List<MonitorInfo> mMonitorInfosMonth;

    private RadioButton mWeekButton;
    private RadioButton mMonthButton;
    private TextView mPTextView;
    private TextView mNTextView;
    Calendar weekCalendar;
    Calendar monthCalendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMonitorInfoLab = MonitorInfoLab.get();
        weekCalendar = Calendar.getInstance();
        monthCalendar = Calendar.getInstance();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy/MM/dd").format(c.getTime());
        mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(today);
        mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(today);

        View view = inflater.inflate(R.layout.feedback_line_chart, container, false);

        mLineChart = view.findViewById(R.id.line_chart);

        Description description =  mLineChart.getDescription();
        description.setTextSize(15);
        description.setText(today);
        setData(mMonitorInfosWeek, 7);
        weekSetting();

        mPTextView = view.findViewById(R.id.feedback_line_chart_p);
        mPTextView.setOnClickListener(v -> {
            if (mWeekButton.isChecked()) {
                weekCalendar.add(Calendar.DAY_OF_MONTH, -7);
                String time = new SimpleDateFormat("yyyy/MM/dd").format(weekCalendar.getTime());
                Log.d(TAG, time);
                description.setText(time);
                mLineChart.animateX(500);
                mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(time);
                setData(mMonitorInfosWeek, 7);
                weekSetting();
            } else if (mMonthButton.isChecked()) {
                monthCalendar.add(Calendar.MONDAY, -1);
                String time = new SimpleDateFormat("yyyy/MM/dd").format(monthCalendar.getTime());
                Log.d(TAG, time);
                description.setText(time);
                mLineChart.animateX(1000);
                mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(time);
                monthSetting();
                setData(mMonitorInfosMonth, 31);
            }

        });
        mNTextView = view.findViewById(R.id.feedback_line_chart_n);
        mNTextView.setOnClickListener(v -> {
            if (mWeekButton.isChecked()) {
                weekCalendar.add(Calendar.DAY_OF_MONTH, 7);
                String time = new SimpleDateFormat("yyyy/MM/dd").format(weekCalendar.getTime());
                Log.d(TAG, time);
                description.setText(time);
                mLineChart.animateX(500);
                mMonitorInfosWeek = mMonitorInfoLab.getMonitorInfoListWeek(time);
                setData(mMonitorInfosWeek, 7);
                weekSetting();
            } else if (mMonthButton.isChecked()) {
                monthCalendar.add(Calendar.MONDAY, 1);
                String time = new SimpleDateFormat("yyyy/MM/dd").format(monthCalendar.getTime());
                Log.d(TAG, time);
                description.setText(time);
                mLineChart.animateX(1000);
                mMonitorInfosMonth = mMonitorInfoLab.getMonitorInfoListMonth(time);
                monthSetting();
                setData(mMonitorInfosMonth, 31);
            }
        });
        mWeekButton = view.findViewById(R.id.feedback_line_chart_week);
        mWeekButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setData(mMonitorInfosWeek, 7);
                weekSetting();
                mPTextView.setText("上一星期");
                mNTextView.setText("下一星期");
            }
        });
        mMonthButton = view.findViewById(R.id.feedback_line_chart_month);
        mMonthButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                monthSetting();
                setData(mMonitorInfosWeek, 31);

                mPTextView.setText("上一月");
                mNTextView.setText("下一月");
            }
        });
        return view;
    }

    private int getWeekNumber(String dataStr) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = sdf.parse(dataStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);

    }

    /**
     * 通过cnt来区分week(cnt = 7)和month(cnt = 31),
     *
     * @param monitorInfos
     * @param cnt
     */
    private void setData(List<MonitorInfo> monitorInfos, int cnt) {

        List<Entry> entries1 = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Entry> entries3 = new ArrayList<>();
        for (int i = 1; i <= cnt; i++) {
            float usePhoneTime = 0, attentionTime = 0, useCount = 0;
            for (int j = 0; j < monitorInfos.size(); j++) {
                if (cnt <= 7) { //星期的数据
                    if (getWeekNumber(monitorInfos.get(j).getTaskBeginTime()) == i) {
                        usePhoneTime += monitorInfos.get(j).getMonitorTaskScreenOnTime();
                        attentionTime += monitorInfos.get(j).getMonitorScreenOnAttentionSpan();
                        useCount += monitorInfos.get(j).getMonitorPhoneUseCount();
                    }
                } else {         // 月的数据
                    if (Integer.parseInt(monitorInfos.get(j).getTaskBeginTime().substring(8, 10)) == i) {
                        usePhoneTime += monitorInfos.get(j).getMonitorTaskScreenOnTime();
                        attentionTime += monitorInfos.get(j).getMonitorScreenOnAttentionSpan();
                        useCount += monitorInfos.get(j).getMonitorPhoneUseCount();
                    }
                }

            }
            entries1.add(new Entry(i - 1, useCount));
            entries2.add(new Entry(i - 1, usePhoneTime / 60));
            entries3.add(new Entry(i - 1, attentionTime / 60));

        }

        LineDataSet dataSet1 = new LineDataSet(entries1, "手机使用次数");
        dataSet1.setColor(Color.RED);

        LineDataSet dataSet2 = new LineDataSet(entries2, "手机使用时间");
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
    }

    void monthSetting() {
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(30);
        xAxis.setValueFormatter((value, axis) -> (int) (value + 1) + "");
    }


}
