package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vi_tu.gtinteractive.domain.Place;

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

public class PlacePersistence extends BasePersistence<Place> {

    public PlacePersistence(SQLiteDatabase db) {
        super(db, Place.Contract.TABLE_NAME, Place.Contract._ID, Place.Contract.COLUMN_PLACE_ID);
    }

    /******** Search Functions ********************************************************************/

    // TODO: improve performance by creating a new function that utilizes SQL full text search (pre-indexing)
    // TODO: filter against place nicknames
    public Place findByPlaceId(String placeId) {
        return findOne(Place.Contract.COLUMN_PLACE_ID + " = \"" + placeId + "\"");
    }

    public List<Place> findByName(String query) {
        return findMany("(LOWER(" + Place.Contract.COLUMN_NAME_TOKENS + ") LIKE \"%" + query.toLowerCase() + "%\")"); // TODO: optimization - assume nameTokens is already lowercase
    }

    public List<Place> findByAddress(String query) {
        return findMany("(LOWER(" + Place.Contract.COLUMN_ADDRESS_TOKENS + ") LIKE \"%" + query.toLowerCase() + "%\")"); // TODO: optimization - assume addressTokens is already lowercase
    }

    public List<Place> findByCategory(Place.Category category) {
        return findMany(Place.Contract.COLUMN_CATEGORY + " = " + category.name()); // TODO: doesn't find "Other" category
    }

