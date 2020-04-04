package com.example.learningsupport_argame.UserManagement.bag;

import com.example.learningsupport_argame.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnityItemLab {
    private List<UnityItem> mUnityItemList;
    private static UnityItemLab sUnityItemLab;

    private UnityItemLab() {
        mUnityItemList = new ArrayList<>(Arrays.asList(
                new UnityItem(0, "人物1", "unity中显示的个人形象", "Mod1", R.drawable.unity_mod_1, 600),
                new UnityItem(1, "人物2", "unity中显示的个人形象", "Mod2", R.drawable.unity_mod_2, 700),
                new UnityItem(2, "人物3", "unity中显示的个人形象", "Mod3", R.drawable.unity_mod_3, 800),
                new UnityItem(3, "人物4", "unity中显示的个人形象", "Mod4", R.drawable.unity_mod_4, 900),
                new UnityItem(4, "人物5", "unity中显示的个人形象", "Mod5", R.drawable.unity_mod_5, 1000),
                new UnityItem(5, "人物6", "unity中显示的个人形象", "Mod6", R.drawable.unity_mod_6, 500)
        ));
    }

    public static UnityItemLab get() {
        if (sUnityItemLab == null)
            sUnityItemLab = new UnityItemLab();
        return sUnityItemLab;
    }

    public List<UnityItem> getUnityItemList() {
        return mUnityItemList;
    }
}
