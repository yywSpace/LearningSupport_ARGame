package com.example.learningsupport_argame.tempararyfile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.bean.PairInfoBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskCreateFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.create_task_layout, container, false);
        return new TaskCreateFragment.ViewAdapter(view).getView();
    }
    class ViewAdapter
    {
        private View view;
        private Spinner choseTaskType;
        private ImageButton choseTaskStartdate;
        private ImageButton choseTaskStarttime;
        private ImageButton choseTaskendate;
        private ImageButton choseTaskentime;
        private ImageButton choseLocation;
        private RelativeLayout layout;
        private TextClock textClockStart;
        private TextClock textClockEnd;
        private Context con;
        public View getView() {
            return view;
        }

        public ViewAdapter(View view) {

            con=getContext();
            choseTaskType=(Spinner) view.findViewById(R.id.task_type);
            choseTaskStartdate=(ImageButton) view.findViewById(R.id.start_reqi);
            choseTaskStarttime=(ImageButton)view .findViewById(R.id.start_shijian);
            choseTaskendate=(ImageButton) view .findViewById(R.id.reqi_end);
            choseTaskentime=(ImageButton)view.findViewById(R.id.shijian_end);
            choseLocation =(ImageButton) view .findViewById(R.id .btn_ditu_create);
            layout=(RelativeLayout)view.findViewById(R.id.layout_task_type);
            textClockStart=(TextClock)view.findViewById(R.id.date_show_create);
            textClockEnd=(TextClock)view.findViewById(R.id.date_window_create_end);
            choseLocation.getBackground().setAlpha(0);
            choseTaskStarttime.getBackground().setAlpha(0);
            choseTaskStartdate.getBackground().setAlpha(0);
            choseTaskentime.getBackground().setAlpha(0);
            choseTaskendate.getBackground().setAlpha(0);
            choseTaskType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String[] taskType = getResources().getStringArray(R.array.taskType);

                    switch (i){
                        case 0://对自己发布
                            layout.removeAllViews();
                            break;
                        case 1:
                            layout.removeAllViews();
                            MultiSelectionSpinner chosefriends=new MultiSelectionSpinner(con);
                            List<PairInfoBean> peopleList=new ArrayList<>();
                            List<String> peopleListString =new ArrayList<>();
                            char a = 'A';
                            for (int j = 0; j < 10; j++) {
                                PairInfoBean pairInfoBean = new PairInfoBean();
                                pairInfoBean.pairName = a + "/" + j;
                                peopleListString.add(pairInfoBean.pairName);
                                peopleList.add(pairInfoBean);
                                a++;
                            }
                            ArrayAdapter<PairInfoBean> adapter= new ArrayAdapter<PairInfoBean>(con,android.R.layout.simple_spinner_dropdown_item,peopleList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //chosefriends.setAdapter(adapter);
                            chosefriends.setItems(peopleListString);
                            layout.addView(chosefriends);
                            chosefriends.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                @Override public void selectedIndices(List<Integer> indices) {
                                    Toast.makeText(con,"22222",Toast.LENGTH_SHORT).show();
                                }
                                @Override public void selectedStrings(List<String> strings) {

                                } });
                            break;
                        case 2:
                            layout.removeAllViews();
                            MultiSelectionSpinner choseshetuan=new MultiSelectionSpinner(con);

                            List<String> shetuanListString =new ArrayList<>();
                            char b = 'A';
                            for (int j = 0; j < 10; j++) {


                                shetuanListString.add(b + "/" + j);

                                b++;
                            }
                            choseshetuan.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                @Override public void selectedIndices(List<Integer> indices) {
                                    Toast.makeText(con,"22222",Toast.LENGTH_SHORT).show();
                                }
                                @Override public void selectedStrings(List<String> strings) {

                                } });
                            choseshetuan.setItems(shetuanListString);
                            layout.addView(choseshetuan);

                            break;
                        case 3:
                            layout.removeAllViews();
                            Button button =new Button(con);
                            button .setText("放置在当前位置");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(con,"22",Toast.LENGTH_SHORT);
                                }
                            });
                            layout.addView(button);

                            break;
                    }
                }


                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            choseLocation.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        choseLocation.getBackground().setAlpha(70);
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        choseLocation.getBackground().setAlpha(0);
                        //这里处理事件。
                        Toast.makeText(con, "这里处理事件", Toast.LENGTH_SHORT).show();

                    }
                    // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            choseTaskStartdate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        choseTaskStartdate.getBackground().setAlpha(70);
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        choseTaskStartdate.getBackground().setAlpha(0);
                        //这里处理事件。
                        Toast.makeText(con, "这里处理事件", Toast.LENGTH_SHORT).show();
                        DatePickerDialog.OnDateSetListener startdateListener=new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                textClockStart.setFormat24Hour(year+"-"+monthOfYear+"-"+dayOfMonth);
                                Toast.makeText(con,"XXX",Toast.LENGTH_SHORT).show();
                            }
                        };
                        DatePickerDialog date=new DatePickerDialog(con,  startdateListener, Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH);
                        date.setTitle("日期对话框");
                        date.show();


                    }
                    // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            choseTaskStarttime.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        choseTaskStarttime.getBackground().setAlpha(70);
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        choseTaskStarttime.getBackground().setAlpha(0);
                        //这里处理事件。
                        Toast.makeText(con, "这里处理事件", Toast.LENGTH_SHORT).show();
                        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Toast.makeText(con, "XXX", Toast.LENGTH_SHORT).show();
                            }
                        };
                        TimePickerDialog d = new TimePickerDialog(con, timeListener, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                        d.setTitle("时间对话框");
                        d.show();
                    }
                    // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            choseTaskendate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        choseTaskendate.getBackground().setAlpha(70);
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        choseTaskendate.getBackground().setAlpha(0);
                        //这里处理事件。
                        choseTaskStartdate.getBackground().setAlpha(0);
                        //这里处理事件。

                        DatePickerDialog.OnDateSetListener startdateListener1=new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                textClockStart.setFormat24Hour(year+"-"+monthOfYear+"-"+dayOfMonth);
                                Toast.makeText(con,"XXX",Toast.LENGTH_SHORT).show();
                            }
                        };
                        DatePickerDialog date=new DatePickerDialog(con,  startdateListener1, Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH);
                        date.setTitle("日期对话框");
                        date.show();

                    }
                    // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            choseTaskentime.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        choseTaskentime.getBackground().setAlpha(70);
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        choseTaskentime.getBackground().setAlpha(0);
                        //这里处理事件。
                        Toast.makeText(con, "这里处理事件", Toast.LENGTH_SHORT).show();
                        TimePickerDialog.OnTimeSetListener timeListener1 = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Toast.makeText(con, "XXX", Toast.LENGTH_SHORT).show();
                            }
                        };
                        TimePickerDialog d1 = new TimePickerDialog(con, timeListener1, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                        d1.setTitle("时间对话框");
                        d1.show();

                    }
                    // Toast.makeText(con,"这里处理事  件",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


            this.view=view;
        }
    }
}
