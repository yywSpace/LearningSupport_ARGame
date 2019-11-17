package com.example.learningsupport_argame.ARModel.Items;

import com.example.learningsupport_argame.R;

import java.util.ArrayList;
import java.util.List;

public class ModelInfoLab {
    private static ModelInfoLab sModelInfoLab;
    private List<ModelInfo> mModelInfoList;
    private ModelInfo mCurrentModelInfo;


    private ModelInfoLab() {
        mModelInfoList = new ArrayList<>();
    }

    public static ModelInfoLab get() {
        if (sModelInfoLab == null)
            sModelInfoLab = new ModelInfoLab();
        return sModelInfoLab;
    }

    public List<ModelInfo> getModelInfoList() {
        return mModelInfoList;
    }

    public ModelInfo getCurrentModelInfo() {
        return mCurrentModelInfo;
    }

    public void setCurrentModelInfo(ModelInfo currentModelInfo) {
        mCurrentModelInfo = currentModelInfo;
    }
}

