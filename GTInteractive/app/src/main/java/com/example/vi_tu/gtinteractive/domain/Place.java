package com.example.vi_tu.gtinteractive.domain;

import android.provider.BaseColumns;
import android.util.Log;

import com.example.vi_tu.gtinteractive.constants.Constants;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Place extends Entity {

    public static final Map<String, String> shortcuts = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put("Georgia Tech Campus", "GEORGIA_TECH_CAMPUS");
                put("Atlanta, GA", "ATLANTA_GA");
            }});

    public static final Place DUMMY = Place.builder()
            .placeId("DUMMY")
            .name("Place Info Not Available")
            .imageURL("")
            .websiteURL("")
            .phoneNum("")
            .street("")
            .city("")
            .state("")
            .postalCode("")
            .latitude(0.0)
            .longitude(0.0)
            .polygons(new ArrayList<LatLng[]>())
            .category(Category.OTHER)
            .description("")
            .locatedIn("")
            .yelpID("")
            .acceptsBuzzFunds(false)
            .priceLevel(0)
            .openTimes(new LocalTime[Constants.DAYS_OF_WEEK])
            .closeTimes(new LocalTime[Constants.DAYS_OF_WEEK])
            .altNames("")
            .nameTokens("")
            .addressTokens("")
            .numFloors(0)
            .build();

    Integer id; // default = null
    boolean isOpen;

    @Override
    public Integer getId() {
        return id;
    }

    // Write function isOpen
    // In API, Day 0 is Sunday, Day 6 is Saturday
    // In Calendar, Day 2 is Monday so Assumption is Day 1 is Sunday
    public boolean isOpen() {
        Integer currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (null == this.getOpenTimes()) {
            Log.d("Place.java", this.getName() + " does not have open/closing times");
            return false;
        }
        LocalTime openTime = (this.getOpenTimes())[currentDay];
        LocalTime closeTime = (this.getCloseTimes())[currentDay];

        LocalTime now = LocalTime.now();
        Log.d("Place.java", this.getName() + " current time: " + now.toString());
        if (null == openTime) {
            return false;
        }

        Log.d("Place.java", this.getName() + " open time today: " + openTime);
        Log.d("Place.java", this.getName() + " close time today: " + closeTime);

        // Deal with scenario when closing time is earlier than closing time: Ex: closing time: 2AM and opening time: 8AM
        if (openTime.isAfter(closeTime)) {
            if (!now.isAfter(closeTime) || !now.isBefore(openTime)) {
                return true;
            }
        } else if (!now.isAfter(openTime) || !now.isBefore(closeTime)) {
            return true;
        }


        return false;
    }
    /**
     * From API: https://gtapp-api.rnoc.gatech.edu/api/v1/places
     */

    @NonNull String placeId; // 280/280
    @NonNull String name; // 280/280
    @NonNull String imageURL; // 280/280
    @NonNull String websiteURL; // 154/280; default = ""
    @NonNull String phoneNum; // 195/280; default = ""

    // address
    @NonNull String street; // 280/280
    @NonNull String city; // 280/280
    @NonNull String state; // 280/280
    @NonNull String postalCode; // 280/280

    // location
    @NonNull Double latitude; // 280/280
    @NonNull Double longitude; // 280/280
    @NonNull List<LatLng[]> polygons; // 227/280; default = new ArrayList<LatLng[]>() TODO: other 53 places not clickable on map?

    // (dining/stores only?)
    @NonNull String description; // 27/280; default = ""
    @NonNull String locatedIn; // 27/280; default = ""
    @NonNull String yelpID; // 32/280; default = ""
    @NonNull LocalTime[] openTimes; // 49/280; default = new LocalTime[Constants.DAYS_OF_WEEK]
    @NonNull LocalTime[] closeTimes; // 49/280; default = new LocalTime[Constants.DAYS_OF_WEEK]
    @NonNull Boolean acceptsBuzzFunds; // 47/280; default = false
    @NonNull Integer priceLevel; // 41/280; default = 0; values = 1 or 2

    // category
    @NonNull Category category; // 280/280; default = Category.OTHER

    /**
     * Custom fields
     */

    @NonNull String altNames; // place nicknames or abbreviations TODO: convert to List<String>
    @NonNull String nameTokens; // name converted into lowercase tokens separated by spaces for easy searching
    @NonNull String addressTokens; // street converted into lowercase tokens separated by spaces for easy searching
    @NonNull Integer numFloors; // default = 0; used for internal layout map

    public enum Category { // TODO: choose our own colors
        FOOD("c0392b"),
        HOUSING("2c3e50"),
        SPORTS("2ecc71"),
        GREEK("3498db"),
        PARKING("373737"),
        ACADEMIC("fccc33"),
        OTHER("888888");

        @Getter private final String color;

        Category(String color) {
            this.color = color;
        }

        public static Category getCategory(String name) {
            for (Category c : values()) {
                if (c.name().equals(name.toUpperCase())) return c;
            }
            return Category.OTHER; // default Category
        }
    }

    public final class Contract implements BaseColumns {
        public static final String TABLE_NAME = "places";
        public static final String COLUMN_PLACE_ID = "placeId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE_URL = "imageURL";
        public static final String COLUMN_WEBSITE_URL = "websiteURL";
        public static final String COLUMN_PHONE_NUM = "phoneNum";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_POSTAL_CODE = "postalCode";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_POLYGONS = "polygons";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LOCATED_IN = "locatedIn";
        public static final String COLUMN_YELP_ID = "yelpID";
        public static final String COLUMN_OPEN_TIMES = "openTimes";
        public static final String COLUMN_CLOSE_TIMES = "closeTimes";
        public static final String COLUMN_ACCEPTS_BUZZ_FUNDS = "acceptsBuzzFunds";
        public static final String COLUMN_PRICE_LEVEL = "priceLevel";
        public static final String COLUMN_ALT_NAMES = "altNames";
        public static final String COLUMN_NAME_TOKENS = "nameTokens";
        public static final String COLUMN_ADDRESS_TOKENS = "addressTokens";
        public static final String COLUMN_NUM_FLOORS = "numFloors";
    }

}
