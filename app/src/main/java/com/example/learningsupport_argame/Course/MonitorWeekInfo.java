package com.example.learningsupport_argame.Course;

import org.litepal.crud.LitePalSupport;

public class MonitorWeekInfo extends LitePalSupport {

    private long useTime;
    private long concentrateTime;
    private long useCount;
    private String monitorDate;

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public long getConcentrateTime() {
        return concentrateTime;
    }

    public void setConcentrateTime(long concentrateTime) {
        this.concentrateTime = concentrateTime;
    }

    public long getUseCount() {
        return useCount;
    }

    public void setUseCount(long useCount) {
        this.useCount = useCount;
    }

    public String getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(String monitorDate) {
        this.monitorDate = monitorDate;
    }
}
