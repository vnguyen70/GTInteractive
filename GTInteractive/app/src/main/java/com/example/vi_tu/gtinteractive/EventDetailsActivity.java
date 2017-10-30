package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.TabType;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.squareup.picasso.Picasso;

public class EventDetailsActivity extends AppCompatActivity {

    private EventPersistence eventsDB;
    private PlacePersistence placesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eventsDB = new EventPersistence(db);
        placesDB = new PlacePersistence(db);

        // event to display
        Event e = Event.DUMMY;
        int objectId = getIntent().getIntExtra(Arguments.OBJECT_ID, -1);
        Event temp = eventsDB.get(objectId);
        if (temp != null) {
            e = temp;
        }

        // Sets up event image TODO: Need to verify URLs for events (currently displays nothing)
        ImageView eventImageView = (ImageView) findViewById(R.id.eventImageView);
        if (!e.getImageURL().isEmpty()) {
            Picasso.with(this).load(e.getImageURL()).fit().into(eventImageView);
        }

        // UI
        TextView idTextView = (TextView) findViewById(R.id.idText);
        TextView eventIdTextView = (TextView) findViewById(R.id.eventIdText);
        TextView titleTextView = (TextView) findViewById(R.id.titleText);
        TextView locationTextView = (TextView) findViewById(R.id.locationText);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionText);
        TextView imageURLTextView = (TextView) findViewById(R.id.imageURLText);
        TextView startDateTextView = (TextView) findViewById(R.id.startDateText);
        TextView endDateTextView = (TextView) findViewById(R.id.endDateText);
        TextView allDayTextView = (TextView) findViewById(R.id.allDayText);
        TextView recurringTextView = (TextView) findViewById(R.id.recurringText);
        TextView categoriesTextView = (TextView) findViewById(R.id.categoriesText);
        TextView placeIdTextView = (TextView) findViewById(R.id.placeIdText);
        Button openPlaceDetailsButton = (Button) findViewById(R.id.openPlaceDetailsButton);
        Button showInMapButton = (Button) findViewById(R.id.showInMapButton);

        String categories = "";
        for (Event.Category c : e.getCategories()) {
            categories += c.getLabel() + " ";
        }

//        idTextView.setText(String.valueOf(e.getId()));
        eventIdTextView.setText(String.valueOf(e.getEventId()));
        titleTextView.setText(e.getTitle());
        locationTextView.setText(e.getLocation());
        descriptionTextView.setText(e.getDescription());
        imageURLTextView.setText(e.getImageURL());
        startDateTextView.setText(e.getStartDate() != null ? e.getStartDate().toString("MM/dd/yy hh:mm a") : "n/a");
        endDateTextView.setText(e.getEndDate() != null ? e.getEndDate().toString("MM/dd/yy hh:mm a") : "n/a");
        allDayTextView.setText(String.valueOf(e.getAllDay()));
        recurringTextView.setText(String.valueOf(e.getRecurring()));
        categoriesTextView.setText(categories);
        placeIdTextView.setText(e.getPlaceId());

        final Place p = placesDB.findByPlaceId(e.getPlaceId());
        if (p != null) {
            openPlaceDetailsButton.setVisibility(View.VISIBLE);
            openPlaceDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent placeDetailIntent = new Intent(EventDetailsActivity.this, PlaceDetailsActivity.class);
                    placeDetailIntent.putExtra(Arguments.OBJECT_ID, p.getId());
                    placeDetailIntent.putExtra(Arguments.DEFAULT_TAB, TabType.INFO);
                    startActivity(placeDetailIntent);
                }
            });
        }

        final Event finalE = e;
        showInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapActivityIntent = new Intent(EventDetailsActivity.this, MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.EVENT);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, finalE.getId());
                startActivity(mapActivityIntent);
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