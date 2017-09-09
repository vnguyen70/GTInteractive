package com.example.vi_tu.gtinteractive;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import org.joda.time.DateTime;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.NetworkUtils.loadBuildingsFromAPI;

public class BuildingsTestActivity extends AppCompatActivity {

    private TextView tvBuildingsTest;

    private BuildingPersistence buildingsDB;

    public static final String REQUEST_TAG = "BuildingsTestActivity";
    private RequestQueue queue;

    private static final long BUILDINGS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings_test);
        tvBuildingsTest = (TextView) findViewById(R.id.tv_buildings_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);

        queue = Volley.newRequestQueue(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        long nowMS = DateTime.now().getMillis();
        long buildingsCacheExpiredMS = sharedPreferences.getLong("buildingsCacheExpiredMS", 0);

        if (nowMS >= buildingsCacheExpiredMS) {
            loadBuildingsFromAPI(buildingsDB, queue);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("buildingsCacheExpiredMS", nowMS + BUILDINGS_CACHE_DURATION_MS);
            editor.apply();
        } else {
            Log.d("NETWORK_TEST", "buildings already loaded");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buildings_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_display:
                displayResults();
                return true;
            case R.id.action_reload:
                tvBuildingsTest.setText("");
                loadBuildingsFromAPI(buildingsDB, queue);
                return true;
            case R.id.action_clear:
                buildingsDB.deleteAll();
                displayResults();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayResults() {
        List<Building> bList = buildingsDB.getAll();
        String results = "";
        for (Building b : bList) {
            results += b.getBuildingId() + " - " + b.getNameTokens() + " - " + b.getAddressTokens() + "\n";
        }
        tvBuildingsTest.setText("" + bList.size() + " buildings found:\n\n" + results);
    }

}
