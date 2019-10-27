package com.example.ARModel;

import com.unity3d.player.UnityPlayer;

/**
 * 继承UnityPlayerActivity，并包含向unity发送信息的方法
 */
public class SendMessageActivity extends UnityPlayerActivity {

    public void SendMessage(String gameObject, String methodName, String args) {
        UnityPlayer.UnitySendMessage(gameObject, methodName, args);
    }
}
