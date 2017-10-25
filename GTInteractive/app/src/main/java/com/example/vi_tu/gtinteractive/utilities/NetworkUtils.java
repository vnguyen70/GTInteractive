package com.example.vi_tu.gtinteractive.utilities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.vi_tu.gtinteractive.constants.Constants;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.PlacePersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.vi_tu.gtinteractive.utilities.PersistenceUtils.deserializePolygons;

public class NetworkUtils {

    Context context;
    FragmentManager fragmentManager;

    public NetworkUtils(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    // TODO: upon any failure, set sharedPreferences so that database is reloaded next time activity starts

    public void loadPlacesFromAPI(final PlacePersistence placesDB) {
        placesDB.deleteAll();
        Log.d("NETWORK_TEST", "loadPlacesFromAPI()...");
//        String placesURL = "https://m.gatech.edu/api/gtplaces/buildings";
        String placesURL = "https://gtapp-api.rnoc.gatech.edu/api/v1/places";
        final JsonArrayRequest placesRequest = new JsonArrayRequest(Request.Method.GET, placesURL, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray r) {
                        Log.d("NETWORK_TEST", "successful response from Places API");
                        Log.d("NETWORK_TEST", "places found: " + r.length());
                        new ProcessPlacesResponseTask(placesDB).execute(r);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("NETWORK_TEST", "failed response from Places API");
                        DialogFragment dialog = new NetworkErrorDialogFragment();
                        dialog.show(fragmentManager, "placesNetworkError");
                        Toast.makeText(context, "ERROR: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
//        placesRequest.setTag(REQUEST_TAG);
        RequestQueueSingleton.getRequestQueue(context).add(placesRequest);
    }

    public void loadEventsFromAPI(final EventPersistence eventsDB, final PlacePersistence placesDB) {
        eventsDB.deleteAll();
        Log.d("NETWORK_TEST", "loadEventsFromAPI()...");
//        String eventsURL = "http://www.calendar.gatech.edu/feeds/events.xml";
        LocalDate today = new LocalDate();
        String eventsURL = "https://gtapp-api.rnoc.gatech.edu/api/v1/events/day/" + today.getYear() + "/" + today.getMonthOfYear() + "/" + today.getDayOfMonth();
        final JsonArrayRequest eventsRequest = new JsonArrayRequest(Request.Method.GET, eventsURL, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray r) {
                        Log.d("NETWORK_TEST", "successful response from events API");
                        Log.d("NETWORK_TEST", "events found: " + r.length());
                        new ProcessEventsResponseTask(eventsDB, placesDB).execute(r);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NETWORK_TEST", "failed response from events API");
                DialogFragment dialog = new NetworkErrorDialogFragment();
                dialog.show(fragmentManager, "eventsNetworkError");
            }
        });
//        eventsRequest.setTag(REQUEST_TAG);
        RequestQueueSingleton.getRequestQueue(context).add(eventsRequest);
    }

    private class ProcessPlacesResponseTask extends AsyncTask<JSONArray, Void, Boolean> {

        private PlacePersistence placesDB;

        ProcessPlacesResponseTask(PlacePersistence placesDB) {
            this.placesDB = placesDB;
        }

        @Override
        protected Boolean doInBackground(JSONArray... jsonArrays) {
            JSONArray r = jsonArrays[0];
            List<Place> pList = new ArrayList<>();
            try {
                for (int i = 0; i < r.length(); i++) {
                    JSONObject o = r.getJSONObject(i);

                    // nested JSON objects
                    JSONObject categoryJSON = o.getJSONObject("category");
                    JSONObject locationJSON = o.getJSONObject("location");
                    JSONObject addressJSON = locationJSON.getJSONObject("address");
                    JSONArray timesJSON = o.optJSONArray("hours");

                    // openTimes and closeTimes
                    LocalTime[] openTimes = new LocalTime[Constants.DAYS_OF_WEEK];
                    LocalTime[] closeTimes = new LocalTime[Constants.DAYS_OF_WEEK];
                    if (timesJSON != null && timesJSON.length() <= 7) {
                        for (int j = 0; j < timesJSON.length(); j++) {
                            JSONObject dayJSON = timesJSON.getJSONObject(j);
                            JSONObject openTimeJSON = dayJSON.optJSONObject("open");
                            JSONObject closeTimeJSON = dayJSON.optJSONObject("close");
                            if (openTimeJSON != null && closeTimeJSON != null) {
                                openTimes[openTimeJSON.getInt("day")] = DateTime.parse(openTimeJSON.getString("time"), DateTimeFormat.forPattern("HHmm")).toLocalTime();
                                closeTimes[closeTimeJSON.getInt("day")] = DateTime.parse(closeTimeJSON.getString("time"), DateTimeFormat.forPattern("HHmm")).toLocalTime();
                            }
                        }
                    }

                    Place b = Place.builder()
                            .placeId(o.getString("id"))
                            .name(o.getString("name"))
                            .imageURL(o.optString("imageURL", ""))
                            .websiteURL(o.optString("websiteURL", ""))
                            .phoneNum(o.optString("phone", ""))
                            .street(addressJSON.getString("street"))
                            .city(addressJSON.getString("city"))
                            .state(addressJSON.getString("state"))
                            .postalCode(addressJSON.getString("postalCode"))
                            .latitude(locationJSON.getDouble("latitude"))
                            .longitude(locationJSON.getDouble("longitude"))
                            .polygons(deserializePolygons(locationJSON.optJSONArray("shapeCoordinates")))
                            .category(Place.Category.getCategory(categoryJSON.getString("title")))
                            .description(o.optString("description", ""))
                            .locatedIn(o.optString("locatedIn", ""))
                            .yelpID(o.optString("yelpID", ""))
                            .acceptsBuzzFunds(o.optBoolean("acceptsBuzzFunds", false))
                            .priceLevel(o.optInt("priceLevel", 0))
                            .openTimes(openTimes)
                            .closeTimes(closeTimes)
                            .altNames("")
                            .nameTokens("")
                            .addressTokens("")
                            .numFloors(0)
                            .build();
                    pList.add(b);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return placesDB.createMany(pList);
        }

        @Override
        protected void onPostExecute(Boolean wasSuccessful) {
            if (!wasSuccessful) {
                DialogFragment dialog = new NetworkErrorDialogFragment();
                dialog.show(fragmentManager, "placesNetworkError");
            }
        }
    }

    private class ProcessEventsResponseTask extends AsyncTask<JSONArray, Void, Boolean> {

        // TODO: guarantee that this task completes uninterrupted - what things can interrupt an Async task? screen orientation change? Minimizing app?
        // TODO: instead of using SQL WHERE LIKE to match strings (which doesn't take into account whitespace - inefficient), TOKENIZE name and address fields and store in place object; this way you can just match tokens
        // TODO: catch common nicknames / abbreviations like CRC and CULC and such; currently - we fail to match these

        private EventPersistence eventsDB;
        private PlacePersistence placesDB;

        ProcessEventsResponseTask(EventPersistence eventsDB, PlacePersistence placesDB) {
            this.eventsDB = eventsDB;
            this.placesDB = placesDB;
        }

        @Override
        protected Boolean doInBackground(JSONArray... jsonArrays) {
            JSONArray r = jsonArrays[0];
            List<Event> eList = new ArrayList<>();

            try {
                for (int i = 0; i < r.length(); i++) {
                    JSONObject o = r.getJSONObject(i);

                    int millisStart = o.optInt("startDate") * 1000;
                    int millisEnd = o.optInt("endDate") * 1000;
                    String location = o.getString("location");
                    String placeId = placesDB.findPlaceIdByLocation(location);
                    List<Event.Category> categories = new ArrayList<>();
                    JSONArray categoriesJSON = o.getJSONArray("tags");
                    for (int j = 0; j < categoriesJSON.length(); j++) {
                        JSONObject categoryJSON = categoriesJSON.getJSONObject(j);
                        categories.add(Event.Category.getCategory(categoryJSON.getString("name")));
                    }

                    Event e = Event.builder()
                            .eventId(o.getString("id"))
                            .title(o.getString("title"))
                            .location(location)
                            .description(o.optString("description", ""))
                            .imageURL(o.optString("imageURL", ""))
                            .startDate(millisStart > 0 ? new DateTime(millisStart) : null)
                            .endDate(millisEnd > 0 ? new DateTime(millisEnd) : null)
                            .allDay(o.optBoolean("allDay"))
                            .recurring(o.optBoolean("recurring"))
                            .categories(categories)
                            .placeId(placeId)
                            .build();
                    eList.add(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return eventsDB.createMany(eList);
        }

        @Override
        protected void onPostExecute(Boolean wasSuccessful) {
            if (!wasSuccessful) {
                DialogFragment dialog = new NetworkErrorDialogFragment();
                dialog.show(fragmentManager, "placesNetworkError");
            }
        }
    }

}
