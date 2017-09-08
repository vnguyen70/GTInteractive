package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button mapButton;
    Button buildingDetailButton;
    Button internalMapButton;
    Button searchButton;
    Button buildingsTestActivityButton;
    Button eventsTestActivityButton;
    Button diningsTestActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapButton = (Button) findViewById(R.id.mapButton);
        buildingDetailButton = (Button) findViewById(R.id.buildingDetailButton);
        internalMapButton = (Button) findViewById(R.id.internalMapButton);
        searchButton = (Button) findViewById(R.id.searchButton);
        buildingsTestActivityButton = (Button) findViewById(R.id.buildingsTestActivityButton);
        eventsTestActivityButton = (Button) findViewById(R.id.eventsTestActivityButton);
        diningsTestActivityButton = (Button) findViewById(R.id.diningsTestActivityButton);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(mapIntent);
            }
        });
        buildingDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingDetailIntent = new Intent(MainActivity.this, buildingDetail_info.class);
                startActivity(buildingDetailIntent);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MainActivity.this, BuildingSearchActivity.class);
                startActivity(searchIntent);
            }
        });
        buildingsTestActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingsTestActivityIntent = new Intent(MainActivity.this, BuildingsTestActivity.class);
                startActivity(buildingsTestActivityIntent);
            }
        });
        eventsTestActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventsTestActivityIntent = new Intent(MainActivity.this, EventsTestActivity.class);
                startActivity(eventsTestActivityIntent);
            }
        });
        diningsTestActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent diningsTestActivityIntent = new Intent(MainActivity.this, DiningsTestActivity.class);
                startActivity(diningsTestActivityIntent);
            }
        });
    }


}
