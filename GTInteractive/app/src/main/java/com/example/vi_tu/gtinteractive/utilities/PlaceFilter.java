package com.example.vi_tu.gtinteractive.utilities;

import com.example.vi_tu.gtinteractive.domain.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 10/8/17.
 */

public class PlaceFilter extends BaseFilter<Place> {

    public PlaceFilter(List<Place> pList) { super(pList); }

    public PlaceFilter filterByName(String name) {
        if (null == name) {
            return new PlaceFilter(list);
        }
        List<Place> results = new ArrayList<>();
        for (Place p : list) {
            if (p.getNameTokens().contains(name.trim().toLowerCase())) {
                results.add(p);
            }
        }
        return new PlaceFilter(results);
    }

    public PlaceFilter filterByCategories(List<Place.Category> categories) {
        if (null == categories || categories.size() == 0) {
            return new PlaceFilter(list);
        }
        List<Place> results = new ArrayList<>();
        for (Place p : list) {
            if (categories.contains(p.getCategory())) {
                results.add(p);
            }
        }
        return new PlaceFilter(results);
    }
}
