package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.squareup.picasso.Picasso;

public class EventDetailsActivity extends AppCompatActivity {

    private EventPersistence eventsDB;
    private BuildingPersistence buildingsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eventsDB = new EventPersistence(db);
        buildingsDB = new BuildingPersistence(db);

        // event to display
        Event e = Event.DUMMY;
        int objectId = getIntent().getIntExtra(Arguments.OBJECT_ID, -1);
        Event temp = eventsDB.get(objectId);
        if (temp != null) {
            e = temp;
        }

        // Sets up event image TODO: Need to verify URLs for events (currently displays nothing)
        ImageView eventImageView = (ImageView) findViewById(R.id.eventImageView);
        Picasso.with(this).load(e.getImageURL()).fit().into(eventImageView);

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
        TextView buildingIdTextView = (TextView) findViewById(R.id.buildingIdText);
        Button openBuildingDetailsButton = (Button) findViewById(R.id.openBuildingDetailsButton);

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
        buildingIdTextView.setText(e.getBuildingId());

        final Building b = buildingsDB.findByBuildingId(e.getBuildingId());
        if (b != null) {
            openBuildingDetailsButton.setVisibility(View.VISIBLE);
            openBuildingDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent buildingDetailIntent = new Intent(EventDetailsActivity.this, BuildingDetailsActivity.class);
                    buildingDetailIntent.putExtra(Arguments.OBJECT_ID, b.getId());
                    buildingDetailIntent.putExtra(Arguments.DEFAULT_TAB, TabType.INFO);
                    startActivity(buildingDetailIntent);
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
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, b.getId());
                startActivity(mapActivityIntent);
            }
        });

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent calendarIntent = new Intent() ;
                calendarIntent.putExtra("beginTime", finalE.getStartDate());
                calendarIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                calendarIntent.setClassName("com.android.calendar","com.android.calendar.AgendaActivity");
                startActivity(calendarIntent);
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