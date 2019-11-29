package com.example.learningsupport_argame.MonitorModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    /**
     * 秒-> 时分秒
     *
     * @param second
     * @return 00：00：00
     */
    public static String second2Time(long second) {
        long sec = second % 60;
        long minute = (second / 60) % 60;
        long hour = second / 60 / 60;
        return String.format("%02d:%02d:%02d", hour, minute, sec);
    }

    /**
     * 计算两个时间间隔秒数
     *
     * @param begin
     * @param end
     * @param pattern yyyy-MM-dd HH:mm
     * @return 间隔秒数
     */
    public static long remainingTime(String begin, String end, String pattern) {

        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date d1 = null, d2 = null;
        try {
            d1 = df.parse(begin);
            d2 = df.parse(end);
        } catch (Exception e) {
        }
        long diff = d2.getTime() - d1.getTime();
        long seconds = diff / 1000;
        return seconds;
    }

}
