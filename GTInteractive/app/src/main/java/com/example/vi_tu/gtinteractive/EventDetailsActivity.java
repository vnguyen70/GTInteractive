package com.example.vi_tu.gtinteractive;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

public class EventDetailsActivity extends AppCompatActivity {

    private EventPersistence eventsDB;

    private int objectId;
    private static Event e; // event to display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eventsDB = new EventPersistence(db);

        // dining to display
        e = Event.DUMMY;
        objectId = getIntent().getIntExtra(Arguments.OBJECT_ID, -1);
        Event temp = eventsDB.get(objectId);
        if (temp != null) {
            e = temp;
        }

        // UI
        TextView idTextView = (TextView) findViewById(R.id.eventIdText);
        TextView titleTextView = (TextView) findViewById(R.id.titleText);
        TextView startDateTextView = (TextView) findViewById(R.id.startDateText);
        TextView endDateTextView = (TextView) findViewById(R.id.endDateText);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionText);
        TextView locationTextView = (TextView) findViewById(R.id.locationText);
        Button showInMapButton = (Button) findViewById(R.id.showInMapButton);

        idTextView.setText(String.valueOf(e.getId()));
        titleTextView.setText(String.valueOf(e.getTitle()));
        startDateTextView.setText(String.valueOf(e.getStartDate()));
        endDateTextView.setText(String.valueOf(e.getEndDate()));
        descriptionTextView.setText(String.valueOf(e.getDescription()));
        locationTextView.setText(String.valueOf(e.getLocation()));

        showInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent mapActivityIntent = new Intent(context, MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.EVENT);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, e.getId());
                context.startActivity(mapActivityIntent);
            }
        });

    }
}