package com.example.vi_tu.gtinteractive.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.domain.Event;

/**
 * Created by Rayner on 11/1/17.
 */

public class EventFilterAdapter extends ArrayAdapter<Event.Category> {
    private LayoutInflater layoutInflater;
    private Event.Category[] categories;

    private int mViewResourceId;

    public EventFilterAdapter(Context context, int viewResourceId, Event.Category[] categories) {
        super(context, viewResourceId, categories);

        layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.categories = categories;

        mViewResourceId = viewResourceId;
    }
    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Event.Category getItem(int position) {
        return categories[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(mViewResourceId, null);
        Event.Category category = categories[position];
        view.setMinimumHeight(132);
        CheckedTextView ctv = view.findViewById(R.id.ctv_filter);
        ctv.setText(category.toString());
        ctv.setTextColor(Color.parseColor("#" + category.getColor()));

        return view;
    }
}
