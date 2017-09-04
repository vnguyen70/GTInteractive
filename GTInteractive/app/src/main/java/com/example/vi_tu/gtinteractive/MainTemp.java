package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainTemp extends AppCompatActivity {
    Button mapButton;
    Button buildingDetailButton;
    Button internalMapButton;
    Button searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);

        mapButton = (Button) findViewById(R.id.mapButton);
        buildingDetailButton = (Button) findViewById(R.id.buildingDetailButton);
        internalMapButton = (Button) findViewById(R.id.internalMapButton);
        // can't rename for some reason
        searchButton = (Button) findViewById(R.id.searchButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(MainTemp.this, MainMapActivity.class);
                startActivity(mapIntent);
            }
        });

        buildingDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buildingDetailIntent = new Intent(MainTemp.this, buildingDetail.class);
                startActivity(buildingDetailIntent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MainTemp.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });
    }


}
