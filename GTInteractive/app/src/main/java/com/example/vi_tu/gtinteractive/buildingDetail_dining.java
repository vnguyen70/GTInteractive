package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.example.vi_tu.gtinteractive.R.color.colorAccent;

public class buildingDetail_dining extends AppCompatActivity {
    Button eventsButton;
    Button infoButton;
    Button diningButton;

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
    }
}
