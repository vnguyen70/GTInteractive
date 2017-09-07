package com.example.vi_tu.gtinteractive.domain;

import org.joda.time.LocalTime;

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
public class Building {

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
    String addressTokens; // name converted into lowercase tokens separated by spaces for easy searching
}
