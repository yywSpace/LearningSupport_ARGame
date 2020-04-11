package com.example.learningsupport_argame.Community.club.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.Community.club.fragment.ClubListFragment;
import com.example.learningsupport_argame.R;

public class ClubListFragmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new ClubListFragment())
                .commit();
    }
}
