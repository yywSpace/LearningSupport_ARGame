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
        mItemList.add(new ModelItem("Android模型", "简单的android模型", "andy.sfb", ModelItemType.MODEL));
        mItemList.add(new ModelItem("任务详情", "在AR界面中显示任务详情界面", R.layout.armodel_view_renderable_text, ModelItemType.VIEW));
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
