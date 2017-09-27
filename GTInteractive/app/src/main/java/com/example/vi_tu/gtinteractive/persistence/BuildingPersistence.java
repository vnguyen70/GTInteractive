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

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.deserializePolygons;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.deserializeTimes;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializePolygons;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializeTimes;
import static com.example.vi_tu.gtinteractive.utilities.StringUtils.isInteger;
import static com.example.vi_tu.gtinteractive.utilities.StringUtils.tokenize;
import static com.example.vi_tu.gtinteractive.utilities.StringUtils.tokenizeList;

public class BuildingPersistence extends BasePersistence<Building> {

    public BuildingPersistence(SQLiteDatabase db) {
        super(db, Building.Contract.TABLE_NAME, Building.Contract._ID, Building.Contract._ID);
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

        boolean skipFlag = false;

        List<String> tokensTemp = tokenizeList(location);
        List<String> tokens = new ArrayList<>();
        // TODO: create more filters like this; find a cleaner solution
        // TODO: presence of "dr", "drive", "st", or "street" indicates address; perhaps flag location for address matching?
        for (String t : tokensTemp) {
            if (skipFlag) {
                skipFlag = false;
            } else if (t.equals("room")) {
                skipFlag = true; // skip this token and skip next token as well (room number)
            } else if (t.equals("bldg")) {
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
            tokens.add(t);
        }

//        Log.d("LOCATION_TOKENS", tokens.toString());

        // Step 3: if hasAddress is true, call findBuildingsByAddress() for each token and update buildingScores

        if (hasAddress) {
            for (String t : tokens) {
                List<Building> partialMatches = findByAddress(t);
                for (Building b : partialMatches) {
                    String bId = b.getBuildingId();
                    Integer oldScore = buildingScores.get(bId);
                    int score = 7;
                    // TODO: create a better scoring system than this
                    if (isInteger(t)) {
                        score = 20; // number matches have highest weight in addresses
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
                } else if (t.equals("conference")) {
                    score = 7;
                }
                if (oldScore != null) {
                    score += oldScore;
                }
                buildingScores.put(bId, score);
            }
        }

        // TODO: if still multiple matches, give more weight correct sequence of tokens (e.g. "student center" vs "student SUCCESS center"); match strings of multiple tokens at a time;

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
        cv.put(Building.Contract.COLUMN_IMAGE_URL, b.getImageURL());
        cv.put(Building.Contract.COLUMN_WEBSITE_URL, b.getWebsiteURL());
        cv.put(Building.Contract.COLUMN_PHONE_NUM, b.getPhoneNum());
        cv.put(Building.Contract.COLUMN_STREET, b.getStreet());
        cv.put(Building.Contract.COLUMN_CITY, b.getCity());
        cv.put(Building.Contract.COLUMN_STATE, b.getState());
        cv.put(Building.Contract.COLUMN_POSTAL_CODE, b.getPostalCode());
        cv.put(Building.Contract.COLUMN_LATITUDE, b.getLatitude());
        cv.put(Building.Contract.COLUMN_LONGITUDE, b.getLongitude());
        cv.put(Building.Contract.COLUMN_POLYGONS, serializePolygons(b.getPolygons()));
        cv.put(Building.Contract.COLUMN_CATEGORY, b.getCategory().name());
        cv.put(Building.Contract.COLUMN_DESCRIPTION, b.getDescription());
        cv.put(Building.Contract.COLUMN_LOCATED_IN, b.getLocatedIn());
        cv.put(Building.Contract.COLUMN_YELP_ID, b.getYelpID());
        cv.put(Building.Contract.COLUMN_OPEN_TIMES, serializeTimes(b.getOpenTimes()));
        cv.put(Building.Contract.COLUMN_CLOSE_TIMES, serializeTimes(b.getCloseTimes()));
        cv.put(Building.Contract.COLUMN_ACCEPTS_BUZZ_FUNDS, b.getAcceptsBuzzFunds());
        cv.put(Building.Contract.COLUMN_PRICE_LEVEL, b.getPriceLevel());
        cv.put(Building.Contract.COLUMN_ALT_NAMES, b.getAltNames());
        cv.put(Building.Contract.COLUMN_NAME_TOKENS, tokenize(b.getName()));
        cv.put(Building.Contract.COLUMN_ADDRESS_TOKENS, tokenize(b.getStreet())); // TODO
        cv.put(Building.Contract.COLUMN_NUM_FLOORS, b.getNumFloors());
        return cv;
    }

    @Override
    protected Building toDomain(Cursor c) {
        return Building.builder()
                .id(c.getInt(c.getColumnIndex(Building.Contract._ID)))
                .buildingId(c.getString(c.getColumnIndex(Building.Contract.COLUMN_BUILDING_ID)))
                .name(c.getString(c.getColumnIndex(Building.Contract.COLUMN_NAME)))
                .imageURL(c.getString(c.getColumnIndex(Building.Contract.COLUMN_IMAGE_URL)))
                .websiteURL(c.getString(c.getColumnIndex(Building.Contract.COLUMN_WEBSITE_URL)))
                .phoneNum(c.getString(c.getColumnIndex(Building.Contract.COLUMN_PHONE_NUM)))
                .street(c.getString(c.getColumnIndex(Building.Contract.COLUMN_STREET)))
                .city(c.getString(c.getColumnIndex(Building.Contract.COLUMN_CITY)))
                .state(c.getString(c.getColumnIndex(Building.Contract.COLUMN_STATE)))
                .postalCode(c.getString(c.getColumnIndex(Building.Contract.COLUMN_POSTAL_CODE)))
                .latitude(c.getDouble(c.getColumnIndex(Building.Contract.COLUMN_LATITUDE)))
                .longitude(c.getDouble(c.getColumnIndex(Building.Contract.COLUMN_LONGITUDE)))
                .polygons(deserializePolygons(c.getString(c.getColumnIndex(Building.Contract.COLUMN_POLYGONS))))
                .category(Building.Category.valueOf(c.getString(c.getColumnIndex(Building.Contract.COLUMN_CATEGORY))))
                .description(c.getString(c.getColumnIndex(Building.Contract.COLUMN_DESCRIPTION)))
                .locatedIn(c.getString(c.getColumnIndex(Building.Contract.COLUMN_LOCATED_IN)))
                .yelpID(c.getString(c.getColumnIndex(Building.Contract.COLUMN_YELP_ID)))
                .openTimes(deserializeTimes(c.getString(c.getColumnIndex(Building.Contract.COLUMN_OPEN_TIMES))))
                .closeTimes(deserializeTimes(c.getString(c.getColumnIndex(Building.Contract.COLUMN_CLOSE_TIMES))))
                .acceptsBuzzFunds(c.getInt(c.getColumnIndex(Building.Contract.COLUMN_ACCEPTS_BUZZ_FUNDS)) == 1)
                .priceLevel(c.getInt(c.getColumnIndex(Building.Contract.COLUMN_PRICE_LEVEL)))
                .altNames(c.getString(c.getColumnIndex(Building.Contract.COLUMN_ALT_NAMES)))
                .nameTokens(c.getString(c.getColumnIndex(Building.Contract.COLUMN_NAME_TOKENS)))
                .addressTokens(c.getString(c.getColumnIndex(Building.Contract.COLUMN_ADDRESS_TOKENS)))
                .numFloors(c.getInt(c.getColumnIndex(Building.Contract.COLUMN_NUM_FLOORS)))
                .build();
    }

}
