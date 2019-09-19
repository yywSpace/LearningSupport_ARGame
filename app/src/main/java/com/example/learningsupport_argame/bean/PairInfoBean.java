package com.example.learningsupport_argame.bean;

import android.graphics.Bitmap;

/**
 * 币对信息
 *
 * @author jiang
 * @date 2018/5/16 00:49
 */
public class PairInfoBean  {



    public String pairId;//主键
    public String pairName;
    public String sex;
    public String brithday;
    public String phone;
    public String college;
    public Bitmap image;//这个类型不知道对不对

    public PairInfoBean(String pairId) {
        this.pairId = pairId;
    }

    public PairInfoBean(String pairId, String pairName, String sex, String brithday, String phone, String college, Bitmap image) {
        this.pairId = pairId;
        this.pairName = pairName;
        this.sex = sex;
        this.brithday = brithday;
        this.phone = phone;
        this.college = college;
        this.image = image;

        //这里通过输入信息，创建一个用户的实例
    }

    public PairInfoBean() {
    }//这个构造方法是为了防止出错使用的，具体后面用不用，以后再决定

    //以下是实例创建好之后，使用的get方法去获取到相应填充信息
    public String getPairId() {
        return pairId;
    }

    public String getPairName() {
        return pairName;
    }

    public String getSex() {
        return sex;
    }

    public String getBrithday() {
        return brithday;
    }

    public String getPhone() {
        return phone;
    }

    public String getCollege() {
        return college;
    }

    public Bitmap getImage() {
        return image;
    }

    private void getInfomation(String pairId){
        //在这里通过id去获取这个用户的其他信息
    }



    //这里再写一个方法可以用uid，从服务器上获取（下载头像保存到本地，并且从本地获取赋值）。

}
