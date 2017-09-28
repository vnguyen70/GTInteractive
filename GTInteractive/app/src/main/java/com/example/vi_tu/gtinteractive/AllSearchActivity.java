package com.example.vi_tu.gtinteractive;

import android.app.SearchManager;
import android.content.ComponentName;
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
import android.view.View;

import com.example.vi_tu.gtinteractive.adapters.SearchAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.adapters.BuildingAdapter;

import java.util.ArrayList;
import java.util.List;

public class AllSearchActivity extends AppCompatActivity {

    private BuildingPersistence buildingsDB;
    private DiningPersistence diningDB;
    private EventPersistence eventsDB;

    private RecyclerView searchListView;
    private SearchAdapter sAdapter;

    private List<Building> bList;
    private List<Dining> dList;
    private List<Event> eList;
    private List<Object> searchList = new ArrayList<Object>();
    private List<Object> matchingObjects;

    private SearchManager searchManager;
    private SearchView searchView;
    private MenuItem searchItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);
        diningDB = new DiningPersistence(db);
        eventsDB = new EventPersistence(db);

        searchList.addAll(buildingsDB.getAll());
        searchList.addAll(diningDB.getAll());
        searchList.addAll(eventsDB.getAll());


        sAdapter = new SearchAdapter(searchList);
        searchListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        searchListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        searchListView.setHasFixedSize(true);
        searchListView.setAdapter(sAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML

        getMenuInflater().inflate(R.menu.menu_building_search, menu);
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
                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.DINING);
//                mapActivityIntent.putExtra(Arguments.OBJECT_ID, matchingObjects.get(0).getId());
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, -1);
                startActivity(mapActivityIntent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                ArrayList tempList = new ArrayList<Object>();
                tempList.addAll(diningDB.findByName(input));
                tempList.addAll(eventsDB.findByTitle(input));
                tempList.addAll(buildingsDB.findByName(input));
                sAdapter.setData(tempList);
                matchingObjects = tempList;
                return true;
            }
        });
        return true;
    }

}
