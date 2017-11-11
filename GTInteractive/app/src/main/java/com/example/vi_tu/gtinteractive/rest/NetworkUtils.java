package com.example.vi_tu.gtinteractive.rest;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.vi_tu.gtinteractive.constants.SingletonProvider;

import java.util.Map;

/**
 * Created by kaliq on 10/15/2017.
 */

public class NetworkUtils {

    public static void getRequest(String url, VolleyResponseListener callback) {
        getRequest(url, null, callback);
    }

    public static void getRequest(String url, Map<String, String> queryParams, VolleyResponseListener callback) {
        String queryString = "";
        if (queryParams != null) {
            for (Map.Entry e : queryParams.entrySet()) {
                queryString += e.getKey() + "=" + e.getValue() + "&";
            }
            if (!queryString.isEmpty()) {
                queryString = "?" + queryString.substring(0, queryString.length()-1); // remove trailing '&'
            }
        }
        baseRequest(url + queryString, callback);
    }

    private static void baseRequest(String url, VolleyResponseListener callback) {
        JsonArrayRequest r = new JsonArrayRequest(url, callback, callback);
        SingletonProvider.getRequestQueue().add(r);
    }

}
