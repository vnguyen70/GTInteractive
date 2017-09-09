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

public class mapFilter_dining extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    Button filterButton;
    Button printButton;
    Button parkingButton;

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
        parkingButton = (Button) findViewById(R.id.parkingButton);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clear = new Intent(mapFilter_dining.this, MapActivity.class);
                startActivity(clear);
            }
        });

        parkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parkIntent = new Intent(mapFilter_dining.this, mapFilter_parking.class);
                startActivity(parkIntent);
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent printingIntent = new Intent(mapFilter_dining.this, PrinterActivity.class);
                startActivity(printingIntent);
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

        // ----Student Center----
        LatLng subwayLL = new LatLng(33.773692, -84.397800);
        Marker subway = mMap.addMarker(new MarkerOptions().position(subwayLL).title("Subway").snippet("Student Center, On the 1st Floor, Next to Kaplan Center"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(subwayLL));
        subway.showInfoWindow();

        LatLng pandaLL = new LatLng(33.773585, -84.398200);
        Marker panda = mMap.addMarker(new MarkerOptions().position(pandaLL).title("Panda Express"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pandaLL));
        panda.showInfoWindow();

        LatLng ferstplaceLL = new LatLng(33.774271, -84.398899);
        Marker ferst = mMap.addMarker(new MarkerOptions().position(ferstplaceLL).title("Ferst Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ferstplaceLL));
        ferst.showInfoWindow();

        LatLng dunkinLL = new LatLng(33.774364, -84.398823);
        Marker dunkin = mMap.addMarker(new MarkerOptions().position(dunkinLL).title("Dunkin Donuts"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dunkinLL));
        dunkin.showInfoWindow();

        // ----Dining Halls----
        LatLng brittainLL = new LatLng(33.772462, -84.391272);
        Marker brittain = mMap.addMarker(new MarkerOptions().position(brittainLL).title("Brittain Dining Hall"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(brittainLL));
        brittain.showInfoWindow();

        LatLng woodiesLL = new LatLng(33.779104, -84.406536);
        Marker woodies = mMap.addMarker(new MarkerOptions().position(woodiesLL).title("Woodies Dining Hall"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(woodiesLL));
        woodies.showInfoWindow();

        LatLng northaveLL = new LatLng(33.771095, -84.391585);
        Marker northave = mMap.addMarker(new MarkerOptions().position(northaveLL).title("North Avenue Dining Hall"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(northaveLL));
        northave.showInfoWindow();
    }

}
