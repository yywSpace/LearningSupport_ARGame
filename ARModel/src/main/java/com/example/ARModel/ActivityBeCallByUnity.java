package com.example.ARModel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityBeCallByUnity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("ActivityBeCallByUnity");
        setContentView(textView);
    }
}
