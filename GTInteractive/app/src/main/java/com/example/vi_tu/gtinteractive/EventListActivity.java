package com.example.vi_tu.gtinteractive;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.vi_tu.gtinteractive.adapters.EventAdapter;
import com.example.vi_tu.gtinteractive.adapters.EventListAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import java.util.List;

/**
 * Created by Rayner on 9/27/17.
 */

public class EventListActivity extends AppCompatActivity {
    private EventPersistence eventsDB;

    private RecyclerView eventsListView;
    private List<Event> eList;
    private EventListAdapter eAdapter;
    private Button artFilterButton;
    private Button careerFilterButton;
    private Button conferenceFilterButton;
    private Button otherFilterButton;

    private SearchManager searchManager;
    private SearchView searchView;
    private MenuItem searchItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);
        artFilterButton = (Button) findViewById(R.id.artFilterButton);
        careerFilterButton = (Button) findViewById(R.id.careerFilterButton);
        conferenceFilterButton = (Button) findViewById(R.id.conferenceFilterButton);
        otherFilterButton = (Button) findViewById(R.id.otherFilterButton);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eventsDB = new EventPersistence(db);
        eList = eventsDB.getAll();

        eAdapter = new EventListAdapter(eList);
        eventsListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        eventsListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        eventsListView.setHasFixedSize(true);

        eventsListView.setAdapter(eAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML

        getMenuInflater().inflate(R.menu.menu_building_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Event> queryResults = eventsDB.findByTitle(query);
                // always return first building in queryResults
                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, queryResults.get(0).getId());
                startActivity(mapActivityIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                eAdapter.setData(eventsDB.findByTitle(input)); // TODO: implement filtering methods, because this is somewhat inefficient
                return true;
            }
        });
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }


}
