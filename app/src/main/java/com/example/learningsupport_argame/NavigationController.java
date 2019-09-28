package com.example.learningsupport_argame;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class NavigationController {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton navigationButton;
    private String TAG = "NavigationController";

    public NavigationController(Context context, View view) {
        drawerLayout = view.findViewById(R.id.navigation_drawer);
        navigationView = view.findViewById(R.id.navigation_nav);
        navigationButton = view.findViewById(R.id.navigation_button);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        navigationButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            Toast.makeText(context, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, ": "+ item.getTitle().toString());
            drawerLayout.closeDrawer(navigationView);
            return true;
        });
    }
}
