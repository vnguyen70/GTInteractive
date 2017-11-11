package com.example.vi_tu.gtinteractive;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.adapters.EventAdapter;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.TabType;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializePolygons;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializeTimes;

//import com.squareup.picasso.Picasso;

public class PlaceDetailsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ImageView placeImageView;

    private PlacePersistence placesDB;
    private EventPersistence eventsDB;

    private NetworkUtils networkUtils;

    private int objectId;
    private static Place p; // place to display

    private static List<Event> eList;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        placeImageView = (ImageView) findViewById(R.id.placeImageView);

        context = this;

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        placesDB = new PlacePersistence(db);
        eventsDB = new EventPersistence(db);

        // place to display
        p = Place.DUMMY;
        objectId = getIntent().getIntExtra(Arguments.OBJECT_ID, -1);
        Place temp = placesDB.get(objectId);
        if (temp != null) {
            p = temp;
        }
        Picasso.with(this).load(p.getImageURL()).fit().into(placeImageView); // TODO: store the bitmaps into database and load image from database
        Log.d("PlaceDetailsActivity", "hello " + p.getPlaceId());
        networkUtils = new NetworkUtils(getApplicationContext(), getFragmentManager());

        // Initializing animated scroll for the toolbar
        CollapsingToolbarLayout collapsingToolBarLayout
                = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolBarLayout.setTitle(p.getName());

        eList = eventsDB.getAll();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        int tab = getIntent().getIntExtra(Arguments.DEFAULT_TAB, -1);
        int position;
        switch(tab) {
            case TabType.INFO:
                position = 0;
                break;
            case TabType.EVENT:
                position = 1;
                break;
            default:
                position = 0; // info tab by default;
        }
        mViewPager.setCurrentItem(position); // info tab by default
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int sectionNum = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView;
            switch (sectionNum) {
                case 1: // events
                    rootView = inflater.inflate(R.layout.fragment_place_details_event, container, false);
                    RecyclerView eventsView = rootView.findViewById(R.id.eventsRecyclerView);
                    eventsView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    eventsView.setAdapter(new EventAdapter(eList));
                    break;
                default: // info (e.g. sectionNum = 0)
                    rootView = inflater.inflate(R.layout.fragment_place_details_info, container, false);

                    TextView idTextView = rootView.findViewById(R.id.idText);
                    TextView placeIdTextView = rootView.findViewById(R.id.placeIdText);
                    TextView nameTextView = rootView.findViewById(R.id.nameText);
                    TextView imageURLTextView = rootView.findViewById(R.id.imageURLText);
                    TextView websiteURLTextView = rootView.findViewById(R.id.websiteURLText);
                    TextView phoneNumTextView = rootView.findViewById(R.id.phoneNumText);
                    TextView streetTextView = rootView.findViewById(R.id.streetText);
                    TextView cityTextView = rootView.findViewById(R.id.cityText);
                    TextView stateTextView = rootView.findViewById(R.id.stateText);
                    TextView postalCodeTextView = rootView.findViewById(R.id.postalCodeText);
                    TextView latitudeTextView = rootView.findViewById(R.id.latitudeText);
                    TextView longitudeTextView = rootView.findViewById(R.id.longitudeText);
                    TextView polygonsTextView = rootView.findViewById(R.id.polygonsText);
                    TextView descriptionTextView = rootView.findViewById(R.id.descriptionText);
                    TextView locatedInTextView = rootView.findViewById(R.id.locatedInText);
                    TextView yelpIDTextView = rootView.findViewById(R.id.yelpIDText);
                    TextView openTimesTextView = rootView.findViewById(R.id.openTimesText);
                    TextView closeTimesTextView = rootView.findViewById(R.id.closeTimesText);
                    TextView acceptsBuzzFundsTextView = rootView.findViewById(R.id.acceptsBuzzFundsText);
                    TextView priceLevelTextView = rootView.findViewById(R.id.priceLevelText);
                    TextView categoryTextVew = rootView.findViewById(R.id.categoryText);
                    TextView altNamesTextVew = rootView.findViewById(R.id.altNamesText);
                    TextView nameTokensTextView = rootView.findViewById(R.id.nameTokensText);
                    TextView addressTokensTextVew = rootView.findViewById(R.id.addressTokensText);
                    TextView numFloorsTextView = rootView.findViewById(R.id.numFloorsText);
                    Button showInMapButton = rootView.findViewById(R.id.showInMapButton);
                    Button viewInternalLayoutButton = rootView.findViewById(R.id.viewInternalLayoutButton);

                    idTextView.setText(String.valueOf(p.getId()));
                    placeIdTextView.setText(p.getPlaceId());
                    nameTextView.setText(p.getName());
                    imageURLTextView.setText(p.getImageURL());
                    websiteURLTextView.setText(p.getWebsiteURL());
                    phoneNumTextView.setText(p.getPhoneNum());
                    streetTextView.setText(p.getStreet());
                    cityTextView.setText(p.getCity() + ",");
                    stateTextView.setText(p.getState());
                    postalCodeTextView.setText(p.getPostalCode());
                    latitudeTextView.setText(String.valueOf(p.getLatitude()));
                    longitudeTextView.setText(String.valueOf(p.getLongitude()));
                    polygonsTextView.setText(serializePolygons(p.getPolygons()));
                    descriptionTextView.setText(p.getDescription());
                    locatedInTextView.setText(p.getLocatedIn());
                    yelpIDTextView.setText(p.getYelpID());
                    openTimesTextView.setText(serializeTimes(p.getOpenTimes()));
                    closeTimesTextView.setText(serializeTimes(p.getCloseTimes()));
                    acceptsBuzzFundsTextView.setText(String.valueOf(p.getAcceptsBuzzFunds()));
                    priceLevelTextView.setText(String.valueOf(p.getPriceLevel()));
                    categoryTextVew.setText(p.getCategory().name());
                    altNamesTextVew.setText(p.getAltNames());
                    nameTokensTextView.setText(p.getNameTokens());
                    addressTokensTextVew.setText(p.getAddressTokens());
                    numFloorsTextView.setText(String.valueOf(p.getNumFloors()));

                    final Place finalP = p;
                    showInMapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent mapActivityIntent = new Intent(context, MapActivity.class);
                            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
                            mapActivityIntent.putExtra(Arguments.OBJECT_ID, finalP.getId());
                            startActivity(mapActivityIntent);
                        }
                    });
                    viewInternalLayoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent internalMapIntent = new Intent(context, InternalMapActivity.class);
                            internalMapIntent.putExtra(Arguments.OBJECT_ID, p.getId());
                            startActivity(internalMapIntent);
                        }
                    });
            }
            return rootView;
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Events";
            }
            return null;
        }
    }

}




