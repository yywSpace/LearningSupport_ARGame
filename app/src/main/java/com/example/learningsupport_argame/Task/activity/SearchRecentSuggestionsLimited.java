package com.example.learningsupport_argame.Task.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.SearchRecentSuggestions;

public class SearchRecentSuggestionsLimited extends SearchRecentSuggestions {

    private int limit;

    public SearchRecentSuggestionsLimited(Context context, String authority, int mode, int limit) {
        super(context, authority, mode);
        this.limit = limit;
    }

    @Override
    protected void truncateHistory(ContentResolver cr, int maxEntries) {
        super.truncateHistory(cr, limit);
    }
}