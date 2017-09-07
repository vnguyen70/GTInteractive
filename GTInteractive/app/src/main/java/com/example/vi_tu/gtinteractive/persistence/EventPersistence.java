package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vi_tu.gtinteractive.domain.Event;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaliq on 9/5/2017.
 */

public class EventPersistence {

    private SQLiteDatabase db;
    private ListSerializer<String> serializer;

    public EventPersistence(SQLiteDatabase db) {
        this.db = db;
        this.serializer = new ListSerializer();
    }

    public long createEvent(Event e) {
        // returns row id of newly inserted object
        return db.insert(EventContract.TABLE_NAME, null, eventToContentValues(e));
    }

    public int updateEvent(Event e, String id) {
        // returns number of rows affected (should be 1)
        return db.update(EventContract.TABLE_NAME, eventToContentValues(e), EventContract._ID + " = " + id, null);
    }

    public int deleteEvent(String id) {
        // returns number of rows affected (should be 1)
        return db.delete(EventContract.TABLE_NAME, EventContract._ID + " = " + id, null);
    }

    public int deleteAllEvents() {
        // returns number of rows affected
        return db.delete(EventContract.TABLE_NAME, null, null);
    }

    public Event getEvent(String id) {
        return queryEvent(EventContract._ID + " = " + id);
    }

    public List<Event> getAllEvents() {
        return queryEvents(null);
    }

    public List<Event> findEventsByBuildingId(String buildingId) {
        return queryEvents(EventContract.COLUMN_BUILDING_ID + " = " + buildingId);
    }

    private Event queryEvent(String selection) {
        Cursor c = db.query(
                EventContract.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null,
                "1"
        );
        Event e = null;
        if (c.moveToNext()) {
            e = cursorToEvent(c);
        }
        c.close();
        return e;
    }

    private List<Event> queryEvents(String selection) {
        Cursor c = db.query(
                EventContract.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                EventContract.COLUMN_START_DATE
        );
        List<Event> eList = new ArrayList<>();
        while (c.moveToNext()) {
            eList.add(cursorToEvent(c));
        }
        c.close();
        return eList;
    }

    private Event cursorToEvent(Cursor c) {
        DateTime startDate = null;
        DateTime endDate = null;
        DateTime pubDate = null;

        int i = c.getColumnIndex(EventContract.COLUMN_START_DATE);
        if (!c.isNull(i)) {
            startDate = new DateTime(c.getLong(i));
        }
        i = c.getColumnIndex(EventContract.COLUMN_END_DATE);
        if (!c.isNull(i)) {
            endDate = new DateTime(c.getLong(i));
        }
        i = c.getColumnIndex(EventContract.COLUMN_PUB_DATE);
        if (!c.isNull(i)) {
            pubDate = new DateTime(c.getLong(i));
        }

        return Event.builder()
                .title(c.getString(c.getColumnIndex(EventContract.COLUMN_TITLE)))
                .link(c.getString(c.getColumnIndex(EventContract.COLUMN_LINK)))
                .description(c.getString(c.getColumnIndex(EventContract.COLUMN_DESCRIPTION)))
                .startDate(startDate)
                .endDate(endDate)
                .location(c.getString(c.getColumnIndex(EventContract.COLUMN_LOCATION)))
                .categories(serializer.deserialize(c.getString(c.getColumnIndex(EventContract.COLUMN_CATEGORIES))))
                .pubDate(pubDate)
                .buildingId(c.getString(c.getColumnIndex(EventContract.COLUMN_BUILDING_ID)))
                .build();
    }

    private ContentValues eventToContentValues(Event e) {
        ContentValues cv = new ContentValues();
        cv.put(EventContract.COLUMN_TITLE, e.getTitle());
        cv.put(EventContract.COLUMN_LINK, e.getLink());
        cv.put(EventContract.COLUMN_DESCRIPTION, e.getDescription());
        cv.put(EventContract.COLUMN_START_DATE, e.getStartDate() != null ? e.getStartDate().getMillis() : null);
        cv.put(EventContract.COLUMN_END_DATE, e.getEndDate() != null ? e.getEndDate().getMillis() : null);
        cv.put(EventContract.COLUMN_LOCATION, e.getLocation());
        cv.put(EventContract.COLUMN_CATEGORIES, serializer.serialize(e.getCategories()));
        cv.put(EventContract.COLUMN_PUB_DATE, e.getPubDate() != null ? e.getPubDate().getMillis() : null);
        cv.put(EventContract.COLUMN_BUILDING_ID, e.getBuildingId());
        return cv;
    }
}
