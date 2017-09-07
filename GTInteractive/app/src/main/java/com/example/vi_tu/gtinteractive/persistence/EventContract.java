package com.example.vi_tu.gtinteractive.persistence;

import android.provider.BaseColumns;

/**
 * Created by kaliq on 9/5/2017.
 */

public final class EventContract implements BaseColumns {
    public static final String TABLE_NAME = "events";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_START_DATE = "startDate";
    public static final String COLUMN_END_DATE = "endDate";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_CATEGORIES = "categories";
    public static final String COLUMN_PUB_DATE = "pubDate";
    public static final String COLUMN_BUILDING_ID = "buildingId";
}
