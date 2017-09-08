package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.utilities.ListSerializer;

import org.joda.time.LocalTime;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.cursorIndexToDateTime;
import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.dateTimeToMillis;
import static com.example.vi_tu.gtinteractive.utilities.StringUtils.tokenize;

/**
 * Created by kaliq on 9/7/2017.
 */

public class DiningPersistence extends BasePersistence<Dining>{

    private ListSerializer<LocalTime> timeSerializer;
    private ListSerializer<Dining.Exception> exceptionSerializer;
    private ListSerializer<Dining.Tag> tagSerializer;


    public DiningPersistence(SQLiteDatabase db) {
        super(db, Dining.Contract.TABLE_NAME, Dining.Contract._ID,
                "LENGTH(" + Dining.Contract.COLUMN_DINING_ID + ")," + Dining.Contract.COLUMN_DINING_ID);
        this.timeSerializer = new ListSerializer<>();
        this.exceptionSerializer = new ListSerializer<>();
        this.tagSerializer = new ListSerializer<>();
    }

    /******** Search Functions ********************************************************************/

    public List<Dining> findByBuildingId(String buildingId) {
        return findMany(Dining.Contract.COLUMN_BUILDING_ID + " = " + buildingId);
    }

    public List<Dining> findByName(String name) {
        return findMany(Dining.Contract.COLUMN_NAME_TOKENS + " LIKE \"%" + name + "%\")");
    }

    public List<Dining> findByTagId(String tagId) {
        return findMany(Dining.Contract.COLUMN_TAG_IDS + " LIKE \"% " + tagId + " %\")");
    }

    public List<Dining> findOpen(boolean reload) {
        if (reload) {
            // TODO: reload fresh data from API
        }
        return findMany(Dining.Contract.COLUMN_IS_OPEN + " = 1");
    }

    public List<Dining> findClosed(boolean reload) {
        if (reload) {
            // TODO: reload fresh data from API
        }
        return findMany(Dining.Contract.COLUMN_IS_OPEN + " = 0");
    }

    /******** Helper Functions ********************************************************************/

    @Override
    protected ContentValues toContentValues(Dining d) {
        String tagIds = " "; // TODO: surround with white space for SQL string-matching
        for (Dining.Tag t : d.getTags()) {
            tagIds += t.getTagId() + " ";
        }
        ContentValues cv = new ContentValues();
        cv.put(Dining.Contract.COLUMN_DINING_ID, d.getDiningId());
        cv.put(Dining.Contract.COLUMN_BUILDING_ID, d.getBuildingId());
        cv.put(Dining.Contract.COLUMN_NAME, d.getName());
        cv.put(Dining.Contract.COLUMN_DESCRIPTION, d.getDescription());
        cv.put(Dining.Contract.COLUMN_LOCATION_DETAILS, d.getLocationDetails());
        cv.put(Dining.Contract.COLUMN_LOGO_URL, d.getLogoURL());
        cv.put(Dining.Contract.COLUMN_MENU_LINK_URL, d.getMenuLinkURL());
        cv.put(Dining.Contract.COLUMN_PROMOTION_MESSAGE, d.getPromotionMessage());
        cv.put(Dining.Contract.COLUMN_PROMOTION_START_DATE, dateTimeToMillis(d.getPromotionStartDate()));
        cv.put(Dining.Contract.COLUMN_PROMOTION_END_DATE, dateTimeToMillis(d.getPromotionEndDate()));
        cv.put(Dining.Contract.COLUMN_OPEN_TIMES, timeSerializer.serializeTimeArray(d.getOpenTimes()));
        cv.put(Dining.Contract.COLUMN_CLOSE_TIMES, timeSerializer.serializeTimeArray(d.getCloseTimes()));
        cv.put(Dining.Contract.COLUMN_EXCEPTIONS, exceptionSerializer.serialize(d.getExceptions()));
        cv.put(Dining.Contract.COLUMN_TAGS, tagSerializer.serialize(d.getTags()));
        cv.put(Dining.Contract.COLUMN_TAG_IDS, tagIds);
        cv.put(Dining.Contract.COLUMN_IS_OPEN, d.getIsOpen());
        cv.put(Dining.Contract.COLUMN_UPCOMING_STATUS_CHANGE, dateTimeToMillis(d.getUpcomingStatusChange()));
        cv.put(Dining.Contract.COLUMN_NAME_TOKENS, tokenize(d.getName()));
        return cv;
    }

    @Override
    protected Dining toDomain(Cursor c) {
        return Dining.builder()
                .diningId(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_DINING_ID)))
                .buildingId(c.getInt(c.getColumnIndex(Dining.Contract.COLUMN_BUILDING_ID)))
                .name(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_NAME)))
                .description(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_DESCRIPTION)))
                .locationDetails(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_LOCATION_DETAILS)))
                .logoURL(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_LOGO_URL)))
                .menuLinkURL(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_MENU_LINK_URL)))
                .promotionMessage(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_PROMOTION_MESSAGE)))
                .promotionStartDate(cursorIndexToDateTime(c, c.getColumnIndex(Dining.Contract.COLUMN_PROMOTION_START_DATE)))
                .promotionEndDate(cursorIndexToDateTime(c,c.getColumnIndex(Dining.Contract.COLUMN_PROMOTION_END_DATE) ))
                .openTimes(timeSerializer.deserializeTimeArray(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_OPEN_TIMES))))
                .closeTimes(timeSerializer.deserializeTimeArray(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_CLOSE_TIMES))))
                .exceptions(exceptionSerializer.deserialize(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_EXCEPTIONS))))
                .tags(tagSerializer.deserialize(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_TAGS))))
                .tagIds(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_TAG_IDS)))
                .isOpen(c.getInt(c.getColumnIndex(Dining.Contract.COLUMN_IS_OPEN)) == 1) // 1 = true; 0 = false
                .upcomingStatusChange(cursorIndexToDateTime(c, c.getColumnIndex(Dining.Contract.COLUMN_UPCOMING_STATUS_CHANGE)))
                .nameTokens(c.getString(c.getColumnIndex(Dining.Contract.COLUMN_NAME_TOKENS)))
                .build();
    }

}
