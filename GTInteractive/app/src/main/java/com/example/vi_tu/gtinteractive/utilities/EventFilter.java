package com.example.vi_tu.gtinteractive.utilities;

import android.util.Log;

import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 10/8/17.
 */

public class EventFilter {

    List<Event> eList;

    public EventFilter() {}
    public EventFilter(List<Event> eList) { setList(eList); }

    public EventFilter filterByTitle(String title) {
        if (null == title) {
            return new EventFilter(eList);
        }
        List<Event> results = new ArrayList<>();
        for (Event e : eList) {
            if (e.getTitle().toLowerCase().contains(title.trim().toLowerCase())) {
                results.add(e);
            }
        }
        return new EventFilter(results);
    }

    public EventFilter filterByCategories(List<Event.Category> categories) {
        if (null == categories || categories.size() == 0) {
            return new EventFilter(eList);
        }
        List<Event> results = new ArrayList<>();
        for (Event e : eList) {
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

    public void setList(List<Event> eList) {
        this.eList = eList;
    }

    public List<Event> getList() {
        return eList;
    }

}
