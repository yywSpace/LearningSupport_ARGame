package com.example.learningsupport_argame.Course;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

public class JsonBeanWeek implements IPickerViewData{

    private String startzhou;
    private List<String> endzhou;

    public String getStartzhou() {
        return startzhou;
    }

    public void setStartzhou(String startzhou) {
        this.startzhou = startzhou;
    }

    public List<String> getEndzhou() {
        return endzhou;
    }

    public void setEndzhou(List<String> endzhou) {
        this.endzhou = endzhou;
    }

    @Override
    public String getPickerViewText() {
        return this.startzhou;
    }
}

