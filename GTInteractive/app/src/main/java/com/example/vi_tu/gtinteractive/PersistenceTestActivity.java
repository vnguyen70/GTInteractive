package com.example.vi_tu.gtinteractive;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import java.util.Calendar;
import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.NetworkUtils.loadBuildingsFromAPI;
import static com.example.vi_tu.gtinteractive.utilities.NetworkUtils.loadEventsFromRSSFeed;

/**
 * Created by kaliq on 9/5/2017.
 */

public class PersistenceTestActivity extends AppCompatActivity {

    private Button displayBuildingsButton;
    private Button displayEventsButton;
    private Button reloadBuildingsButton;
    private Button reloadEventsButton;
    private TextView resultsTextView;

    private SQLiteDatabase db;
    private BuildingPersistence buildingsDB;
    private EventPersistence eventsDB;

    public static final String REQUEST_TAG = "PersistenceTestActivity";
    private RequestQueue queue;

    private static final long BUILDINGS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day
    private static final long EVENTS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persistence_test);

        // Setup database and networking

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);
        eventsDB = new EventPersistence(db);

        queue = Volley.newRequestQueue(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        long nowMS = Calendar.getInstance().getTime().getTime();
        long buildingsCacheExpiredMS = sharedPreferences.getLong("buildingsCacheExpiredMS", 0);
        long eventsCacheExpiredMS = sharedPreferences.getLong("eventsCacheExpiredMS", 0);

        if (nowMS >= buildingsCacheExpiredMS) {
            loadBuildingsFromAPI(buildingsDB, queue);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("buildingsCacheExpiredMS", nowMS + BUILDINGS_CACHE_DURATION_MS);
            editor.apply();
        } else {
            Log.d("NETWORK_TEST", "buildings already loaded");
        }
        if (nowMS >= eventsCacheExpiredMS) {
            loadEventsFromRSSFeed(eventsDB, buildingsDB, queue);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("eventsCacheExpiredMS", nowMS + EVENTS_CACHE_DURATION_MS);
            editor.apply();
        } else {
            Log.d("NETWORK_TEST", "events already loaded");
        }

        // Setup listeners for UI elements

        displayBuildingsButton = (Button) this.findViewById(R.id.display_buildings_button);
        displayEventsButton = (Button) this.findViewById(R.id.display_events_button);
        reloadBuildingsButton = (Button) this.findViewById(R.id.reload_buildings_button);
        reloadEventsButton = (Button) this.findViewById(R.id.reload_events_button);
        resultsTextView = (TextView) this.findViewById(R.id.tv_results);

        displayBuildingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Building> bList = buildingsDB.getAll();
                String results = "";
                for (Building b : bList) {
                    results += b.getBuildingId() + " - " + b.getNameTokens() + " - " + b.getAddressTokens() + "\n";
//                    results += b.getLatitude() + ", " + b.getLongitude() + "\n";
                }
                resultsTextView.setText("" + bList.size() + " buildings found:\n\n" + results);
            }
        });

        displayEventsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Event> eList = eventsDB.getAll();
                String results = "";
                int none = 0;
                int single = 0;
                int multiple = 0;
                for (Event e : eList) {
//                    results += e.getStartDate() + " to " + e.getEndDate() + "\n\n";

                    results += e.getLocation() + "\n-- " + e.getBuildingId() + " --\n\n";
                    if (e.getBuildingId().equals("NONE")) {
                        none++;
                    } else if (e.getBuildingId().equals("MULTIPLE")) {
                        multiple++;
                    } else {
                        single++;
                    }
                }
                resultsTextView.setText("" + eList.size() + " events found:\n\nbuildingId matches:\n - none: " + none + "\n - single: " + single + "\n - multiple: " + multiple + "\n\n" + results);
            }
        });

        reloadBuildingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                resultsTextView.setText("");
                loadBuildingsFromAPI(buildingsDB, queue);
            }
        });

        reloadEventsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                resultsTextView.setText("");
                loadEventsFromRSSFeed(eventsDB, buildingsDB, queue);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(REQUEST_TAG);
        }
    }

}
