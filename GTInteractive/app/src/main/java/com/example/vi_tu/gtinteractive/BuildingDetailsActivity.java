package com.example.vi_tu.gtinteractive;

import android.content.Context;
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
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.temp.DiningAdapter;
import com.example.vi_tu.gtinteractive.temp.EventAdapter;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.NetworkUtils.updateDiningStatus;

public class BuildingDetailsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private BuildingPersistence buildingsDB;
    private DiningPersistence diningsDB;
    private EventPersistence eventsDB;

    private String buildingId;
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
        buildingId = getIntent().getStringExtra("buildingId");
        b = buildingsDB.findByBuildingId(buildingId);
        if (b == null) {
            buildingId = "166"; // CULC by default TODO
            b = buildingsDB.findByBuildingId(buildingId);
        }

        updateDiningStatus(diningsDB, getApplicationContext()); // TODO: only update dinings associated with building?

        dList = diningsDB.getAll();
        eList = eventsDB.getAll();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
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
                    TextView altNamesText = rootView.findViewById(R.id.altNamesText);
                    TextView hoursText = rootView.findViewById(R.id.hoursText);
                    TextView addressText = rootView.findViewById(R.id.addressText);
                    altNamesText.setText(b.getAltNames());
                    hoursText.setText(b.getTimeOpen() + " - " + b.getTimeClose());
                    addressText.setText(b.getAddress());
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




