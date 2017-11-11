package com.example.vi_tu.gtinteractive.rest;

import android.content.Context;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.vi_tu.gtinteractive.constants.Constants.EVENTS_CACHE_DURATION_MS;
import static com.example.vi_tu.gtinteractive.constants.Constants.EVENTS_CACHE_EXPIRATION_MS_KEY;

/**
 * Created by kaliq on 10/21/2017.
 */

public class EventDAO extends BaseDAO<Event> {

    EventPersistence eventsDB;
    BuildingPersistence buildingsDB;

    public EventDAO(Context context, Listener<Event> callback) {
        super(Event.getBaseUrl(), new EventPersistence(new PersistenceHelper(context).getWritableDatabase()),
                EVENTS_CACHE_DURATION_MS, EVENTS_CACHE_EXPIRATION_MS_KEY, context, callback);
        eventsDB = new EventPersistence(new PersistenceHelper(context).getWritableDatabase());
        buildingsDB = new BuildingPersistence(new PersistenceHelper(context).getWritableDatabase());
    }

    /******** Search Functions ********************************************************************/

    public void findByEventId(final String eventId) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onListReady(eventsDB.findByEventId(eventId));
            }
        });
    }

    public void findByTitle(final String query) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onListReady(eventsDB.findByTitle(query));
            }
        });
    }

    public void findByCategory(final Event.Category category) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onListReady(eventsDB.findByCategory(category));
            }
        });
    }

    public void findByBuildingId(final String buildingId) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onListReady(eventsDB.findByBuildingId(buildingId));
            }
        });
    }


    @Override
    public Event toDomain(JSONObject o) throws JSONException {
        int millisStart = o.optInt("startDate") * 1000;
        int millisEnd = o.optInt("endDate") * 1000;
        String location = o.getString("location");
        Building b = buildingsDB.findBuildingByLocation(location); // building matching algorithm
        List<Event.Category> categories = new ArrayList<>();
        JSONArray categoriesJSON = o.getJSONArray("tags");
        for (int j = 0; j < categoriesJSON.length(); j++) {
            JSONObject categoryJSON = categoriesJSON.getJSONObject(j);
            categories.add(Event.Category.getCategory(categoryJSON.getString("name")));
        }

        return Event.builder()
                .eventId(o.getString("id"))
                .title(o.getString("title"))
                .location(location)
                .description(o.optString("description", ""))
                .imageURL(o.optString("imageURL", ""))
                .startDate(millisStart > 0 ? new DateTime(millisStart) : null)
                .endDate(millisEnd > 0 ? new DateTime(millisEnd) : null)
                .allDay(o.optBoolean("allDay"))
                .recurring(o.optBoolean("recurring"))
                .categories(categories)
                .buildingId(b != null ? b.getBuildingId() : "")
                .build();
    }
}
