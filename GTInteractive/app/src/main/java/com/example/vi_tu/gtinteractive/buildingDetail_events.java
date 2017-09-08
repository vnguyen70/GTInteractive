package com.example.vi_tu.gtinteractive;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.vi_tu.gtinteractive.R.color.colorAccent;

public class buildingDetail_events extends AppCompatActivity {
    Button eventsButton;
    Button infoButton;
    Button diningButton;

    private RecyclerView eventsRecycler;
    private RecyclerView.Adapter eventsAdapter;
    private RecyclerView.LayoutManager eventsLayoutManager;

    String[] placeData = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail_events);

        eventsButton = (Button) findViewById(R.id.eventsButton);
        infoButton = (Button) findViewById(R.id.infoButton);
        diningButton = (Button) findViewById(R.id.diningButton);


        eventsButton.setBackgroundColor(getResources().getColor(colorAccent));

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

        placeData[0] = "test0";
        placeData[1] = "test1";
        placeData[2] = "test2";

        eventsRecycler = (RecyclerView) findViewById(R.id.recyclerView);

        eventsRecycler.setHasFixedSize(true);

        eventsLayoutManager = new LinearLayoutManager(this);
        eventsRecycler.setLayoutManager(eventsLayoutManager);

        eventsAdapter = new EventAdapter();
        eventsRecycler.setAdapter(eventsAdapter);

    }





}
