package com.example.vi_tu.gtinteractive.utilities;

import android.database.Cursor;

import com.example.vi_tu.gtinteractive.domain.Event;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PersistenceUtils {

    public static final String DELIMITER = " ";

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

    public static String serializeTimes(LocalTime[] times) {
        String result = "";
        for (LocalTime lt : times) {
            if (lt != null) {
                result += lt.toString("HHmm") + DELIMITER;
            } else {
                result += "NULL" + DELIMITER; // TODO: refactor "NULL" into variable
            }
        }
        return result.trim();
    }

    public static LocalTime[] deserializeTimes(String s) {
        String[] timeStrings = s.split(DELIMITER);
        LocalTime[] times = new LocalTime[timeStrings.length];
        for (int i = 0; i < timeStrings.length; i++) {
            if (!timeStrings[i].equals("NULL")) { // TODO: refactor "NULL" into variable
                times[i] = DateTime.parse(timeStrings[i], DateTimeFormat.forPattern("HHmm")).toLocalTime();
            } else {
                times[i] = null;
            }
        }
        return times;
    }

    public static String serializePolygons(List<LatLng[]> polygons) {
        JSONArray polygonsJSON = new JSONArray();
        for (LatLng[] polygon : polygons) {
            JSONArray polygonJSON = new JSONArray();
            for (LatLng vertex : polygon) {
                JSONArray vertexJSON = new JSONArray();
                try {
                    vertexJSON.put(0, vertex.latitude);
                    vertexJSON.put(1, vertex.longitude);
                    polygonJSON.put(vertexJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            polygonsJSON.put(polygonJSON);
        }
        return polygonsJSON.toString();
    }

    public static List<LatLng[]> deserializePolygons(String s) {
        List<LatLng[]> polygons = new ArrayList<>();
        try {
            JSONArray polygonsJSON = new JSONArray(s);
            polygons = deserializePolygons(polygonsJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polygons;
    }

    public static List<LatLng[]> deserializePolygons(JSONArray polygonsJSON) {
        List<LatLng[]> polygons = new ArrayList<>();
        if (polygonsJSON != null) {
            try {
                for (int i = 0; i < polygonsJSON.length(); i++) {
                    JSONArray polygonJSON = polygonsJSON.getJSONArray(i);
                    LatLng[] polygon = new LatLng[polygonJSON.length()];
                    for (int j = 0; j < polygonJSON.length(); j++) {
                        JSONArray vertexJSON = polygonJSON.getJSONArray(j);
                        LatLng vertex = new LatLng(vertexJSON.getDouble(0), vertexJSON.getDouble(1));
                        polygon[j] = vertex;
                    }
                    polygons.add(polygon);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polygons;
    }

    public static String serializeCategories(List<Event.Category> categories) {
        String result = "";
        for (Event.Category c : categories) {
            result += c.name() + DELIMITER;
        }
        return result.trim();
    }

    public static List<Event.Category> deserializeCategories(String str) {
        List<Event.Category> categories = new ArrayList<>();
        String[] categoryStrings = str.split(DELIMITER);
        for (String s : categoryStrings) {
            if (s.length() > 0) {
                categories.add(Event.Category.valueOf(s));
            }
        }
        return categories;
    }

}
