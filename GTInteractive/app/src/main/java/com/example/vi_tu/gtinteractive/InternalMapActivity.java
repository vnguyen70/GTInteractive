package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;

import static com.example.vi_tu.gtinteractive.constants.Arguments.OBJECT_ID;
import static com.example.vi_tu.gtinteractive.utilities.NumberUtils.ordinal;

/*
 * Small bug found that does not allow the user to go back. App crashes. Unable to start Place
 * details activity due to Illegal arg exception. path must not be empty. places detail Act #98
 * file from 10/20/17/ 2:14pm
 */


public class InternalMapActivity extends AppCompatActivity {

    PlacePersistence placesDB;

    int objectID;
    Place place;

    TextView title;
    ImageView blueprint;

    int MAX_NUM_FLOORS = 5; // TODO: hard-coded 5 floors max - change to 10 floors?
    int numFloors;
    int currFloor;
    boolean buttonsShown = false;

    FloatingActionButton[] floorButtons = new FloatingActionButton[MAX_NUM_FLOORS];
    TextView[] floorButtonLabels = new TextView[MAX_NUM_FLOORS];
    FloatingActionButton floorSelectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_bulding);

        placesDB = new PlacePersistence(new PersistenceHelper(this).getWritableDatabase());
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        int currObjectID = objectID;
        objectID = getIntent().getIntExtra(OBJECT_ID, -1);
        if (currObjectID != objectID) { // TODO: only use this pattern for internal map activity, since internal map activitiy content (e.g. blueprints) are never changed/refreshed?
            // perform initialization
            place = placesDB.get(objectID);

//            numFloors = place.getNumFloors(); // TODO
            numFloors = MAX_NUM_FLOORS;

            title = (TextView) findViewById(R.id.text_floor_banner);
            blueprint = (ImageView) findViewById(R.id.image_blueprint);

            floorButtons = new FloatingActionButton[MAX_NUM_FLOORS];
            floorButtons[0] = (FloatingActionButton) findViewById(R.id.button_floor_1);
            floorButtons[1] = (FloatingActionButton) findViewById(R.id.button_floor_2);
            floorButtons[2] = (FloatingActionButton) findViewById(R.id.button_floor_3);
            floorButtons[3] = (FloatingActionButton) findViewById(R.id.button_floor_4);
            floorButtons[4] = (FloatingActionButton) findViewById(R.id.button_floor_5);

            floorButtonLabels = new TextView[MAX_NUM_FLOORS];
            floorButtonLabels[0] = (TextView) findViewById(R.id.text_floor_1_label);
            floorButtonLabels[1] = (TextView) findViewById(R.id.text_floor_2_label);
            floorButtonLabels[2] = (TextView) findViewById(R.id.text_floor_3_label);
            floorButtonLabels[3] = (TextView) findViewById(R.id.text_floor_4_label);
            floorButtonLabels[4] = (TextView) findViewById(R.id.text_floor_5_label);

            floorSelectButton = (FloatingActionButton) findViewById(R.id.button_floor_expand);

            for (int i = 0; i < numFloors; i++) {
                final int floorNum = i + 1;
                floorButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCurrFloor(floorNum);
                    }
                });
            }

            floorSelectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (buttonsShown) {
                        hideButtons();
                    } else {
                        showButtons();
                    }
                }
            });

            setCurrFloor(1);
        }
    }

    private int getImageResourceID(Place p, int floor) { // TODO: refactor
        final String RESOURCE_TYPE = "drawable";
        String prefix = p.getPlaceId().replaceAll("-", "_");
        String resourceName = prefix + "_" + String.valueOf(floor);
        return getResources().getIdentifier(resourceName, RESOURCE_TYPE, getPackageName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this,PlaceDetailsActivity.class);
                intent.putExtra(OBJECT_ID, objectID);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void setCurrFloor(int floorNum) {
        currFloor = floorNum;
        title.setText(ordinal(floorNum) + " Floor"); // TODO
        // TODO: set text of floating action button to floor number
        blueprint.setImageResource(getImageResourceID(place, floorNum));
        hideButtons(); // TODO
    }

    private void hideButtons() {
        for (int i = 0; i < numFloors; i ++) {
            floorButtons[i].setVisibility(View.GONE);
            floorButtonLabels[i].setVisibility(View.GONE);
        }
        buttonsShown = false;
    }

    private void showButtons() {
        for (int i = 0; i < numFloors; i ++) {
            floorButtons[i].setVisibility(View.VISIBLE);
            floorButtonLabels[i].setVisibility(View.VISIBLE);
        }
        buttonsShown = true;
    }
}

