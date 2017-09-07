package com.example.vi_tu.gtinteractive.domain;

import org.joda.time.DateTime;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kaliq on 9/4/2017.
 */

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
}