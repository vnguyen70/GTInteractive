package com.example.vi_tu.gtinteractive.domain;

import android.provider.BaseColumns;

import org.joda.time.LocalTime;

import java.util.Collections;
import java.util.HashMap;
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

    public static final Map<String, String> shortcuts = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put("Georgia Tech Campus", "GEORGIA_TECH_CAMPUS");
                put("Atlanta, GA", "ATLANTA_GA");
            }});

    // From API: http://gtjourney.gatech.edu/gt-devhub/apis/places
    String buildingId;
    String name;
    String address;
    Double latitude;
    Double longitude;
    String phoneNum;

    // Custom fields
    String link;
    LocalTime timeOpen;
    LocalTime timeClose;
    Integer numFloors; // used for internal layout map
    String altNames; // building nicknames or abbreviations TODO: convert to List<String>
    String nameTokens; // name converted into lowercase tokens separated by spaces for easy searching
    String addressTokens; // address converted into lowercase tokens separated by spaces for easy searching

    public final class Contract implements BaseColumns {
        public static final String TABLE_NAME = "buildings";
        public static final String COLUMN_BUILDING_ID = "buildingId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_PHONE_NUM = "phoneNum";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_TIME_OPEN = "timeOpen";
        public static final String COLUMN_TIME_CLOSE = "timeClose";
        public static final String COLUMN_NUM_FLOORS = "numFloors";
        public static final String COLUMN_ALT_NAMES = "altNames";
        public static final String COLUMN_NAME_TOKENS = "nameTokens";
        public static final String COLUMN_ADDRESS_TOKENS = "addressTokens";
    }

}
