package com.example.vi_tu.gtinteractive;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.vi_tu.gtinteractive.adapters.EntityAdapter;
import com.example.vi_tu.gtinteractive.adapters.EventFilterAdapter;
import com.example.vi_tu.gtinteractive.adapters.PlaceFilterAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.domain.Entity;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.EntityFilter;
import com.example.vi_tu.gtinteractive.utilities.EventFilter;
import com.example.vi_tu.gtinteractive.utilities.PlaceFilter;

import java.util.ArrayList;
import java.util.List;

public class EntityListActivity extends AppCompatActivity implements ListView.OnItemClickListener   {

    private Event.Category[] eventFilterItems = Event.Category.values();
    private Place.Category[] placeFilterItems = Place.Category.values();
    private PlacePersistence placesDB;
    private EventPersistence eventsDB;

    private RecyclerView entityListView;
    private EntityAdapter eAdapter;

    private List<Entity> entityList = new ArrayList<Entity>();
    private List<Place> placeList;
    private List<Event> eventList;
    private EntityFilter eFilter;
    private EntityFilter eFilter2;
    private PlaceFilter placeFilter;
    private EventFilter eventFilter;
    private List<Class> entityActiveFilters = new ArrayList<>();
    private List<Place.Category> placeActiveFilters = new ArrayList<>();
    private List<Event.Category> eventActiveFilters = new ArrayList<>();
    private String userInput;

    private SearchView searchView;
    private MenuItem searchItem;

