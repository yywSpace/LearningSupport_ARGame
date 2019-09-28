package com.example.learningsupport_argame;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_layout);
        drawerLayout = findViewById(R.id.activity_na);
        navigationView =  findViewById(R.id.nav);
        menu=  findViewById(R.id.main_menu);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        menu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(item -> {
            //item.setChecked(true);
            Toast.makeText(MainActivity.this,item.getTitle().toString(), Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(navigationView);
            return true;
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_menu://点击菜单，跳出侧滑菜单
                if (drawerLayout.isDrawerOpen(navigationView)){
                    drawerLayout.closeDrawer(navigationView);
                }else{
                    drawerLayout.openDrawer(navigationView);
                }
                break;
        }
    }

}
