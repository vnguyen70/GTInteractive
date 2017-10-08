package com.example.vi_tu.gtinteractive;

import android.content.Context;
import android.content.DialogInterface;
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

import com.example.vi_tu.gtinteractive.adapters.DiningAdapter;
import com.example.vi_tu.gtinteractive.adapters.EventAdapter;
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
import com.example.vi_tu.gtinteractive.utilities.NetworkErrorDialogFragment;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializePolygons;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializeTimes;

//import com.squareup.picasso.Picasso;

public class BuildingDetailsActivity extends AppCompatActivity implements NetworkErrorDialogFragment.NetworkErrorDialogListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ImageView buildingImageView;

    private BuildingPersistence buildingsDB;
    private DiningPersistence diningsDB;
    private EventPersistence eventsDB;

    private NetworkUtils networkUtils;

    private int objectId;
    private static Building b; // building to display

    private static List<Event> eList;
    private static List<Dining> dList;

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
        diningsDB = new DiningPersistence(db);
        eventsDB = new EventPersistence(db);

        // building to display
        b = Building.DUMMY;
        objectId = getIntent().getIntExtra(Arguments.OBJECT_ID, -1);
        Building temp = buildingsDB.get(objectId);
        if (temp != null) {
            b = temp;
        }
        Picasso.with(this).load(b.getImageURL()).fit().into(buildingImageView); // TODO: store the bitmaps into database and load image from database
        Log.d("BuildingDetailsActivity", "hello " + b.getBuildingId());
        networkUtils = new NetworkUtils(getApplicationContext(), getFragmentManager());
        networkUtils.updateDiningStatus(diningsDB); // TODO: only update dinings associated with building?

        // Initializing animated scroll for the toolbar
        CollapsingToolbarLayout collapsingToolBarLayout
                = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolBarLayout.setTitle(b.getName());

//        updateDiningStatus(diningsDB, getApplicationContext()); // TODO: only update dinings associated with building?

        dList = diningsDB.getAll();
        eList = eventsDB.getAll();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        int tab = getIntent().getIntExtra(Arguments.DEFAULT_TAB, -1);
        int position;
        switch(tab) {
            case TabType.INFO:
                position = 0;
                break;
            case TabType.DINING:
                position = 1;
                break;
            case TabType.EVENT:
                position = 2;
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

    @Override
    public void onDialogPositiveClick(DialogInterface dialog) {
        networkUtils.updateDiningStatus(diningsDB);
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog) {
        // TODO: show toast?
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
                case 1: // dining
                    rootView = inflater.inflate(R.layout.fragment_building_details_dining, container, false);
                    RecyclerView diningsView = rootView.findViewById(R.id.diningsRecyclerView);
                    diningsView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    diningsView.setAdapter(new DiningAdapter(dList));
                    break;
                case 2: // events
                    rootView = inflater.inflate(R.layout.fragment_building_details_events, container, false);
                    RecyclerView eventsView = rootView.findViewById(R.id.eventsRecyclerView);
                    eventsView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    eventsView.setAdapter(new EventAdapter(eList));
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
                    TextView categoryTextVew = rootView.findViewById(R.id.categoryText);
                    TextView altNamesTextVew = rootView.findViewById(R.id.altNamesText);
                    TextView nameTokensTextView = rootView.findViewById(R.id.nameTokensText);
                    TextView addressTokensTextVew = rootView.findViewById(R.id.addressTokensText);
                    TextView numFloorsTextView = rootView.findViewById(R.id.numFloorsText);
                    Button showInMapButton = rootView.findViewById(R.id.showInMapButton);
                    Button viewInternalLayoutButton = rootView.findViewById(R.id.viewInternalLayoutButton);

                    idTextView.setText(String.valueOf(b.getId()));
                    buildingIdTextView.setText(b.getBuildingId());
                    nameTextView.setText(b.getName());
                    imageURLTextView.setText(b.getImageURL());
                    websiteURLTextView.setText(b.getWebsiteURL());
                    phoneNumTextView.setText(b.getPhoneNum());
                    streetTextView.setText(b.getStreet());
                    cityTextView.setText(b.getCity());
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
                    categoryTextVew.setText(b.getCategory().name());
                    altNamesTextVew.setText(b.getAltNames());
                    nameTokensTextView.setText(b.getNameTokens());
                    addressTokensTextVew.setText(b.getAddressTokens());
                    numFloorsTextView.setText(String.valueOf(b.getNumFloors()));

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
                    viewInternalLayoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent internalMapIntent = new Intent(context, InternalMapActivity.class);
                            internalMapIntent.putExtra(Arguments.OBJECT_ID, b.getId());
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
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Dining";
                case 2:
                    return "Events";
            }
            return null;
        }
    }

}




