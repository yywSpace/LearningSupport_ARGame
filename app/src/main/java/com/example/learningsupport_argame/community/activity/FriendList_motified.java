package com.example.learningsupport_argame.community.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.NavigationController;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.bean.PairInfoBean;
import com.example.learningsupport_argame.community.DBService;
import com.example.learningsupport_argame.community.fragment.HaoyouFragment;


public class FriendList_motified extends AppCompatActivity {

    FragmentManager fragmentManager;

    FragmentTransaction fragmentTransaction;
    HaoyouFragment fragment;
    private LinearLayout lisearch;
    private ImageButton searchButton;
    private EditText searchBox;
    private Context con;
    View preSearchView;
    View onSearchView;
    private DBService service;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist_navigation_layout);
        con=this;

        new NavigationController(this, getWindow().getDecorView());
        service=DBService.getDbService() ;
        fragment=new HaoyouFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.home_container_motified, fragment).commit();



    }

    @Override
    protected void onStart() {
        super.onStart();
        lisearch= findViewById(R.id.ll_search);
        searchBox=findViewById(R.id.search_EditText);
        searchButton=findViewById(R.id.btn_search);
        searchButton.getBackground().setAlpha(0);
        searchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    view.getBackground().setAlpha(70);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    view .getBackground().setAlpha(0);
                    //这里处理事件。


                    PairInfoBean p1=new PairInfoBean();
                    String user_name=searchBox.getText().toString();
                    p1= service.getUserData(user_name).get(0);

                    Toast.makeText(con,p1.getPairName()+p1.getPhone()+p1.getBrithday(),Toast.LENGTH_SHORT).show();
                    return false;
                }
                return false;
            }
        });



    }
}
