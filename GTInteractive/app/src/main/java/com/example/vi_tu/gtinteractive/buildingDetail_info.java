package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    TextView phoneNumberText;
    TextView hoursText;
    TextView addressText;
    TextView buildingNameText;

    ImageView buildingPicture;
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

        buildingNameText = (TextView) findViewById(R.id.nameTextView);
        phoneNumberText = (TextView) findViewById(R.id.phoneNumberText);
        hoursText = (TextView) findViewById(R.id.hoursText);
        addressText = (TextView) findViewById(R.id.addressText);

        buildingPicture = (ImageView) findViewById(R.id.buildingImageView);

        // Database instantiation
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        db = dbHelper.getWritableDatabase();
        buildingDB = new BuildingPersistence(db);

        // find Building by id from intent
        Intent intent = getIntent();
        Building currBuilding = buildingDB.findByBuildingId(intent.getStringExtra("buildingId"));

        // Setting elements with proper data
        String name = currBuilding.getName();
//        String altName = currBuilding.getAltNames();
//        String hours = currBuilding.getTimeOpen().toString("HH:mm") + " - " + currBuilding.getTimeClose().toString();
        String address = currBuilding.getAddress();
        String phoneNumber = currBuilding.getPhoneNum();
        String buildingPictureLink = currBuilding.getLink();
        buildingNameText.setText(name);
        phoneNumberText.setText(phoneNumber);
        addressText.setText(address);
//        buildingPicture.setImageURI(Uri.parse(buildingPictureLink));
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
