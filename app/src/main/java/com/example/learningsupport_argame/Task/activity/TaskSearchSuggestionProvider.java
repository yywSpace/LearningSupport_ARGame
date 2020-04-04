package com.example.learningsupport_argame.Task.activity;

import android.content.SearchRecentSuggestionsProvider;

public class TaskSearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.learningsupport_argame.Task.activity.TaskSearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public TaskSearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}