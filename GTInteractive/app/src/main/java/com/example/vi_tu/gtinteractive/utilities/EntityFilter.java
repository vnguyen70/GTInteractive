package com.example.vi_tu.gtinteractive.utilities;

import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.domain.Entity;
import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 10/20/17.
 */

public class EntityFilter extends BaseFilter<Entity> {
    public EntityFilter(List<Entity> eList) { super(eList); }

    public EntityFilter filterByName(String name) {
        if (null == name) {
            return new EntityFilter(list);
        }
        List<Entity> results = new ArrayList<>();
        for (Entity e : list) {
            if (e instanceof Event) {
                Event event = (Event) e;
                if (event.getTitle().toLowerCase().contains(name.trim().toLowerCase())) {
                    results.add(event);
                }
            } else { // e is a Place
                Place place = (Place) e;
                if (place.getNameTokens().contains(name.trim().toLowerCase())) {
                    results.add(place);
                }
            }

        }
        return new EntityFilter(results);
    }

    public EntityFilter filterByClass(List<Class> classes) {
        if (null == classes|| classes.size() == 0) {
            return new EntityFilter(list);
        }
        List<Entity> results = new ArrayList<>();
        for (Entity e : list) {
            if (classes.contains(e.getClass())) {
                results.add(e);
            }
        }
        return new EntityFilter(results);
    }
}
