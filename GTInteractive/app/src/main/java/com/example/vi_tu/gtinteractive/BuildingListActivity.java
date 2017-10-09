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

import com.example.vi_tu.gtinteractive.adapters.BuildingListAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.BuildingFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 9/27/17.
 */

public class BuildingListActivity extends AppCompatActivity {
    private BuildingPersistence buildingsDB;

    private RecyclerView buildingsListView;
    private BuildingListAdapter bAdapter;
    private List<Building> bList;
    private BuildingFilter bFilter;
    private BuildingFilter bFilter2;
    private List<Building.Category> activeFilters = new ArrayList<>();
    private String userInput;

    private SearchManager searchManager;
    private SearchView searchView;
    private MenuItem searchItem;

    private Button foodFilterButton;
    private Button housingFilterButton;
    private Button sportsFilterButton;
    private Button greekFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_list);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);
        bList = buildingsDB.getAll();
        bFilter = new BuildingFilter(buildingsDB.getAll());
        bFilter2 = new BuildingFilter(buildingsDB.getAll());
        bAdapter = new BuildingListAdapter(bList);
        buildingsListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        buildingsListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        buildingsListView.setHasFixedSize(true);

        buildingsListView.setAdapter(bAdapter);

        foodFilterButton = (Button) findViewById(R.id.foodFilterButton);
        foodFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFoodFilter(view);
            }
        });
        housingFilterButton = (Button) findViewById(R.id.housingFilterButton);
        housingFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleHousingFilter(view);
            }
        });
        sportsFilterButton = (Button) findViewById(R.id.sportsFilterButton);
        sportsFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSportsFilter(view);
            }
        });
        greekFilterButton = (Button) findViewById(R.id.greekFilterButton);
        greekFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleGreekFilter(view);
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
                List<Building> queryResults = bFilter2.filterByName(query).getList();
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
                bAdapter.setData(bFilter2.filterByName(userInput).getList()); // TODO: implement filtering methods, because this is somewhat inefficient
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

    public void toggleFoodFilter(View view) {
        if (activeFilters.contains(Building.Category.FOOD)) {
            activeFilters.remove(Building.Category.FOOD);
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            activeFilters.add(Building.Category.FOOD);
            view.setBackgroundColor(Color.WHITE);
        }
        updateFilters();
    }

    public void toggleHousingFilter(View view) {
        if (activeFilters.contains(Building.Category.HOUSING)) {
            activeFilters.remove(Building.Category.HOUSING);
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            activeFilters.add(Building.Category.HOUSING);
            view.setBackgroundColor(Color.WHITE);
        }
        updateFilters();
    }

    public void toggleSportsFilter(View view) {
        if (activeFilters.contains(Building.Category.SPORTS)) {
            activeFilters.remove(Building.Category.SPORTS);
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            activeFilters.add(Building.Category.SPORTS);
            view.setBackgroundColor(Color.WHITE);
        }
        updateFilters();

    }

    public void toggleGreekFilter(View view) {
        if (activeFilters.contains(Building.Category.GREEK)) {
            activeFilters.remove(Building.Category.GREEK);
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            activeFilters.add(Building.Category.GREEK);
            view.setBackgroundColor(Color.WHITE);
        }
        updateFilters();

    }

    public void updateFilters() {
        bFilter2 = bFilter.filterByCategories(activeFilters);
        List<Building> filteredList = bFilter2.filterByName(userInput).getList();
        bAdapter.setData(filteredList);
    }
}
