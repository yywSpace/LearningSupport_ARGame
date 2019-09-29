package com.example.learningsupport_argame;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import androidx.drawerlayout.widget.DrawerLayout;

import com.example.learningsupport_argame.unity.GameActivity;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends Activity {

    private Button btn_start;
    private Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start = findViewById(R.id.start);
        con = this;
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, GameActivity.class);
                startActivity(intent);
            }
        });
    }

}
