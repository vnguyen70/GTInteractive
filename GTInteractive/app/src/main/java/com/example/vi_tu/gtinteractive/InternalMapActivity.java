package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import static com.example.vi_tu.gtinteractive.constants.Arguments.OBJECT_ID;

/*
 * Small bug found that does not allow the user to go back. App crashes. Unable to start Building
 * details activity due to Illegal arg exception. path must not be empty. buildings detail Act #98
 * file from 10/20/17/ 2:14pm
 */


public class InternalMapActivity extends AppCompatActivity {

    FloatingActionButton floorExpandButton;
    FloatingActionButton floor1Button;
    FloatingActionButton floor2Button;
    FloatingActionButton floor3Button;
    FloatingActionButton floor4Button;
    FloatingActionButton floor5Button;
    ImageView blueprint;
    TextView title;
    TextView floor1Label;
    TextView floor2Label;
    TextView floor3Label;
    TextView floor4Label;
    TextView floor5Label;
    Boolean buttonsShown = false;

    int floors = 5;
    private String building;
    private String name;
    private int objectID;
    private int id;

    //private ScaleGestureDetector scaleGestureDetector;
    //private Matrix matrix = new Matrix();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*
         * Looks at database to get currently selected building
         */
        objectID = getIntent().getIntExtra(OBJECT_ID, -1);
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        BuildingPersistence buildingsDB = new BuildingPersistence(db);

        /*
         * Once building is retrieved an id is generated for that building in order to search
         * and return correct drawable file when displaying.
         */
        building = buildingsDB.get(objectID).getBuildingId();
        final String resourceName = building.replaceAll("-", "_");
        name = resourceName + "_floor_1";
        id = getResources().getIdentifier(name, "drawable", getPackageName());

        /*
         * Identify all intractable objects that will be displayed on screen
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_bulding);
        floorExpandButton = (FloatingActionButton) findViewById(R.id.button_floor_expand);
        //floorExpandButton.setImageResource(R.drawable.ic_layers_black_24dp);
        floor1Button = (FloatingActionButton) findViewById(R.id.button_floor_1);
        floor2Button = (FloatingActionButton) findViewById(R.id.button_floor_2);
        floor3Button = (FloatingActionButton) findViewById(R.id.button_floor_3);
        floor4Button = (FloatingActionButton) findViewById(R.id.button_floor_4);
        floor5Button = (FloatingActionButton) findViewById(R.id.button_floor_5);
        floor1Label = (TextView) findViewById(R.id.text_floor_1_label);
        floor2Label = (TextView) findViewById(R.id.text_floor_2_label);
        floor3Label = (TextView) findViewById(R.id.text_floor_3_label);
        floor4Label = (TextView) findViewById(R.id.text_floor_4_label);
        floor5Label = (TextView) findViewById(R.id.text_floor_5_label);
        title = (TextView) findViewById(R.id.text_floor_banner);
        blueprint = (ImageView) findViewById(R.id.image_blueprint);

        /*
         * Set initial blueprint and label
         */
        blueprint.setImageResource(id);
        title.setText("1st Floor");

        /*
         * This statement checks to see if buttons are shown or not.
         * will take different actions based on what state the buttons are in.
         */
        floorExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonsShown) {
                    hideButtons();
                } else {
                    showButtons(floors);
                }
            }
        });

        /*
         * The following statements are actions that the button takes when pressed. Depending on
         * what floor button was pressed the activity will look for the appropriate floor blueprint
         * display it and change the text label at the top.
         */
        //TODO: Set check for non existent blueprints

        floor1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = resourceName + "_floor_1";
                id = getResources().getIdentifier(name, "drawable", getPackageName());
                blueprint.setImageResource(id);
                title.setText("1st Floor");
            }
        });

        floor2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = resourceName + "_floor_2";
                id = getResources().getIdentifier(name, "drawable", getPackageName());
                blueprint.setImageResource(id);
                title.setText("2nd Floor");
            }
        });
        floor3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = resourceName + "_floor_3";
                id = getResources().getIdentifier(name, "drawable", getPackageName());
                blueprint.setImageResource(id);
                title.setText("3rd Floor");
            }
        });
        floor4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = resourceName + "_floor_4";
                id = getResources().getIdentifier(name, "drawable", getPackageName());
                blueprint.setImageResource(id);
                title.setText("4th Floor");
            }
        });

        floor5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = resourceName + "_floor_5";
                id = getResources().getIdentifier(name, "drawable", getPackageName());
                blueprint.setImageResource(id);
                title.setText("5th Floor");
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this,BuildingDetailsActivity.class);
                intent.putExtra(OBJECT_ID,objectID);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * hideButtons method takes no parameter. Hides all the buttons that are currently displayed
     * by setting their visibility to GONE.
     */
    private void hideButtons() {
        floor1Button.setVisibility(View.GONE);
        floor1Label.setVisibility(View.GONE);

        floor2Button.setVisibility(View.GONE);
        floor2Label.setVisibility(View.GONE);

        floor3Button.setVisibility(View.GONE);
        floor3Label.setVisibility(View.GONE);

        floor4Button.setVisibility(View.GONE);
        floor4Label.setVisibility(View.GONE);

        floor5Button.setVisibility(View.GONE);
        floor5Label.setVisibility(View.GONE);

        buttonsShown = false;
    }

    /*
     * method that takes in the number of floors of a building and then sets the appropriate amount
     * of buttons to be displayed based on how many floors the building has.
     */
    private void showButtons(int numFloors) {
        if (numFloors == 1) {
            floor1Button.setVisibility(View.VISIBLE);
            floor1Label.setVisibility(View.VISIBLE);
        } else if (numFloors == 2){
            floor1Button.setVisibility(View.VISIBLE);
            floor1Label.setVisibility(View.VISIBLE);

            floor2Button.setVisibility(View.VISIBLE);
            floor2Label.setVisibility(View.VISIBLE);
        } else if (numFloors == 3) {
            floor1Button.setVisibility(View.VISIBLE);
            floor1Label.setVisibility(View.VISIBLE);

            floor2Button.setVisibility(View.VISIBLE);
            floor2Label.setVisibility(View.VISIBLE);

            floor3Button.setVisibility(View.VISIBLE);
            floor3Label.setVisibility(View.VISIBLE);
        } else if (numFloors == 4) {
            floor1Button.setVisibility(View.VISIBLE);
            floor1Label.setVisibility(View.VISIBLE);

            floor2Button.setVisibility(View.VISIBLE);
            floor2Label.setVisibility(View.VISIBLE);

            floor3Button.setVisibility(View.VISIBLE);
            floor3Label.setVisibility(View.VISIBLE);

            floor4Button.setVisibility(View.VISIBLE);
            floor4Label.setVisibility(View.VISIBLE);

        } else if (numFloors == 5) {
            floor1Button.setVisibility(View.VISIBLE);
            floor1Label.setVisibility(View.VISIBLE);

            floor2Button.setVisibility(View.VISIBLE);
            floor2Label.setVisibility(View.VISIBLE);

            floor3Button.setVisibility(View.VISIBLE);
            floor3Label.setVisibility(View.VISIBLE);

            floor4Button.setVisibility(View.VISIBLE);
            floor4Label.setVisibility(View.VISIBLE);

            floor5Button.setVisibility(View.VISIBLE);
            floor5Label.setVisibility(View.VISIBLE);

        }

        buttonsShown = true;
    }
}

