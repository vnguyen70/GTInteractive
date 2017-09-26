package com.example.vi_tu.gtinteractive.domain;

import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Building {

    public final static int DAYS_PER_WEEK = 7; // TODO: refactor

    public static final Map<String, String> shortcuts = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put("Georgia Tech Campus", "GEORGIA_TECH_CAMPUS");
                put("Atlanta, GA", "ATLANTA_GA");
            }});

    public static final Building DEFAULT_BUILDING = Building.builder()
            .buildingId("DEFAULT")
            .name("Building Info Not Available")
            .build();

    Integer id;

    // From API: https://gtapp-api.rnoc.gatech.edu/api/v1/places
    String buildingId;
    String name;
    String imageURL;
    String websiteURL;
    String phoneNum;
    String description;
    String locatedIn;
    String yelpID;
    Boolean acceptsBuzzFunds;
    Integer priceLevel;
    LocalTime[] openTimes;
    LocalTime[] closeTimes;

    String categoryTitle; // TODO
    String categoryColor; // TODO

    String street;
    String city;
    String state;
    String postalCode;

    Double latitude;
    Double longitude;
    List<LatLng[]> polygons; // list of polygons, each of which is a LatLng[] of vertices

    // Custom fields
    String altNames; // building nicknames or abbreviations TODO: convert to List<String>
    String nameTokens; // name converted into lowercase tokens separated by spaces for easy searching
    String addressTokens; // street converted into lowercase tokens separated by spaces for easy searching
    Integer numFloors; // used for internal layout map

    public final class Contract implements BaseColumns {
        public static final String TABLE_NAME = "buildings";
        public static final String COLUMN_BUILDING_ID = "buildingId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE_URL = "imageURL";
        public static final String COLUMN_WEBSITE_URL = "websiteURL";
        public static final String COLUMN_PHONE_NUM = "phoneNum";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LOCATED_IN = "locatedIn";
        public static final String COLUMN_YELP_ID = "yelpID";
        public static final String COLUMN_ACCEPTS_BUZZ_FUNDS = "acceptsBuzzFunds";
        public static final String COLUMN_PRICE_LEVEL = "priceLevel";
        public static final String COLUMN_OPEN_TIMES = "openTimes";
        public static final String COLUMN_CLOSE_TIMES = "closeTimes";
        public static final String COLUMN_CATEGORY_TITLE = "categoryTitle";
        public static final String COLUMN_CATEGORY_COLOR = "categoryColor";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_POSTAL_CODE = "postalCode";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_POLYGONS = "polygons";
        public static final String COLUMN_ALT_NAMES = "altNames";
        public static final String COLUMN_NAME_TOKENS = "nameTokens";
        public static final String COLUMN_ADDRESS_TOKENS = "addressTokens";
        public static final String COLUMN_NUM_FLOORS = "numFloors";
    }

}
