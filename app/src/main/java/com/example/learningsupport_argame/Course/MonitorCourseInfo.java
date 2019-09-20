package com.example.learningsupport_argame.Course;

import org.litepal.crud.LitePalSupport;

public class MonitorCourseInfo extends LitePalSupport {

    private int useTime;
    private int concentrateTime;
    private int useCount;
    private String monitorDate;

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    public int getConcentrateTime() {
        return concentrateTime;
    }

    public void setConcentrateTime(int concentrateTime) {
        this.concentrateTime = concentrateTime;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public String getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(String monitorDate) {
        this.monitorDate = monitorDate;
    }
}
