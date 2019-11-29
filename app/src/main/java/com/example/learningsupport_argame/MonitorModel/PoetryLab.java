package com.example.learningsupport_argame.MonitorModel;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class PoetryLab {
    private static PoetryLab mPoetryLab;
    private List<Poetry> mPoetryList;

    public static PoetryLab get() {
        if (mPoetryLab == null)
            mPoetryLab = new PoetryLab();
        return mPoetryLab;
    }

    private PoetryLab() {
        mPoetryList = new ArrayList<>();
        mPoetryList.add(new Poetry("越人歌", "佚名", "山有木兮木有枝，", "心悦君兮君不知。"));
        mPoetryList.add(new Poetry("木兰词·拟古决绝词柬友", "纳兰性德", "人生若只如初见，", "何事秋风悲画扇。"));
        mPoetryList.add(new Poetry("离思五首·其四", "元稹", "曾经沧海难为水，", "除却巫山不是云。"));
        mPoetryList.add(new Poetry("江城子·乙卯正月二十日夜记梦", "苏轼", "十年生死两茫茫，", "不思量，自难忘。"));
        mPoetryList.add(new Poetry("南歌子词二首 / 新添声杨柳枝词", "温庭筠", "玲珑骰子安红豆，", "入骨相思知不知。"));
        mPoetryList.add(new Poetry("卜算子·我住长江头", "李之仪", "只愿君心似我心，", "定不负相思意"));
        mPoetryList.add(new Poetry("折桂令·春情", "徐再思", "入我相思门，", "知我相思苦。"));
        mPoetryList.add(new Poetry("三五七言 / 秋风词", "李白", "平生不会相思，", "才会相思，便害相思。"));
        mPoetryList.add(new Poetry("鹊桥仙·纤云弄巧", "秦观", "两情若是久长时，", "又岂在朝朝暮暮。"));
    }

    public List<Poetry> getPoetryList() {
        return mPoetryList;
    }
}
