package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class BuildingDetailsActivity extends AppCompatActivity {

    Button internalMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail_info);

        internalMapButton = (Button) findViewById(R.id.internalMapButton);

        internalMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent internalBuildingActivityIntent = new Intent(BuildingDetailsActivity.this, InternalBuildingActivity.class);
                startActivity(internalBuildingActivityIntent);
            }
        });



    }
}
