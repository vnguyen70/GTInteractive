package com.example.vi_tu.gtinteractive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.Constants;
import com.example.vi_tu.gtinteractive.constants.TabType;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnInfoWindowClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int REQUEST_LOCATION_PERMISSION = 0;

    public static final int DEFAULT_EVENT_MARKER_ID = 0;

    PlacePersistence placesDB;
    EventPersistence eventsDB;

    List<Place> pList;
    List<Event> eList;

    GoogleMap googleMap;
    Map<Integer, List<Polygon>> placesPolygons;
    Map<Integer, Marker> placesMarkers;
    Map<Integer, Marker> eventMarkers;
    KmlLayer printersLayer;
    GroundOverlay parkingOverlay;
    List<Polygon> highlighted;

    EditText searchBar;
    ImageButton drawerButton;
    ImageButton searchOptionsButton;

    FloatingActionButton placesViewButton;
    FloatingActionButton eventsViewButton;
    FloatingActionButton parkingViewButton;
    FloatingActionButton printingViewButton;
    FloatingActionButton selectViewButton;
    FloatingActionButton resetCameraButton;
    FloatingActionButton toggleTrafficButton;
    FloatingActionButton togglePolygonsButton;
    FloatingActionButton toggleOverlayButton;

    TextView placesViewLabel;
    TextView eventsViewLabel;
    TextView parkingViewLabel;
    TextView printingViewLabel;

    DrawerLayout drawerLayout;
    ListView drawerList;
    NavigationView navigationView;

    int view;
    int objectId;
    int currView;
    boolean buttonsShown;
    boolean polygonsShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        placesDB = new PlacePersistence(db);
        eventsDB = new EventPersistence(db);
        pList = placesDB.getAll(); // TODO: move to AsyncTask
        eList = eventsDB.getAll(); // TODO: move to AsyncTask

        // context
        Intent i = getIntent();
        view = i.getIntExtra(Arguments.DEFAULT_VIEW, -1);
        objectId = i.getIntExtra(Arguments.OBJECT_ID, -1);

        // navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        Log.d("map activity", drawerLayout.toString());
        //drawerList = findViewById(R.id.left_drawer);
