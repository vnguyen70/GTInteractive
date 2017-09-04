package com.example.vi_tu.gtinteractive;

/**
 * Created by Rayner on 9/3/17.
 */

public class BuildingMessage implements Message {
    String building_id;
    String building_name;

    public BuildingMessage(String id, String name) {
        building_id = id;
        building_name = name;
    }
    public String getBuilding_id() {
        return building_id;
    }
    public String getBuilding_name() {
        return building_name;
    }
    @Override
    public String toString() {
        String s = "id: " + building_id + " building: " + building_name;
        return s;
    }
}
