package com.example.vi_tu.gtinteractive;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;

import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.adapters.SuggestionProvider;

import java.util.List;

public class SearchableActivity extends Activity {

    private PlacePersistence placesDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_list);
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        placesDB = new PlacePersistence(db);
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            List<Place> queryResults = placesDB.findByName(query);
            if (queryResults.size() == 1) {
                Place p = queryResults.get(0);
                Intent mapActivityIntent = new Intent(SearchableActivity.this, MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, p.getId());
                startActivity(mapActivityIntent);
            }
            suggestions.saveRecentQuery(query, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("PLACES_SEARCH", "reached new intent in searchable activity");
    }

}


