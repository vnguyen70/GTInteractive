package com.example.vi_tu.gtinteractive.persistence;

import android.util.Log;

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
}
