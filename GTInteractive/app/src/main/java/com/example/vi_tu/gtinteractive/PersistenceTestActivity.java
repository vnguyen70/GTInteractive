package com.example.vi_tu.gtinteractive;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Event;
import com.example.vi_tu.gtinteractive.persistence.BuildingPersistence;
import com.example.vi_tu.gtinteractive.persistence.EventPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaliq on 9/5/2017.
 */

public class PersistenceTestActivity extends AppCompatActivity {

    private Button displayBuildingsButton;
    private Button displayEventsButton;
    private Button reloadBuildingsButton;
    private Button reloadEventsButton;
    private TextView resultsTextView;

    private SQLiteDatabase db;
    private BuildingPersistence buildingsDB;
    private EventPersistence eventsDB;

    public static final String REQUEST_TAG = "PersistenceTestActivity";
    private RequestQueue queue;

    private static final long BUILDINGS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day
    private static final long EVENTS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display UI

        setContentView(R.layout.activity_main_volley);

        // Setup database and networking

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        db = dbHelper.getWritableDatabase();
        buildingsDB = new BuildingPersistence(db);
        eventsDB = new EventPersistence(db);

        queue = Volley.newRequestQueue(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        long nowMS = Calendar.getInstance().getTime().getTime();
        long buildingsCacheExpiredMS = sharedPreferences.getLong("buildingsCacheExpiredMS", 0);
        long eventsCacheExpiredMS = sharedPreferences.getLong("eventsCacheExpiredMS", 0);

        if (nowMS >= buildingsCacheExpiredMS) {
            loadBuildingsFromAPI();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("buildingsCacheExpiredMS", nowMS + BUILDINGS_CACHE_DURATION_MS);
            editor.apply();
        } else {
            Log.d("NETWORK_TEST", "buildings already loaded");
        }
        if (nowMS >= eventsCacheExpiredMS) {
            loadEventsFromRSSFeed();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("eventsCacheExpiredMS", nowMS + EVENTS_CACHE_DURATION_MS);
            editor.apply();
        } else {
            Log.d("NETWORK_TEST", "events already loaded");
        }

        // Setup listeners for UI elements

        displayBuildingsButton = (Button) this.findViewById(R.id.display_buildings_button);
        displayEventsButton = (Button) this.findViewById(R.id.display_events_button);
        reloadBuildingsButton = (Button) this.findViewById(R.id.reload_buildings_button);
        reloadEventsButton = (Button) this.findViewById(R.id.reload_events_button);
        resultsTextView = (TextView) this.findViewById(R.id.tv_results);

        displayBuildingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Building> bList = buildingsDB.getAllBuildings();
                String results = "";
                for (Building b : bList) {
                    results += b.getBuildingId() + " - " + b.getNameTokens() + " - " + b.getAddressTokens() + "\n";
//                    results += b.getLatitude() + ", " + b.getLongitude() + "\n";
                }
                resultsTextView.setText("" + bList.size() + " buildings found:\n\n" + results);
            }
        });

        displayEventsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Event> eList = eventsDB.getAllEvents();
                String results = "";
                int none = 0;
                int single = 0;
                int multiple = 0;
                for (Event e : eList) {
//                    results += e.getStartDate() + " to " + e.getEndDate() + "\n\n";

                    results += e.getLocation() + "\n-- " + e.getBuildingId() + " --\n\n";
                    if (e.getBuildingId().equals("NONE")) {
                        none++;
                    } else if (e.getBuildingId().equals("MULTIPLE")) {
                        multiple++;
                    } else {
                        single++;
                    }
                }
                resultsTextView.setText("" + eList.size() + " events found:\n\nbuildingId matches:\n - none: " + none + "\n - single: " + single + "\n - multiple: " + multiple + "\n\n" + results);
            }
        });

        reloadBuildingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                resultsTextView.setText("");
                loadBuildingsFromAPI();
            }
        });

        reloadEventsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                resultsTextView.setText("");
                loadEventsFromRSSFeed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(REQUEST_TAG);
        }
    }

    private void loadBuildingsFromAPI() {
        buildingsDB.deleteAllBuildings(); // clear cache
        Log.d("NETWORK_TEST", "loading buildings from API...");
        String buildingsURL = "https://m.gatech.edu/api/gtplaces/buildings";
        final JsonArrayRequest buildingsRequest = new JsonArrayRequest(Request.Method.GET, buildingsURL, new JSONObject(),
                new Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray r) {
                        Log.d("NETWORK_TEST", "successful response from Buildings API");
                        Log.d("NETWORK_TEST", "buildings found: " + r.length());
                        new ProcessBuildingsResponseTask().execute(r);
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("NETWORK_TEST", "failed response from Buildings API");
                        // TODO: display Toast indicating error loading buildings
                    }
                });
        buildingsRequest.setTag(REQUEST_TAG);
        queue.add(buildingsRequest);
    }

    private class ProcessBuildingsResponseTask extends AsyncTask<JSONArray, Void, Void> {

        @Override
        protected Void doInBackground(JSONArray... jsonArrays) {
            JSONArray r = jsonArrays[0];
            for (int i = 0; i < r.length(); i++) {
                try {
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
                            .nameTokens(o.has("name") ? o.getString("name").replaceAll("\\W", " ").toLowerCase() : "") // TODO: collapse whitespace into single spaces
                            .addressTokens(o.has("address") ? o.getString("address").replaceAll("\\W", " ").toLowerCase() : "") // TODO: collapse whitespace into single spaces
                            .build();

                    buildingsDB.createBuilding(b);

                } catch (Exception e) {
                    e.printStackTrace(); // TODO: handle error
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // TODO: display Toast indicating successful loading of buildings
        }
    }

    private void loadEventsFromRSSFeed() {
        eventsDB.deleteAllEvents(); // clear cache
        Log.d("NETWORK_TEST", "loading events from RSS feed...");
        String eventsURL = "http://www.calendar.gatech.edu/feeds/events.xml";
        final StringRequest eventsRequest = new StringRequest(Request.Method.GET, eventsURL,
                new Listener<String>() {
                    @Override
                    public void onResponse(String r) {
                        Log.d("NETWORK_TEST", "successful response from events RSS feed");
                        new ProcessEventsResponseTask().execute(r);
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("NETWORK_TEST", "failed response from events RSS feed");
                        // TODO: display Toast indicating error loading events
                    }
                });
        eventsRequest.setTag(REQUEST_TAG);
        queue.add(eventsRequest);
    }

    // TODO: guarantee that this task copmletes uninterrupted - what things can interrupt an Async task? screen orientation change? Minimizing app?
    private class ProcessEventsResponseTask extends AsyncTask<String, Void, Void> {

        // TODO: instead of using SQL WHERE LIKE to match strings (which doesn't take into account whitespace - inefficient), TOKENIZE name and address fields and store in building object; this way you can just match tokens

        @Override
        protected Void doInBackground(String... strings) {
            String r = strings[0];
            try {
                Document doc = Jsoup.parse(r, "", Parser.xmlParser());

                Elements items = doc.getElementsByTag("item");

                Log.d("NETWORK_TEST", "events found: " + items.size());

                List<Building> allBuildings = buildingsDB.getAllBuildings();

                int numBuildings = allBuildings.size();

                Map<String, Integer> matches = new HashMap<>();

                Map<String, String> keyPhrases = new HashMap<>(); // TODO: locations are matched against these common phrases first
                keyPhrases.put("Georgia Tech Campus", "Georgia Tech Campus");
                keyPhrases.put("Atlanta, GA", "Atlanta, GA");

                // TODO: catch common nicknames / abbreviations like CRC and CULC and such; currently - we fail to match these

                for (Element item : items) {
                    String title = item.getElementsByTag("title").first().text();
                    String link = item.getElementsByTag("link").first().text();
                    String description = Parser.unescapeEntities(item.getElementsByTag("description").first().text(), false);
                    DateTime pubDate = DateTime.parse(item.getElementsByTag("pubDate").first().text(), DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z")); // TODO: null check?
                    List<String> categories = new ArrayList<>();
                    Elements categoryItems = item.getElementsByTag("category");
                    for (Element c : categoryItems) {
                        categories.add(c.text());
                    }

                    String body = "";
                    DateTime startDate = null;
                    DateTime endDate = null;
                    String location = "";
                    String buildingId = "";

                    Document desc = Jsoup.parseBodyFragment(description);
                    Elements fields = desc.body().children();
                    for (Element field : fields) {
                        if (field.hasClass("field-name-body")) {
                            Element bodyElement = field.getElementsByClass("field-item").first(); // TODO: we assume there is only one field-item for body
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
                                // startDate = dates.first().attr("content"); // TODO: this doesn't work? content attribute cannot be found? startDate is an empty string
                            }
                        } else if (field.hasClass("field-name-field-location")) {
                            Element locationElement = field.getElementsByClass("field-item").first(); // TODO: we assume there is only one field-item for location
                            if (locationElement != null) {
                                location = locationElement.text();
                            }
                        }
                    }

                    // 0 - text location against known key phrases
                    String matchedId = keyPhrases.get(location.trim());
                    if (matchedId != null) {
                        buildingId = matchedId;
                    } else {
                        // 1 - split string pattern (location) into tokens by whitespace
                        String[] splited = location.replaceAll("\\W", " ").toLowerCase().split("\\s+"); // TODO: check regex expression
                        List<String> tokens = new ArrayList<>();
                        for (String s : splited) {
                            tokens.add(s);
                            if (s.equals("bldg")) { // TODO: create more filters like this; find a cleaner solution
                                tokens.add("building");
                            } else if (s.equals("dr")) { // TODO: presence of "dr", "drive", "st", or "street" indicates address; perhaps flag location for address matching?
                                tokens.add("drive");
                            } else if (s.equals("drive")) {
                                tokens.add("dr");
                            } else if (s.equals("st")) {
                                tokens.add("street");
                            } else if (s.equals("street")) {
                                tokens.add("st");
                            }
                        }

                        Log.d("LOCATION_TOKENS", tokens.toString());

                        // TODO: filter against building nicknames

                        //2 - for each token, query buildings database for partial matches against building name field and store all matches in "matches" map
                        matches.clear();
                        for (String s : tokens) {
                            List<Building> partialMatches = buildingsDB.findBuildingsByName(s);
                            for (Building b : partialMatches) {
                                String bId = b.getBuildingId();
                                Integer oldValue = matches.get(bId);
                                int incrValue = 10;
                                // TODO: give "room" less weight; create a better filter solution than this
                                if (s.equals("room")) { // lowest weight - can apply to any building / location
                                    incrValue = 1;
                                } else if (s.equals("building") || s.equals("bldg") || s.equals("hall") || s.equals("lab")) { // low weight - can apply to nearly any building / location
                                    incrValue = 3;
                                } else if (s.equals("house") || s.equals("deck") || s.equals("apartments") || s.equals("center")) { // medium weight - can apply to some buildings / locations
                                    incrValue = 5;
                                }
                                if (oldValue != null) {
                                    matches.put(bId, oldValue + incrValue);
                                } else {
                                    matches.put(bId, incrValue);
                                }
                            }
                        }

                        // 3 - get all buildings with max matches

                        List<String> bestMatches = new ArrayList<>();
                        if (!matches.isEmpty()) {
                            int maxNumMatches = Collections.max(matches.values());
                            for (Map.Entry<String, Integer> entry : matches.entrySet()) {
                                if (entry.getValue() == maxNumMatches) {
                                    bestMatches.add(entry.getKey());
                                }
                            }
                        }

                        // 5 - if more than one best match; filter again, this time against building address field

                        if (bestMatches.size() > 1) { // TODO: matched to more than one location; now filter against building address; question: can we ONLY search within best matches here?
                            for (String s : tokens) {
                                List<Building> partialMatches = buildingsDB.findBuildingsByAddress(s);
                                for (Building b : partialMatches) {
                                    String bId = b.getBuildingId();
                                    Integer oldValue = matches.get(bId);
                                    int incrValue = 10;
                                    // TODO: create a better filter solution than this
                                    if (isInteger(s)) {
                                        incrValue = 20; // prioritize number matches in addresses
                                    }
                                    if (s.equals("atlanta") || s.equals("ga") || s.equals("georgia")) { // lowest weight - can apply to any address
                                        incrValue = 1;
                                    }
                                    if (oldValue != null) {
                                        matches.put(bId, oldValue + incrValue);
                                    } else {
                                        matches.put(bId, incrValue);
                                    }
                                }
                            }

                            bestMatches.clear();
                            if (!matches.isEmpty()) {
                                int maxNumMatches = Collections.max(matches.values());
                                for (Map.Entry<String, Integer> entry : matches.entrySet()) {
                                    if (entry.getValue() == maxNumMatches) {
                                        bestMatches.add(entry.getKey());
                                    }
                                }
                            }
                        }

                        // 5 save buildingId in event object
                        if (bestMatches.size() == 1) {
                            buildingId = bestMatches.get(0);
                        } else if (bestMatches.size() > 1){
                            buildingId = "MULTIPLE";
                        } else {
                            buildingId = "NONE";
                        }

//                        for (String id : bestMatches) {
//                            buildingId += id + ", ";
//                        }
                    }

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

                    eventsDB.createEvent(e); // TODO: faster way to add multiple events to database in bulk?

                }
            } catch (Exception e) {
                e.printStackTrace(); // TODO: handle error
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // TODO: display Toast indicating successful loading of events
        }
    }

    private static boolean isInteger(String str) {
//        if (str == null) {
//            return false;
//        }
        int length = str.length();
//        if (length == 0) {
//            return false;
//        }
        int i = 0;
//        if (str.charAt(0) == '-') {
//            if (length == 1) {
//                return false;
//            }
//            i = 1;
//        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

}
