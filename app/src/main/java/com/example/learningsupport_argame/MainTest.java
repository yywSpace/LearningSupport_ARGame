package com.example.learningsupport_argame;

import java.util.Timer;
import java.util.TimerTask;


public class MainTest {
    static int count = 0;

    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println(count++);
            }
        };
        timer.schedule(task, 0, 1000);
    }

}
