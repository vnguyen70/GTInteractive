package com.example.vi_tu.gtinteractive.utilities;

import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 10/8/17.
 */

public class EventFilter extends BaseFilter<Event> {

    public EventFilter(List<Event> eList) { super(eList); }

    public EventFilter filterByTitle(String title) {
        if (null == title) {
            return new EventFilter(list);
        }
        List<Event> results = new ArrayList<>();
        for (Event e : list) {
            if (e.getTitle().toLowerCase().contains(title.trim().toLowerCase())) {
                results.add(e);
            }
        }
        return new EventFilter(results);
    }

    public EventFilter filterByCategories(List<Event.Category> categories) {
        if (null == categories || categories.size() == 0) {
            return new EventFilter(list);
        }
        List<Event> results = new ArrayList<>();
        for (Event e : list) {
            boolean added = false;
            for (Event.Category category : e.getCategories()) {
                if (categories.contains(category) && !added) {
                    results.add(e);
                    added = true;
                }
            }
        }
        return new EventFilter(results);
    }
}
