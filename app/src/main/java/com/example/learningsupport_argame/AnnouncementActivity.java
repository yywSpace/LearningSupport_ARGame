package com.example.learningsupport_argame;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AnnouncementActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement_activity_layout);
        ImageButton exitButton = findViewById(R.id.announcement_exit_button);
        TextSwitcher switcher1 = findViewById(R.id.announcement_title_1);
        TextSwitcher switcher2 = findViewById(R.id.announcement_title_2);
        switcher1.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.announcement_in));
        switcher1.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.announcement_out));
        switcher2.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.announcement_in));
        switcher2.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.announcement_out));
        ViewSwitcher.ViewFactory factory = () -> {
            TextView t = new TextView(this);
            t.setTextColor(Color.parseColor("#333333"));
            t.setMaxLines(1);
            float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, this.getResources().getDisplayMetrics());
            t.setTextSize(textSize);
            return t;
        };
        switcher1.setFactory(factory);
        switcher2.setFactory(factory);

        new Thread(() -> {
            if (TaskLab.sAllPeopleTaskList == null) {
                TaskLab.getAllPeopleTask();
            }
        }).start();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (TaskLab.sAllPeopleTaskList == null) {
                    switcher1.setCurrentText("加载中...");
                    switcher2.setCurrentText("加载中...");
                    return;
                }
                List<Task> taskList = new ArrayList<>(TaskLab.sAllPeopleTaskList);
                Random random = new Random(System.currentTimeMillis());
                int ran1 = random.nextInt(taskList.size());
                int ran2 = random.nextInt(taskList.size());
                runOnUiThread(() -> {
                    switcher1.setText(taskList.get(ran1).getTaskName());
                    switcher2.setText(taskList.get(ran2).getTaskName());
                });
            }
        }, 0, 3000);
        exitButton.setOnClickListener((v1) -> {
            finish();
            timer.cancel();
        });
    }
}
