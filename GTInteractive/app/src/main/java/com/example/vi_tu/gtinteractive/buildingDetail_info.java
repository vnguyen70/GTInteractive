package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import org.w3c.dom.Text;

import static com.example.vi_tu.gtinteractive.R.color.colorAccent;

public class buildingDetail_info extends AppCompatActivity {
    Button eventsButton;
    Button infoButton;
    Button diningButton;

    TextView altNamesText;
    TextView hoursText;
    TextView addressText;

    private SQLiteDatabase db;
    private BuildingPersistence buildingDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail_info);

        // Instantiating on-screen elements
        eventsButton = (Button) findViewById(R.id.eventsButton);
        infoButton = (Button) findViewById(R.id.infoButton);
        diningButton = (Button) findViewById(R.id.diningButton);

        altNamesText = (TextView) findViewById(R.id.altNamesText);
        hoursText = (TextView) findViewById(R.id.hoursText);
        addressText = (TextView) findViewById(R.id.addressText);

        // Database instantiation
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        db = dbHelper.getWritableDatabase();
        buildingDB = new BuildingPersistence(db);

        // Currently hardcoded to find Clough (???)
        Building currBuilding = buildingDB.findByBuildingId("166");

        // Setting elements with proper data
        String altName = currBuilding.getAltNames();
        String hours = currBuilding.getTimeOpen() + " - " + currBuilding.getTimeClose();
        String address = currBuilding.getAddress();
        altNamesText.setText(altName);
        hoursText.setText(hours);
        addressText.setText(address);

        infoButton.setBackgroundColor(getResources().getColor(colorAccent));

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingDetailIntent = new Intent(buildingDetail_info.this,
                        buildingDetail_events.class);
                startActivity(buildingDetailIntent);
            }
        });
        diningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingDetailIntent = new Intent(buildingDetail_info.this,
                        buildingDetail_dining.class);
                startActivity(buildingDetailIntent);
            }
        });
    }
}
