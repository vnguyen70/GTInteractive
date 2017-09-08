package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.temp.EventAdapter;

import java.util.List;

import static com.example.vi_tu.gtinteractive.R.color.colorAccent;

public class buildingDetail_events extends AppCompatActivity {
    Button eventsButton;
    Button infoButton;
    Button diningButton;

    private RecyclerView eventsRecycler;
    private EventAdapter eventsAdapter;
    private RecyclerView.LayoutManager eventsLayoutManager;

    private SQLiteDatabase db;
    private EventPersistence eventsDB;
    List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail_events);

        // Instantiating top banner buttons
        eventsButton = (Button) findViewById(R.id.eventsButton);
        infoButton = (Button) findViewById(R.id.infoButton);
        diningButton = (Button) findViewById(R.id.diningButton);

        // Changes styling of currently selected button
        eventsButton.setBackgroundColor(getResources().getColor(colorAccent));

        // Click listeners
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingDetailIntent = new Intent(buildingDetail_events.this,
                        buildingDetail_info.class);
                startActivity(buildingDetailIntent);
            }
        });
        diningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingDetailIntent = new Intent(buildingDetail_events.this,
                        buildingDetail_dining.class);
                startActivity(buildingDetailIntent);
            }
        });

        // Database instantiation
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        db = dbHelper.getWritableDatabase();
        eventsDB = new EventPersistence(db);

        eventList = eventsDB.getAll();

        // Recycle view creation
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        eventsRecycler = (RecyclerView) findViewById(R.id.recyclerView);

        eventsRecycler.setLayoutManager(layoutManager);


        eventsAdapter = new EventAdapter();
        eventsAdapter.setData(eventList);

        eventsRecycler.setAdapter(eventsAdapter);

    }
}
