package com.example.learningsupport_argame.bean;

import android.graphics.Bitmap;

/**
 * 币对信息
 *
 * @author jiang
 * @date 2018/5/16 00:49
 */
public class PairInfoBean  {

    public String getPairId() {
        return pairId;
    }

    public void setPairId(String pairId) {
        this.pairId = pairId;
    }

    public String getPairName() {
        return pairName;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String pairId;
    public String pairName;
    public String pairUnit;
    public String pairUnitId;
    public Bitmap image;

}
