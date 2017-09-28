package com.example.vi_tu.gtinteractive;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

public class DiningDetailsActivity extends AppCompatActivity {

    private DiningPersistence diningsDB;

    private int objectId;
    private static Dining d; // dining to display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_details);

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        diningsDB = new DiningPersistence(db);

        // dining to display
        d = Dining.DEFAULT_DINING;
        objectId = getIntent().getIntExtra(Arguments.OBJECT_ID, -1);
        Dining temp = diningsDB.get(objectId);
        if (temp != null) {
            d = temp;
        }

        // UI
        TextView idTextView = (TextView) findViewById(R.id.diningIdText);
        TextView nameTextView = (TextView) findViewById(R.id.nameText);
        TextView hoursTextView = (TextView) findViewById(R.id.hoursText);
        TextView locationTextView = (TextView) findViewById(R.id.locationText);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionText);
        TextView menuURLTextView = (TextView) findViewById(R.id.menuURLText);
        Button showInMapButton = (Button) findViewById(R.id.showInMapButton);

        idTextView.setText(String.valueOf(d.getId()));
        nameTextView.setText(String.valueOf(d.getName()));
//        hoursTextView.setText(String.valueOf(d.get));
        locationTextView.setText(String.valueOf(d.getLocationDetails()));
        descriptionTextView.setText(String.valueOf(d.getDescription()));
        menuURLTextView.setText(String.valueOf(d.getMenuLinkURL()));

        showInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent mapActivityIntent = new Intent(context, MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.DINING);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, d.getId());
                context.startActivity(mapActivityIntent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}