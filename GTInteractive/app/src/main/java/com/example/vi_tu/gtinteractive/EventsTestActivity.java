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
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import org.joda.time.DateTime;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.NetworkUtils.loadEventsFromRSSFeed;

public class EventsTestActivity extends AppCompatActivity {

    private TextView tvEventsTest;

    private EventPersistence eventsDB;
    private BuildingPersistence buildingsDB;

    public static final String REQUEST_TAG = "EventsTestActivity";
    private RequestQueue queue;

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
        buildingsDB = new BuildingPersistence(db);

        queue = Volley.newRequestQueue(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        long nowMS = DateTime.now().getMillis();
        long eventsCacheExpiredMS = sharedPreferences.getLong("eventsCacheExpiredMS", 0);

        if (nowMS >= eventsCacheExpiredMS) {
            loadEventsFromRSSFeed(eventsDB, buildingsDB, queue);
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
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_display:
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
                return true;
            case R.id.action_reload:
                tvEventsTest.setText("");
                loadEventsFromRSSFeed(eventsDB, buildingsDB, queue);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
