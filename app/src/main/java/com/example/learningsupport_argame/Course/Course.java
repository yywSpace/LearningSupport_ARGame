package com.example.learningsupport_argame.Course;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Course extends LitePalSupport {
    private int id;
    private String course_name;
    private String classroom;
    private List<Jie> jie=new ArrayList<>();
    private String teacher;
    private int start_zhoushu;
    private int end_zhoushu;
    private String weekstyle;

    public List<Jie> getJie() {
        return jie;
    }

    public void setJie(List<Jie> jie) {
        this.jie = jie;
    }
    public String getWeekstyle() {
        return weekstyle;
    }

    public void setWeekstyle(String weekstyle) {
        this.weekstyle = weekstyle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public int getStart_zhoushu() {
        return start_zhoushu;
    }

    public void setStart_zhoushu(int start_zhoushu) {
        this.start_zhoushu = start_zhoushu;
    }

    public int getEnd_zhoushu() {
        return end_zhoushu;
    }

    public void setEnd_zhoushu(int end_zhoushu) {
        this.end_zhoushu = end_zhoushu;
    }



    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }


    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }


}
