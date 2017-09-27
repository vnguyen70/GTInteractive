package com.example.vi_tu.gtinteractive.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;

public class PersistenceHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "persistence.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 13;

    // Constructor
    public PersistenceHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BUILDINGS_TABLE = "CREATE TABLE " + Building.Contract.TABLE_NAME + " (" +
                Building.Contract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Building.Contract.COLUMN_BUILDING_ID + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_NAME + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_WEBSITE_URL + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_PHONE_NUM + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_STREET + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_CITY + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_STATE + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_POSTAL_CODE + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                Building.Contract.COLUMN_LONGITUDE + " DOUBLE NOT NULL, " +
                Building.Contract.COLUMN_POLYGONS + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_LOCATED_IN + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_YELP_ID + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_OPEN_TIMES + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_CLOSE_TIMES + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_ACCEPTS_BUZZ_FUNDS + " BOOLEAN NOT NULL, " +
                Building.Contract.COLUMN_PRICE_LEVEL + " INTEGER NOT NULL, " +
                Building.Contract.COLUMN_ALT_NAMES + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_NAME_TOKENS + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_ADDRESS_TOKENS + " TEXT NOT NULL, " +
                Building.Contract.COLUMN_NUM_FLOORS + " INTEGER NOT NULL " +
                "); ";

        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " + Event.Contract.TABLE_NAME + " (" +
                Event.Contract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Event.Contract.COLUMN_EVENT_ID + " TEXT NOT NULL, " +
                Event.Contract.COLUMN_TITLE + " TEXT NOT NULL, " +
                Event.Contract.COLUMN_LOCATION + " TEXT NOT NULL, " +
                Event.Contract.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                Event.Contract.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                Event.Contract.COLUMN_START_DATE + " DATETIME, " +
                Event.Contract.COLUMN_END_DATE + " DATETIME, " +
                Event.Contract.COLUMN_ALL_DAY + " BOOLEAN NOT NULL, " +
                Event.Contract.COLUMN_RECURRING + " BOOLEAN NOT NULL, " +
                Event.Contract.COLUMN_CATEGORIES + " TEXT NOT NULL, " +
                Event.Contract.COLUMN_BUILDING_ID + " TEXT NOT NULL " +
                "); ";

        final String SQL_CREATE_DININGS_TABLE = "CREATE TABLE " + Dining.Contract.TABLE_NAME + " (" +
                Dining.Contract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Dining.Contract.COLUMN_DINING_ID + " TEXT, " +
                Dining.Contract.COLUMN_BUILDING_ID + " TEXT, " +
                Dining.Contract.COLUMN_LATITUDE + " DOUBLE, " +
                Dining.Contract.COLUMN_LONGITUDE + " DOUBLE, " +
                Dining.Contract.COLUMN_NAME + " TEXT, " +
                Dining.Contract.COLUMN_DESCRIPTION + " TEXT, " +
                Dining.Contract.COLUMN_LOCATION_DETAILS + " TEXT, " +
                Dining.Contract.COLUMN_LOGO_URL + " TEXT, " +
                Dining.Contract.COLUMN_MENU_LINK_URL + " TEXT, " +
                Dining.Contract.COLUMN_PROMOTION_MESSAGE + " TEXT, " +
                Dining.Contract.COLUMN_PROMOTION_START_DATE + " DATETIME, " +
                Dining.Contract.COLUMN_PROMOTION_END_DATE + " DATETIME, " +
                Dining.Contract.COLUMN_OPEN_TIMES + " TEXT, " +
                Dining.Contract.COLUMN_CLOSE_TIMES + " TEXT, " +
                Dining.Contract.COLUMN_EXCEPTIONS + " TEXT, " +
                Dining.Contract.COLUMN_TAGS + " TEXT, " +
                Dining.Contract.COLUMN_TAG_IDS + " TEXT, " +
                Dining.Contract.COLUMN_IS_OPEN + " BOOLEAN, " +
                Dining.Contract.COLUMN_UPCOMING_STATUS_CHANGE + " DATETIME, " +
                Dining.Tag.Contract.COLUMN_NAME_TOKENS + " TEXT " +
                "); ";

        final String SQL_CREATE_DINING_TAGS_TABLE = "CREATE TABLE " + Dining.Tag.Contract.TABLE_NAME + " (" +
                Dining.Tag.Contract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Dining.Tag.Contract.COLUMN_TAG_ID + " TEXT, " +
                Dining.Tag.Contract.COLUMN_NAME + " TEXT, " +
                Dining.Tag.Contract.COLUMN_HELP_TEXT + " TEXT, " +
                Dining.Tag.Contract.COLUMN_NAME_TOKENS + " TEXT " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_BUILDINGS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DININGS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DINING_TAGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Building.Contract.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Event.Contract.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Dining.Contract.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Dining.Tag.Contract.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}