package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class mapFilter_parking extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    Button filterButton;
    Button printButton;
    Button diningButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        filterButton = (Button) findViewById(R.id.filterButton);
        printButton = (Button) findViewById(R.id.printButton);
        diningButton = (Button) findViewById(R.id.diningButton);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clear = new Intent(mapFilter_parking.this, MapActivity.class);
                startActivity(clear);
            }
        });

        diningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent diningIntent = new Intent(mapFilter_parking.this, mapFilter_dining.class);
                startActivity(diningIntent);
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent printIntent = new Intent(mapFilter_parking.this, PrinterActivity.class);
                startActivity(printIntent);
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
        mMap = googleMap;
        mMap.setMinZoomPreference(14.0f);
        mMap.setMaxZoomPreference(24.0f);

        // E/W Zones - Non Residential
        LatLng klausLL = new LatLng(33.777715, -84.396240);
        Marker klaus = mMap.addMarker(new MarkerOptions().position(klausLL).title("E40 Klaus Parking Deck"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(klausLL));
        klaus.showInfoWindow();

        LatLng centergyLL = new LatLng(33.778130, -84.390260);
        Marker centergy = mMap.addMarker(new MarkerOptions().position(centergyLL).title("E82 Centergy Parking Deck"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centergyLL));
        centergy.showInfoWindow();

        LatLng stucenLL = new LatLng(33.773922, -84.400111);
        Marker stucen = mMap.addMarker(new MarkerOptions().position(stucenLL).title("W02 Student Center Parking Deck"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(stucenLL));
        stucen.showInfoWindow();

        LatLng crcLL = new LatLng(33.776206, -84.403944);
        Marker crc = mMap.addMarker(new MarkerOptions().position(crcLL).title("W10 CRC Parking Deck"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(crcLL));
        crc.showInfoWindow();

        LatLng petersLL = new LatLng(33.775014, -84.393511);
        Marker peters = mMap.addMarker(new MarkerOptions().position(petersLL).title("E52 Peters Parking Deck"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(petersLL));
        peters.showInfoWindow();
    }
}
