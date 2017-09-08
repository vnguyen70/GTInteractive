package com.example.vi_tu.gtinteractive.utilities;

import android.database.Cursor;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * Created by kaliq on 9/7/2017.
 */

public class PersistenceUtils {

    public static Long timeToMillis(LocalTime input) {
        DateTime epoch = new DateTime(0);
        return input != null ? epoch.withTime(input).getMillis() : null;
    }

    public static Long dateTimeToMillis(DateTime input) {
        return input != null ? input.getMillis() : null;
    }

    public static DateTime cursorIndexToDateTime(Cursor c, int i) {
        if (!c.isNull(i)) {
            return new DateTime(c.getLong(i));
        }
        return null;
    }

    public static LocalTime cursorIndexToTime(Cursor c, int i) {
        if (!c.isNull(i)) {
            return new LocalTime(c.getLong(i));
        }
        return null;
    }

    public static Integer cursorIndexToInteger(Cursor c, int i) {
        if (!c.isNull(i)) {
            return c.getInt(i);
        }
        return null;
    }

}
