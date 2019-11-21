package com.example.learningsupport_argame.ARModel.Items;

import com.example.learningsupport_argame.R;

import java.util.ArrayList;
import java.util.List;

public class ItemsLab {
    private static ItemsLab sItemsLab;
    private List<Item> mItemList;

    // TODO: 19-7-14 更改Path使其通过模型直接读取
    private ItemsLab() {
        mItemList = new ArrayList<>();
        mItemList.add(new Item("Android 模型", "简单的android模型", "andy.sfb", ItemType.MODEL));
        mItemList.add(new Item("文字", "只包含文字的布局", R.layout.armodel_view_renderable_text, ItemType.VIEW));

    }

    public static ItemsLab get() {
        if (sItemsLab == null)
            sItemsLab = new ItemsLab();
        return sItemsLab;
    }

    public List<Item> getItemList() {
        return mItemList;
    }
}
