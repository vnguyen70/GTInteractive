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

import com.example.vi_tu.gtinteractive.adapters.BuildingListAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import java.util.List;

/**
 * Created by Rayner on 9/27/17.
 */

public class BuildingListActivity extends AppCompatActivity {
    private BuildingPersistence buildingsDB;

    private RecyclerView buildingsListView;
    private List<Building> bList;
    private BuildingListAdapter bAdapter;

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
        bList = buildingsDB.getAll();

        bAdapter = new BuildingListAdapter(bList);
        buildingsListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        buildingsListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        buildingsListView.setHasFixedSize(true);
        buildingsListView.setAdapter(bAdapter);
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
                List<Building> queryResults = buildingsDB.findByName(query);
                // always return first building in queryResults
                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, queryResults.get(0).getId());
                startActivity(mapActivityIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                bAdapter.setData(buildingsDB.findByName(input)); // TODO: implement filtering methods, because this is somewhat inefficient
                return true;
            }
        });
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
}
