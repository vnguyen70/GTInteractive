package com.example.vi_tu.gtinteractive;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.rest.BaseDAO;
import com.example.vi_tu.gtinteractive.rest.BuildingDAO;
import com.example.vi_tu.gtinteractive.utilities.NetworkErrorDialogFragment;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtilsTemp;

import java.util.List;

import static com.example.vi_tu.gtinteractive.constants.Constants.BUILDINGS_CACHE_EXPIRATION_MS_KEY;

public class BuildingsTestActivity extends AppCompatActivity implements NetworkErrorDialogFragment.NetworkErrorDialogListener {

    private TextView tvBuildingsTest;

    private BuildingPersistence buildingsDB;

    private NetworkUtilsTemp networkUtilsTemp;

    public static final String REQUEST_TAG = "BuildingsTestActivity";

    private static final long BUILDINGS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day

    SharedPreferences prefs;

    BuildingDAO buildingDAO;

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

        networkUtilsTemp = new NetworkUtilsTemp(getApplicationContext(), getFragmentManager());

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        buildingDAO = new BuildingDAO(this, new BaseDAO.Listener<Building>() {
            @Override
            public void onDAOError(BaseDAO.Error error) {
                Toast.makeText(BuildingsTestActivity.this, "BuildingDAO error", Toast.LENGTH_SHORT).show(); // TODO: error handling
            }
            @Override
            public void onListReady(List<Building> buildings) {
                displayResults();
                Toast.makeText(BuildingsTestActivity.this, "Buildings Loaded", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onObjectReady(Building building) {}
        });
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
                buildingDAO.getAllAsync();
//                networkUtilsTemp.loadBuildingsFromAPI(buildingsDB);
                return true;
            case R.id.action_clear:
                buildingsDB.deleteAll();
                prefs.edit().putLong(BUILDINGS_CACHE_EXPIRATION_MS_KEY, 0).apply();
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
        networkUtilsTemp.loadBuildingsFromAPI(buildingsDB);
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog) {
        // TODO: display toast?
    }
}
