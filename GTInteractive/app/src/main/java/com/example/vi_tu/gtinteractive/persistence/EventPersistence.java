package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.utilities.ListSerializer;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.cursorIndexToDateTime;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.dateTimeToMillis;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.deserializeCategories;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.serializeCategories;

public class EventPersistence extends BasePersistence<Event> {

    private ListSerializer<String> serializer;

    public EventPersistence(SQLiteDatabase db) {
        super(db, Event.Contract.TABLE_NAME, Event.Contract._ID, Event.Contract.COLUMN_TITLE); // TODO: ascending or descending?
        this.serializer = new ListSerializer<>();
    }

    /******** Search Functions ********************************************************************/

    public List<Event> findByEventId(String eventId) {
        return findMany(Event.Contract.COLUMN_EVENT_ID + " = " + eventId);
    }

    public List<Event> findByTitle(String query) {
        return findMany("(LOWER(" + Event.Contract.COLUMN_TITLE + ") LIKE \"%" + query.toLowerCase() + "%\")");
    }

    public List<Event> findByCategory(Event.Category category) {
        return findMany(Event.Contract.COLUMN_CATEGORIES + " LIKE \"%" + category.name() + "%\""); // TODO: whole word matching
    }

    public List<Event> findByBuildingId(String buildingId) {
        return findMany(Event.Contract.COLUMN_BUILDING_ID + " = " + buildingId);
    }

    /******** Helper Functions ********************************************************************/

    @Override
    protected ContentValues toContentValues(Event e) {
        ContentValues cv = new ContentValues();
        cv.put(Event.Contract.COLUMN_EVENT_ID, e.getEventId());
        cv.put(Event.Contract.COLUMN_TITLE, e.getTitle());
        cv.put(Event.Contract.COLUMN_LOCATION, e.getLocation());
        cv.put(Event.Contract.COLUMN_DESCRIPTION, e.getDescription());
        cv.put(Event.Contract.COLUMN_IMAGE_URL, e.getImageURL());
        cv.put(Event.Contract.COLUMN_START_DATE, dateTimeToMillis(e.getStartDate()));
        cv.put(Event.Contract.COLUMN_END_DATE, dateTimeToMillis(e.getEndDate()));
        cv.put(Event.Contract.COLUMN_ALL_DAY, e.getAllDay());
        cv.put(Event.Contract.COLUMN_RECURRING, e.getRecurring());
        cv.put(Event.Contract.COLUMN_CATEGORIES, serializeCategories(e.getCategories()));
        cv.put(Event.Contract.COLUMN_BUILDING_ID, e.getBuildingId());
        return cv;
    }

    @Override
    protected Event toDomain(Cursor c) {
        return Event.builder()
                .id(c.getInt(c.getColumnIndex(Event.Contract._ID)))
                .eventId(c.getString(c.getColumnIndex(Event.Contract.COLUMN_EVENT_ID)))
                .title(c.getString(c.getColumnIndex(Event.Contract.COLUMN_TITLE)))
                .location(c.getString(c.getColumnIndex(Event.Contract.COLUMN_LOCATION)))
                .description(c.getString(c.getColumnIndex(Event.Contract.COLUMN_DESCRIPTION)))
                .imageURL(c.getString(c.getColumnIndex(Event.Contract.COLUMN_IMAGE_URL)))
                .startDate(cursorIndexToDateTime(c, c.getColumnIndex(Event.Contract.COLUMN_START_DATE)))
                .endDate(cursorIndexToDateTime(c, c.getColumnIndex(Event.Contract.COLUMN_END_DATE)))
                .allDay(c.getInt(c.getColumnIndex(Event.Contract.COLUMN_ALL_DAY)) == 1)
                .recurring(c.getInt(c.getColumnIndex(Event.Contract.COLUMN_RECURRING)) == 1)
                .categories(deserializeCategories(c.getString(c.getColumnIndex(Event.Contract.COLUMN_CATEGORIES))))
                .buildingId(c.getString(c.getColumnIndex(Event.Contract.COLUMN_BUILDING_ID)))
                .build();
    }

}
