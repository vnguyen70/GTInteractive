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
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
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

    public void loadBuildingsFromAPI(final BuildingPersistence buildingsDB) {
        buildingsDB.deleteAll();
        Log.d("NETWORK_TEST", "loadBuildingsFromAPI()...");
//        String buildingsURL = "https://m.gatech.edu/api/gtplaces/buildings";
        String buildingsURL = "https://gtapp-api.rnoc.gatech.edu/api/v1/places";
        final JsonArrayRequest buildingsRequest = new JsonArrayRequest(Request.Method.GET, buildingsURL, new JSONObject(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray r) {
                        Log.d("NETWORK_TEST", "successful response from Buildings API");
                        Log.d("NETWORK_TEST", "buildings found: " + r.length());
                        new ProcessBuildingsResponseTask(buildingsDB).execute(r);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("NETWORK_TEST", "failed response from Buildings API");
                        DialogFragment dialog = new NetworkErrorDialogFragment();
                        dialog.show(fragmentManager, "buildingsNetworkError");
                        Toast.makeText(context, "ERROR: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
//        buildingsRequest.setTag(REQUEST_TAG);
        RequestQueueSingleton.getRequestQueue(context).add(buildingsRequest);
    }

    public void loadDiningsFromAPI(final DiningPersistence diningsDB) {
        diningsDB.deleteAll();
        Log.d("NETWORK_TEST", "loadDiningsFromAPI()...");
        String diningsURL = "http://diningdata.itg.gatech.edu:80/api/DiningLocations";
        final JsonArrayRequest diningsRequest = new JsonArrayRequest(Request.Method.GET, diningsURL, new JSONObject(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray r) {
                        Log.d("NETWORK_TEST", "successful response from Dinings API");
                        Log.d("NETWORK_TEST", "dinings found: " + r.length());
                        new ProcessDiningsResponseTask(diningsDB).execute(r);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NETWORK_TEST", "failed response from Dinings API");
                DialogFragment dialog = new NetworkErrorDialogFragment();
                dialog.show(fragmentManager, "diningsNetworkError");
            }
        });
//        diningsRequest.setTag(REQUEST_TAG);
        RequestQueueSingleton.getRequestQueue(context).add(diningsRequest);
    }

    public void updateDiningStatus(final DiningPersistence diningsDB) {
        Log.d("NETWORK_TEST", "updateDiningStatus()...");
        String diningURL = "http://diningdata.itg.gatech.edu:80/api/DiningLocations?type=dynamic";
        final JsonArrayRequest diningsRequest = new JsonArrayRequest(Request.Method.GET, diningURL, new JSONObject(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray r) {
                        Log.d("NETWORK_TEST", "successful response from Dinings API");
                        new UpdateDiningStatusTask(diningsDB).execute(r);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NETWORK_TEST", "failed response from Dinings API");
                DialogFragment dialog = new NetworkErrorDialogFragment();
                dialog.show(fragmentManager, "diningsNetworkError");
            }
        });
//        diningsRequest.setTag(REQUEST_TAG);
        RequestQueueSingleton.getRequestQueue(context).add(diningsRequest);
    }

    public void loadEventsFromAPI(final EventPersistence eventsDB, final BuildingPersistence buildingsDB) {
        eventsDB.deleteAll();
        Log.d("NETWORK_TEST", "loadEventsFromAPI()...");
//        String eventsURL = "http://www.calendar.gatech.edu/feeds/events.xml";
        LocalDate today = new LocalDate();
        String eventsURL = "https://gtapp-api.rnoc.gatech.edu/api/v1/events/day/" + today.getYear() + "/" + today.getMonthOfYear() + "/" + today.getDayOfMonth();
        final JsonArrayRequest eventsRequest = new JsonArrayRequest(Request.Method.GET, eventsURL, new JSONObject(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray r) {
                        Log.d("NETWORK_TEST", "successful response from events API");
                        Log.d("NETWORK_TEST", "events found: " + r.length());
                        new ProcessEventsResponseTask(eventsDB, buildingsDB).execute(r);
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

    private class ProcessBuildingsResponseTask extends AsyncTask<JSONArray, Void, Boolean> {

        private BuildingPersistence buildingsDB;

        ProcessBuildingsResponseTask(BuildingPersistence buildingsDB) {
            this.buildingsDB = buildingsDB;
        }

        @Override
        protected Boolean doInBackground(JSONArray... jsonArrays) {
            JSONArray r = jsonArrays[0];
            List<Building> bList = new ArrayList<>();
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

                    Building b = Building.builder()
                            .buildingId(o.getString("id"))
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
                            .category(Building.Category.getCategory(categoryJSON.getString("title")))
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
                    bList.add(b);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return buildingsDB.createMany(bList);
        }

        @Override
        protected void onPostExecute(Boolean wasSuccessful) {
            if (!wasSuccessful) {
                DialogFragment dialog = new NetworkErrorDialogFragment();
                dialog.show(fragmentManager, "buildingsNetworkError");
            }
        }
    }

    private class ProcessDiningsResponseTask extends AsyncTask<JSONArray, Void, Boolean> {

        private DiningPersistence diningsDB;

        ProcessDiningsResponseTask(DiningPersistence diningsDB) {
            this.diningsDB = diningsDB;
        }

        @Override
        protected Boolean doInBackground(JSONArray... jsonArrays) {
            JSONArray r = jsonArrays[0];
            List<Dining> dList = new ArrayList<>();
            try {
                for (int i = 0; i < r.length(); i++) {
                    JSONObject o = r.getJSONObject(i);

                    // tags
                    List<Dining.Tag> tags = new ArrayList<>();
                    JSONArray tagsArray = o.optJSONArray("Tags");
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject tagObject = tagsArray.getJSONObject(j);
                        Dining.Tag t = Dining.Tag.builder()
                                .tagId(tagObject.optString("TagID"))
                                .name(tagObject.optString("Name"))
                                .helpText(tagObject.optString("HelpText"))
                                .build();
                        tags.add(t);
                    }

                    // open and close times
                    LocalTime[] openTimes= new LocalTime[Constants.DAYS_OF_WEEK];
                    LocalTime[] closeTimes= new LocalTime[Constants.DAYS_OF_WEEK];
                    JSONArray timesArray = o.optJSONArray("HoursOfOperations");
                    if (timesArray.length() > 7) {
                        // TODO: this shouldn't be possible - check to make sure
                    }
                    for (int j = 0; j < timesArray.length(); j++) {
                        JSONObject timesObject = timesArray.getJSONObject(j);
                        JSONObject openTimeObject = timesObject.optJSONObject("Open");
                        JSONObject closeTimeObject = timesObject.optJSONObject("Close");
                        openTimes[openTimeObject.optInt("Day")] = DateTime.parse(openTimeObject.optString("Time"), DateTimeFormat.forPattern("HH:mm:ss")).toLocalTime(); // TODO: null check?
                        closeTimes[closeTimeObject.optInt("Day")] = DateTime.parse(closeTimeObject.optString("Time"), DateTimeFormat.forPattern("HH:mm:ss")).toLocalTime(); // TODO: null check?
                    }

                    // exceptions
                    List<Dining.Exception> exceptions = new ArrayList<>();
                    JSONArray exceptionsArray = o.optJSONArray("Exceptions");
                    for (int j = 0; j < exceptionsArray.length(); j++) {
                        JSONObject exceptionObject = exceptionsArray.getJSONObject(j).optJSONObject("Exception");

                        LocalTime[] exceptionOpenTimes= new LocalTime[Constants.DAYS_OF_WEEK];
                        LocalTime[] exceptionCloseTimes= new LocalTime[Constants.DAYS_OF_WEEK];
                        JSONArray exceptionTimesArray = exceptionObject.optJSONArray("HoursOfExceptions");
                        if (exceptionTimesArray.length() > 7) {
                            // TODO: this shouldn't be possible - check to make sure
                        }
                        for (int k = 0; k < exceptionTimesArray.length(); k++) {
                            JSONObject timesObject = exceptionTimesArray.getJSONObject(k);
                            JSONObject openTimeObject = timesObject.optJSONObject("Open");
                            JSONObject closeTimeObject = timesObject.optJSONObject("Close");
                            exceptionOpenTimes[openTimeObject.optInt("Day")] = DateTime.parse(openTimeObject.optString("Time"), DateTimeFormat.forPattern("HH:mm:ss")).toLocalTime(); // TODO: null check?
                            exceptionCloseTimes[closeTimeObject.optInt("Day")] = DateTime.parse(closeTimeObject.optString("Time"), DateTimeFormat.forPattern("HH:mm:ss")).toLocalTime(); // TODO: null check?
                        }

                        Dining.Exception e = Dining.Exception.builder()
                                .openTimes(exceptionOpenTimes)
                                .closeTimes(exceptionCloseTimes)
                                .startDateTime(DateTime.parse(exceptionObject.optString("StartDateTime"))) // TODO: null check?
                                .endDateTime(DateTime.parse(exceptionObject.optString("EndDateTime"))) // TODO: null check?
                                .build();
                        exceptions.add(e);
                    }

                    // build Dining object
                    Dining d = Dining.builder()
                            .diningId(o.optString("ID"))
                            .buildingId(Integer.toString(o.optJSONObject("Building").optInt("BuildingID")))
                            .latitude(o.has("Latitude") ? Double.parseDouble(o.getString("Latitude")) : null)
                            .longitude(o.has("Longitude") ? Double.parseDouble(o.getString("Longitude")) : null)
                            .name(o.optString("Name"))
                            .description(o.optString("Description"))
                            .locationDetails(o.optString("LocationDetails"))
                            .logoURL(o.optString("LogoURL"))
                            .menuLinkURL(o.optString("MenuLinkURL"))
                            .promotionMessage(o.optString("PromotionMessage"))
                            .promotionStartDate(o.optString("PromotionStartDate") != "null" ? DateTime.parse(o.optString("PromotionStartDate")) : null)
                            .promotionEndDate(o.optString("PromotionEndDate") != "null" ? DateTime.parse(o.optString("PromotionEndDate")) : null)
                            .openTimes(openTimes)
                            .closeTimes(closeTimes)
                            .exceptions(exceptions)
                            .tags(tags)
                            .isOpen(o.optBoolean("isOpen"))
                            .upcomingStatusChange(o.optString("upcomingStatusChange") != "null" ? DateTime.parse(o.optString("upcomingStatusChange")) : null)
                            .build();
                    dList.add(d);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return diningsDB.createMany(dList);
        }

        @Override
        protected void onPostExecute(Boolean wasSuccessful) {
            if (!wasSuccessful) {
                DialogFragment dialog = new NetworkErrorDialogFragment();
                dialog.show(fragmentManager, "buildingsNetworkError");
            }
        }
    }

    private class UpdateDiningStatusTask extends AsyncTask<JSONArray, Void, Boolean> {

        private DiningPersistence diningsDB;

        UpdateDiningStatusTask(DiningPersistence diningsDB) {
            this.diningsDB = diningsDB;
        }

        @Override
        protected Boolean doInBackground(JSONArray... jsonArrays) {
            JSONArray r = jsonArrays[0];
            try {
                for (int i = 0; i < r.length(); i++) {
                    JSONObject o = r.getJSONObject(i);
                    String diningId = o.optString("ID");
                    Dining d = diningsDB.findByDiningId(diningId);
                    if (d != null) {
                        d.setIsOpen(o.optBoolean("isOpen"));
                        d.setUpcomingStatusChange(!o.optString("upcomingStatusChange").equals("null") ? DateTime.parse(o.optString("upcomingStatusChange")) : null);
                        diningsDB.update(d, d.getId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean wasSuccessful) {
            if (!wasSuccessful) {
                DialogFragment dialog = new NetworkErrorDialogFragment();
                dialog.show(fragmentManager, "buildingsNetworkError");
            }
        }

    }

    private class ProcessEventsResponseTask extends AsyncTask<JSONArray, Void, Boolean> {

        // TODO: guarantee that this task completes uninterrupted - what things can interrupt an Async task? screen orientation change? Minimizing app?
        // TODO: instead of using SQL WHERE LIKE to match strings (which doesn't take into account whitespace - inefficient), TOKENIZE name and address fields and store in building object; this way you can just match tokens
        // TODO: catch common nicknames / abbreviations like CRC and CULC and such; currently - we fail to match these

        private EventPersistence eventsDB;
        private BuildingPersistence buildingsDB;

        ProcessEventsResponseTask(EventPersistence eventsDB, BuildingPersistence buildingsDB) {
            this.eventsDB = eventsDB;
            this.buildingsDB = buildingsDB;
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
                    String buildingId = buildingsDB.findBuildingIdByLocation(location);
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
                            .buildingId(buildingId)
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
                dialog.show(fragmentManager, "buildingsNetworkError");
            }
        }
    }

}
