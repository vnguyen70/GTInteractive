package com.example.vi_tu.gtinteractive;

import android.content.Context;
import android.content.DialogInterface;
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
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.NetworkErrorDialogFragment;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

import static android.R.color.white;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializePolygons;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializeTimes;

//import com.squareup.picasso.Picasso;

public class BuildingDetailsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ImageView buildingImageView;

    private BuildingPersistence buildingsDB;
    private EventPersistence eventsDB;

    private NetworkUtils networkUtils;

    private int objectId;
    private static Building b; // building to display

    private static List<Event> eList;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildingImageView = (ImageView) findViewById(R.id.buildingImageView);

        context = this;

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);
        eventsDB = new EventPersistence(db);

        // building to display
        b = Building.DUMMY;
        objectId = getIntent().getIntExtra(Arguments.OBJECT_ID, -1);
        Building temp = buildingsDB.get(objectId);
        if (temp != null) {
            b = temp;
        }
        Picasso.with(this).load(b.getImageURL()).fit().into(buildingImageView); // COMPLETE(?): store the bitmaps into database and load image from database
        Log.d("BuildingDetailsActivity", "hello " + b.getBuildingId());
        networkUtils = new NetworkUtils(getApplicationContext(), getFragmentManager());

        // Initializing animated scroll for the toolbar
        CollapsingToolbarLayout collapsingToolBarLayout
                = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolBarLayout.setTitle(b.getName());

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
                    rootView = inflater.inflate(R.layout.fragment_building_details_events, container, false);
                    RecyclerView eventsView = rootView.findViewById(R.id.eventsRecyclerView);
                    eventsView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    eventsView.setAdapter(new EventListAdapter(eList));
                    break;
                default: // info (e.g. sectionNum = 0)
                    rootView = inflater.inflate(R.layout.fragment_building_details_info, container, false);

                    TextView idTextView = rootView.findViewById(R.id.idText);
                    TextView buildingIdTextView = rootView.findViewById(R.id.buildingIdText);
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

                    View buildingIdLine = rootView.findViewById(R.id.line_buildingIdText);
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

                    idTextView.setText(String.valueOf(b.getId()));
                    buildingIdTextView.setText(b.getBuildingId());
                    nameTextView.setText(b.getName());
                    imageURLTextView.setText(b.getImageURL());
                    websiteURLTextView.setText(b.getWebsiteURL());
                    phoneNumTextView.setText(b.getPhoneNum());
                    streetTextView.setText(b.getStreet());
                    cityTextView.setText(b.getCity() + ",");
                    stateTextView.setText(b.getState());
                    postalCodeTextView.setText(b.getPostalCode());
                    latitudeTextView.setText(String.valueOf(b.getLatitude()));
                    longitudeTextView.setText(String.valueOf(b.getLongitude()));
                    polygonsTextView.setText(serializePolygons(b.getPolygons()));
                    descriptionTextView.setText(b.getDescription());
                    locatedInTextView.setText(b.getLocatedIn());
                    yelpIDTextView.setText(b.getYelpID());
                    openTimesTextView.setText(serializeTimes(b.getOpenTimes()));
                    closeTimesTextView.setText(serializeTimes(b.getCloseTimes()));
                    acceptsBuzzFundsTextView.setText(String.valueOf(b.getAcceptsBuzzFunds()));
                    priceLevelTextView.setText(String.valueOf(b.getPriceLevel()));
                    categoryTextView.setText(b.getCategory().name());
                    altNamesTextView.setText(b.getAltNames());
                    nameTokensTextView.setText(b.getNameTokens());
                    addressTokensTextView.setText(b.getAddressTokens());
                    numFloorsTextView.setText(String.valueOf(b.getNumFloors()));

                    if(b.getName() != null) {
//                        nameLine.setVisibility(View.VISIBLE);
                        nameIcon.setVisibility(View.VISIBLE);
                        nameTextView.setVisibility(View.VISIBLE);
                        viewInternalLayoutButton.setVisibility(View.VISIBLE);
                    }

                    if(b.getStreet() != null || b.getLocatedIn() != null) {
                        addressLine.setVisibility(View.VISIBLE);
                        addressIcon.setVisibility(View.VISIBLE);
                        if(b.getStreet() != null) {
                            streetTextView.setVisibility(View.VISIBLE);
                            cityTextView.setVisibility(View.VISIBLE);
                            stateTextView.setVisibility(View.VISIBLE);
                            postalCodeTextView.setVisibility(View.VISIBLE);
                            showInMapButton.setVisibility(View.VISIBLE);
                        } else {
                            locatedInTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    if(b.getOpenTimes() != null && b.getCloseTimes() != null
                            && !b.getOpenTimes().toString().startsWith("NULL")
                            && !b.getCloseTimes().toString().startsWith("NULL")) {
                        openTimesLine.setVisibility(View.VISIBLE);
                        hoursIcon.setVisibility(View.VISIBLE);
                        openTimesTextView.setVisibility(View.VISIBLE);
                        closeTimesTextView.setVisibility(View.VISIBLE);
                        openTimesTextView.setText("N/A");
                        closeTimesTextView.setText("");
                    }

                    if(b.getPhoneNum() != null && b.getPhoneNum().startsWith("+")) {
                        phoneNumLine.setVisibility(View.VISIBLE);
                        phoneIcon.setVisibility(View.VISIBLE);
                        phoneNumTextView.setVisibility(View.VISIBLE);
                    }

                    if(b.getWebsiteURL() != null && b.getWebsiteURL().startsWith("h")) {
                        websiteURLLine.setVisibility(View.VISIBLE);
                        websiteIcon.setVisibility(View.VISIBLE);
                        websiteURLTextView.setVisibility(View.VISIBLE);
                    }

                    if(b.getCategory() != null) {
                        categoryLine.setVisibility(View.VISIBLE);
                        categoryIcon.setVisibility(View.VISIBLE);
                        categoryTextView.setVisibility(View.VISIBLE);
                    }

                    if(b.getDescription() != null && !b.getDescription().trim().equals("")) {
                        descriptionLine.setVisibility(View.VISIBLE);
                        descriptionIcon.setVisibility(View.VISIBLE);
                        descriptionTextView.setVisibility(View.VISIBLE);
                    }

                    if(b.getAcceptsBuzzFunds()) {
                        acceptsBuzzFundsLine.setVisibility(View.VISIBLE);
                        buzzfundsIcon.setVisibility(View.VISIBLE);
                        acceptsBuzzFundsTextView.setVisibility(View.VISIBLE);
                        acceptsBuzzFundsTextView.setText("Accepts BuzzFunds");
                    }

                    if(b.getPriceLevel() != null && b.getCategory().name().equals("FOOD")) {
                        priceLevelLine.setVisibility(View.VISIBLE);
                        priceIcon.setVisibility(View.VISIBLE);
                        priceLevelTextView.setVisibility(View.VISIBLE);
                    }

                    final Building finalB = b;
                    showInMapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent mapActivityIntent = new Intent(context, MapActivity.class);
                            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
                            mapActivityIntent.putExtra(Arguments.OBJECT_ID, finalB.getId());
                            startActivity(mapActivityIntent);
                        }
                    });
                    streetTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent mapActivityIntent = new Intent(context, MapActivity.class);
                            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
                            mapActivityIntent.putExtra(Arguments.OBJECT_ID, finalB.getId());
                            startActivity(mapActivityIntent);
                        }
                    });
                    viewInternalLayoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent internalMapIntent = new Intent(context, InternalMapActivity.class);
                            internalMapIntent.putExtra(Arguments.OBJECT_ID, b.getId());
                            startActivity(internalMapIntent);
                        }
                    });
                    phoneNumTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:"+ finalB.getPhoneNum()));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }
                    });
                    websiteURLTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(finalB.getWebsiteURL()));
                            startActivity(browserIntent);
                        }
                    });
                    categoryTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent buildingListIntent = new Intent(context, BuildingListActivity.class);
                            buildingListIntent.putExtra(Arguments.OBJECT_ID, b.getCategory().name());
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




