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

import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.NetworkErrorDialogFragment;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtils;

import org.joda.time.DateTime;

import java.util.List;

public class PlaceTestActivity extends AppCompatActivity implements NetworkErrorDialogFragment.NetworkErrorDialogListener {

    private TextView tvPlacesTest;

    private PlacePersistence placesDB;

    private NetworkUtils networkUtils;

    public static final String REQUEST_TAG = "PlaceTestActivity";

    private static final long PLACES_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_test);
        tvPlacesTest = (TextView) findViewById(R.id.tv_places_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        placesDB = new PlacePersistence(db);

        networkUtils = new NetworkUtils(getApplicationContext(), getFragmentManager());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        long nowMS = DateTime.now().getMillis();
        long placesCacheExpiredMS = sharedPreferences.getLong("placesCacheExpiredMS", 0);

        if (nowMS >= placesCacheExpiredMS) {
            networkUtils.loadPlacesFromAPI(placesDB);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("placesCacheExpiredMS", nowMS + PLACES_CACHE_DURATION_MS);
            editor.apply();
        } else {
            Log.d("NETWORK_TEST", "places already loaded");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_places_test, menu);
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
                tvPlacesTest.setText("");
                networkUtils.loadPlacesFromAPI(placesDB);
                return true;
            case R.id.action_clear:
                placesDB.deleteAll();
                displayResults();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayResults() {
        List<Place> pList = placesDB.getAll();
        String results = "";
        for (Place p : pList) {
            results += p.getId() + ": " + p.getPlaceId() + " (" + p.getName() + ")\n";
        }
        tvPlacesTest.setText("" + pList.size() + " places found:\n\n" + results);
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog) {
        tvPlacesTest.setText("");
        networkUtils.loadPlacesFromAPI(placesDB);
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog) {
        // TODO: display toast?
    }
}
