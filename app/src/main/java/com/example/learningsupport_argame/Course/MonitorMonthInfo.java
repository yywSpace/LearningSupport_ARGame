package com.example.learningsupport_argame.Course;

import org.litepal.crud.LitePalSupport;

public class MonitorMonthInfo extends LitePalSupport {

    private long useTime;
    private long concentrateTime;
    private long useCount;


    public long getUseCount() {
        return useCount;
    }

    public void setUseCount(long useCount) {
        this.useCount = useCount;
    }


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

}
