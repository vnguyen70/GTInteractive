package com.example.vi_tu.gtinteractive;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.graphics.Color;
import android.media.Image;
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
import com.example.vi_tu.gtinteractive.adapters.EventListAdapter;
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

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

import static android.R.color.white;
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

        // Logic to see if place has any events currently going on here

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
                    eventsView.setAdapter(new EventListAdapter(eList));
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
                    TextView categoryTextView = rootView.findViewById(R.id.categoryText);
                    TextView altNamesTextView = rootView.findViewById(R.id.altNamesText);
                    TextView nameTokensTextView = rootView.findViewById(R.id.nameTokensText);
                    TextView addressTokensTextView = rootView.findViewById(R.id.addressTokensText);
                    TextView numFloorsTextView = rootView.findViewById(R.id.numFloorsText);
                    Button showInMapButton = rootView.findViewById(R.id.location_listener);
                    Button viewInternalLayoutButton = rootView.findViewById(R.id.viewInternalLayoutButton);

                    View placeIdLine = rootView.findViewById(R.id.line_placeIdText);
                    View nameLine = rootView.findViewById(R.id.line_nameText);
                    View imageURLLine = rootView.findViewById(R.id.line_imageURLText);
                    View addressLine = rootView.findViewById(R.id.line_addressText);
                    View openTimesLine = rootView.findViewById(R.id.line_openTimesText);
                    View phoneNumLine = rootView.findViewById(R.id.line_phoneNumText);
                    View websiteURLLine = rootView.findViewById(R.id.line_websiteURLText);
                    View categoryLine = rootView.findViewById(R.id.line_categoryText);
                    View latitudeLine = rootView.findViewById(R.id.line_latitudeText);
                    View polygonLine = rootView.findViewById(R.id.line_polygonText);
                    View descriptionLine = rootView.findViewById(R.id.line_descriptionText);
                    View yelpIDLine = rootView.findViewById(R.id.line_yelpIDText);
                    View acceptsBuzzFundsLine = rootView.findViewById(R.id.line_acceptsBuzzFundsText);
                    View priceLevelLine = rootView.findViewById(R.id.line_priceLevelText);
                    View altNamesLine = rootView.findViewById(R.id.line_altNamesText);
                    View nameTokensLine = rootView.findViewById(R.id.line_nameTokensText);
                    View addressTokensLine = rootView.findViewById(R.id.line_addressTokensText);
                    View numFloorsLine = rootView.findViewById(R.id.line_numFloorsText);

                    ImageView nameIcon = rootView.findViewById(R.id.image_name);
                    ImageView addressIcon = rootView.findViewById(R.id.image_address);
                    ImageView hoursIcon = rootView.findViewById(R.id.image_hours);
                    ImageView phoneIcon = rootView.findViewById(R.id.image_phone);
                    ImageView websiteIcon = rootView.findViewById(R.id.image_website);
                    ImageView categoryIcon = rootView.findViewById(R.id.image_category);
                    ImageView descriptionIcon = rootView.findViewById(R.id.image_description);
                    ImageView buzzfundsIcon = rootView.findViewById(R.id.image_buzzfunds);
                    ImageView priceIcon = rootView.findViewById(R.id.image_price);

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
                    categoryTextView.setText(p.getCategory().name());
                    altNamesTextView.setText(p.getAltNames());
                    nameTokensTextView.setText(p.getNameTokens());
                    addressTokensTextView.setText(p.getAddressTokens());
                    numFloorsTextView.setText(String.valueOf(p.getNumFloors()));

                    if(p.getName() != null) {
//                        nameLine.setVisibility(View.VISIBLE);
                        nameIcon.setVisibility(View.VISIBLE);
                        nameTextView.setVisibility(View.VISIBLE);
                        viewInternalLayoutButton.setVisibility(View.VISIBLE);
                    }

                    if(p.getStreet() != null || p.getLocatedIn() != null) {
                        addressLine.setVisibility(View.VISIBLE);
                        addressIcon.setVisibility(View.VISIBLE);
                        if(p.getStreet() != null) {
                            streetTextView.setVisibility(View.VISIBLE);
                            cityTextView.setVisibility(View.VISIBLE);
                            stateTextView.setVisibility(View.VISIBLE);
                            postalCodeTextView.setVisibility(View.VISIBLE);
                            showInMapButton.setVisibility(View.VISIBLE);
                        } else {
                            locatedInTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    if(p.getOpenTimes() != null && p.getCloseTimes() != null
                            && !p.getOpenTimes().toString().startsWith("NULL")
                            && !p.getCloseTimes().toString().startsWith("NULL")) {
                        openTimesLine.setVisibility(View.VISIBLE);
                        hoursIcon.setVisibility(View.VISIBLE);
                        openTimesTextView.setVisibility(View.VISIBLE);
                        closeTimesTextView.setVisibility(View.VISIBLE);
                        openTimesTextView.setText("N/A");
                        closeTimesTextView.setText("");
                    }

                    if(p.getPhoneNum() != null && p.getPhoneNum().startsWith("+")) {
                        phoneNumLine.setVisibility(View.VISIBLE);
                        phoneIcon.setVisibility(View.VISIBLE);
                        phoneNumTextView.setVisibility(View.VISIBLE);
                    }

                    if(p.getWebsiteURL() != null && p.getWebsiteURL().startsWith("h")) {
                        websiteURLLine.setVisibility(View.VISIBLE);
                        websiteIcon.setVisibility(View.VISIBLE);
                        websiteURLTextView.setVisibility(View.VISIBLE);
                    }

                    if(p.getCategory() != null) {
                        categoryLine.setVisibility(View.VISIBLE);
                        categoryIcon.setVisibility(View.VISIBLE);
                        categoryTextView.setVisibility(View.VISIBLE);
                    }

                    if(p.getDescription() != null && !p.getDescription().trim().equals("")) {
                        descriptionLine.setVisibility(View.VISIBLE);
                        descriptionIcon.setVisibility(View.VISIBLE);
                        descriptionTextView.setVisibility(View.VISIBLE);
                    }

                    if(p.getAcceptsBuzzFunds()) {
                        acceptsBuzzFundsLine.setVisibility(View.VISIBLE);
                        buzzfundsIcon.setVisibility(View.VISIBLE);
                        acceptsBuzzFundsTextView.setVisibility(View.VISIBLE);
                        acceptsBuzzFundsTextView.setText("Accepts BuzzFunds");
                    }

                    if(p.getPriceLevel() != null && p.getCategory().name().equals("FOOD")) {
                        priceLevelLine.setVisibility(View.VISIBLE);
                        priceIcon.setVisibility(View.VISIBLE);
                        priceLevelTextView.setVisibility(View.VISIBLE);
                    }

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
                    streetTextView.setOnClickListener(new View.OnClickListener() {
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
                    phoneNumTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:"+ finalP.getPhoneNum()));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }
                    });
                    websiteURLTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(finalP.getWebsiteURL()));
                            startActivity(browserIntent);
                        }
                    });
                    categoryTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent buildingListIntent = new Intent(context, PlaceListActivity.class);
                            buildingListIntent.putExtra(Arguments.OBJECT_ID, p.getCategory().name());
                            startActivity(buildingListIntent);
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




