package com.example.vi_tu.gtinteractive.utilities;

import com.example.vi_tu.gtinteractive.domain.Building;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 10/8/17.
 */

public class BuildingFilter {

    List<Building> bList;

    public BuildingFilter() {}
    public BuildingFilter(List<Building> bList) { setList(bList); }

    public BuildingFilter filterByName(String name) {
        if (null == name) {
            return new BuildingFilter(bList);
        }
        List<Building> results = new ArrayList<>();
        for (Building b : bList) {
            if (b.getNameTokens().contains(name.trim().toLowerCase())) {
                results.add(b);
            }
        }
        return new BuildingFilter(results);
    }

    public BuildingFilter filterByCategories(List<Building.Category> categories) {
        if (null == categories || categories.size() == 0) {
            return new BuildingFilter(bList);
        }
        List<Building> results = new ArrayList<>();
        for (Building b : bList) {
            if (categories.contains(b.getCategory())) {
                results.add(b);
            }
        }
        return new BuildingFilter(results);
    }

    public void setList(List<Building> bList) {
        this.bList = bList;
    }

    public List<Building> getList() {
        return bList;
    }

}
