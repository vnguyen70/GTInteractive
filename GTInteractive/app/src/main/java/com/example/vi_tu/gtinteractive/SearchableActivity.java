package com.example.android.testproject;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raykris on 8/31/17.
 */

public class SearchableActivity extends Activity {
    private RecyclerView mRecyclerView;
    private BuildingAdapter mBuildingAdapter;
    List<String> buildingData;
    SQLiteDBHelper myDb = new SQLiteDBHelper(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);
        buildingData = myDb.getBuildingNames();
        mRecyclerView = findViewById(R.id.recyclerview_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mBuildingAdapter = new BuildingAdapter();
        // building data has to be String[]
        mBuildingAdapter.setBuildingData(buildingData.toArray(new String[buildingData.size()]));
        mRecyclerView.setAdapter(mBuildingAdapter);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
            // saves the query
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
    }
            //TODO: Allow user to clear suggestions for privacy? Perhaps not important because it's just building searches
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("MyTag", "reached new intent in searchable activity");
    }

    public void doMySearch(String query) {
        for (String building : buildingData) {
            //TODO: Allow user to enter partial building name
            if (query.equals(building)) {
                String[] dummyBuildingData2 = new String[] {building};
                mBuildingAdapter.setBuildingData(dummyBuildingData2);

            }
        }
    }
    //TODO: Control search suggestions
    // https://developer.android.com/guide/topics/search/search-dialog.html#SearchableActivity
}


