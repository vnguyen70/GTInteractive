package com.example.vi_tu.gtinteractive.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kaliq on 9/4/2017.
 */

public class PersistenceHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "persistence.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public PersistenceHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BUILDINGS_TABLE = "CREATE TABLE " + BuildingContract.TABLE_NAME + " (" +
                BuildingContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BuildingContract.COLUMN_BUILDING_ID + " TEXT, " +
                BuildingContract.COLUMN_NAME + " TEXT, " +
                BuildingContract.COLUMN_ADDRESS + " TEXT, " +
                BuildingContract.COLUMN_LATITUDE + " DOUBLE, " +
                BuildingContract.COLUMN_LONGITUDE + " DOUBLE, " +
                BuildingContract.COLUMN_PHONE_NUM + " TEXT, " +
                BuildingContract.COLUMN_LINK + " TEXT, " +
                BuildingContract.COLUMN_TIME_OPEN + " TIME, " +
                BuildingContract.COLUMN_TIME_CLOSE + " TIME, " +
                BuildingContract.COLUMN_NUM_FLOORS + " INTEGER, " +
                BuildingContract.COLUMN_ALT_NAMES + " TEXT, " +
                BuildingContract.COLUMN_NAME_TOKENS + " TEXT, " +
                BuildingContract.COLUMN_ADDRESS_TOKENS + " TEXT " +
                "); ";

        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " + EventContract.TABLE_NAME + " (" +
                EventContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EventContract.COLUMN_TITLE + " TEXT, " +
                EventContract.COLUMN_LINK + " TEXT, " +
                EventContract.COLUMN_DESCRIPTION + " TEXT, " +
                EventContract.COLUMN_START_DATE + " DATETIME, " +
                EventContract.COLUMN_END_DATE + " DATETIME, " +
                EventContract.COLUMN_LOCATION + " TEXT, " +
                EventContract.COLUMN_CATEGORIES + " TEXT, " +
                EventContract.COLUMN_PUB_DATE + " DATETIME, " +
                EventContract.COLUMN_BUILDING_ID + " TEXT " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_BUILDINGS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BuildingContract.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventContract.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
