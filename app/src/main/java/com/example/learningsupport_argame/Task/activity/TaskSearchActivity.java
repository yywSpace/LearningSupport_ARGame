package com.example.learningsupport_argame.Task.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;

public class TaskSearchActivity extends AppCompatActivity {
    TextView mQueryTextView;

    @Override
    public void onCreate(Bundle savedInstanceStated) {
        super.onCreate(savedInstanceStated);
        setContentView(R.layout.task_search_activity_layout);
        mQueryTextView = findViewById(R.id.search_query_string);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        handleIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mQueryTextView.setText(query);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    TaskSearchSuggestionProvider.AUTHORITY, TaskSearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            // 清除历史数据
            // suggestions.clearHistory();

        }
    }
}
