package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vi_tu.gtinteractive.domain.Building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.cursorIndexToInteger;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.cursorIndexToTime;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.timeToMillis;
import static com.example.vi_tu.gtinteractive.utilities.StringUtils.isInteger;
import static com.example.vi_tu.gtinteractive.utilities.StringUtils.tokenize;
import static com.example.vi_tu.gtinteractive.utilities.StringUtils.tokenizeList;

public class BuildingPersistence extends BasePersistence<Building> {

    public BuildingPersistence(SQLiteDatabase db) {
        super(db, Building.Contract.TABLE_NAME, Building.Contract._ID,
                "LENGTH(" + Building.Contract.COLUMN_BUILDING_ID + ")," + Building.Contract.COLUMN_BUILDING_ID);
    }

    /******** Search Functions ********************************************************************/

    // TODO: improve performance by creating a new function that utilizes SQL full text search (pre-indexing)
    // TODO: filter against building nicknames
    public Building findByBuildingId(String buildingId) {
        return findOne(Building.Contract.COLUMN_BUILDING_ID + " = \"" + buildingId + "\"");
    }

    public List<Building> findByName(String query) {
        return findMany("(LOWER(" + Building.Contract.COLUMN_NAME_TOKENS + ") LIKE \"%" + query.toLowerCase() + "%\")"); // TODO: optimization - assume nameTokens is already lowercase
    }

    public List<Building> findByAddress(String query) {
        return findMany("(LOWER(" + Building.Contract.COLUMN_ADDRESS_TOKENS + ") LIKE \"%" + query.toLowerCase() + "%\")"); // TODO: optimization - assume addressTokens is already lowercase
    }

    public String findBuildingIdByLocation(String location) {

        Map<String, Integer> buildingScores = new HashMap<>();
        List<String> bestMatches;

        // Step 1: check location against shortcut matches

        location = location.trim();
        String buildingId = Building.shortcuts.get(location);
        if (buildingId != null) {
            return buildingId;
        }

        // Step 2: tokenize location and check for keywords indicating address

        boolean hasAddress = false;

        List<String> tokensTemp = tokenizeList(location);
        List<String> tokens = new ArrayList<>(tokensTemp);
        // TODO: create more filters like this; find a cleaner solution
        // TODO: presence of "dr", "drive", "st", or "street" indicates address; perhaps flag location for address matching?
        for (String t : tokensTemp) {
            if (t.equals("bldg")) {
                tokens.add("building");
            } else if (t.equals("dr")) {
                tokens.add("drive");
                hasAddress = true;
            } else if (t.equals("drive")) {
                tokens.add("dr");
                hasAddress = true;
            } else if (t.equals("st")) {
                tokens.add("street");
                hasAddress = true;
            } else if (t.equals("street")) {
                tokens.add("st");
                hasAddress = true;
            } else if (t.equals("way")) {
                hasAddress = true;
            }
        }

//        Log.d("LOCATION_TOKENS", tokens.toString());

        // Step 3: if hasAddress is true, call findBuildingsByAddress() for each token and update buildingScores

        if (hasAddress) {
            for (String t : tokens) {
                List<Building> partialMatches = findByAddress(t);
                for (Building b : partialMatches) {
                    String bId = b.getBuildingId();
                    Integer oldScore = buildingScores.get(bId);
                    int score = 8;
                    // TODO: create a better scoring system than this
                    if (isInteger(t)) {
                        score = 20; // number matches have highest weight in addresses
                    }
                    if (t.equals("atlanta") || t.equals("ga") || t.equals("georgia")) { // lowest weight - can apply to almost any address
                        score = 1;
                    }
                    if (oldScore != null) {
                        score += oldScore;
                    }
                    buildingScores.put(bId, score);
                }
            }

            bestMatches = getBestMatches(buildingScores);
            if (bestMatches.size() == 1) {
                return bestMatches.get(0);
            }
        }

        // Step 4: call findBuildingsByName() for each token and update buildingScores

        for (String t : tokens) {
            List<Building> partialMatches = findByName(t);
            for (Building b : partialMatches) {
                String bId = b.getBuildingId();
                Integer oldScore = buildingScores.get(bId);
                // TODO: create a better scoring system than this
                int score = 10;
                if (t.equals("room")) { // lowest weight - can apply to any building / location
                    score = 1;
                } else if (t.equals("building") || t.equals("bldg") || t.equals("hall") || t.equals("lab")) { // low weight - can apply to nearly any building / location
                    score = 3;
                } else if (t.equals("house") || t.equals("deck") || t.equals("apartments") || t.equals("center")) { // medium weight - can apply to some buildings / locations
                    score = 5;
                }
                if (oldScore != null) {
                    score += oldScore;
                }
                buildingScores.put(bId, score);
            }
        }

        // Step 5: get bestMatches and return a buildingId

        bestMatches = getBestMatches(buildingScores);
        if (bestMatches.size() == 1) {
            buildingId = bestMatches.get(0);
        } else if (bestMatches.size() > 1) {
            buildingId = "MANY: " + bestMatches.toString();
        } else {
            buildingId = "NONE";
        }

        return buildingId;
    }

