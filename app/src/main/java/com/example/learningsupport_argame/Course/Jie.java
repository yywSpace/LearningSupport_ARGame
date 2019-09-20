package com.example.learningsupport_argame.Course;

import org.litepal.crud.LitePalSupport;

public class Jie extends LitePalSupport {

        private  int id;
        private String zhou;
        private int start_jieshu;
        private int end_jieshu;
        private Course course;

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }


        public Course getCourse() {
                return course;
        }

        public void setCourse(Course course) {
                this.course = course;
        }


        public String getZhou() {
                return zhou;
        }

        public void setZhou(String zhou) {
                this.zhou = zhou;
        }

        public int getStart_jieshu() {
                return start_jieshu;
        }

        public void setStart_jieshu(int start_jieshu) {
                this.start_jieshu = start_jieshu;
        }

        public int getEnd_jieshu() {
                return end_jieshu;
        }

        public void setEnd_jieshu(int end_jieshu) {
                this.end_jieshu = end_jieshu;
        }

}
