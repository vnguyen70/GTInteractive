package com.example.vi_tu.gtinteractive;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.TabType;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializeTimes;

public class DiningDetailsActivity extends AppCompatActivity {

    private DiningPersistence diningsDB;
    private BuildingPersistence buildingsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_details);

        // persistence
        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        diningsDB = new DiningPersistence(db);
        buildingsDB = new BuildingPersistence(db);

        // dining to display
        Dining d = Dining.DEFAULT_DINING;
        int objectId = getIntent().getIntExtra(Arguments.OBJECT_ID, -1);
        Dining temp = diningsDB.get(objectId);
        if (temp != null) {
            d = temp;
        }

        // UI
        TextView idTextView = (TextView) findViewById(R.id.idText);
        TextView diningIdTextView = (TextView) findViewById(R.id.diningIdText);
        TextView buildingIdTextView = (TextView) findViewById(R.id.buildingIdText);
        TextView latitudeTextView = (TextView) findViewById(R.id.latitudeText);
        TextView longitudeTextView = (TextView) findViewById(R.id.longitudeText);
        TextView nameTextView = (TextView) findViewById(R.id.nameText);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionText);
        TextView locationDetailsTextView = (TextView) findViewById(R.id.locationDetailsText);
        TextView logoURLTextView = (TextView) findViewById(R.id.logoURLText);
        TextView menuLinkURLTextView = (TextView) findViewById(R.id.menuLinkURLText);
        TextView promotionMessageTextView = (TextView) findViewById(R.id.promotionMessageText);
        TextView promotionStartDateTextView = (TextView) findViewById(R.id.promotionStartDateText);
        TextView promotionEndDateTextView = (TextView) findViewById(R.id.promotionEndDateText);
        TextView openTimesTextView = (TextView) findViewById(R.id.openTimesText);
        TextView closeTimesTextView = (TextView) findViewById(R.id.closeTimesText);
        TextView exceptionsTextView = (TextView) findViewById(R.id.exceptionsText);
        TextView tagsTextView = (TextView) findViewById(R.id.tagsText);
        TextView tagIdsTextView = (TextView) findViewById(R.id.tagIdsText);
        TextView isOpenTextView = (TextView) findViewById(R.id.isOpenText);
        TextView upcomingStatusChangeTextView = (TextView) findViewById(R.id.upcomingStatusChangeText);
        TextView nameTokensTextView = (TextView) findViewById(R.id.nameTokensText);
        Button openBuildingDetailsButton = (Button) findViewById(R.id.openBuildingDetailsButton);
        Button showInMapButton = (Button) findViewById(R.id.showInMapButton);

        idTextView.setText(String.valueOf(d.getId()));
        diningIdTextView.setText(d.getDiningId());
        buildingIdTextView.setText(d.getBuildingId());
        latitudeTextView.setText(String.valueOf(d.getLatitude()));
        longitudeTextView.setText(String.valueOf(d.getLongitude()));
        nameTextView.setText(d.getName());
        descriptionTextView.setText(d.getDescription());
        locationDetailsTextView.setText(d.getLocationDetails());
        logoURLTextView.setText(d.getLogoURL());
        menuLinkURLTextView.setText(d.getMenuLinkURL());
        promotionMessageTextView.setText(d.getPromotionMessage());
        promotionStartDateTextView.setText(d.getPromotionStartDate() != null ? d.getPromotionStartDate().toString("MM/dd/yy hh:mm a") : "n/a");
        promotionEndDateTextView.setText(d.getPromotionEndDate() != null ? d.getPromotionEndDate().toString("MM/dd/yy hh:mm a") : "n/a");
        openTimesTextView.setText(serializeTimes(d.getOpenTimes()));
        closeTimesTextView.setText(serializeTimes(d.getCloseTimes()));
        exceptionsTextView.setText(String.valueOf(d.getExceptions()));
        tagsTextView.setText(String.valueOf(d.getTags()));
        tagIdsTextView.setText(d.getTagIds());
        isOpenTextView.setText(String.valueOf(d.getIsOpen()));
        upcomingStatusChangeTextView.setText(String.valueOf(d.getUpcomingStatusChange()));
        nameTokensTextView.setText(d.getNameTokens());

        final Building b = buildingsDB.findByBuildingId(d.getBuildingId());
        if (b != null) {
            openBuildingDetailsButton.setVisibility(View.VISIBLE);
            openBuildingDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent buildingDetailIntent = new Intent(DiningDetailsActivity.this, BuildingDetailsActivity.class);
                    buildingDetailIntent.putExtra(Arguments.OBJECT_ID, b.getId());
                    buildingDetailIntent.putExtra(Arguments.DEFAULT_TAB, TabType.INFO);
                    startActivity(buildingDetailIntent);
                }
            });
        }

        final Dining finalD = d;
        showInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapActivityIntent = new Intent(DiningDetailsActivity.this, MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.DINING);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, finalD.getId());
                startActivity(mapActivityIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}