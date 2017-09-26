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
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class MapActivity extends FragmentActivity implements ListView.OnItemClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int REQUEST_LOCATION_PERMISSION = 0;
    public static final int BUTTONS_HIDDEN = 0;
    public static final int BUTTONS_SHOWN = 1;

    private static final String[] drawerItems = {"Buildings Test", "Dinings Test", "Events Test"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private GoogleMap googleMap;

    KmlLayer printersLayer;
    GroundOverlayOptions parkingLayer;

    FloatingActionButton buildingsViewButton;
    FloatingActionButton diningsViewButton;
    FloatingActionButton eventsViewButton;
    FloatingActionButton parkingViewButton;
    FloatingActionButton printingViewButton;
    FloatingActionButton selectViewButton;

    TextView buildingsViewLabel;
    TextView diningsViewLabel;
    TextView eventsViewLabel;
    TextView parkingViewLabel;
    TextView printingViewLabel;

    EditText searchBar;
    ImageButton drawerButton;

    private static int buttonsState;

    private BuildingPersistence buildingsDB;
    private DiningPersistence diningsDB;
    private EventPersistence eventsDB;

    List<Building> bList;
    List<Dining> dList;
    List<Event> eList;

    private int currView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);
        diningsDB = new DiningPersistence(db);
        eventsDB = new EventPersistence(db);
        bList = buildingsDB.getAll(); // TODO: move to AsyncTask
        dList = diningsDB.getAll(); // TODO: move to AsyncTask
        eList = eventsDB.getAll(); // TODO: move to AsyncTask

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

        buildingsViewLabel = findViewById(R.id.buildingsViewLabel);
        diningsViewLabel = findViewById(R.id.diningsViewLabel);
        eventsViewLabel = findViewById(R.id.eventsViewLabel);
        parkingViewLabel = findViewById(R.id.parkingViewLabel);
        printingViewLabel = findViewById(R.id.printingViewLabel);

        searchBar = findViewById(R.id.searchBar);
        drawerButton = findViewById(R.id.drawerButton);

        buttonsState = BUTTONS_HIDDEN;

        buildingsViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBuildingsOverlay(-1);
            }
        });
        diningsViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiningsOverlay(-1);
            }
        });
        eventsViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventsOverlay(-1);
            }
        });
        parkingViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showParkingOverlay();
            }
        });
        printingViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrintersOverlay();
            }
        });
        selectViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonsState == BUTTONS_SHOWN) {
                    hideButtons();
                } else {
                    showButtons();
                }
            }
        });

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

        parkingLayer = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.parking_map))
                .position(new LatLng(33.777470, -84.397780), 2250f); // TODO: alignment
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
        googleMap.setMinZoomPreference(14.0f);
        googleMap.setMaxZoomPreference(24.0f);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setPadding(0, 180, 0, 0); // leave room for search bar
        showUserLocation();

        try {
            printersLayer = new KmlLayer(googleMap, R.raw.printers, getApplicationContext());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent i = getIntent();
        int view = i.getIntExtra(Arguments.DEFAULT_VIEW, -1);
        int objectId = i.getIntExtra(Arguments.OBJECT_ID, -1);

        switch (view) {
            case ViewType.BUILDING:
                showBuildingsOverlay(objectId);
                break;
            case ViewType.DINING:
                showDiningsOverlay(objectId);
                break;
            case ViewType.EVENT:
                showEventsOverlay(objectId);
                break;
            default:
                showBuildingsOverlay(objectId); // TODO: default view for now
        }
    }

    private void showBuildingsOverlay(int objectId) {
        googleMap.clear();
        Building b = buildingsDB.get(objectId);
        if (b != null) {
            Marker m = addBuildingMarker(b);
            m.showInfoWindow();
            centerMarker(m);
        } else {
            for (Building building : bList) {
                addBuildingMarker(building); // TODO: transparent hitboxes?
            }
            resetCamera();
        }
        setCurrView(ViewType.BUILDING);
    }

    private void showDiningsOverlay(int objectId) {
        googleMap.clear();
        Dining d = diningsDB.get(objectId);
        if (d != null) {
            Marker m = addDiningMarker(d);
            m.showInfoWindow();
            centerMarker(m);
        } else {
            for (Dining dining : dList) {
                addDiningMarker(dining);
            }
            resetCamera();
        }
        setCurrView(ViewType.DINING);
    }

    private void showEventsOverlay(int objectId) {
        googleMap.clear();
        Event e = eventsDB.get(objectId);
        if (e != null) {
            Marker m = addEventMarker(e);
            m.showInfoWindow();
            centerMarker(m);
        } else {
            resetCamera();
        }
        setCurrView(ViewType.EVENT);
    }

    private void showParkingOverlay() {
        googleMap.clear();
        googleMap.addGroundOverlay(parkingLayer);
        setCurrView(ViewType.PARKING);
    }

    private void showPrintersOverlay() {
        googleMap.clear();
        try {
            printersLayer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        setCurrView(ViewType.PRINTERS);
    }

    private Marker addBuildingMarker(Building b) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(b.getLatitude(), b.getLongitude()))
                .title(b.getName()));
        m.setTag(b.getId());
        return m;
    }

    private Marker addDiningMarker(Dining d) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(d.getLatitude(), d.getLongitude()))
                .title(d.getName())
                .snippet(d.getLocationDetails()));
        m.setTag(d.getId());
        return m;
    }

    private Marker addEventMarker(Event e) {
        String buildingId = e.getBuildingId();
        Building b = buildingsDB.findByBuildingId(buildingId);
        LatLng ll = new LatLng(33.774637, -84.397321); // Tech Green by default
        if (b != null) {
            ll = new LatLng(b.getLatitude(), b.getLongitude());

        }
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(ll)
                .title(e.getTitle())
                .snippet(e.getLocation()));
        m.setTag(e.getId());
        return m;
    }

    private void showUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
    }

    private void centerMarker(Marker m) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 18.0f));
    }

    private void resetCamera() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.777433, -84.398636), 14.6f));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.777433, -84.398636), 14.6f)); // tech campus
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
                // TODO: ?
        }
        hideButtons();
        currView = v;
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
        buttonsState = BUTTONS_SHOWN;
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
        buttonsState = BUTTONS_HIDDEN;
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        showUserLocation();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        centerMarker(marker);
        return false;
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
            case ViewType.DINING: // TODO: open DiningDetailsActivity
                Intent diningDetailIntent = new Intent(MapActivity.this, DiningDetailsActivity.class);
                diningDetailIntent.putExtra(Arguments.OBJECT_ID, Integer.parseInt(marker.getTag().toString()));
                startActivity(diningDetailIntent);
                break;
            case ViewType.EVENT: // TODO: open EventDetailsActivity
                Intent eventDetailIntent = new Intent(MapActivity.this, EventDetailsActivity.class);
                eventDetailIntent.putExtra(Arguments.OBJECT_ID, Integer.parseInt(marker.getTag().toString()));
                startActivity(eventDetailIntent);
                break;
            default:
                Toast.makeText(this, "InfoWindow clicked!", Toast.LENGTH_SHORT).show();
        }
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
                // TODO: ?
        }
    }
}