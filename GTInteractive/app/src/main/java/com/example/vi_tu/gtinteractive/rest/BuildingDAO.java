package com.example.vi_tu.gtinteractive.rest;

import android.content.Context;

import com.example.vi_tu.gtinteractive.constants.Constants;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.vi_tu.gtinteractive.constants.Constants.BUILDINGS_CACHE_DURATION_MS;
import static com.example.vi_tu.gtinteractive.constants.Constants.BUILDINGS_CACHE_EXPIRATION_MS_KEY;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.deserializePolygons;

/**
 * Created by kaliq on 10/21/2017.
 */

public class BuildingDAO extends BaseDAO<Building> {

    BuildingPersistence buildingsDB;

    public BuildingDAO(Context context, Listener<Building> callback) {
        super(Building.getBaseUrl(), new BuildingPersistence(new PersistenceHelper(context).getWritableDatabase()),
                BUILDINGS_CACHE_DURATION_MS, BUILDINGS_CACHE_EXPIRATION_MS_KEY, context, callback);
        buildingsDB = new BuildingPersistence(new PersistenceHelper(context).getWritableDatabase());
    }

    public void findByBuildingId(final String buildingId) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onObjectReady(buildingsDB.findByBuildingId(buildingId));
            }
        });
    }

    public void findByName(final String query) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onListReady(buildingsDB.findByName(query));
            }
        });
    }

    public void findByAddress(final String query) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onListReady(buildingsDB.findByAddress(query));
            }
        });
    }

    public void findByCategory(final Building.Category category) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onListReady(buildingsDB.findByCategory(category));
            }
        });
    }

    public void findBuildingByLocation(final String location) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onObjectReady(buildingsDB.findBuildingByLocation(location));
            }
        });
    }

    public BuildingPersistence getCache() {
        return buildingsDB;
    }

    @Override
    public Building toDomain(JSONObject o) throws JSONException {
        // nested JSON objects
        JSONObject categoryJSON = o.getJSONObject("category");
        JSONObject locationJSON = o.getJSONObject("location");
        JSONObject addressJSON = locationJSON.getJSONObject("address");
        JSONArray timesJSON = o.optJSONArray("hours");

        // openTimes and closeTimes
        LocalTime[] openTimes = new LocalTime[Constants.DAYS_OF_WEEK];
        LocalTime[] closeTimes = new LocalTime[Constants.DAYS_OF_WEEK];
        if (timesJSON != null && timesJSON.length() <= 7) {
            for (int j = 0; j < timesJSON.length(); j++) {
                JSONObject dayJSON = timesJSON.getJSONObject(j);
                JSONObject openTimeJSON = dayJSON.optJSONObject("open");
                JSONObject closeTimeJSON = dayJSON.optJSONObject("close");
                if (openTimeJSON != null && closeTimeJSON != null) {
                    openTimes[openTimeJSON.getInt("day")] = DateTime.parse(openTimeJSON.getString("time"), DateTimeFormat.forPattern("HHmm")).toLocalTime();
                    closeTimes[closeTimeJSON.getInt("day")] = DateTime.parse(closeTimeJSON.getString("time"), DateTimeFormat.forPattern("HHmm")).toLocalTime();
                }
            }
        }

        return Building.builder()
                .buildingId(o.getString("id"))
                .name(o.getString("name"))
                .imageURL(o.optString("imageURL", ""))
                .websiteURL(o.optString("websiteURL", ""))
                .phoneNum(o.optString("phone", ""))
                .street(addressJSON.getString("street"))
                .city(addressJSON.getString("city"))
                .state(addressJSON.getString("state"))
                .postalCode(addressJSON.getString("postalCode"))
                .latitude(locationJSON.getDouble("latitude"))
                .longitude(locationJSON.getDouble("longitude"))
                .polygons(deserializePolygons(locationJSON.optJSONArray("shapeCoordinates")))
                .category(Building.Category.getCategory(categoryJSON.getString("title")))
                .description(o.optString("description", ""))
                .locatedIn(o.optString("locatedIn", ""))
                .yelpID(o.optString("yelpID", ""))
                .acceptsBuzzFunds(o.optBoolean("acceptsBuzzFunds", false))
                .priceLevel(o.optInt("priceLevel", 0))
                .openTimes(openTimes)
                .closeTimes(closeTimes)
                .altNames("")
                .nameTokens("")
                .addressTokens("")
                .numFloors(0)
                .build();
    }
}
