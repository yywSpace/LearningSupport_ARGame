package com.example.learningsupport_argame.ARModel;

public enum ModelPutStatus {
    /**
     * 未放置模型，灰色
     */
    DO_NOT_PUT,
    /**
     * 左右，前后控制，绿色
     */
    LR_BA_MODE,
    /**
     * 旋转，上下控制，红色
     */
    ROTATE_UD_MODE,
    /**
     * 缩放模型, 蓝色
     */
    SCALE_MODE
}
