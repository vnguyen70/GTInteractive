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

import com.example.vi_tu.gtinteractive.adapters.PlaceFilterAdapter;
import com.example.vi_tu.gtinteractive.adapters.PlaceListAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.PlaceFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 9/27/17.
 */

public class PlaceListActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private Place.Category[] filterItems = Place.Category.values();
    private PlacePersistence placesDB;

    private RecyclerView placesListView;
    private PlaceListAdapter pAdapter;
    private List<Place> pList;
    private PlaceFilter pFilter;
    private PlaceFilter pFilter2;
    private List<Place.Category> activeFilters = new ArrayList<>();
    private String userInput;

    private SearchManager searchManager;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        placesDB = new PlacePersistence(db);
        pList = placesDB.getAll();
        pFilter = new PlaceFilter(placesDB.getAll());
        pFilter2 = new PlaceFilter(placesDB.getAll());
        pAdapter = new PlaceListAdapter(pList, getIntent().getBooleanExtra("ShowMapNext", false));
        placesListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        placesListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        placesListView.setHasFixedSize(true);

        placesListView.setAdapter(pAdapter);
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
                List<Place> queryResults = pFilter2.filterByName(query).getList();
                // always return first place in queryResults
//                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
//                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
//                mapActivityIntent.putExtra(Arguments.OBJECT_ID, queryResults.get(0).getId());
//                startActivity(mapActivityIntent);
                Intent detailsActivityIntent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                detailsActivityIntent.putExtra(Arguments.OBJECT_ID, queryResults.get(0).getId());
                startActivity(detailsActivityIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                userInput = input;
                pAdapter.setData(pFilter2.filterByName(userInput).getList()); // TODO: implement filtering methods, because this is somewhat inefficient
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

        listView.setAdapter(new PlaceFilterAdapter(this, R.layout.filter_list_item, filterItems));
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

    public void toggleFoodFilter(View view) {
        if (activeFilters.contains(Place.Category.FOOD)) {
            activeFilters.remove(Place.Category.FOOD);
        } else {
            activeFilters.add(Place.Category.FOOD);
        }
        updateFilters();
    }

    public void toggleHousingFilter(View view) {
        if (activeFilters.contains(Place.Category.HOUSING)) {
            activeFilters.remove(Place.Category.HOUSING);
        } else {
            activeFilters.add(Place.Category.HOUSING);
        }
        updateFilters();
    }

    public void toggleSportsFilter(View view) {
        if (activeFilters.contains(Place.Category.SPORTS)) {
            activeFilters.remove(Place.Category.SPORTS);
        } else {
            activeFilters.add(Place.Category.SPORTS);
        }
        updateFilters();
    }

    public void toggleGreekFilter(View view) {
        if (activeFilters.contains(Place.Category.GREEK)) {
            activeFilters.remove(Place.Category.GREEK);
        } else {
            activeFilters.add(Place.Category.GREEK);
        }
        updateFilters();
    }

    public void toggleParkingFilter(View view) {
        if (activeFilters.contains(Place.Category.PARKING)) {
            activeFilters.remove(Place.Category.PARKING);
        } else {
            activeFilters.add(Place.Category.PARKING);
        }
        updateFilters();
    }

    public void toggleAcademicFilter(View view) {
        if (activeFilters.contains(Place.Category.ACADEMIC)) {
            activeFilters.remove(Place.Category.ACADEMIC);
        } else {
            activeFilters.add(Place.Category.ACADEMIC);
        }
        updateFilters();
    }

    public void toggleOtherFilter(View view) {
        if (activeFilters.contains(Place.Category.OTHER)) {
            activeFilters.remove(Place.Category.OTHER);
        } else {
            activeFilters.add(Place.Category.OTHER);
        }
        updateFilters();
    }

    public void updateFilters() {
        pFilter2 = pFilter.filterByCategories(activeFilters);
        List<Place> filteredList = pFilter2.filterByName(userInput).getList();
        pAdapter.setData(filteredList);
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
