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
import android.widget.Toast;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.rest.BaseDAO;
import com.example.vi_tu.gtinteractive.rest.BuildingDAO;
import com.example.vi_tu.gtinteractive.rest.EventDAO;
import com.example.vi_tu.gtinteractive.utilities.NetworkErrorDialogFragment;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtilsTemp;

import org.joda.time.DateTime;

import java.util.List;

import static com.example.vi_tu.gtinteractive.constants.Constants.BUILDINGS_CACHE_EXPIRATION_MS_KEY;
import static com.example.vi_tu.gtinteractive.constants.Constants.EVENTS_CACHE_EXPIRATION_MS_KEY;

public class EventsTestActivity extends AppCompatActivity implements NetworkErrorDialogFragment.NetworkErrorDialogListener {

    private TextView tvEventsTest;

    private EventPersistence eventsDB;
    private BuildingPersistence buildingsDB;

    private NetworkUtilsTemp networkUtilsTemp;

    public static final String REQUEST_TAG = "EventsTestActivity";

    private static final long EVENTS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day

    SharedPreferences prefs;

    EventDAO eventDAO;

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
        buildingsDB = new BuildingPersistence(db);

        networkUtilsTemp = new NetworkUtilsTemp(getApplicationContext(), getFragmentManager());

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        eventDAO = new EventDAO(this, new BaseDAO.Listener<Event>() {
            @Override
            public void onDAOError(BaseDAO.Error error) {
                Toast.makeText(EventsTestActivity.this, "EventDAO error", Toast.LENGTH_SHORT).show(); // TODO: error handling
            }
            @Override
            public void onListReady(List<Event> events) {
                displayResults();
                Toast.makeText(EventsTestActivity.this, "Events Loaded", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onObjectReady(Event event) {}
        });

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
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_display:
                displayResults();
                return true;
            case R.id.action_reload:
                tvEventsTest.setText("");
                eventDAO.getAllAsync();
//                networkUtilsTemp.loadEventsFromAPI(eventsDB, buildingsDB);
                return true;
            case R.id.action_clear:
                eventsDB.deleteAll();
                prefs.edit().putLong(EVENTS_CACHE_EXPIRATION_MS_KEY, 0).apply();
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
            results += e.getLocation() + "\n-- " + e.getBuildingId() + " --\n\n";
            if (e.getBuildingId().equals("NONE")) {
                none++;
            } else if (e.getBuildingId().startsWith("MANY", 0)) {
                multiple++;
            } else {
                single++;
            }
        }
        tvEventsTest.setText("" + eList.size() + " events found:\n\nbuildingId matches:\n - none: " + none + "\n - single: " + single + "\n - multiple: " + multiple + "\n\n" + results);
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog) {
        tvEventsTest.setText("");
        networkUtilsTemp.loadEventsFromAPI(eventsDB, buildingsDB);
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog) {
        // TODO: display toast?
    }
}
