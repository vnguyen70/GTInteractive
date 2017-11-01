package com.example.vi_tu.gtinteractive;

import android.content.DialogInterface;
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

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.NetworkErrorDialogFragment;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtils;

import org.joda.time.DateTime;

import java.util.List;

public class BuildingsTestActivity extends AppCompatActivity implements NetworkErrorDialogFragment.NetworkErrorDialogListener {

    private TextView tvBuildingsTest;

    private BuildingPersistence buildingsDB;

    private NetworkUtils networkUtils;

    public static final String REQUEST_TAG = "BuildingsTestActivity";

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

        networkUtils = new NetworkUtils(getApplicationContext(), getFragmentManager());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        long nowMS = DateTime.now().getMillis();
        long buildingsCacheExpiredMS = sharedPreferences.getLong("buildingsCacheExpiredMS", 0);

        if (nowMS >= buildingsCacheExpiredMS) {
            networkUtils.loadBuildingsFromAPI(buildingsDB);
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
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_display:
                displayResults();
                return true;
            case R.id.action_reload:
                tvBuildingsTest.setText("");
                networkUtils.loadBuildingsFromAPI(buildingsDB);
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
            results += b.getId() + ": " + b.getBuildingId() + " (" + b.getName() + ")\n";
        }
        tvBuildingsTest.setText("" + bList.size() + " buildings found:\n\n" + results);
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog) {
        tvBuildingsTest.setText("");
        networkUtils.loadBuildingsFromAPI(buildingsDB);
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog) {
        // TODO: display toast?
    }
}