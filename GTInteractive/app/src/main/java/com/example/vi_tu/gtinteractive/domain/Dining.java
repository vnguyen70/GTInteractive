package com.example.vi_tu.gtinteractive.domain;

import android.provider.BaseColumns;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dining {

    public final static int DAYS_PER_WEEK = 7;

    public static final Dining DEFAULT_DINING = Dining.builder()
            .diningId("DEFAULT")
            .buildingId("DEFAULT")
            .name("Dining Info Not Available")
            .description("n/a")
            .locationDetails("n/a")
            .promotionMessage("n/a")
            .build();

    // From API: http://diningdata.itg.gatech.edu:80/api/DiningLocations

    Integer id;

    // Object info
    String diningId;
    String buildingId;
    Double latitude;
    Double longitude;

    // Display info
    String name; // e.g. "Subway"
    String description; // e.g. "Customize your own Subs and Salads."
    String locationDetails; // e.g. "On the 1st Floor, next to Kaplan Center"
    String logoURL; // link to an image file
    String menuLinkURL; // link to a website
    String promotionMessage; // e.g. "Antiboitic-free Rotisserie Chicken everday at Subway!"
    DateTime promotionStartDate;
    DateTime promotionEndDate;

    // Hours of operation
    LocalTime[] openTimes; // slots 0 through 6 correspond with days Sunday through Saturday
    LocalTime[] closeTimes; // slots 0 through 6 correspond with days Sunday through Saturday

    // Hours of exceptions
    List<Dining.Exception> exceptions;

    // Tags
    List<Dining.Tag> tags;
    String tagIds; // tagIds separated by spaces for easy searching

    // Dynamic status (updated regularly)
    Boolean isOpen;
    DateTime upcomingStatusChange; // upcoming date and time when status changes (e.g. from open to closed, or from closed to open) // TODO: schedule device to fetch fresh data from dining API at this datetime

    // Custom fields
    String nameTokens; // name converted into lowercase tokens separated by spaces for easy searching

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Exception {
        DateTime startDateTime;
        DateTime endDateTime;
        LocalTime[] openTimes; // slots 0 through 6 correspond with days Sunday through Saturday
        LocalTime[] closeTimes; // slots 0 through 6 correspond with days Sunday through Saturday

        public final class Contract implements BaseColumns {
            public static final String TABLE_NAME = "diningExceptions";
            public static final String COLUMN_START_DATE_TIME = "startDateTime";
            public static final String COLUMN_END_DATE_TIME = "endDateTime";
            public static final String COLUMN_OPEN_TIMES = "openTimes";
            public static final String COLUMN_CLOSE_TIMES = "closeTimes";
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag {
        // From API: http://diningdata.itg.gatech.edu:80/api/Tags
        String tagId;
        String name;
        String helpText;

        // Custom fields
        String nameTokens; // name converted into lowercase tokens separated by spaces for easy searching

        public final class Contract implements BaseColumns {
            public static final String TABLE_NAME = "diningTags";
            public static final String COLUMN_TAG_ID = "tagId";
            public static final String COLUMN_NAME = "name";
            public static final String COLUMN_HELP_TEXT = "helpText";
            public static final String COLUMN_NAME_TOKENS = "nameTokens";
        }
    }

    public final class Contract implements BaseColumns {
        public static final String TABLE_NAME = "dinings";
        public static final String COLUMN_DINING_ID = "diningId";
        public static final String COLUMN_BUILDING_ID = "buildingId";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LOCATION_DETAILS = "locationDetails";
        public static final String COLUMN_LOGO_URL = "logoURL";
        public static final String COLUMN_MENU_LINK_URL = "menuLinkURL";
        public static final String COLUMN_PROMOTION_MESSAGE = "promotionMessage";
        public static final String COLUMN_PROMOTION_START_DATE = "promotionStartDate";
        public static final String COLUMN_PROMOTION_END_DATE = "promotionEndDate";
        public static final String COLUMN_OPEN_TIMES = "openTimes";
        public static final String COLUMN_CLOSE_TIMES = "closeTimes";
        public static final String COLUMN_EXCEPTIONS = "exceptions";
        public static final String COLUMN_TAGS = "tags";
        public static final String COLUMN_TAG_IDS = "tagIds";
        public static final String COLUMN_IS_OPEN = "isOpen";
        public static final String COLUMN_UPCOMING_STATUS_CHANGE = "upcomingStatusChange";
        public static final String COLUMN_NAME_TOKENS = "nameTokens";
    }

}
