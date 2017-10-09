package com.example.vi_tu.gtinteractive.domain;

import android.provider.BaseColumns;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

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
public class Event extends Entity {

    public static final Event DUMMY = Event.builder()
            .eventId("DUMMY")
            .title("Event Info Not Available")
            .location("")
            .description("")
            .imageURL("")
            .allDay(false)
            .recurring(false)
            .categories(new ArrayList<Category>())
            .buildingId("DUMMY")
            .build();

    Integer id;

    /**
     * From API: https://gtapp-api.rnoc.gatech.edu/api/v1/places
     */

    @NonNull String eventId;
    @NonNull String title;
    @NonNull String location;
    @NonNull String description;
    @NonNull String imageURL;
    DateTime startDate;
    DateTime endDate;
    @NonNull Boolean allDay;
    @NonNull Boolean recurring;
    @NonNull List<Category> categories;

    /**
     *  Custom fields
     */

    @NonNull String buildingId;

    public enum Category { // TODO: choose our own colors
        ARTS("arts and performance", "812990"),
        CAREER("career/professional development", "0088bf"),
        CONFERENCE("conference/symposium", "f15a22"),
        OTHER("other/miscellaneous", "305e41"),
        SEMINAR("seminar/lecture/colloquium", "002c57"),
        SPECIAL("special event", "444444"),
        SPORTS("sports/athletics", "eeb211"),
        STUDENT("student sponsored", "ABD037"),
        TRAINING("training/workshop", "6C0C02");

        @Getter private final String label;
        @Getter private final String color;

        Category(String label, String color) {
            this.label = label;
            this.color = color;
        }

        public static Category getCategory(String label) {
            for (Category c : values()) {
                if (c.name().equals(label.substring(0, c.name().length()).toUpperCase())) return c;
            }
            return Category.OTHER; // default Category
        }
    }

    public final class Contract implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_EVENT_ID = "eventId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE_URL = "imageURL";
        public static final String COLUMN_START_DATE = "startDate";
        public static final String COLUMN_END_DATE = "endDate";
        public static final String COLUMN_ALL_DAY = "allDay";
        public static final String COLUMN_RECURRING = "recurring";
        public static final String COLUMN_CATEGORIES = "categories";
        public static final String COLUMN_BUILDING_ID = "buildingId";
    }

}