//        navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        setNavigationViewListener();

        // buttons
        searchBar = findViewById(R.id.searchBar);
        drawerButton = findViewById(R.id.drawerButton);
        searchOptionsButton = findViewById(R.id.searchOptionsButton);
        placesViewButton = findViewById(R.id.placesViewButton);
        eventsViewButton = findViewById(R.id.eventsViewButton);
        parkingViewButton = findViewById(R.id.parkingViewButton);
        printingViewButton = findViewById(R.id.printingViewButton);
        selectViewButton = findViewById(R.id.selectViewButton);
        resetCameraButton = findViewById(R.id.resetCameraButton);
        toggleTrafficButton = findViewById(R.id.toggleTrafficButton);
        togglePolygonsButton = findViewById(R.id.togglePolygonsButton);
        toggleOverlayButton = findViewById(R.id.toggleOverlayButton);

        // button labels
        placesViewLabel = findViewById(R.id.placesViewLabel);
        eventsViewLabel = findViewById(R.id.eventsViewLabel);
        parkingViewLabel = findViewById(R.id.parkingViewLabel);
        printingViewLabel = findViewById(R.id.printingViewLabel);

        // button icons
        drawerButton.setImageResource(R.drawable.ic_menu_black_24dp);
        searchOptionsButton.setImageResource(R.drawable.ic_more_vert_black_24dp);
        placesViewButton.setImageResource(R.drawable.ic_business_black_24dp);
        eventsViewButton.setImageResource(R.drawable.ic_today_black_24dp);
        parkingViewButton.setImageResource(R.drawable.ic_local_parking_black_24dp);
        printingViewButton.setImageResource(R.drawable.ic_print_black_24dp);
        selectViewButton.setImageResource(R.drawable.ic_business_black_24dp);

        buttonsShown = false;

        // listeners TODO: risk of button being pressed before map is ready?
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MapActivity.this, EntityListActivity.class);
                searchIntent.putExtra("ViewType", currView);
                startActivity(searchIntent);
            }
        });
        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_NULL
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                }
                return true;
            }
        });
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });
        placesViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currView != ViewType.PLACE) {
                    showPlacesOverlay();
                }
                hideButtons();
            }
        });
        eventsViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currView != ViewType.EVENT) {
                    showEventsOverlay();
                }
                hideButtons();
            }
        });
        parkingViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currView != ViewType.PARKING) {
                    showParkingOverlay();
                }
                hideButtons();
            }
        });
        printingViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currView != ViewType.PRINTERS) {
                    showPrintersOverlay();
                }
                hideButtons();
            }
        });
        selectViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonsShown) {
                    hideButtons();
                } else {
                    showButtons();
                }
            }
        });
        resetCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerCampus();
            }
        });
        toggleTrafficButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setTrafficEnabled(!googleMap.isTrafficEnabled());
            }
        });
        togglePolygonsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (polygonsShown) {
                    hidePlacePolygons();
                } else {
                    showPlacePolygons();
                }
            }
        });
        toggleOverlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingOverlay.setVisible(!parkingOverlay.isVisible());
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        resetCamera();
        showUserLocation();
        googleMap.setPadding(0, 180, 0, 0); // leave room for search bar
        googleMap.setMinZoomPreference(14.0f);
        googleMap.setMaxZoomPreference(24.0f);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnPolygonClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        placesPolygons = new HashMap<>();
        placesMarkers = new HashMap<>();
        eventMarkers = new HashMap<>();
        highlighted = new ArrayList<>();

        for (Place p : pList) {
            addPlacePolygons(p);
            addPlaceMarker(p);
        }
        for (Event e : eList) {
            addEventMarker(e);
        }
        addDefaultEventMarker();

        parkingOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.parking_map))
                .position(new LatLng(33.777450, -84.397840), 2260f) // TODO: alignment
                .visible(false));
        try {
            printersLayer = new KmlLayer(googleMap, R.raw.printers, getApplicationContext());
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        switch (view) {
            case ViewType.PLACE:
                showPlacesOverlay();
                break;
            case ViewType.EVENT:
                showEventsOverlay();
                break;
            case ViewType.PARKING:
                showParkingOverlay();
            case ViewType.PRINTERS:
                showPrintersOverlay();
            default:
                showPlacesOverlay(); // TODO: default view for now
        }
    }

    private List<Polygon> addPlacePolygons(Place p) {
        List<Polygon> added = new ArrayList<>();
        List<LatLng[]> polygons = p.getPolygons();
        for (LatLng[] polygon : polygons) {
            Polygon poly = googleMap.addPolygon(new PolygonOptions()
                    .addAll(Arrays.asList(polygon))
                    .strokeWidth(5)
                    .strokeColor(0x9900254c) // TODO: refactor colors into colors.xml
                    .fillColor(0x9900254c)
                    .clickable(false)
                    .visible(false)
            );
            poly.setTag(p.getId());
            added.add(poly);
        }
        placesPolygons.put(p.getId(), added);
        return added;
    }

    private Marker addPlaceMarker(Place p) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(p.getLatitude(), p.getLongitude()))
                .title(p.getName())
                .visible(false));
        m.setTag(p.getId());
        placesMarkers.put(p.getId(), m);
        return m;
    }

    private Marker addEventMarker(Event e) {
        String placeId = e.getPlaceId();
        Place p = placesDB.findByPlaceId(placeId);
        if (p != null) {
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(p.getLatitude(), p.getLongitude()))
                    .title(e.getTitle())
                    .snippet(e.getLocation())
                    .visible(false));
            m.setTag(e.getId());
            eventMarkers.put(e.getId(), m);
            return m;
        }
        return null;
    }

    private Marker addDefaultEventMarker() { // TODO: for all "other" events that haven't been matched to a physical location on campus
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE))
                .title("Other Events") // TODO
                .snippet("Other Events") // TODO
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) // TODO
                .visible(false));
        m.setTag(DEFAULT_EVENT_MARKER_ID);
        eventMarkers.put(DEFAULT_EVENT_MARKER_ID, m);
        return m;
    }

    private void showPlacesOverlay() {
        clearMap();
        showPlacePolygons();
        if (view == ViewType.PLACE && objectId != -1) {
            highlightPolygons(placesPolygons.get(objectId));
            Marker m = placesMarkers.get(objectId);
            m.setVisible(true);
            m.showInfoWindow();
            centerMarkerZoom(m);
            view = -1;
            objectId = -1;
        } else {
            centerCampus();
        }
        togglePolygonsButton.setVisibility(View.VISIBLE);
        setCurrView(ViewType.PLACE);
    }

    private void showEventsOverlay() {
        clearMap();
        showEventMarkers();
        if (view == ViewType.EVENT && objectId != -1) {
            Marker m = eventMarkers.get(objectId);
            m.showInfoWindow();
            centerMarkerZoom(m);
            view = -1;
            objectId = -1;
        } else {
            centerCampus();
        }
        setCurrView(ViewType.EVENT);
    }

    private void showParkingOverlay() {
        clearMap();
        parkingOverlay.setVisible(true);
        toggleOverlayButton.setVisibility(View.VISIBLE);
        centerCampus();
        setCurrView(ViewType.PARKING);
    }

    private void showPrintersOverlay() { // TODO: change marker style to dots
        clearMap();
        try {
            printersLayer.addLayerToMap();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        centerCampus();
        setCurrView(ViewType.PRINTERS);
    }

    private void showPlacePolygons() {
        for (List<Polygon> pList : placesPolygons.values()) {
            for (Polygon p : pList) {
                p.setVisible(true);
                p.setClickable(true);
            }
        }
        polygonsShown = true;
    }

    private void hidePlacePolygons() {
        for (List<Polygon> pList : placesPolygons.values()) {
            for (Polygon p : pList) {
                p.setVisible(false);
                p.setClickable(false);
            }
        }
        polygonsShown = false;
    }

    private void showPlaceMarkers() {
        for (Marker m : placesMarkers.values()) {
            m.setVisible(true);
        }
    }

    private void hidePlaceMarkers() {
        for (Marker m : placesMarkers.values()) {
            m.setVisible(false);
        }
    }

    private void showEventMarkers() {
        for (Marker m : eventMarkers.values()) {
            m.setVisible(true);
        }
    }

    private void hideEventMarkers() {
        for (Marker m : eventMarkers.values()) {
            m.setVisible(false);
        }
    }

    private void clearMap() {
        resetPolygonColors();
        hidePlacePolygons();
        hidePlaceMarkers();
        hideEventMarkers();
        printersLayer.removeLayerFromMap();
        parkingOverlay.setVisible(false);
        togglePolygonsButton.setVisibility(View.GONE);
        toggleOverlayButton.setVisibility(View.GONE);
    }

    private void centerMarker(Marker m) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(m.getPosition()), 400, null);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
    }

    private void centerMarkerZoom(Marker m) {
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(m.getPosition(), 17.0f, 0, 0)));
//        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(m.getPosition(), 17.0f, 0, 0)));
    }

    private void centerCampus() {
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Constants.GATECH_LATITUDE, Constants.GATECH_LONGITUDE), 14.6f, 0, 0))); // tech campus
//        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Constants.GATECH_LATITUDE, Constants.GATECH_LONGITUDE), 14.6f, 0, 0)));
    }

    private void resetCamera() {
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Constants.GATECH_LATITUDE, Constants.GATECH_LONGITUDE), 14.6f, 0, 0))); // tech campus
    }

    private void showUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
        if (location == null) {
            Toast.makeText(this, "Location services unavailable on your device", Toast.LENGTH_SHORT).show();
        }
    }

    private void showButtons() {
        placesViewButton.setVisibility(View.VISIBLE);
        eventsViewButton.setVisibility(View.VISIBLE);
        parkingViewButton.setVisibility(View.VISIBLE);
        printingViewButton.setVisibility(View.VISIBLE);
        placesViewLabel.setVisibility(View.VISIBLE);
        eventsViewLabel.setVisibility(View.VISIBLE);
        parkingViewLabel.setVisibility(View.VISIBLE);
        printingViewLabel.setVisibility(View.VISIBLE);
        buttonsShown = true;
    }

    private void hideButtons() {
        placesViewButton.setVisibility(View.GONE);
        eventsViewButton.setVisibility(View.GONE);
        parkingViewButton.setVisibility(View.GONE);
        printingViewButton.setVisibility(View.GONE);
        placesViewLabel.setVisibility(View.GONE);
        eventsViewLabel.setVisibility(View.GONE);
        parkingViewLabel.setVisibility(View.GONE);
        printingViewLabel.setVisibility(View.GONE);
        buttonsShown = false;
    }

    private void setCurrView(int v) {
        switch(v) {
            case ViewType.PLACE:
                selectViewButton.setBackgroundTintList(placesViewButton.getBackgroundTintList());
                selectViewButton.setImageResource(R.drawable.ic_business_black_24dp);
                break;
            case ViewType.EVENT:
                selectViewButton.setBackgroundTintList(eventsViewButton.getBackgroundTintList());
                selectViewButton.setImageResource(R.drawable.ic_today_black_24dp);
                break;
            case ViewType.PARKING:
                selectViewButton.setBackgroundTintList(parkingViewButton.getBackgroundTintList());
                selectViewButton.setImageResource(R.drawable.ic_local_parking_black_24dp);
                break;
            case ViewType.PRINTERS:
                selectViewButton.setBackgroundTintList(printingViewButton.getBackgroundTintList());
                selectViewButton.setImageResource(R.drawable.ic_print_black_24dp);
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        hideButtons();
        currView = v;
    }

    private void resetPolygonColors() {
        for (Polygon p : highlighted) {
            p.setFillColor(0x9900254c);
            p.setStrokeColor(0x9900254c);
        }
        highlighted.clear();
    }

    private void highlightPolygons(List<Polygon> polygons) {
        for (Polygon p : polygons) {
            highlightPolygon(p);
        }
    }

    private void highlightPolygon(Polygon p) {
        p.setFillColor(0x99004f9f);
        p.setStrokeColor(0x99004f9f);
        highlighted.add(p);
    }

    @Override

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("navitemselect","nav item is running");
        if (id == R.id.nav_places) {
            drawerLayout.closeDrawer(navigationView);
            Intent placeListActivityIntent = new Intent(MapActivity.this, PlaceListActivity.class);
            startActivity(placeListActivityIntent);
            Log.d("navplacesrun","nav_places was clicked");
        } else if (id == R.id.nav_events) {
            drawerLayout.closeDrawer(navigationView);
            Intent eventListActivityIntent = new Intent(MapActivity.this, EventListActivity.class);
            startActivity(eventListActivityIntent);
        } else if (id == R.id.nav_place_test) {
            drawerLayout.closeDrawer(navigationView);
            Intent placesTestActivityIntent = new Intent(MapActivity.this, PlaceTestActivity.class);
            startActivity(placesTestActivityIntent);
        } else if (id == R.id.nav_event_test) {
            drawerLayout.closeDrawer(navigationView);
            Intent eventsTestActivityIntent = new Intent(MapActivity.this, EventsTestActivity.class);
            startActivity(eventsTestActivityIntent);
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public boolean onMarkerClick(Marker m) {
        m.showInfoWindow();
        centerMarker(m);
        return false;
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        resetPolygonColors();
        highlightPolygon(polygon);
        hidePlaceMarkers();
        Marker m = placesMarkers.get(Integer.parseInt(polygon.getTag().toString()));
        m.setVisible(true);
        m.showInfoWindow();
        centerMarker(m);
        //TODO: change color of polygon
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        switch (currView) {
            case ViewType.PLACE:
                Log.d("MapActivity", String.valueOf(Arguments.OBJECT_ID));
                Intent placeDetailIntent = new Intent(MapActivity.this, PlaceDetailsActivity.class);
                placeDetailIntent.putExtra(Arguments.OBJECT_ID, Integer.parseInt(marker.getTag().toString()));
                placeDetailIntent.putExtra(Arguments.DEFAULT_TAB, TabType.INFO);
                startActivity(placeDetailIntent);
                break;
            case ViewType.EVENT:
                int tag = (int) marker.getTag(); // TODO
                if (tag == DEFAULT_EVENT_MARKER_ID) {
                    Intent intent = new Intent(MapActivity.this, EventListActivity.class);
                    startActivity(intent);
                } else {
                    Intent eventDetailIntent = new Intent(MapActivity.this, EventDetailsActivity.class);
                    eventDetailIntent.putExtra(Arguments.OBJECT_ID, Integer.parseInt(marker.getTag().toString()));
                    startActivity(eventDetailIntent);
                }
                break;
            default:
                Toast.makeText(this, "InfoWindow clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        showUserLocation();
    }

}