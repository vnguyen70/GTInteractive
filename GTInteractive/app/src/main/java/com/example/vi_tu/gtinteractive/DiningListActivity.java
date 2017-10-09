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

import com.example.vi_tu.gtinteractive.adapters.DiningListAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import java.util.List;

/**
 * Created by Rayner on 9/27/17.
 */

public class DiningListActivity extends AppCompatActivity {
    private DiningPersistence diningDB;

    private RecyclerView diningListView;
    private List<Dining> dList;
    private DiningListAdapter dAdapter;
    private Button filterOpenButton;
    private Button filterFundsButton;

    private boolean filterOpen = false;
    private boolean filterFunds = false;

    private SearchManager searchManager;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_list);
        filterOpenButton = (Button) findViewById(R.id.filterOpenButton);
        filterFundsButton = (Button) findViewById(R.id.filterFundsButton);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        diningDB = new DiningPersistence(db);
        dList = diningDB.getAll();

        dAdapter = new DiningListAdapter(dList);
        diningListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        diningListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        diningListView.setHasFixedSize(true);

        diningListView.setAdapter(dAdapter);
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
                List<Dining> queryResults = diningDB.findByName(query);
                // always return first building in queryResults
                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.DINING);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, queryResults.get(0).getId());
                startActivity(mapActivityIntent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                dAdapter.setData(diningDB.findByName(input)); // TODO: implement filtering methods, because this is somewhat inefficient
                return true;
            }
        });
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }

    public void setFilterOpen() {
        filterOpen = !filterOpen;
    }

    public void setFilterFunds() {
        filterFunds = !filterFunds;
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
