package com.example.vi_tu.gtinteractive;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.temp.BuildingAdapter;
import com.example.vi_tu.gtinteractive.temp.SuggestionProvider;

import java.util.List;

/**
 * Created by Rayner on 8/31/17.
 */

public class SearchableActivity extends Activity {

    private RecyclerView mRecyclerView;
    private BuildingAdapter mBuildingAdapter;

    private SQLiteDatabase db;
    private BuildingPersistence buildingsDB;

    private SearchRecentSuggestions suggestions;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);
        // get access to database through BuildingPersistence object
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);


        // search suggestions
        suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
        // search intent
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) { // TODO: auto-search as the user types (whenever a new character is entered)
            String query = intent.getStringExtra(SearchManager.QUERY);
            List<Building> queryResults = buildingsDB.findByName(query); // TODO: allow the user to choose whether to search by name, address, or buildingId
            setContentView(R.layout.activity_building_detail);
            suggestions.saveRecentQuery(query, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("BUILDINGS_SEARCH", "reached new intent in searchable activity");
    }

}


