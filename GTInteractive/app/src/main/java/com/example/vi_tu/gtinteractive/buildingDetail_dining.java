package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.temp.DiningAdapter;
import com.example.vi_tu.gtinteractive.temp.EventAdapter;

import java.util.List;

import static com.example.vi_tu.gtinteractive.R.color.colorAccent;

public class buildingDetail_dining extends AppCompatActivity {
    Button eventsButton;
    Button infoButton;
    Button diningButton;

    private RecyclerView diningRecycler;
    private DiningAdapter diningAdapter;
    private RecyclerView.LayoutManager diningLayoutManager;

    private SQLiteDatabase db;
    private DiningPersistence diningDB;
    List<Dining> diningList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail_dining);

        eventsButton = (Button) findViewById(R.id.eventsButton);
        infoButton = (Button) findViewById(R.id.infoButton);
        diningButton = (Button) findViewById(R.id.diningButton);

        diningButton.setBackgroundColor(getResources().getColor(colorAccent));

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingDetailIntent = new Intent(buildingDetail_dining.this,
                        buildingDetail_events.class);
                startActivity(buildingDetailIntent);
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingDetailIntent = new Intent(buildingDetail_dining.this,
                        buildingDetail_info.class);
                startActivity(buildingDetailIntent);
            }
        });

        // Database instantiation
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        db = dbHelper.getWritableDatabase();
        diningDB = new DiningPersistence(db);

        diningList = diningDB.getAll();

        // Recycle view creation
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        diningRecycler = (RecyclerView) findViewById(R.id.recyclerView);

        diningRecycler.setLayoutManager(layoutManager);


        diningAdapter = new DiningAdapter();
        diningAdapter.setData(diningList);

        diningRecycler.setAdapter(diningAdapter);
    }
}
