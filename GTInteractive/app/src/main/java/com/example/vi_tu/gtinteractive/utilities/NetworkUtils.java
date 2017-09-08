package com.example.vi_tu.gtinteractive.utilities;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaliq on 9/7/2017.
 */

public class NetworkUtils {

    // TODO: upon any failure, set sharedPreferences so that database is reloaded next time activity starts

    public static void loadBuildingsFromAPI(final BuildingPersistence buildingsDB, final RequestQueue queue) {
        buildingsDB.deleteAll();
        Log.d("NETWORK_TEST", "loadBuildingsFromAPI()...");
        String buildingsURL = "https://m.gatech.edu/api/gtplaces/buildings";
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
                        // TODO: display Toast indicating error loading buildings
                    }
                });
//        buildingsRequest.setTag(REQUEST_TAG);
        queue.add(buildingsRequest);
    }

    public static void loadEventsFromRSSFeed(final EventPersistence eventsDB, final BuildingPersistence buildingsDB, final RequestQueue queue) {
        eventsDB.deleteAll();
        Log.d("NETWORK_TEST", "loadEventsFromRSSFeed()...");
        String eventsURL = "http://www.calendar.gatech.edu/feeds/events.xml";
        final StringRequest eventsRequest = new StringRequest(Request.Method.GET, eventsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String r) {
                        Log.d("NETWORK_TEST", "successful response from events RSS feed");
                        new ProcessEventsResponseTask(eventsDB, buildingsDB).execute(r);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("NETWORK_TEST", "failed response from events RSS feed");
                        // TODO: display Toast indicating error loading events
                    }
                });
//        eventsRequest.setTag(REQUEST_TAG);
        queue.add(eventsRequest);
    }

    public static void loadDiningsFromAPI(final DiningPersistence diningsDB, final RequestQueue queue) {
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
                // TODO: display Toast indicating error loading dinings
            }
        });
//        diningsRequest.setTag(REQUEST_TAG);
        queue.add(diningsRequest);
    }

    private static class ProcessBuildingsResponseTask extends AsyncTask<JSONArray, Void, Boolean> {

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
                    Building b = Building.builder()
                            .buildingId(o.optString("b_id"))
                            .name(o.optString("name"))
                            .address(o.optString("address"))
                            .latitude(o.has("latitude") ? Double.parseDouble(o.getString("latitude")) : null)
                            .longitude(o.has("longitude") ? Double.parseDouble(o.getString("longitude")) : null)
                            .phoneNum(o.optString("phone_num"))
                            .link("")
                            .timeOpen(null)
                            .timeClose(null)
                            .numFloors(null)
                            .altNames("")
                            .nameTokens("")
                            .addressTokens("")
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
            // TODO: display Toast indicating success / failure
        }
    }

    private static class ProcessEventsResponseTask extends AsyncTask<String, Void, Boolean> {

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
        protected Boolean doInBackground(String... strings) {
            String r = strings[0];
            List<Event> eList = new ArrayList<>();

            try {

                // Step 1: parse response into XML elements

                Document doc = Jsoup.parse(r, "", Parser.xmlParser());
                Elements items = doc.getElementsByTag("item");
                Log.d("NETWORK_TEST", "events found: " + items.size());

                for (Element item : items) {

                    // Step 2: extract top-level fields from event item

                    String title = item.getElementsByTag("title").first().text();
                    String link = item.getElementsByTag("link").first().text();
                    String description = Parser.unescapeEntities(item.getElementsByTag("description").first().text(), false);
                    DateTime pubDate = DateTime.parse(item.getElementsByTag("pubDate").first().text(), DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z")); // TODO: null check?
                    List<String> categories = new ArrayList<>();
                    Elements categoryItems = item.getElementsByTag("category");
                    for (Element c : categoryItems) {
                        categories.add(c.text());
                    }

                    // Step 3: parse description into HTML elements

                    Document desc = Jsoup.parseBodyFragment(description);
                    Elements fields = desc.body().children();

                    String body = "";
                    DateTime startDate = null;
                    DateTime endDate = null;
                    String location = "";
                    String buildingId = "";

                    // Step 4: extract remaining fields from description

                    for (Element field : fields) {

                        if (field.hasClass("field-name-body")) {
                            // TODO: we assume there is only one field-item for body
                            Element bodyElement = field.getElementsByClass("field-item").first();
                            if (bodyElement != null) {
                                body = bodyElement.text();
                            }
                        } else if (field.hasClass("field-name-field-date")) {
                            Elements dates = field.getElementsByClass("date-display-single");
                            if (!dates.isEmpty()) {
                                Element startDateElement = dates.first();
                                Element endDateElement = dates.last();
                                if (startDateElement.hasAttr("content")) {
                                    startDate = DateTime.parse(startDateElement.attr("content"));
                                } else {
                                    Element test = startDateElement.getElementsByAttribute("content").first();
                                    if (test.hasAttr("content")) {
                                        startDate = DateTime.parse(test.attr("content"));
                                    }
                                }
                                if (endDateElement.hasAttr("content")) {
                                    endDate = DateTime.parse(endDateElement.attr("content"));
                                } else {
                                    Element test = endDateElement.getElementsByAttribute("content").last();
                                    if (test.hasAttr("content")) {
                                        endDate = DateTime.parse(test.attr("content"));
                                    }
                                }
                            }
                        } else if (field.hasClass("field-name-field-location")) {
                            // TODO: we assume there is only one field-item for location
                            Element locationElement = field.getElementsByClass("field-item").first();
                            if (locationElement != null) {
                                location = locationElement.text();
                            }
                        }
                    }

                    // Step 5: match event location to a buildingId

                    buildingId = buildingsDB.findBuildingIdByLocation(location);

                    // Step 6: build Event object

                    Event e = Event.builder()
                            .title(title)
                            .link(link)
                            .description(body)
                            .startDate(startDate)
                            .endDate(endDate)
                            .location(location)
                            .categories(categories)
                            .pubDate(pubDate)
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
            // TODO: display Toast indicating success / failure
        }
    }

    private static class ProcessDiningsResponseTask extends AsyncTask<JSONArray, Void, Boolean> {

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
                    LocalTime[] openTimes= new LocalTime[Dining.DAYS_PER_WEEK];
                    LocalTime[] closeTimes= new LocalTime[Dining.DAYS_PER_WEEK];
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

                        LocalTime[] exceptionOpenTimes= new LocalTime[Dining.DAYS_PER_WEEK];
                        LocalTime[] exceptionCloseTimes= new LocalTime[Dining.DAYS_PER_WEEK];
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
                            .buildingId(o.optJSONObject("Building").optInt("BuildingID"))
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
            // TODO: display Toast indicating success / failure
        }
    }
}
