package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.TabType;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
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

        View titleLine = findViewById(R.id.line_TitleText);
        View locationLine = findViewById(R.id.line_LocationText);
        View descripLine = findViewById(R.id.line_descripText);
        View startDateLine = findViewById(R.id.line_startDateText);
        View endDateLine = findViewById(R.id.line_endDateText);
        View categoryLine = findViewById(R.id.line_categoriesText);

        ImageView locationIcon = (ImageView) findViewById(R.id.location_icon);
        ImageView descripIcon = (ImageView) findViewById(R.id.description_icon);
        ImageView categoryIcon = (ImageView) findViewById(R.id.categories_icon);


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
//        for(int i = 0; i < categories.length(); i++) {
//            categoriesTextView.setTextColor(Color.parseColor("#" + e.getCategories().get(i).getColor()));
//        }
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
        if (e.getLocation() != null) {
            locationIcon.setVisibility(View.VISIBLE);
            locationTextView.setVisibility(View.VISIBLE);
        }
        if (e.getDescription() !=null) {
            descripLine.setVisibility(View.VISIBLE);
            descripIcon.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
        }
        if (e.getCategories() !=null) {
            categoryLine.setVisibility(View.VISIBLE);
            categoryIcon.setVisibility(View.VISIBLE);
            categoriesTextView.setVisibility(View.VISIBLE);
        }
        if (e.getStartDate() != null) {
            startDateLine.setVisibility(View.VISIBLE);
            startDateTextView.setVisibility(View.VISIBLE);
        }
        if (e.getEndDate() != null) {
            endDateLine.setVisibility(View.VISIBLE);
            endDateTextView.setVisibility(View.VISIBLE);
        }

        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapActivityIntent = new Intent(EventDetailsActivity.this, MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, p.getId());
                startActivity(mapActivityIntent);
            }
        });

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: show confirmation dialog "Add event to calendar?"
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.Events.TITLE, finalE.getTitle())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, finalE.getLocation())
                        .putExtra(CalendarContract.Events.DESCRIPTION, finalE.getDescription())
                        .putExtra(CalendarContract.Events.ALL_DAY, finalE.getAllDay());
                if (finalE.getStartDate() != null) {
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, finalE.getStartDate().getMillis());
                }
                if (finalE.getEndDate() != null) {
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, finalE.getEndDate().getMillis());
                }
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
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