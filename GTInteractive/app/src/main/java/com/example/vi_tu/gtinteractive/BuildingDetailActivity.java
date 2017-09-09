package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.temp.DiningAdapter;
import com.example.vi_tu.gtinteractive.temp.EventAdapter;

import java.util.List;

import static android.R.drawable.btn_default;
import static com.example.vi_tu.gtinteractive.R.color.colorAccent;

public class BuildingDetailActivity extends AppCompatActivity {

    private BuildingPersistence buildingsDB;
    private DiningPersistence diningsDB;
    private EventPersistence eventsDB;

    private String buildingId;
    private String currTab;

    TextView buildingNameText;
    Button internalMapButton;
    ImageView buildingPicture;
    Button infoButton;
    Button diningButton;
    Button eventsButton;

    private ScrollView infoView;
    TextView hoursText;
    TextView addressText;
    TextView phoneNumberText;

    private RecyclerView eventsView;
    private EventAdapter eventsAdapter;
    private List<Event> eList;

    private RecyclerView diningView;
    private DiningAdapter diningAdapter;
    private List<Dining> dList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);
        diningsDB = new DiningPersistence(db);
        eventsDB = new EventPersistence(db);

        // building to display
        buildingId = getIntent().getStringExtra("buildingId");
        Building b = buildingsDB.findByBuildingId(buildingId);
        if (b == null) {
            buildingId = "166"; // CULC by default TODO
            b = buildingsDB.findByBuildingId(buildingId);
        }
//        dList = diningsDB.findByBuildingId(buildingId); // TODO: buildingID is not consistent between buildings API and dining API gg...
//        eList = eventsDB.findByBuildingId(buildingId); // TODO: buildingID is not consistent between buildings API and dining API gg...
        dList = diningsDB.getAll();
        eList = eventsDB.getAll();

        // UI
        buildingNameText = (TextView) findViewById(R.id.nameTextView);
        internalMapButton = (Button) findViewById(R.id.internalMapButton);
        infoButton = (Button) findViewById(R.id.infoButton);
        buildingPicture = (ImageView) findViewById(R.id.buildingImageView);
        diningButton = (Button) findViewById(R.id.diningButton);
        eventsButton = (Button) findViewById(R.id.eventsButton);

        // info tab
        infoView = (ScrollView) findViewById(R.id.infoView);
        hoursText = (TextView) findViewById(R.id.hoursText);
        addressText = (TextView) findViewById(R.id.addressText);
        phoneNumberText = (TextView) findViewById(R.id.phoneNumberText);

        // dining tab
        diningView = (RecyclerView) findViewById(R.id.diningsRecyclerView);
        diningView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        diningView.setAdapter(new DiningAdapter(dList));

        // events tab
        eventsView = (RecyclerView) findViewById(R.id.eventsRecyclerView);
        eventsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        eventsView.setAdapter(new EventAdapter(eList));

        resetTabs();

        buildingNameText.setText(b.getName());
        hoursText.setText(b.getTimeOpen() + " - " + b.getTimeClose()); // TODO: null check
        addressText.setText(b.getAddress());
        phoneNumberText.setText(b.getPhoneNum());

        // on-click listeners

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoTab();
            }
        });
        diningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiningTab();
            }
        });
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventsTab();
            }
        });
        internalMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent internalMapActivityIntent = new Intent(BuildingDetailActivity.this, InternalMapActivity.class);
                internalMapActivityIntent.putExtra("buildingId", buildingId);
                startActivity(internalMapActivityIntent);
            }
        });

        currTab = "";
        String defaultTab = getIntent().getStringExtra("defaultTab");
        if (defaultTab == null) {
            showInfoTab();
        } else if (defaultTab.equals("dining")) {
            showDiningTab();
        } else if (defaultTab.equals("events")) {
            showEventsTab();
        } else {
            showInfoTab(); // TODO: show info tab by default
        }

    }

    private void showInfoTab() {
        if (currTab.equals("info")) {
            return;
        }
        resetTabs();
        infoButton.setBackgroundColor(getResources().getColor(colorAccent));
        infoView.setVisibility(View.VISIBLE);
        currTab = "info";
    }

    private void showDiningTab() {
        if (currTab.equals("dining")) {
            return;
        }
        resetTabs();
        diningButton.setBackgroundColor(getResources().getColor(colorAccent));
        diningView.setVisibility(View.VISIBLE);
        currTab = "dining";
    }

    private void showEventsTab() {
        if (currTab.equals("events")) {
            return;
        }
        resetTabs();
        eventsButton.setBackgroundColor(getResources().getColor(colorAccent));
        eventsView.setVisibility(View.VISIBLE);
        currTab = "events";
    }

    private void resetTabs() {
        infoView.setVisibility(View.GONE);
        diningView.setVisibility(View.GONE);
        eventsView.setVisibility(View.GONE);
        infoButton.setBackgroundResource(btn_default);
        diningButton.setBackgroundResource(btn_default);
        eventsButton.setBackgroundResource(btn_default);
    }
}
