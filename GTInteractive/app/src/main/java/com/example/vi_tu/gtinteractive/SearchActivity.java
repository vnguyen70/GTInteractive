package com.example.vi_tu.gtinteractive;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SQLiteDBHelper myDb;
    List<BuildingMessage> buildings = new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new SQLiteDBHelper(this);
        if (myDb.getSize() == 0) {
            VolleyRequest(this, "https://m.gatech.edu/api/gtplaces/buildings", new ServerCallback() {
                @Override
                public void onSuccess(List<BuildingMessage> buildings) {
                    //Adding information to database
                    boolean isInserted = false;
                    for (BuildingMessage building : buildings) {
                        isInserted = myDb.insertData(building.getBuilding_id(), building.getBuilding_name());
                    }
                    if (isInserted == true) {
                        Toast.makeText(SearchActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        Log.d("mySuperTag", "data was inserted");
                    }
                }
            });
        }




    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Find a Building...");
        ComponentName cn = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        // no change with below line of code, maybe i have to make it an icon first?
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        return true;
    }

    @Override
    protected void onDestroy() {
        myDb.close();
        super.onDestroy();
    }

    public void VolleyRequest(final Context context, final String url, final ServerCallback callback) {
        // must add gradle dependency for RequestQueue
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // convert the response to input stream
                        InputStream is = new ByteArrayInputStream( response.getBytes(Charset.defaultCharset()) );
                        try {
                            // interpret the input stream and return as array list of Building Messages
                            buildings = readJsonStream(is);
                            callback.onSuccess(buildings);
                        } catch (IOException e) {
                            Log.d("myTag", "stream reading failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myTag", "there was an error in getting a response");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public List<BuildingMessage> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<BuildingMessage> readMessagesArray(JsonReader reader) throws IOException {
        List<BuildingMessage> messages = new ArrayList<BuildingMessage>();
        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    public BuildingMessage readMessage(JsonReader reader) throws IOException {
        String id = null;
        String text = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("b_id")) {
                id = reader.nextString();
            } else if (name.equals("name")) {
                text = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new BuildingMessage(id, text);
    }

}
