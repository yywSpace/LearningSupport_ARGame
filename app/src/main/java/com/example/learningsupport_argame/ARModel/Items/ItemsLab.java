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
//        mItemList.add(new Item("Text", "只包含文字的ViewRenderable", R.layout.armodel_view_renderable_text,ItemType.VIEW));
        mItemList.add(new Item("Model", "模型", "andy.sfb",ItemType.MODEL));
        for (int i = 0; i < 3; i++) {
            mItemList.add(new Item(" ", " ", "",ItemType.OTHER));
        }

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