    public String findPlaceIdByLocation(String location) {

        Map<String, Integer> placeScores = new HashMap<>();
        List<String> bestMatches;

        // Step 1: check location against shortcut matches

        location = location.trim();
        String placeId = Place.shortcuts.get(location);
        if (placeId != null) {
            return placeId;
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

        // Step 3: if hasAddress is true, call findBuildingsByAddress() for each token and update placeScores

        if (hasAddress) {
            for (String t : tokens) {
                List<Place> partialMatches = findByAddress(t);
                for (Place p : partialMatches) {
                    String pId = p.getPlaceId();
                    Integer oldScore = placeScores.get(pId);
                    int score = 7;
                    // TODO: create a better scoring system than this
                    if (isInteger(t)) {
                        score = 20; // number matches have highest weight in addresses
                    }
                    if (oldScore != null) {
                        score += oldScore;
                    }
                    placeScores.put(pId, score);
                }
            }

            bestMatches = getBestMatches(placeScores);
            if (bestMatches.size() == 1) {
                return bestMatches.get(0);
            }
        }

        // Step 4: call findBuildingsByName() for each token and update placeScores

        for (String t : tokens) {
            List<Place> partialMatches = findByName(t);
            for (Place p : partialMatches) {
                String pId = p.getPlaceId();
                Integer oldScore = placeScores.get(pId);
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
                placeScores.put(pId, score);
            }
        }

        // TODO: if still multiple matches, give more weight correct sequence of tokens (e.g. "student center" vs "student SUCCESS center"); match strings of multiple tokens at a time;

        // Step 5: get bestMatches and return a placeId

        bestMatches = getBestMatches(placeScores);
        if (bestMatches.size() == 1) {
            placeId = bestMatches.get(0);
        } else if (bestMatches.size() > 1) {
            placeId = "MANY: " + bestMatches.toString();
        } else {
            placeId = "NONE";
        }

        return placeId;
    }

    /******** Helper Functions ********************************************************************/

    private List<String> getBestMatches(Map<String, Integer> placeScores) {
        List<String> bestMatches = new ArrayList<>();
        if (!placeScores.isEmpty()) {
            int maxNumMatches = Collections.max(placeScores.values());
            for (Map.Entry<String, Integer> entry : placeScores.entrySet()) {
                if (entry.getValue() == maxNumMatches) {
                    bestMatches.add(entry.getKey());
                }
            }
        }
        return bestMatches;
    }

    @Override
    protected ContentValues toContentValues(Place b) {
        ContentValues cv = new ContentValues();
        cv.put(Place.Contract.COLUMN_PLACE_ID, b.getPlaceId());
        cv.put(Place.Contract.COLUMN_NAME, b.getName());
        cv.put(Place.Contract.COLUMN_IMAGE_URL, b.getImageURL());
        cv.put(Place.Contract.COLUMN_WEBSITE_URL, b.getWebsiteURL());
        cv.put(Place.Contract.COLUMN_PHONE_NUM, b.getPhoneNum());
        cv.put(Place.Contract.COLUMN_STREET, b.getStreet());
        cv.put(Place.Contract.COLUMN_CITY, b.getCity());
        cv.put(Place.Contract.COLUMN_STATE, b.getState());
        cv.put(Place.Contract.COLUMN_POSTAL_CODE, b.getPostalCode());
        cv.put(Place.Contract.COLUMN_LATITUDE, b.getLatitude());
        cv.put(Place.Contract.COLUMN_LONGITUDE, b.getLongitude());
        cv.put(Place.Contract.COLUMN_POLYGONS, serializePolygons(b.getPolygons()));
        cv.put(Place.Contract.COLUMN_CATEGORY, b.getCategory().name());
        cv.put(Place.Contract.COLUMN_DESCRIPTION, b.getDescription());
        cv.put(Place.Contract.COLUMN_LOCATED_IN, b.getLocatedIn());
        cv.put(Place.Contract.COLUMN_YELP_ID, b.getYelpID());
        cv.put(Place.Contract.COLUMN_OPEN_TIMES, serializeTimes(b.getOpenTimes()));
        cv.put(Place.Contract.COLUMN_CLOSE_TIMES, serializeTimes(b.getCloseTimes()));
        cv.put(Place.Contract.COLUMN_ACCEPTS_BUZZ_FUNDS, b.getAcceptsBuzzFunds());
        cv.put(Place.Contract.COLUMN_PRICE_LEVEL, b.getPriceLevel());
        cv.put(Place.Contract.COLUMN_ALT_NAMES, b.getAltNames());
        cv.put(Place.Contract.COLUMN_NAME_TOKENS, tokenize(b.getName()));
        cv.put(Place.Contract.COLUMN_ADDRESS_TOKENS, tokenize(b.getStreet())); // TODO
        cv.put(Place.Contract.COLUMN_NUM_FLOORS, b.getNumFloors());
        return cv;
    }

    @Override
    protected Place toDomain(Cursor c) {
        return Place.builder()
                .id(c.getInt(c.getColumnIndex(Place.Contract._ID)))
                .placeId(c.getString(c.getColumnIndex(Place.Contract.COLUMN_PLACE_ID)))
                .name(c.getString(c.getColumnIndex(Place.Contract.COLUMN_NAME)))
                .imageURL(c.getString(c.getColumnIndex(Place.Contract.COLUMN_IMAGE_URL)))
                .websiteURL(c.getString(c.getColumnIndex(Place.Contract.COLUMN_WEBSITE_URL)))
                .phoneNum(c.getString(c.getColumnIndex(Place.Contract.COLUMN_PHONE_NUM)))
                .street(c.getString(c.getColumnIndex(Place.Contract.COLUMN_STREET)))
                .city(c.getString(c.getColumnIndex(Place.Contract.COLUMN_CITY)))
                .state(c.getString(c.getColumnIndex(Place.Contract.COLUMN_STATE)))
                .postalCode(c.getString(c.getColumnIndex(Place.Contract.COLUMN_POSTAL_CODE)))
                .latitude(c.getDouble(c.getColumnIndex(Place.Contract.COLUMN_LATITUDE)))
                .longitude(c.getDouble(c.getColumnIndex(Place.Contract.COLUMN_LONGITUDE)))
                .polygons(deserializePolygons(c.getString(c.getColumnIndex(Place.Contract.COLUMN_POLYGONS))))
                .category(Place.Category.valueOf(c.getString(c.getColumnIndex(Place.Contract.COLUMN_CATEGORY))))
                .description(c.getString(c.getColumnIndex(Place.Contract.COLUMN_DESCRIPTION)))
                .locatedIn(c.getString(c.getColumnIndex(Place.Contract.COLUMN_LOCATED_IN)))
                .yelpID(c.getString(c.getColumnIndex(Place.Contract.COLUMN_YELP_ID)))
                .openTimes(deserializeTimes(c.getString(c.getColumnIndex(Place.Contract.COLUMN_OPEN_TIMES))))
                .closeTimes(deserializeTimes(c.getString(c.getColumnIndex(Place.Contract.COLUMN_CLOSE_TIMES))))
                .acceptsBuzzFunds(c.getInt(c.getColumnIndex(Place.Contract.COLUMN_ACCEPTS_BUZZ_FUNDS)) == 1)
                .priceLevel(c.getInt(c.getColumnIndex(Place.Contract.COLUMN_PRICE_LEVEL)))
                .altNames(c.getString(c.getColumnIndex(Place.Contract.COLUMN_ALT_NAMES)))
                .nameTokens(c.getString(c.getColumnIndex(Place.Contract.COLUMN_NAME_TOKENS)))
                .addressTokens(c.getString(c.getColumnIndex(Place.Contract.COLUMN_ADDRESS_TOKENS)))
                .numFloors(c.getInt(c.getColumnIndex(Place.Contract.COLUMN_NUM_FLOORS)))
                .build();
    }

}
