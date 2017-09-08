package com.example.vi_tu.gtinteractive.temp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 9/2/17.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {

// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "building.db";
    public static final String TABLE_NAME = "building_table";
    public static final String BUILDING_COLUMN_ID = "_id";
    public static final String BUILDING_COLUMN_NAME = "name";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BUILDING_COLUMN_ID + " TEXT," +
                    BUILDING_COLUMN_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertData(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BUILDING_COLUMN_ID, id);
        contentValues.put(BUILDING_COLUMN_NAME, name);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        }
        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public List getBuildingNames() {
        Cursor res = getAllData();
        List buildingNames = new ArrayList<>();
        while (res.moveToNext()) {
            // column 0 is primary key, column 1 is id, column 2 is building_name
            buildingNames.add(res.getString(2));
        }
        return buildingNames;
    }

    public int getSize() {
        Cursor res = getAllData();
        return res.getCount();
    }

    public List getBuildingNames2(String userInput) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                BUILDING_COLUMN_NAME
        };
        String selection = BUILDING_COLUMN_NAME + " = ?";
        String[] selectionArgs = {"Howell Residence Hall"};

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
        );

        List buildingNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            String buildingName = cursor.getString(
                    cursor.getColumnIndexOrThrow(BUILDING_COLUMN_NAME));
            buildingNames.add(buildingName);
        }
        cursor.close();
        return buildingNames;
    }
}

