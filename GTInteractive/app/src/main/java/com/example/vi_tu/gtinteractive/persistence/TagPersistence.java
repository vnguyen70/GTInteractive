package com.example.vi_tu.gtinteractive.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vi_tu.gtinteractive.domain.Dining;

import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.StringUtils.tokenize;

/**
 * Created by kaliq on 9/7/2017.
 */

public class TagPersistence extends BasePersistence<Dining.Tag> {

    public TagPersistence(SQLiteDatabase db) {
        super(db, Dining.Tag.Contract.TABLE_NAME, Dining.Tag.Contract._ID, Dining.Tag.Contract.COLUMN_TAG_ID);
    }

    /******** Search Functions ********************************************************************/

    public Dining.Tag findByTagId(String tagId) {
        return findOne(Dining.Tag.Contract.COLUMN_TAG_ID + " = " + tagId);
    }

    public List<Dining.Tag> findByName(String name) {
        return findMany(Dining.Tag.Contract.COLUMN_NAME_TOKENS + " LIKE \"%" + name + "%\")");
    }

    /******** Helper Functions ********************************************************************/

    @Override
    protected ContentValues toContentValues(Dining.Tag t) {
        ContentValues cv = new ContentValues();
        cv.put(Dining.Tag.Contract.COLUMN_TAG_ID, t.getTagId());
        cv.put(Dining.Tag.Contract.COLUMN_NAME, t.getName());
        cv.put(Dining.Tag.Contract.COLUMN_HELP_TEXT, t.getHelpText());
        cv.put(Dining.Tag.Contract.COLUMN_NAME_TOKENS, tokenize(t.getName()));
        return cv;
    }

    @Override
    protected Dining.Tag toDomain(Cursor c) {
        return Dining.Tag.builder()
                .tagId(c.getString(c.getColumnIndex(Dining.Tag.Contract.COLUMN_TAG_ID)))
                .name(c.getString(c.getColumnIndex(Dining.Tag.Contract.COLUMN_NAME)))
                .helpText(c.getString(c.getColumnIndex(Dining.Tag.Contract.COLUMN_HELP_TEXT)))
                .nameTokens(c.getString(c.getColumnIndex(Dining.Tag.Contract.COLUMN_NAME_TOKENS)))
                .build();
    }

}
