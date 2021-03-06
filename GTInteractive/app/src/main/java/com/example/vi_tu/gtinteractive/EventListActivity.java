package com.example.vi_tu.gtinteractive;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vi_tu.gtinteractive.adapters.EventFilterAdapter;
import com.example.vi_tu.gtinteractive.adapters.EventListAdapter;
import com.example.vi_tu.gtinteractive.adapters.PlaceFilterAdapter;
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

public class EventListActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private Event.Category[] filterItems = Event.Category.values();
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML

        getMenuInflater().inflate(R.menu.menu_place_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Event> queryResults = eFilter2.filterByTitle(query).getList();
                // always return first place in queryResults
                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
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
        searchView.setMaxWidth(800);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_filter) {
            createAndDisplayDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAndDisplayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        final ListView listView = new ListView(this);

        listView.setAdapter(new EventFilterAdapter(this, R.layout.filter_list_item, filterItems));
        listView.setOnItemClickListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Dialog does not save which items were checked previously
        // Thus, we have to manually check these when creating the layout
        for (int i = 0; i < filterItems.length; i++) {
            if (activeFilters.contains(filterItems[i])) {
                listView.setItemChecked(i, true);
            }
        }

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        builder.setView(layout);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.d("PlaceListActivity", "Done clicked");
            }
        });

        builder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // do nothing. Will override later to prevent automatic closing dialog behavior
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // uncheck all the filters
                for (int i = 0; i < filterItems.length; i++) {
                    listView.setItemChecked(i, false);
                }
                // remove all active filters
                activeFilters.clear();
                updateFilters();
            }
        });
    }

    public void toggleArtFilter(View view) {
        if (activeFilters.contains(Event.Category.ARTS)) {
            activeFilters.remove(Event.Category.ARTS);
        } else {
            activeFilters.add(Event.Category.ARTS);
        }
        updateFilters();
    }

    public void toggleCareerFilter(View view) {
        if (activeFilters.contains(Event.Category.CAREER)) {
            activeFilters.remove(Event.Category.CAREER);
        } else {
            activeFilters.add(Event.Category.CAREER);
        }
        updateFilters();
    }

    public void toggleConferenceFilter(View view) {
        if (activeFilters.contains(Event.Category.CONFERENCE)) {
            activeFilters.remove(Event.Category.CONFERENCE);
        } else {
            activeFilters.add(Event.Category.CONFERENCE);
        }
        updateFilters();
    }

    public void toggleOtherFilter(View view) {
        if (activeFilters.contains(Event.Category.OTHER)) {
            activeFilters.remove(Event.Category.OTHER);
        } else {
            activeFilters.add(Event.Category.OTHER);
        }
        updateFilters();

    }

    public void toggleSeminarFilter(View view) {
        if (activeFilters.contains(Event.Category.SEMINAR)) {
            activeFilters.remove(Event.Category.SEMINAR);
        } else {
            activeFilters.add(Event.Category.SEMINAR);
        }
        updateFilters();
    }

    public void toggleSpecialEventFilter(View view) {
        if (activeFilters.contains(Event.Category.SPECIAL)) {
            activeFilters.remove(Event.Category.SPECIAL);
        } else {
            activeFilters.add(Event.Category.SPECIAL);
        }
        updateFilters();
    }

    public void toggleSportsFilter(View view) {
        if (activeFilters.contains(Event.Category.SPORTS)) {
            activeFilters.remove(Event.Category.SPORTS);
        } else {
            activeFilters.add(Event.Category.SPORTS);
        }
        updateFilters();
    }

    public void toggleStudentSponsoredFilter(View view) {
        if (activeFilters.contains(Event.Category.STUDENT)) {
            activeFilters.remove(Event.Category.STUDENT);
        } else {
            activeFilters.add(Event.Category.STUDENT);
        }
        updateFilters();
    }

    public void toggleTrainingFilter(View view) {
        if (activeFilters.contains(Event.Category.TRAINING)) {
            activeFilters.remove(Event.Category.TRAINING);
        } else {
            activeFilters.add(Event.Category.TRAINING);
        }
        updateFilters();
    }

    public void updateFilters() {
        eFilter2 = eFilter.filterByCategories(activeFilters);
        List<Event> filteredList = eFilter2.filterByTitle(userInput).getList();
        eAdapter.setData(filteredList);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch(i) {
            case 0: // Art
                toggleArtFilter(view);
                break;
            case 1: // Career
                toggleCareerFilter(view);
                break;
            case 2: // Conference
                toggleConferenceFilter(view);
                break;
            case 3: // Other
                toggleOtherFilter(view);
                break;
            case 4: // Seminar
                toggleSeminarFilter(view);
                break;
            case 5: // Special Event
                toggleSpecialEventFilter(view);
                break;
            case 6: // Sports
                toggleSportsFilter(view);
                break;
            case 7: // Student Sponsored
                toggleStudentSponsoredFilter(view);
                break;
            case 8: // Training
                toggleTrainingFilter(view);
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
