package com.example.learningsupport_argame.ARModel.Items;

import com.example.learningsupport_argame.R;

import java.util.ArrayList;
import java.util.List;

public class ModelItemsLab {
    private static ModelItemsLab sModelItemsLab;
    private List<ModelItem> mItemList;

    // 在未找找到好的解决方法之前，再次硬编码
    private ModelItemsLab() {
        mItemList = new ArrayList<>();
        // 1
        mItemList.add(new ModelItem(0, "Android", "简单的android模型", "andy.sfb", R.drawable.model_android_icon, ModelItemType.MODEL,500));
        // 2
        mItemList.add(new ModelItem(1, "任务详情", "在AR界面中显示任务详情界面", R.layout.armodel_view_renderable_text, ModelItemType.VIEW,500));
        // 3
        mItemList.add(new ModelItem(2, "Earth", "Earth", "Earth.sfb", R.drawable.model_earth_icon, ModelItemType.MODEL,500));
        // 4
        mItemList.add(new ModelItem(3, "Jupiter", "Jupiter", "Jupiter.sfb", R.drawable.model_jupiter_icon, ModelItemType.MODEL,500));
        // 5
        mItemList.add(new ModelItem(4, "Luna", "Luna", "Luna.sfb", R.drawable.model_luna_icon, ModelItemType.MODEL,500));
        // 6
        mItemList.add(new ModelItem(5, "Mars", "Mars", "Mars.sfb", R.drawable.model_mars_icon, ModelItemType.MODEL,500));
        // 7
        mItemList.add(new ModelItem(6, "Mercury", "Mercury", "Mercury.sfb", R.drawable.model_mercury_icon, ModelItemType.MODEL,500));
        // 8
        mItemList.add(new ModelItem(7, "Neptune", "Neptune", "Neptune.sfb", R.drawable.model_neptune_icon, ModelItemType.MODEL,500));
        // 9
        mItemList.add(new ModelItem(8, "Saturn", "Saturn", "Saturn.sfb", R.drawable.model_saturn_icon, ModelItemType.MODEL,500));
        // 10
        mItemList.add(new ModelItem(9, "Sol", "Sol", "Sol.sfb", R.drawable.model_sol_icon, ModelItemType.MODEL,500));
        // 11
        mItemList.add(new ModelItem(10, "Uranus", "Uranus", "Uranus.sfb", R.drawable.model_uranus_icon, ModelItemType.MODEL,500));
        // 12
        mItemList.add(new ModelItem(11, "Venus", "Venus", "Venus.sfb", R.drawable.model_venus_icon, ModelItemType.MODEL,500));
    }

    public static ModelItemsLab get() {
        if (sModelItemsLab == null)
            sModelItemsLab = new ModelItemsLab();
        return sModelItemsLab;
    }

    public List<ModelItem> getItemList() {
        return mItemList;
    }
}
