package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vi_tu.gtinteractive.domain.Building;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaliq on 9/4/2017.
 */

public class BuildingPersistence {

    private SQLiteDatabase db;

    public BuildingPersistence(SQLiteDatabase db) {
        this.db = db;
    }

    public long createBuilding(Building b) {
        // returns row id of newly inserted object
        return db.insert(BuildingContract.TABLE_NAME, null, buildingToContentValues(b));
    }

    public int updateBuilding(Building b, String id) {
        // returns number of rows affected (should be 1)
        return db.update(BuildingContract.TABLE_NAME, buildingToContentValues(b), BuildingContract._ID + " = " + id, null);
    }

    public int deleteBuilding(String id) {
        // returns number of rows affected (should be 1)
        return db.delete(BuildingContract.TABLE_NAME, BuildingContract._ID + " = " + id, null);
    }

    public int deleteAllBuildings() {
        // returns number of rows affected
        return db.delete(BuildingContract.TABLE_NAME, null, null);
    }

    public Building getBuilding(String id) {
        return queryBuilding(BuildingContract._ID + " = " + id);
    }

    public Building getBuildingByBuildingId(String buildingId) {
        return queryBuilding(BuildingContract.COLUMN_BUILDING_ID + " = " + buildingId);
    }

    public List<Building> getAllBuildings() {
        return queryBuildings(null);
    }

    // TODO: improve performance by creating a new function that utilizes SQL full text search (pre-indexing)

    public List<Building> findBuildingsByName(String query) {
        return queryBuildings("(LOWER(" + BuildingContract.COLUMN_NAME_TOKENS + ") LIKE \"%" + query.toLowerCase() + "%\")"); // TODO: optimization - assume nameTokens is already lowercase
    }

    public List<Building> findBuildingsByAddress(String query) {
        return queryBuildings("(LOWER(" + BuildingContract.COLUMN_ADDRESS_TOKENS + ") LIKE \"%" + query.toLowerCase() + "%\")"); // TODO: optimization - assume addressTokens is already lowercase
    }

    private Building queryBuilding(String selection) {
        Cursor c = db.query(
                BuildingContract.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null,
                "1"
        );
        Building b = null;
        if (c.moveToNext()) {
            b = cursorToBuilding(c);
        }
        c.close();
        return b;
    }

    private List<Building> queryBuildings(String selection) {
        Cursor c = db.query(
                BuildingContract.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                "LENGTH(" + BuildingContract.COLUMN_BUILDING_ID + ")," + BuildingContract.COLUMN_BUILDING_ID
        );
        List<Building> bList = new ArrayList<>();
        while (c.moveToNext()) {
            bList.add(cursorToBuilding(c));
        }
        c.close();
        return bList;
    }

    private Building cursorToBuilding(Cursor c) {
        LocalTime timeOpen = null;
        LocalTime timeClose = null;
        Integer numFloors = null;

        int i = c.getColumnIndex(BuildingContract.COLUMN_TIME_OPEN);
        if (!c.isNull(i)) {
            timeOpen = new LocalTime(c.getLong(i));
        }
        i = c.getColumnIndex(BuildingContract.COLUMN_TIME_CLOSE);
        if (!c.isNull(i)) {
            timeClose = new LocalTime(c.getLong(i));
        }
        i = c.getColumnIndex(BuildingContract.COLUMN_NUM_FLOORS);
        if (!c.isNull(i)) {
            numFloors = c.getInt(i);
        }

        return Building.builder()
                .buildingId(c.getString(c.getColumnIndex(BuildingContract.COLUMN_BUILDING_ID)))
                .name(c.getString(c.getColumnIndex(BuildingContract.COLUMN_NAME)))
                .address(c.getString(c.getColumnIndex(BuildingContract.COLUMN_ADDRESS)))
                .latitude(c.getDouble(c.getColumnIndex(BuildingContract.COLUMN_LATITUDE)))
                .longitude(c.getDouble(c.getColumnIndex(BuildingContract.COLUMN_LONGITUDE)))
                .phoneNum(c.getString(c.getColumnIndex(BuildingContract.COLUMN_PHONE_NUM)))
                .link(c.getString(c.getColumnIndex(BuildingContract.COLUMN_LINK)))
                .timeOpen(timeOpen)
                .timeClose(timeClose)
                .numFloors(numFloors)
                .altNames(c.getString(c.getColumnIndex(BuildingContract.COLUMN_ALT_NAMES)))
                .nameTokens(c.getString(c.getColumnIndex(BuildingContract.COLUMN_NAME_TOKENS)))
                .addressTokens(c.getString(c.getColumnIndex(BuildingContract.COLUMN_ADDRESS_TOKENS)))
                .build();
    }

    private ContentValues buildingToContentValues(Building b) {
        DateTime epoch = new DateTime(0);

        ContentValues cv = new ContentValues();
        cv.put(BuildingContract.COLUMN_BUILDING_ID, b.getBuildingId());
        cv.put(BuildingContract.COLUMN_NAME, b.getName());
        cv.put(BuildingContract.COLUMN_ADDRESS, b.getAddress());
        cv.put(BuildingContract.COLUMN_LATITUDE, b.getLatitude());
        cv.put(BuildingContract.COLUMN_LONGITUDE, b.getLongitude());
        cv.put(BuildingContract.COLUMN_PHONE_NUM, b.getPhoneNum());
        cv.put(BuildingContract.COLUMN_LINK, b.getLink());
        cv.put(BuildingContract.COLUMN_TIME_OPEN, b.getTimeOpen() != null ? epoch.withTime(b.getTimeOpen()).getMillis() : null);
        cv.put(BuildingContract.COLUMN_TIME_CLOSE, b.getTimeClose() != null ? epoch.withTime(b.getTimeClose()).getMillis() : null);
        cv.put(BuildingContract.COLUMN_NUM_FLOORS, b.getNumFloors());
        cv.put(BuildingContract.COLUMN_ALT_NAMES, b.getAltNames());
        cv.put(BuildingContract.COLUMN_NAME_TOKENS, b.getNameTokens());
        cv.put(BuildingContract.COLUMN_ADDRESS_TOKENS, b.getAddressTokens());
        return cv;
    }

}
