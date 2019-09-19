package com.example.learningsupport_argame.bean;

import java.util.List;

public class TaskInfo {
    private  String tid;
    private  String taskname;
    private  String tasktime;
    private String tasklocation;
    private String taskinfo;
    private  String frieendid;
    private  String monitorid;


    public  TaskInfo(){
        //这个空参数表的构造方法也是用有用，逆向保存的时候需要。
    }
    public TaskInfo(String tid) {//构造函数时通过一个tid去获取到一个任务的实例

        this.tid = tid;
        //我需要方法，以向服务器索要tid对应的数据、其中包括taskname、tasktime、tasklocation（字符串中午+经纬）、taskinfo、friendid<列表>、monitorid<列表>

    }
    public void Format(){
         //在这里连接服务器，将一些不是以字符串呈现的数据转化为合适的数据类型。
        //或者在使用的时候把他规范化也可以
    }

    public void unzip(){

    }
    class FormatTask{
        String TaskName;
        String TaskTime;//因为设置时间是使用TaskClock传入一个字符串设置的的，所以这里使用字符串//注意格式format
        String TaskLocation;//字符串约定，形如“地点名称，经纬度”
        String TaskInfo;//长字符串
        List<PairInfoBean> friendlist;
        String monitorid;//传递一个monitorid即可。

        public FormatTask() {
            //在此处规划好FormatTask的规范化
        }

        public String getTaskName() {
            return TaskName;
        }

        public String getTaskTime() {
            return TaskTime;
        }

        public String getTaskLocation() {
            return TaskLocation;
        }

        public String getTaskInfo() {
            return TaskInfo;
        }

        public List<PairInfoBean> getFriendlist() {
            return friendlist;
        }

        public String getMonitorid() {
            return monitorid;
        }

        public void setTaskName(String taskName) {
            TaskName = taskName;
        }

        public void setTaskTime(String taskTime) {
            TaskTime = taskTime;
        }

        public void setTaskLocation(String taskLocation) {
            TaskLocation = taskLocation;
        }

        public void setTaskInfo(String taskInfo) {
            TaskInfo = taskInfo;
        }

        public void setFriendlist(List<PairInfoBean> friendlist) {
            this.friendlist = friendlist;
        }

        public void setMonitorid(String monitorid) {
            this.monitorid = monitorid;
        }

        public void set(String taskName,String taskTime,String taskLocation,List <PairInfoBean> friendList,String taskInfo,String monitorid){
            this.setTaskName(taskName);
            this.setTaskTime(taskTime);
            this.setTaskLocation(taskLocation);
            this.setFriendlist(friendList);
            this.setTaskInfo(taskInfo);
            this.setMonitorid(monitorid);

        }
    }

}


