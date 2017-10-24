package com.example.vi_tu.gtinteractive;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

public class BuildingListActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    public static final String[] drawerItems = {"Food", "Housing", "Sports", "Greek", "Parking", "Academic", "Other"};

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

    private DrawerLayout drawerLayout;
    private ListView drawerList;

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

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.filter_list_item, drawerItems));
        drawerList.setOnItemClickListener(this);
        drawerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

//        Toolbar t = (Toolbar) findViewById(R.id.tToolbar);
//        ActionMenuView amvMenu = (ActionMenuView) t.findViewById(R.id.amvMenu);
//        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                return onOptionsItemSelected(menuItem);
//            }
//        });
//
//        setSupportActionBar(t);
//        getSupportActionBar().setTitle(null);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public void toggleFoodFilter(View view) {
        if (activeFilters.contains(Building.Category.FOOD)) {
            activeFilters.remove(Building.Category.FOOD);
        } else {
            activeFilters.add(Building.Category.FOOD);
        }
        updateFilters();
    }

    public void toggleHousingFilter(View view) {
        if (activeFilters.contains(Building.Category.HOUSING)) {
            activeFilters.remove(Building.Category.HOUSING);
        } else {
            activeFilters.add(Building.Category.HOUSING);
        }
        updateFilters();
    }

    public void toggleSportsFilter(View view) {
        if (activeFilters.contains(Building.Category.SPORTS)) {
            activeFilters.remove(Building.Category.SPORTS);
        } else {
            activeFilters.add(Building.Category.SPORTS);
        }
        updateFilters();
    }

    public void toggleGreekFilter(View view) {
        if (activeFilters.contains(Building.Category.GREEK)) {
            activeFilters.remove(Building.Category.GREEK);
        } else {
            activeFilters.add(Building.Category.GREEK);
        }
        updateFilters();
    }
    public void toggleParkingFilter(View view) {
        if (activeFilters.contains(Building.Category.PARKING)) {
            activeFilters.remove(Building.Category.PARKING);
        } else {
            activeFilters.add(Building.Category.PARKING);
        }
        updateFilters();
    }
    public void toggleAcademicFilter(View view) {
        if (activeFilters.contains(Building.Category.ACADEMIC)) {
            activeFilters.remove(Building.Category.ACADEMIC);
        } else {
            activeFilters.add(Building.Category.ACADEMIC);
        }
        updateFilters();
    }
    public void toggleOtherFilter(View view) {
        if (activeFilters.contains(Building.Category.OTHER)) {
            activeFilters.remove(Building.Category.OTHER);
        } else {
            activeFilters.add(Building.Category.OTHER);
        }
        updateFilters();
    }

    public void updateFilters() {
        bFilter2 = bFilter.filterByCategories(activeFilters);
        List<Building> filteredList = bFilter2.filterByName(userInput).getList();
        bAdapter.setData(filteredList);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch(i) {
            case 0: // Food
                toggleFoodFilter(view);
                break;
            case 1: // Housing
                toggleHousingFilter(view);
                break;
            case 2: // Sports
                toggleSportsFilter(view);
                break;
            case 3: // Greek
                toggleGreekFilter(view);
                break;
            case 4: // Parking
                toggleParkingFilter(view);
                break;
            case 5: // Academic
                toggleAcademicFilter(view);
                break;
            case 6: // Other
                toggleOtherFilter(view);
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
