package com.example.vi_tu.gtinteractive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.joda.time.LocalTime;

import java.util.List;

public class BuildingDetailsActivity extends AppCompatActivity implements NetworkErrorDialogFragment.NetworkErrorDialogListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

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

        networkUtils = new NetworkUtils(getApplicationContext(), getFragmentManager());
        networkUtils.updateDiningStatus(diningsDB); // TODO: only update dinings associated with building?

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
                    TextView nameText = rootView.findViewById(R.id.nameText);
                    TextView altNamesText = rootView.findViewById(R.id.altNamesText);
                    TextView hoursText = rootView.findViewById(R.id.hoursText);
                    TextView addressText = rootView.findViewById(R.id.addressText);
                    Button showInMap = rootView.findViewById(R.id.showInMapButton);
                    Button viewInternalLayout = rootView.findViewById(R.id.viewInternalLayoutButton);

                    String hoursString = "n/a";
                    LocalTime[] ot = b.getOpenTimes();
                    LocalTime[] ct = b.getCloseTimes();
                    if (ot != null && ot.length >= 1 && ct != null && ct.length >= 1) {
                        LocalTime openTime = ot[1]; // TODO
                        LocalTime closeTime = ct[1]; // TODO
                        if (openTime != null && closeTime != null) {
                            hoursString = openTime.toString("hh:mm a") + " - " + closeTime.toString("hh:mm a") + " (Monday)";
                        }
                    }


                    nameText.setText(b.getName());
                    altNamesText.setText(b.getAltNames());
                    hoursText.setText(hoursString);
                    addressText.setText(b.getStreet());
                    showInMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent mapActivityIntent = new Intent(context, MapActivity.class);
                            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
                            mapActivityIntent.putExtra(Arguments.OBJECT_ID, b.getId());
                            context.startActivity(mapActivityIntent);
                        }
                    });
                    viewInternalLayout.setOnClickListener(new View.OnClickListener() {
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




