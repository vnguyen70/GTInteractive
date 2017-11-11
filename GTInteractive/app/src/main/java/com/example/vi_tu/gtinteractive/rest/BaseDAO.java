package com.example.vi_tu.gtinteractive.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.VolleyError;
import com.example.vi_tu.gtinteractive.domain.Entity;
import com.example.vi_tu.gtinteractive.persistence.BasePersistence;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.vi_tu.gtinteractive.rest.NetworkUtils.getRequest;

public abstract class BaseDAO<T extends Entity> {

    /**************** DAO Listeners ***************************************************************/

    public interface Listener<T extends Entity> {
        void onDAOError(Error error);
        void onListReady(List<T> tList);
        void onObjectReady(T t);
    }

    interface CacheListener {
        void onCacheReady();
    }

    public static class Error {
        public VolleyError volleyError;
        public Exception exception;

        public Error(VolleyError volleyError, Exception exception) {
            this.volleyError = volleyError;
            this.exception = exception;
        }
    }

    /**************** Instance ********************************************************************/

    final String baseURL;
    BasePersistence<T> cache;
    private long cacheDurationMS;
    private String cacheExpirationMSKey;
    Listener<T> callback;
    private SharedPreferences prefs;

    // status
    private int numRequestsWaitingForResponse;

    public BaseDAO(String baseURL, BasePersistence<T> cache, long cacheDurationMS, String cacheExpirationMSKey, Context context, Listener<T> callback) {
        this.baseURL = baseURL;
        this.cache = cache;
        this.cacheDurationMS = cacheDurationMS;
        this.cacheExpirationMSKey = cacheExpirationMSKey;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.callback = callback;
        this.numRequestsWaitingForResponse = 0;
    }

    /**************** CRUD ************************************************************************/

    public T get(int id) {
        return cache.get(id);
    }

    public List<T> getALl() {
        return cache.getAll();
    }

    public void getAsync(final int id) {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onObjectReady(cache.get(id));
            }
        });
    }

    public void getAllAsync() {
        verifyCache(new CacheListener() {
            @Override
            public void onCacheReady() {
                callback.onListReady(cache.getAll());
            }
        });
    }

    /**************** Cache ***********************************************************************/

    void verifyCache(final CacheListener cacheCallback) {
        if (DateTime.now().getMillis() >= prefs.getLong(cacheExpirationMSKey, 0)) { // reload cache
            cache.deleteAll();
            getRequest(baseURL, new VolleyResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    numRequestsWaitingForResponse--;
                    if (callback != null) {
                        callback.onDAOError(new Error(error, null));
                    }
                }
                @Override
                public void onResponse(JSONArray response) {
                    numRequestsWaitingForResponse--;
                    if (callback != null) {
                        try {
                            List<T> results = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                results.add(toDomain(response.getJSONObject(i)));
                            }
                            cache.createMany(results);
                            prefs.edit().putLong(cacheExpirationMSKey, DateTime.now().getMillis() + cacheDurationMS).apply();
                            cacheCallback.onCacheReady();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onDAOError(new Error(null, e));
                        }
                    }
                }
            });
            numRequestsWaitingForResponse++;
        } else {
            cacheCallback.onCacheReady();
        }
    }

    /**************** Status **********************************************************************/

    public boolean isLoading() {
        return numRequestsWaitingForResponse > 0;
    }

    /**************** Factory Methods *************************************************************/

    public abstract T toDomain(JSONObject o) throws JSONException;

}
