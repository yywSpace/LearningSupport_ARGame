package com.example.ARModel;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.unity3d.player.UnityPlayer;

public class ModelScanActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
        UnityPlayer.UnitySendMessage("InteractionController", "LoadModelScanScene", "");
    }

}