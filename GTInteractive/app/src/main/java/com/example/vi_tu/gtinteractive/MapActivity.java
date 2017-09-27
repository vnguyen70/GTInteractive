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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.Constants;
import com.example.vi_tu.gtinteractive.constants.TabType;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
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

public class MapActivity extends FragmentActivity implements ListView.OnItemClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnInfoWindowClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String[] drawerItems = {"Buildings Test", "Dinings Test", "Events Test"};

    public static final int REQUEST_LOCATION_PERMISSION = 0;

    BuildingPersistence buildingsDB;
    DiningPersistence diningsDB;
    EventPersistence eventsDB;

    List<Building> bList;
    List<Dining> dList;
    List<Event> eList;

    GoogleMap googleMap;
    Map<Integer, List<Polygon>> buildingPolygons;
    Map<Integer, Marker> buildingMarkers;
    Map<Integer, Marker> diningMarkers;
    Map<Integer, Marker> eventMarkers;
    KmlLayer printersLayer;
    GroundOverlay parkingOverlay;
    List<Polygon> highlighted;

    EditText searchBar;
    ImageButton drawerButton;

    FloatingActionButton buildingsViewButton;
    FloatingActionButton diningsViewButton;
    FloatingActionButton eventsViewButton;
    FloatingActionButton parkingViewButton;
    FloatingActionButton printingViewButton;
    FloatingActionButton selectViewButton;
    FloatingActionButton resetCameraButton;
    FloatingActionButton toggleTrafficButton;
    FloatingActionButton togglePolygonsButton;
    FloatingActionButton toggleOverlayButton;

    TextView buildingsViewLabel;
    TextView diningsViewLabel;
    TextView eventsViewLabel;
    TextView parkingViewLabel;
    TextView printingViewLabel;

    DrawerLayout drawerLayout;
    ListView drawerList;

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
        buildingsDB = new BuildingPersistence(db);
        diningsDB = new DiningPersistence(db);
        eventsDB = new EventPersistence(db);
        bList = buildingsDB.getAll(); // TODO: move to AsyncTask
        dList = diningsDB.getAll(); // TODO: move to AsyncTask
        eList = eventsDB.getAll(); // TODO: move to AsyncTask

        // context
        Intent i = getIntent();
        view = i.getIntExtra(Arguments.DEFAULT_VIEW, -1);
        objectId = i.getIntExtra(Arguments.OBJECT_ID, -1);

        // UI
        searchBar = findViewById(R.id.searchBar);
        drawerButton = findViewById(R.id.drawerButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, drawerItems));
        drawerList.setOnItemClickListener(this);

        buildingsViewButton = findViewById(R.id.buildingsViewButton);
        diningsViewButton = findViewById(R.id.diningsViewButton);
        eventsViewButton = findViewById(R.id.eventsViewButton);
        parkingViewButton = findViewById(R.id.parkingViewButton);
        printingViewButton = findViewById(R.id.printingViewButton);
        selectViewButton = findViewById(R.id.selectViewButton);

        resetCameraButton = findViewById(R.id.resetCameraButton);
        toggleTrafficButton = findViewById(R.id.toggleTrafficButton);
        togglePolygonsButton = findViewById(R.id.togglePolygonsButton);
        toggleOverlayButton = findViewById(R.id.toggleOverlayButton);

        buildingsViewLabel = findViewById(R.id.buildingsViewLabel);
        diningsViewLabel = findViewById(R.id.diningsViewLabel);
        eventsViewLabel = findViewById(R.id.eventsViewLabel);
        parkingViewLabel = findViewById(R.id.parkingViewLabel);
        printingViewLabel = findViewById(R.id.printingViewLabel);

        buttonsShown = false;

        // listeners TODO: risk of button being pressed before map is ready?
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MapActivity.this, BuildingSearchActivity.class);
                startActivity(searchIntent);
            }
        });
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(drawerList)) {
                    drawerLayout.closeDrawer(drawerList);
                } else {
                    drawerLayout.openDrawer(drawerList);
                }
            }
        });
        buildingsViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currView != ViewType.BUILDING) {
                    showBuildingsOverlay();
                }
                hideButtons();
            }
        });
        diningsViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currView != ViewType.DINING) {
                    showDiningsOverlay();
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
                    hideBuildingPolygons();
                } else {
                    showBuildingPolygons();
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

        buildingPolygons = new HashMap<>();
        buildingMarkers = new HashMap<>();
        diningMarkers = new HashMap<>();
        eventMarkers = new HashMap<>();
        highlighted = new ArrayList<>();

        for (Building b : bList) {
            addBuildingPolygons(b);
            addBuildingMarker(b);
        }
        for (Dining d : dList) {
            addDiningMarker(d);
        }
        for (Event e : eList) {
            addEventMarker(e);
        }
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
            case ViewType.BUILDING:
                showBuildingsOverlay();
                break;
            case ViewType.DINING:
                showDiningsOverlay();
                break;
            case ViewType.EVENT:
                showEventsOverlay();
                break;
            case ViewType.PARKING:
                showParkingOverlay();
            case ViewType.PRINTERS:
                showPrintersOverlay();
            default:
                showBuildingsOverlay(); // TODO: default view for now
        }
    }

    private List<Polygon> addBuildingPolygons(Building b) {
        List<Polygon> added = new ArrayList<>();
        List<LatLng[]> polygons = b.getPolygons();
        for (LatLng[] polygon : polygons) {
            Polygon p = googleMap.addPolygon(new PolygonOptions()
                    .addAll(Arrays.asList(polygon))
                    .strokeWidth(5)
                    .strokeColor(0x9900254c) // TODO: refactor colors into colors.xml
                    .fillColor(0x9900254c)
                    .clickable(true)
                    .visible(false)
            );
            p.setTag(b.getId());
            added.add(p);
        }
        buildingPolygons.put(b.getId(), added);
        return added;
    }

    private Marker addBuildingMarker(Building b) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(b.getLatitude(), b.getLongitude()))
                .title(b.getName())
                .visible(false));
        m.setTag(b.getId());
        buildingMarkers.put(b.getId(), m);
        return m;
    }

    private Marker addDiningMarker(Dining d) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(d.getLatitude(), d.getLongitude()))
                .title(d.getName())
                .snippet(d.getLocationDetails())
                .visible(false));
        m.setTag(d.getId());
        diningMarkers.put(d.getId(), m);
        return m;
    }

    private Marker addEventMarker(Event e) {
        String buildingId = e.getBuildingId();
        Building b = buildingsDB.findByBuildingId(buildingId);
        LatLng ll = new LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE);
        if (b != null) {
            ll = new LatLng(b.getLatitude(), b.getLongitude());
        }
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(ll)
                .title(e.getTitle())
                .snippet(e.getLocation())
                .visible(false));
        m.setTag(e.getId());
        eventMarkers.put(e.getId(), m);
        return m;
    }

    private void showBuildingsOverlay() {
        clearMap();
        showBuildingPolygons();
        if (view == ViewType.BUILDING && objectId != -1) {
            highlightPolygons(buildingPolygons.get(objectId));
            Marker m = buildingMarkers.get(objectId);
            m.setVisible(true);
            m.showInfoWindow();
            centerMarkerZoom(m);
            view = -1;
            objectId = -1;
        } else {
            centerCampus();
        }
        togglePolygonsButton.setVisibility(View.VISIBLE);
        setCurrView(ViewType.BUILDING);
    }

    private void showDiningsOverlay() {
        clearMap();
        showDiningMarkers();
        if (view == ViewType.DINING && objectId != -1) {
            Marker m = diningMarkers.get(objectId);
            m.showInfoWindow();
            centerMarkerZoom(m);
            view = -1;
            objectId = -1;
        } else {
            centerCampus();
        }
        setCurrView(ViewType.DINING);
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

    private void showBuildingPolygons() {
        for (List<Polygon> pList : buildingPolygons.values()) {
            for (Polygon p : pList) {
                p.setVisible(true);
            }
        }
        polygonsShown = true;
    }

    private void hideBuildingPolygons() {
        for (List<Polygon> pList : buildingPolygons.values()) {
            for (Polygon p : pList) {
                p.setVisible(false);
            }
        }
        polygonsShown = false;
    }

    private void showBuildingMarkers() {
        for (Marker m : buildingMarkers.values()) {
            m.setVisible(true);
        }
    }

    private void hideBuildingMarkers() {
        for (Marker m : buildingMarkers.values()) {
            m.setVisible(false);
        }
    }

    private void showDiningMarkers() {
        for (Marker m : diningMarkers.values()) {
            m.setVisible(true);
        }
    }

    private void hideDiningMarkers() {
        for (Marker m : diningMarkers.values()) {
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
        hideBuildingPolygons();
        hideBuildingMarkers();
        hideDiningMarkers();
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
        buildingsViewButton.setVisibility(View.VISIBLE);
        diningsViewButton.setVisibility(View.VISIBLE);
        eventsViewButton.setVisibility(View.VISIBLE);
        parkingViewButton.setVisibility(View.VISIBLE);
        printingViewButton.setVisibility(View.VISIBLE);
        buildingsViewLabel.setVisibility(View.VISIBLE);
        diningsViewLabel.setVisibility(View.VISIBLE);
        eventsViewLabel.setVisibility(View.VISIBLE);
        parkingViewLabel.setVisibility(View.VISIBLE);
        printingViewLabel.setVisibility(View.VISIBLE);
        buttonsShown = true;
    }

    private void hideButtons() {
        buildingsViewButton.setVisibility(View.GONE);
        diningsViewButton.setVisibility(View.GONE);
        eventsViewButton.setVisibility(View.GONE);
        parkingViewButton.setVisibility(View.GONE);
        printingViewButton.setVisibility(View.GONE);
        buildingsViewLabel.setVisibility(View.GONE);
        diningsViewLabel.setVisibility(View.GONE);
        eventsViewLabel.setVisibility(View.GONE);
        parkingViewLabel.setVisibility(View.GONE);
        printingViewLabel.setVisibility(View.GONE);
        buttonsShown = false;
    }

    private void setCurrView(int v) {
        switch(v) {
            case ViewType.BUILDING:
                selectViewButton.setBackgroundTintList(buildingsViewButton.getBackgroundTintList());
                break;
            case ViewType.DINING:
                selectViewButton.setBackgroundTintList(diningsViewButton.getBackgroundTintList());
                break;
            case ViewType.EVENT:
                selectViewButton.setBackgroundTintList(eventsViewButton.getBackgroundTintList());
                break;
            case ViewType.PARKING:
                selectViewButton.setBackgroundTintList(parkingViewButton.getBackgroundTintList());
                break;
            case ViewType.PRINTERS:
                selectViewButton.setBackgroundTintList(printingViewButton.getBackgroundTintList());
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch(i) {
            case 0: // Buildings Test
                drawerLayout.closeDrawer(drawerList);
                Intent buildingsTestActivityIntent = new Intent(MapActivity.this, BuildingsTestActivity.class);
                startActivity(buildingsTestActivityIntent);
                break;
            case 1: // Dinings Test
                drawerLayout.closeDrawer(drawerList);
                Intent diningsTestActivityIntent = new Intent(MapActivity.this, DiningsTestActivity.class);
                startActivity(diningsTestActivityIntent);
                break;
            case 2: // Events Test
                drawerLayout.closeDrawer(drawerList);
                Intent eventsTestActivityIntent = new Intent(MapActivity.this, EventsTestActivity.class);
                startActivity(eventsTestActivityIntent);
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
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
        hideBuildingMarkers();
        Marker m = buildingMarkers.get(Integer.parseInt(polygon.getTag().toString()));
        m.setVisible(true);
        m.showInfoWindow();
        centerMarker(m);
        //TODO: change color of polygon
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        switch (currView) {
            case ViewType.BUILDING:
                Intent buildingDetailIntent = new Intent(MapActivity.this, BuildingDetailsActivity.class);
                buildingDetailIntent.putExtra(Arguments.OBJECT_ID, Integer.parseInt(marker.getTag().toString()));
                buildingDetailIntent.putExtra(Arguments.DEFAULT_TAB, TabType.INFO);
                startActivity(buildingDetailIntent);
                break;
            case ViewType.DINING:
                Intent diningDetailIntent = new Intent(MapActivity.this, DiningDetailsActivity.class);
                diningDetailIntent.putExtra(Arguments.OBJECT_ID, Integer.parseInt(marker.getTag().toString()));
                startActivity(diningDetailIntent);
                break;
            case ViewType.EVENT:
                Intent eventDetailIntent = new Intent(MapActivity.this, EventDetailsActivity.class);
                eventDetailIntent.putExtra(Arguments.OBJECT_ID, Integer.parseInt(marker.getTag().toString()));
                startActivity(eventDetailIntent);
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