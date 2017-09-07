package com.example.vi_tu.gtinteractive.persistence;

import android.provider.BaseColumns;

/**
 * Created by kaliq on 9/5/2017.
 */

public final class BuildingContract implements BaseColumns {
    public static final String TABLE_NAME = "buildings";
    public static final String COLUMN_BUILDING_ID = "buildingId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_PHONE_NUM = "phoneNum";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_TIME_OPEN = "timeOpen";
    public static final String COLUMN_TIME_CLOSE = "timeClose";
    public static final String COLUMN_NUM_FLOORS = "numFloors";
    public static final String COLUMN_ALT_NAMES = "altNames";
    public static final String COLUMN_NAME_TOKENS = "nameTokens";
    public static final String COLUMN_ADDRESS_TOKENS = "addressTokens";
}
