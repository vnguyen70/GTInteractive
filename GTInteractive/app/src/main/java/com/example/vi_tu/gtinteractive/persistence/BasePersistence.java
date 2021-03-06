package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class BasePersistence<T> {

    SQLiteDatabase db;
    String tableName;
    String idColumnName;
    String orderBy;

    BasePersistence(SQLiteDatabase db, String tableName, String idColumnName, String orderBy) {
        this.db = db;
        this.tableName = tableName;
        this.idColumnName = idColumnName;
        this.orderBy = orderBy;
    }

    /******** Standard CRUD (Create, Retrieve, Update, Delete) ************************************/

    // returns whether transaction succeeded or not
    public boolean createMany(List<T> tList) {
        boolean wasSuccessful = true;
        db.beginTransaction();
        try {
            for (T t : tList) {
                create(t);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            wasSuccessful = false;
        } finally {
            db.endTransaction();
        }
        return wasSuccessful;
    }

    // returns row id of newly inserted object
    public long create(T t) {
        return db.insert(tableName, null, toContentValues(t));
    }

    // returns number of rows affected (should be 1)
    public int update(T t, int id) {
        return db.update(tableName, toContentValues(t), idColumnName + " = " + id, null); // TODO: object primary keys are required to be type int
    }

    // returns number of rows affected (should be 1)
    public int delete(int id) {
        return db.delete(tableName, idColumnName + " = " + id, null);
    } // TODO: object primary keys are required to be type int

    // returns number of rows affected
    public int deleteAll() {
        return db.delete(tableName, null, null);
    }

    public T get(int id) {
        return findOne(idColumnName + " = " + id);
    } // TODO: object primary keys are required to be type int

    public List<T> getAll() {
        return findMany(null);
    }

    /******** Helper Functions ********************************************************************/

    T findOne(String selection) {
        Cursor c = db.query(
                tableName,
                null,
                selection,
                null,
                null,
                null,
                null,
                "1"
        );
        T t = null;
        if (c.moveToNext()) {
            t = toDomain(c);
        }
        c.close();
        return t;
    }

    List<T> findMany(String selection) {
        Cursor c = db.query(
                tableName,
                null,
                selection,
                null,
                null,
                null,
                orderBy
        );
        List<T> tList = new ArrayList<>();
        while (c.moveToNext()) {
            tList.add(toDomain(c));
        }
        c.close();
        return tList;
    }

    T toDomain(Cursor c) {
        return null;
    }

    ContentValues toContentValues(T t) {
        return new ContentValues();
    }

}
