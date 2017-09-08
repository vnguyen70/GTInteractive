package com.example.vi_tu.gtinteractive.utilities;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaliq on 9/7/2017.
 */

public class ListSerializer<T> {

    public String serialize(List<T> list) {
        JSONObject json = new JSONObject();
        try {
            json.put("items", new JSONArray(list));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public List<T> deserialize(String str) {
        List<T> list = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(str);
            JSONArray array = json.optJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                list.add((T) array.opt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String serializeArray(T[] array) {
        JSONObject json = new JSONObject();
        try {
            json.put("items", new JSONArray(array));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public T[] deserializeArray(String str) {
        try {
            JSONObject json = new JSONObject(str);
            JSONArray array = json.optJSONArray("items");
            T[] result = (T[]) new Object[array.length()];
            for (int i = 0; i < array.length(); i++) {
                result[i] = (T) array.opt(i);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (T[]) new Object[0];
    }

    public String serializeTimeArray(LocalTime[] array) {
        JSONObject json = new JSONObject();
        List<Long> times = new ArrayList<>();
        DateTime epoch = new DateTime(0);
        for (LocalTime t : array) {
            if (t != null) {
                times.add(epoch.withTime(t).getMillis());
            } else {
                times.add(null);
            }
        }
        try {
            json.put("items", new JSONArray(times));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public LocalTime[] deserializeTimeArray (String str) {
        List<LocalTime> times = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(str);
            JSONArray array = json.optJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                Long time = array.optLong(i);
                if (time != 0) {
                    times.add(new DateTime().toLocalTime());
                } else {
                    times.add(null);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return times.toArray(new LocalTime[times.size()]);
    }

}
