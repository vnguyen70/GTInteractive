package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.utilities.ListSerializer;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.cursorIndexToDateTime;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.dateTimeToMillis;

/**
 * Created by kaliq on 9/5/2017.
 */

public class EventPersistence extends BasePersistence<Event>{

    private ListSerializer<String> serializer;

    public EventPersistence(SQLiteDatabase db) {
        super(db, Event.Contract.TABLE_NAME, Event.Contract._ID,
                Event.Contract.COLUMN_START_DATE + "," + Event.Contract.COLUMN_END_DATE); // TODO: ascending or descending?
        this.serializer = new ListSerializer<>();
    }

    /******** Search Functions ********************************************************************/

    public List<Event> findByBuildingId(String buildingId) {
        String selection = Event.Contract.COLUMN_BUILDING_ID + " = " + buildingId;
        return findMany(selection);
    }

    /******** Helper Functions ********************************************************************/

    @Override
    protected ContentValues toContentValues(Event e) {
        ContentValues cv = new ContentValues();
        cv.put(Event.Contract.COLUMN_TITLE, e.getTitle());
        cv.put(Event.Contract.COLUMN_LINK, e.getLink());
        cv.put(Event.Contract.COLUMN_DESCRIPTION, e.getDescription());
        cv.put(Event.Contract.COLUMN_START_DATE, dateTimeToMillis(e.getStartDate()));
        cv.put(Event.Contract.COLUMN_END_DATE, dateTimeToMillis(e.getEndDate()));
        cv.put(Event.Contract.COLUMN_LOCATION, e.getLocation());
        cv.put(Event.Contract.COLUMN_CATEGORIES, serializer.serialize(e.getCategories()));
        cv.put(Event.Contract.COLUMN_PUB_DATE, dateTimeToMillis(e.getPubDate()));
        cv.put(Event.Contract.COLUMN_BUILDING_ID, e.getBuildingId());
        return cv;
    }

    @Override
    protected Event toDomain(Cursor c) {
        return Event.builder()
                .title(c.getString(c.getColumnIndex(Event.Contract.COLUMN_TITLE)))
                .link(c.getString(c.getColumnIndex(Event.Contract.COLUMN_LINK)))
                .description(c.getString(c.getColumnIndex(Event.Contract.COLUMN_DESCRIPTION)))
                .startDate(cursorIndexToDateTime(c, c.getColumnIndex(Event.Contract.COLUMN_START_DATE)))
                .endDate(cursorIndexToDateTime(c, c.getColumnIndex(Event.Contract.COLUMN_END_DATE)))
                .location(c.getString(c.getColumnIndex(Event.Contract.COLUMN_LOCATION)))
                .categories(serializer.deserialize(c.getString(c.getColumnIndex(Event.Contract.COLUMN_CATEGORIES))))
                .pubDate(cursorIndexToDateTime(c, c.getColumnIndex(Event.Contract.COLUMN_PUB_DATE)))
                .buildingId(c.getString(c.getColumnIndex(Event.Contract.COLUMN_BUILDING_ID)))
                .build();
    }

}
