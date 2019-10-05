package com.example.ARModel;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;


public class MainPageActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
        UnityPlayer.UnitySendMessage("InteractionController", "LoadMainPageScene", "");
    }


    public void startFriendActivity() {
        Toast.makeText(this, "startFriendActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ActivityBeCallByUnity.class));
    }

    public void startTaskActivity() {
        Toast.makeText(this, "startTaskActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ActivityBeCallByUnity.class));

    }

    public void startCourseActivity() {
        Toast.makeText(this, "startCourseActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ActivityBeCallByUnity.class));

    }

    public void startFeedbackActivity() {
        Toast.makeText(this, "startFeedbackActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ActivityBeCallByUnity.class));

    }
}