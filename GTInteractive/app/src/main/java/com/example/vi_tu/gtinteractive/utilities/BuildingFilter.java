package com.example.vi_tu.gtinteractive.utilities;

import com.example.vi_tu.gtinteractive.domain.Building;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 10/8/17.
 */

public class BuildingFilter extends BaseFilter<Building> {

    public BuildingFilter(List<Building> bList) { super(bList); }

    public BuildingFilter filterByName(String name) {
        if (null == name) {
            return new BuildingFilter(list);
        }
        List<Building> results = new ArrayList<>();
        for (Building b : list) {
            if (b.getNameTokens().contains(name.trim().toLowerCase())) {
                results.add(b);
            }
        }
        return new BuildingFilter(results);
    }

    public BuildingFilter filterByCategories(List<Building.Category> categories) {
        if (null == categories || categories.size() == 0) {
            return new BuildingFilter(list);
        }
        List<Building> results = new ArrayList<>();
        for (Building b : list) {
            if (categories.contains(b.getCategory())) {
                results.add(b);
            }
        }
        return new BuildingFilter(results);
    }
}
