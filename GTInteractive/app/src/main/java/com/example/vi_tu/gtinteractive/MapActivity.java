package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap googleMap;

    KmlLayer printersLayer;

    Button diningButton;
    Button printButton;
    Button parkingButton;

    private BuildingPersistence buildingsDB;
    private DiningPersistence diningsDB;

    List<Building> bList;
    List<Dining> dList;

    private String currView;

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
        bList = buildingsDB.getAll(); // TODO: move to AsyncTask
        dList = diningsDB.getAll(); // TODO: move to AsyncTask

        diningButton = findViewById(R.id.diningButton);
        printButton = findViewById(R.id.printButton);
        parkingButton = findViewById(R.id.parkingButton);

        diningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiningOverlay();
            }
        });
        parkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showParkingOverlay();
            }
        });
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrinterOverlay();
            }
        });

        currView = "";
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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.777433, -84.398636), (float) 14.6)); // gatech
        googleMap.setMinZoomPreference(14.0f);
        googleMap.setMaxZoomPreference(24.0f);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        try {
            printersLayer = new KmlLayer(googleMap, R.raw.printers, getApplicationContext());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        showDiningOverlay(); // TODO: default view for now
    }

    private void showDiningOverlay() {
        googleMap.clear();
        for (Dining d : dList) {
           Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(d.getLatitude(), d.getLongitude()))
                    .title(d.getName())
                    .snippet(d.getLocationDetails()));
            m.setTag(d.getBuildingId());
            Log.d("GOOGLE_MAP", "DINING OBJECT: (" + d.getLatitude() + ", " + d.getLongitude() + ") " + d.getName());
        }
        currView = "dining";
    }

    private void showParkingOverlay() {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(new LatLng(33.777715, -84.396240)).title("E40 Klaus Parking Deck"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(33.778130, -84.390260)).title("E82 Centergy Parking Deck"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(33.773922, -84.400111)).title("W02 Student Center Parking Deck"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(33.776206, -84.403944)).title("W10 CRC Parking Deck"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(33.775014, -84.393511)).title("E52 Peters Parking Deck"));
        currView = "parking";
    }

    private void showPrinterOverlay() {
        googleMap.clear();
        try {
            printersLayer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        currView = "printers";
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (currView.equals("dining")) {
            Intent buildingDetailIntent = new Intent(MapActivity.this, BuildingDetailActivity.class);
            buildingDetailIntent.putExtra("buildingId", marker.getTag().toString());
            buildingDetailIntent.putExtra("defaultTab", "dining");
            startActivity(buildingDetailIntent);
        }
    }
}