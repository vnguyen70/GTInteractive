package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainTemp extends AppCompatActivity {
    Button mapButton;
    Button buildingDetailButton;
    Button internalMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapButton = (Button) findViewById(R.id.mapButton);
        buildingDetailButton = (Button) findViewById(R.id.buildingDetailButton);
        internalMapButton = (Button) findViewById(R.id.internalMapButton);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(MainTemp.this, MapActivity.class);
                startActivity(mapIntent);
            }
        });

        buildingDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("TRANSITION", "Building Info page hit");
                Intent buildingDetailIntent = new Intent(MainTemp.this, buildingDetail_info.class);
                startActivity(buildingDetailIntent);
            }
        });
    }


}
