package com.example.vi_tu.gtinteractive.constants;

/**
 * Created by kaliq on 9/27/2017.
 */

public class Constants {
    public static final int DAYS_OF_WEEK = 7;
    public static final double GATECH_LATITUDE = 33.777433;
    public static final double GATECH_LONGITUDE = -84.398636;
    public static final double DEFAULT_LATITUDE = 33.774637; // Tech Green by default
    public static final double DEFAULT_LONGITUDE = -84.397321; // Tech Green by default

    public static final String HOST_NAME = "https://gtapp-api.rnoc.gatech.edu";
    public static final String BASE_URL = HOST_NAME + "/api/v1";

    public static final long BUILDINGS_CACHE_DURATION_MS = 86400000; // milliseconds in one day
    public static final long EVENTS_CACHE_DURATION_MS = 86400000; // milliseconds in one day

    public static final String BUILDINGS_CACHE_EXPIRATION_MS_KEY = "buildingsCacheExpirationMS";
    public static final String EVENTS_CACHE_EXPIRATION_MS_KEY = "eventsCacheExpirationMS";
}
