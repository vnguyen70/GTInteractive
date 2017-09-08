package com.example.vi_tu.gtinteractive.temp;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Rayner on 9/2/17.
 */

public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.SuggestionProvider2";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