    private ToggleButton placeButton;
    private ToggleButton eventButton;
    private boolean placeOn;
    private boolean eventOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_list);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        placesDB = new PlacePersistence(db);
        eventsDB = new EventPersistence(db);

        // Stores entities from database in respective lists
        placeList = placesDB.getAll();
        eventList = eventsDB.getAll();

        // Set up place and event filters
        placeFilter = new PlaceFilter(placeList);
        eventFilter = new EventFilter(eventList);

        // Add all places and events to entityList
        entityList.addAll(placeList);
        entityList.addAll(eventList);

        // Setup place filter and event filter

        // Create two entity filters: one that always stores all the entities
        // the other that stores the filtered entities
        eFilter = new EntityFilter(entityList);
        eFilter2 = new EntityFilter(entityList);

        // Set up recycler view
        eAdapter = new EntityAdapter(entityList);
        entityListView = (RecyclerView) findViewById(R.id.recyclerview_search);
        entityListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        entityListView.setHasFixedSize(true);
        entityListView.setAdapter(eAdapter);

        placeButton = (ToggleButton) findViewById(R.id.tb_places);
        eventButton = (ToggleButton) findViewById(R.id.tb_events);

        // Based on previous view, set button and filter states
        int previousView = getIntent().getIntExtra("ViewType", -1);

        if (previousView == ViewType.EVENT) {
            placeOn = false;
            eventOn = true;
            entityActiveFilters.add(Event.class);
        } else {
            // default is place list
            placeOn = true;
            eventOn = false;
            entityActiveFilters.add(Place.class);
        }
        updateEntityData();

        placeButton.setChecked(placeOn);
        eventButton.setChecked(eventOn);

        // Handle onClick events for each button
        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEntityFilters(view);
            }
        });
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEntityFilters(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML

        getMenuInflater().inflate(R.menu.menu_place_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // always return first place in queryResults
                List<Entity> queryResults = eFilter2.filterByName(query).getList();
                // bring user to map
                Intent mapActivityIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE); // TODO: Check for ViewType
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, queryResults.get(0).getId());
                startActivity(mapActivityIntent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                userInput = input;
                // change list data so entities with matching names are displayed
                if (placeOn) {
                    eAdapter.setData(placeFilter.filterByName(userInput).getList());
                } else {
                    eAdapter.setData(eventFilter.filterByTitle(userInput).getList());
                }
                return true;
            }
        });
        searchView.setMaxWidth(800);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_filter) {
            if (eventOn && !placeOn) {
                createAndDisplayEventDialog();
            }
            if (!eventOn && placeOn) {
                createAndDisplayPlaceDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAndDisplayEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        final ListView listView = new ListView(this);

        listView.setAdapter(new EventFilterAdapter(this, R.layout.filter_list_item, eventFilterItems));
        listView.setOnItemClickListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Dialog does not save which items were checked previously
        // Thus, we have to manually check these when creating the layout
        for (int i = 0; i < eventFilterItems.length; i++) {
            if (eventActiveFilters.contains(eventFilterItems[i])) {
                listView.setItemChecked(i, true);
            }
        }

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        builder.setView(layout);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.d("PlaceListActivity", "Done clicked");
            }
        });

        builder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // do nothing. Will override later to prevent automatic closing dialog behavior
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // uncheck all the filters
                for (int i = 0; i < eventFilterItems.length; i++) {
                    listView.setItemChecked(i, false);
                }
                // remove all active filters
                eventActiveFilters.clear();
                updateEntityData();
            }
        });
    }

    private void createAndDisplayPlaceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        final ListView listView = new ListView(this);

        listView.setAdapter(new PlaceFilterAdapter(this, R.layout.filter_list_item, placeFilterItems));
        listView.setOnItemClickListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Dialog does not save which items were checked previously
        // Thus, we have to manually check these when creating the layout
        for (int i = 0; i < placeFilterItems.length; i++) {
            if (placeActiveFilters.contains(placeFilterItems[i])) {
                listView.setItemChecked(i, true);
            }
        }

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        builder.setView(layout);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.d("PlaceListActivity", "Done clicked");
            }
        });

        builder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // do nothing. Will override later to prevent automatic closing dialog behavior
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // uncheck all the filters
                for (int i = 0; i < placeFilterItems.length; i++) {
                    listView.setItemChecked(i, false);
                }
                // remove all active filters
                placeActiveFilters.clear();
                updatePlaceData();
            }
        });
    }

    // Entity Filters
    public void togglePlacesFilter(View view) {
        if (entityActiveFilters.contains(Place.class)) {
            entityActiveFilters.remove(Place.class);
        } else {
            entityActiveFilters.add(Place.class);
        }
        updateEntityData();
    }
    public void toggleEventsFilter(View view) {
        if (entityActiveFilters.contains(Event.class)) {
            entityActiveFilters.remove(Event.class);
        } else {
            entityActiveFilters.add(Event.class);
        }
        updateEntityData();
    }

    // Place Filters
    public void toggleFoodFilter(View view) {
        if (placeActiveFilters.contains(Place.Category.FOOD)) {
            placeActiveFilters.remove(Place.Category.FOOD);
        } else {
            placeActiveFilters.add(Place.Category.FOOD);
        }
        updatePlaceData();
    }

    public void toggleHousingFilter(View view) {
        if (placeActiveFilters.contains(Place.Category.HOUSING)) {
            placeActiveFilters.remove(Place.Category.HOUSING);
        } else {
            placeActiveFilters.add(Place.Category.HOUSING);
        }
        updatePlaceData();
    }

    public void toggleSportsFilter(View view) {
        if (placeActiveFilters.contains(Place.Category.SPORTS)) {
            placeActiveFilters.remove(Place.Category.SPORTS);
        } else {
            placeActiveFilters.add(Place.Category.SPORTS);
        }
        updatePlaceData();
    }

    public void toggleGreekFilter(View view) {
        if (placeActiveFilters.contains(Place.Category.GREEK)) {
            placeActiveFilters.remove(Place.Category.GREEK);
        } else {
            placeActiveFilters.add(Place.Category.GREEK);
        }
        updatePlaceData();
    }

    public void toggleParkingFilter(View view) {
        if (placeActiveFilters.contains(Place.Category.PARKING)) {
            placeActiveFilters.remove(Place.Category.PARKING);
        } else {
            placeActiveFilters.add(Place.Category.PARKING);
        }
        updatePlaceData();
    }

    public void toggleAcademicFilter(View view) {
        if (placeActiveFilters.contains(Place.Category.ACADEMIC)) {
            placeActiveFilters.remove(Place.Category.ACADEMIC);
        } else {
            placeActiveFilters.add(Place.Category.ACADEMIC);
        }
        updatePlaceData();
    }

    public void toggleOtherFilter(View view) {
        if (placeActiveFilters.contains(Place.Category.OTHER)) {
            placeActiveFilters.remove(Place.Category.OTHER);
        } else {
            placeActiveFilters.add(Place.Category.OTHER);
        }
        updatePlaceData();
    }

    // Event Filters
    public void toggleArtFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.ARTS)) {
            eventActiveFilters.remove(Event.Category.ARTS);
        } else {
            eventActiveFilters.add(Event.Category.ARTS);
        }
        updateEventData();
    }

    public void toggleCareerFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.CAREER)) {
            eventActiveFilters.remove(Event.Category.CAREER);
        } else {
            eventActiveFilters.add(Event.Category.CAREER);
        }
        updateEventData();
    }

    public void toggleConferenceFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.CONFERENCE)) {
            eventActiveFilters.remove(Event.Category.CONFERENCE);
        } else {
            eventActiveFilters.add(Event.Category.CONFERENCE);
        }
        updateEventData();
    }

    public void toggleOtherEventFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.OTHER)) {
            eventActiveFilters.remove(Event.Category.OTHER);
        } else {
            eventActiveFilters.add(Event.Category.OTHER);
        }
        updateEventData();

    }

    public void toggleSeminarFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.SEMINAR)) {
            eventActiveFilters.remove(Event.Category.SEMINAR);
        } else {
            eventActiveFilters.add(Event.Category.SEMINAR);
        }
        updateEventData();
    }

    public void toggleSpecialEventFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.SPECIAL)) {
            eventActiveFilters.remove(Event.Category.SPECIAL);
        } else {
            eventActiveFilters.add(Event.Category.SPECIAL);
        }
        updateEventData();
    }

    public void toggleSportsEventFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.SPORTS)) {
            eventActiveFilters.remove(Event.Category.SPORTS);
        } else {
            eventActiveFilters.add(Event.Category.SPORTS);
        }
        updateEventData();
    }

    public void toggleStudentSponsoredFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.STUDENT)) {
            eventActiveFilters.remove(Event.Category.STUDENT);
        } else {
            eventActiveFilters.add(Event.Category.STUDENT);
        }
        updateEventData();
    }

    public void toggleTrainingFilter(View view) {
        if (eventActiveFilters.contains(Event.Category.TRAINING)) {
            eventActiveFilters.remove(Event.Category.TRAINING);
        } else {
            eventActiveFilters.add(Event.Category.TRAINING);
        }
        updateEventData();
    }

    // upda
    public void updateEntityData() {
        eFilter2 = eFilter.filterByClass(entityActiveFilters);
        List<Entity> filteredList = eFilter2.filterByName(userInput).getList();
        eAdapter.setData(filteredList);
    }
    public void updatePlaceData() {
        placeFilter = new PlaceFilter(placeList).filterByCategories(placeActiveFilters);
        List<? extends Entity> filteredList = placeFilter.filterByName(userInput).getList();
        eAdapter.setData(filteredList);
    }

    public void updateEventData() {
        eventFilter = new EventFilter(eventList).filterByCategories(eventActiveFilters);
        List<? extends Entity> filteredList = eventFilter.filterByTitle(userInput).getList();
        eAdapter.setData(filteredList);
    }

    public void toggleEntityFilters(View view) {
        togglePlacesFilter(view);
        toggleEventsFilter(view);
        placeOn = !placeOn;
        eventOn = !eventOn;
        placeButton.setChecked(placeOn);
        eventButton.setChecked(eventOn);
        placeActiveFilters.clear();
        eventActiveFilters.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (placeOn) {
            switch(i) {
                case 0: // Food
                    toggleFoodFilter(view);
                    break;
                case 1: // Housing
                    toggleHousingFilter(view);
                    break;
                case 2: // Sports
                    toggleSportsFilter(view);
                    break;
                case 3: // Greek
                    toggleGreekFilter(view);
                    break;
                case 4: // Parking
                    toggleParkingFilter(view);
                    break;
                case 5: // Academic
                    toggleAcademicFilter(view);
                    break;
                case 6: // Other
                    toggleOtherFilter(view);
                    break;
                default:
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            switch (i) {
                case 0: // Art
                    toggleArtFilter(view);
                    break;
                case 1: // Career
                    toggleCareerFilter(view);
                    break;
                case 2: // Conference
                    toggleConferenceFilter(view);
                    break;
                case 3: // Other
                    toggleOtherEventFilter(view);
                    break;
                case 4: // Seminar
                    toggleSeminarFilter(view);
                    break;
                case 5: // Special Event
                    toggleSpecialEventFilter(view);
                    break;
                case 6: // Sports
                    toggleSportsEventFilter(view);
                    break;
                case 7: // Student Sponsored
                    toggleStudentSponsoredFilter(view);
                    break;
                case 8: // Training
                    toggleTrainingFilter(view);
                    break;
                default:
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
