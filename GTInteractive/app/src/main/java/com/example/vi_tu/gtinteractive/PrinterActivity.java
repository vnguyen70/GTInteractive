package com.example.vi_tu.gtinteractive;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PrinterActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        LatLng gatech = new LatLng(33.777433, -84.398636);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gatech, (float) 14.6));

        LatLng glc = new LatLng(33.781806, -84.396332);
        mMap.addMarker(new MarkerOptions().position(glc).title("Graduate Living Center"));

        LatLng fitten = new LatLng(33.778227, -84.403711);
        mMap.addMarker(new MarkerOptions().position(fitten).title("Fitten Hall"));

        LatLng woodruff = new LatLng(33.779104, -84.406561);
        mMap.addMarker(new MarkerOptions().position(woodruff).title("Woodruff Hall"));

        LatLng brittainrec = new LatLng(33.772458, -84.391261);
        mMap.addMarker(new MarkerOptions().position(brittainrec).title("Brittain Rec"));

        LatLng glen = new LatLng(33.773712, -84.391283);
        mMap.addMarker(new MarkerOptions().position(glen).title("Glenn/Towers Connector"));

        LatLng northave = new LatLng(33.770510, -84.391205);
        mMap.addMarker(new MarkerOptions().position(northave).title("North Ave Apts"));

        LatLng library = new LatLng(33.774256, -84.395736);
        mMap.addMarker(new MarkerOptions().position(library).title("Library"));

        LatLng culc = new LatLng(33.774945, -84.396412);
        mMap.addMarker(new MarkerOptions().position(culc).title("Clough"));

        LatLng sc = new LatLng(33.774274, -84.398896);
        mMap.addMarker(new MarkerOptions().position(sc).title("Student Center"));

        LatLng coc = new LatLng(33.777435, -84.397342);
        mMap.addMarker(new MarkerOptions().position(coc).title("College of Computing"));

        LatLng klaus = new LatLng(33.773712, -84.391283);
        mMap.addMarker(new MarkerOptions().position(klaus).title("Klaus Building"));

        LatLng mrdc = new LatLng(33.777029, -84.400624);
        mMap.addMarker(new MarkerOptions().position(mrdc).title("MRDC"));

        LatLng scheller = new LatLng(33.776366, -84.388415);
        mMap.addMarker(new MarkerOptions().position(scheller).title("Scheller CoB"));
    }
}
