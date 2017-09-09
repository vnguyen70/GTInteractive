package com.example.vi_tu.gtinteractive.domain;

import android.provider.BaseColumns;

import org.joda.time.DateTime;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    // From RSS feed: http://www.calendar.gatech.edu/feeds/events.xml
    String title;
    String link;
    String description;
    DateTime startDate;
    DateTime endDate;
    String location;
    List<String> categories; // TODO: create Category enum class?
    DateTime pubDate;

    // Custom fields
    String buildingId;

    public final class Contract implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_START_DATE = "startDate";
        public static final String COLUMN_END_DATE = "endDate";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_CATEGORIES = "categories";
        public static final String COLUMN_PUB_DATE = "pubDate";
        public static final String COLUMN_BUILDING_ID = "buildingId";
    }

}