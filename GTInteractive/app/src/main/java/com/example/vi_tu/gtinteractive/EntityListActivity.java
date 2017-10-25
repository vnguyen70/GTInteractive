package com.example.vi_tu.gtinteractive;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vi_tu.gtinteractive.adapters.EntityAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.domain.Entity;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.EntityFilter;

import java.util.ArrayList;
import java.util.List;

public class EntityListActivity extends AppCompatActivity implements ListView.OnItemClickListener   {

    public static final String[] drawerItems = {"Places", "Events"};

    private PlacePersistence buildingsDB;
    private EventPersistence eventsDB;

    private RecyclerView entityListView;
    private EntityAdapter eAdapter;

    private List<Entity> entityList = new ArrayList<Entity>();
    private List<Entity> matchingObjects;
    private EntityFilter eFilter;
    private EntityFilter eFilter2;
    private List<Class> activeFilters = new ArrayList<>();
    private String userInput;

    private SearchManager searchManager;
    private SearchView searchView;
    private MenuItem searchItem;

    private DrawerLayout drawerLayout;
    private ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_list);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        buildingsDB = new PlacePersistence(db);
        eventsDB = new EventPersistence(db);

        entityList.addAll(buildingsDB.getAll());
        entityList.addAll(eventsDB.getAll());
        eFilter = new EntityFilter(entityList);
        eFilter2 = new EntityFilter(entityList);
        eAdapter = new EntityAdapter(entityList);
        entityListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        entityListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        entityListView.setHasFixedSize(true);
        entityListView.setAdapter(eAdapter);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.filter_list_item, drawerItems));
        drawerList.setOnItemClickListener(this);
        drawerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML

        getMenuInflater().inflate(R.menu.menu_place_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // always return first building in queryResults
                List<Entity> queryResults = eFilter2.filterByName(query).getList();
                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE); // TODO: Check for ViewType
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, queryResults.get(0).getId());
                startActivity(mapActivityIntent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                userInput = input;
                eAdapter.setData(eFilter2.filterByName(userInput).getList());
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
            if (drawerLayout.isDrawerOpen(drawerList)) {
                drawerLayout.closeDrawer(drawerList);
            } else {
                drawerLayout.openDrawer(drawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void togglePlacesFilter(View view) {
        if (activeFilters.contains(Place.class)) {
            activeFilters.remove(Place.class);
        } else {
            activeFilters.add(Place.class);
        }
        updateFilters();
    }
    public void toggleEventsFilter(View view) {
        if (activeFilters.contains(Event.class)) {
            activeFilters.remove(Event.class);
        } else {
            activeFilters.add(Event.class);
        }
        updateFilters();
    }
    public void updateFilters() {
        eFilter2 = eFilter.filterByClass(activeFilters);
        List<Entity> filteredList = eFilter2.filterByName(userInput).getList();
        eAdapter.setData(filteredList);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0: // Places
                togglePlacesFilter(view);
                break;
            case 1: // Events
                toggleEventsFilter(view);
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

        }
    }
}
