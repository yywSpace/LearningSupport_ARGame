package com.example.learningsupport_argame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.learningsupport_argame.Navi.Activity.SelectLocationPopWindow;
import com.example.learningsupport_argame.Navi.Activity.ShowLocationPopWindow;

public class TestActivity extends AppCompatActivity {
    private static String TAG = "TestActivity";
    private Button mStartButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartButton = findViewById(R.id.start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                new ShowLocationPopWindow(TestActivity.this,34.822577f, 114.314684f);
                SelectLocationPopWindow slpw = new SelectLocationPopWindow(TestActivity.this);
                slpw.setOnMarkerSet((address, latLng) -> {
                    Toast.makeText(TestActivity.this, address, Toast.LENGTH_SHORT).show();
                });
                slpw.showMapDialog();
            }
        });
    }
}

