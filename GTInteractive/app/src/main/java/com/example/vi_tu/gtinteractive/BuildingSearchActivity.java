package com.example.vi_tu.gtinteractive;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.temp.BuildingAdapter;

import java.util.ArrayList;
import java.util.List;

public class BuildingSearchActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private BuildingPersistence buildingsDB;
    private RecyclerView mRecyclerView;
    private BuildingAdapter mBuildingAdapter;
    List<Building> bList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_search);
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);
        bList = buildingsDB.getAll();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_building_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search for a building by name..."); // TODO: allow the user to choose whether to search by name, address, or buildingId
        ComponentName cn = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        searchView.setIconifiedByDefault(true); // TODO: No change? Make icon first? Do not iconify the widget; expand it by default

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.search_list);
                mRecyclerView =  (RecyclerView) findViewById(R.id.recyclerview_search);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
                mBuildingAdapter = new BuildingAdapter();
                mBuildingAdapter.setBuildingsData(bList);
                mRecyclerView.setAdapter(mBuildingAdapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        newText = newText.toLowerCase();
                        List newBList = new ArrayList();
                        for (Building b : bList) {
                            if (b.getName().toLowerCase().contains(newText)) {
                                newBList.add(b);
                            }
                        }
                        mBuildingAdapter.setBuildingsData(newBList);
                        return true;
                    }
                });
            }
        });
        return true;
    }

}