    /******** Helper Functions ********************************************************************/

    private List<String> getBestMatches(Map<String, Integer> buildingScores) {
        List<String> bestMatches = new ArrayList<>();
        if (!buildingScores.isEmpty()) {
            int maxNumMatches = Collections.max(buildingScores.values());
            for (Map.Entry<String, Integer> entry : buildingScores.entrySet()) {
                if (entry.getValue() == maxNumMatches) {
                    bestMatches.add(entry.getKey());
                }
            }
        }
        return bestMatches;
    }

    @Override
    protected ContentValues toContentValues(Building b) {
        ContentValues cv = new ContentValues();
        cv.put(Building.Contract.COLUMN_BUILDING_ID, b.getBuildingId());
        cv.put(Building.Contract.COLUMN_NAME, b.getName());
        cv.put(Building.Contract.COLUMN_ADDRESS, b.getAddress());
        cv.put(Building.Contract.COLUMN_LATITUDE, b.getLatitude());
        cv.put(Building.Contract.COLUMN_LONGITUDE, b.getLongitude());
        cv.put(Building.Contract.COLUMN_PHONE_NUM, b.getPhoneNum());
        cv.put(Building.Contract.COLUMN_LINK, b.getLink());
        cv.put(Building.Contract.COLUMN_TIME_OPEN, timeToMillis(b.getTimeOpen()));
        cv.put(Building.Contract.COLUMN_TIME_CLOSE, timeToMillis(b.getTimeClose()));
        cv.put(Building.Contract.COLUMN_NUM_FLOORS, b.getNumFloors());
        cv.put(Building.Contract.COLUMN_ALT_NAMES, b.getAltNames());
        cv.put(Building.Contract.COLUMN_NAME_TOKENS, tokenize(b.getName()));
        cv.put(Building.Contract.COLUMN_ADDRESS_TOKENS, tokenize(b.getName()));
        return cv;
    }

    @Override
    protected Building toDomain(Cursor c) {
        return Building.builder()
                .id(c.getInt(c.getColumnIndex(Building.Contract._ID)))
                .buildingId(c.getString(c.getColumnIndex(Building.Contract.COLUMN_BUILDING_ID)))
                .name(c.getString(c.getColumnIndex(Building.Contract.COLUMN_NAME)))
                .address(c.getString(c.getColumnIndex(Building.Contract.COLUMN_ADDRESS)))
                .latitude(c.getDouble(c.getColumnIndex(Building.Contract.COLUMN_LATITUDE)))
                .longitude(c.getDouble(c.getColumnIndex(Building.Contract.COLUMN_LONGITUDE)))
                .phoneNum(c.getString(c.getColumnIndex(Building.Contract.COLUMN_PHONE_NUM)))
                .link(c.getString(c.getColumnIndex(Building.Contract.COLUMN_LINK)))
                .timeOpen(cursorIndexToTime(c, c.getColumnIndex(Building.Contract.COLUMN_TIME_OPEN)))
                .timeClose(cursorIndexToTime(c, c.getColumnIndex(Building.Contract.COLUMN_TIME_CLOSE)))
                .numFloors(cursorIndexToInteger(c, c.getColumnIndex(Building.Contract.COLUMN_NUM_FLOORS)))
                .altNames(c.getString(c.getColumnIndex(Building.Contract.COLUMN_ALT_NAMES)))
                .nameTokens(c.getString(c.getColumnIndex(Building.Contract.COLUMN_NAME_TOKENS)))
                .addressTokens(c.getString(c.getColumnIndex(Building.Contract.COLUMN_ADDRESS_TOKENS)))
                .build();
    }

}
