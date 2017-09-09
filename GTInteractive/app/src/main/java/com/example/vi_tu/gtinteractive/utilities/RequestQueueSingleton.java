package com.example.vi_tu.gtinteractive.utilities;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by kaliq on 9/9/2017.
 */

public class RequestQueueSingleton {

    private static RequestQueue requestQueue;

    private RequestQueueSingleton(){}

    public static synchronized RequestQueue getRequestQueue(Context context){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

}
