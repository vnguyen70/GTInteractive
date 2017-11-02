package com.example.vi_tu.gtinteractive.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.domain.Place;

/**
 * Created by Rayner on 10/27/17.
 */

public class PlaceFilterAdapter extends ArrayAdapter<Place.Category> {
    private LayoutInflater layoutInflater;
    private Place.Category[] categories;

    private int mViewResourceId;

    public PlaceFilterAdapter(Context context, int viewResourceId, Place.Category[] categories) {
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
    public Place.Category getItem(int position) {
        return categories[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(mViewResourceId, null);
        Place.Category category = categories[position];
        view.setMinimumHeight(132);
        CheckedTextView ctv = view.findViewById(R.id.ctv_filter);
        ctv.setText(category.toString());
        ctv.setTextColor(Color.parseColor("#" + category.getColor()));

        return view;
    }
}
