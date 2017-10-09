package com.example.vi_tu.gtinteractive;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.vi_tu.gtinteractive.adapters.EventListAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.EventFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 9/27/17.
 */

public class EventListActivity extends AppCompatActivity {
    private EventPersistence eventsDB;

    private RecyclerView eventsListView;
    private EventListAdapter eAdapter;
    private List<Event> eList;
    private EventFilter eFilter;
    private EventFilter eFilter2;
    private List<Event.Category> activeFilters = new ArrayList<>();
    private String userInput;


    private SearchManager searchManager;
    private SearchView searchView;
    private MenuItem searchItem;

    private Button artFilterButton;
    private Button careerFilterButton;
    private Button conferenceFilterButton;
    private Button otherFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eventsDB = new EventPersistence(db);
        eList = eventsDB.getAll();
        eFilter = new EventFilter(eventsDB.getAll());
        eFilter2 = new EventFilter(eventsDB.getAll());
        eAdapter = new EventListAdapter(eList);
        eventsListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        eventsListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        eventsListView.setHasFixedSize(true);

        eventsListView.setAdapter(eAdapter);

        artFilterButton = (Button) findViewById(R.id.artFilterButton);
        artFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleArtFilter(view);
            }
        });
        careerFilterButton = (Button) findViewById(R.id.careerFilterButton);
        careerFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCareerFilter(view);
            }
        });
        conferenceFilterButton = (Button) findViewById(R.id.conferenceFilterButton);
        conferenceFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleConferenceFilter(view);
            }
        });
        otherFilterButton = (Button) findViewById(R.id.otherFilterButton);
        otherFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleOtherFilter(view);
            }
        });
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
                List<Event> queryResults = eFilter2.filterByTitle(query).getList();
                // always return first building in queryResults
                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, queryResults.get(0).getId());
                startActivity(mapActivityIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                userInput = input;
                eAdapter.setData(eFilter2.filterByTitle(userInput).getList()); // TODO: implement filtering methods, because this is somewhat inefficient
                return true;
            }
        });
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleArtFilter(View view) {
        if (activeFilters.contains(Event.Category.ARTS)) {
            activeFilters.remove(Event.Category.ARTS);
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            activeFilters.add(Event.Category.ARTS);
            view.setBackgroundColor(Color.WHITE);
        }
        updateFilters();
    }

    public void toggleCareerFilter(View view) {
        if (activeFilters.contains(Event.Category.CAREER)) {
            activeFilters.remove(Event.Category.CAREER);
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            activeFilters.add(Event.Category.CAREER);
            view.setBackgroundColor(Color.WHITE);
        }
        updateFilters();
    }

    public void toggleConferenceFilter(View view) {
        if (activeFilters.contains(Event.Category.CONFERENCE)) {
            activeFilters.remove(Event.Category.CONFERENCE);
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            activeFilters.add(Event.Category.CONFERENCE);
            view.setBackgroundColor(Color.WHITE);
        }
        updateFilters();

    }

    public void toggleOtherFilter(View view) {
        if (activeFilters.contains(Event.Category.OTHER)) {
            activeFilters.remove(Event.Category.OTHER);
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            activeFilters.add(Event.Category.OTHER);
            view.setBackgroundColor(Color.WHITE);
        }
        updateFilters();

    }

    public void updateFilters() {
        eFilter2 = eFilter.filterByCategories(activeFilters);
        List<Event> filteredList = eFilter2.filterByTitle(userInput).getList();
        eAdapter.setData(filteredList);
    }
}
