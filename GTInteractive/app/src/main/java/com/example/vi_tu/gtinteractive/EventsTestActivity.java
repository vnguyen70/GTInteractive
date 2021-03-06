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

import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.NetworkErrorDialogFragment;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtils;

import org.joda.time.DateTime;

import java.util.List;

public class EventsTestActivity extends AppCompatActivity implements NetworkErrorDialogFragment.NetworkErrorDialogListener {

    private TextView tvEventsTest;

    private EventPersistence eventsDB;
    private PlacePersistence placesDB;

    private NetworkUtils networkUtils;

    public static final String REQUEST_TAG = "EventsTestActivity";

    private static final long EVENTS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_test);
        tvEventsTest = (TextView) findViewById(R.id.tv_events_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eventsDB = new EventPersistence(db);
        placesDB = new PlacePersistence(db);

        networkUtils = new NetworkUtils(getApplicationContext(), getFragmentManager());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        long nowMS = DateTime.now().getMillis();
        long eventsCacheExpiredMS = sharedPreferences.getLong("eventsCacheExpiredMS", 0);

        if (nowMS >= eventsCacheExpiredMS) {
            networkUtils.loadEventsFromAPI(eventsDB, placesDB);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("eventsCacheExpiredMS", nowMS + EVENTS_CACHE_DURATION_MS);
            editor.apply();
        } else {
            Log.d("NETWORK_TEST", "events already loaded");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_events_test, menu);
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
                tvEventsTest.setText("");
                networkUtils.loadEventsFromAPI(eventsDB, placesDB);
                return true;
            case R.id.action_clear:
                eventsDB.deleteAll();
                displayResults();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayResults() {
        List<Event> eList = eventsDB.getAll();
        String results = "";
        int none = 0;
        int single = 0;
        int multiple = 0;
        for (Event e : eList) {
            results += e.getLocation() + "\n-- " + e.getPlaceId() + " --\n\n";
            if (e.getPlaceId().equals("NONE")) {
                none++;
            } else if (e.getPlaceId().startsWith("MANY", 0)) {
                multiple++;
            } else {
                single++;
            }
        }
        tvEventsTest.setText("" + eList.size() + " events found:\n\nplaceId matches:\n - none: " + none + "\n - single: " + single + "\n - multiple: " + multiple + "\n\n" + results);
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog) {
        tvEventsTest.setText("");
        networkUtils.loadEventsFromAPI(eventsDB, placesDB);
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog) {
        // TODO: display toast?
    }
}
