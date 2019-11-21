package com.example.learningsupport_argame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.learningsupport_argame.ARModel.Items.ItemsAdapter;
import com.example.learningsupport_argame.ARModel.Items.ItemsLab;
import com.example.learningsupport_argame.ARModel.Utils.Utils;

public class TestActivity extends AppCompatActivity {
    private static String TAG = "TestActivity";
    private RecyclerView mItemsRecyclerView;
    private ItemsAdapter mItemsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener((v) -> {
            View view = LayoutInflater.from(this).inflate(R.layout.ar_item_recycle_layout, null, false);
            mItemsRecyclerView = view.findViewById(R.id.ar_items_recycler_view);
            mItemsAdapter = new ItemsAdapter(this, ItemsLab.get().getItemList());
            mItemsRecyclerView.setAdapter(mItemsAdapter);
            mItemsAdapter.setOnModelItemClickListener(item -> {
                Toast.makeText(this, item.getItemName(), Toast.LENGTH_SHORT).show();
            });
            mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("选择模型")
                    .setView(view)
                    .create();
            dialog.show();
            dialog.getWindow().setLayout(Utils.dp2px(this, 340), Utils.dp2px(this, 500));

        });

    }
}

