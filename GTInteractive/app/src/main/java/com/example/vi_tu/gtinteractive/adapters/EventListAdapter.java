package com.example.vi_tu.gtinteractive.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.MapActivity;
import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 9/28/17.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> eList;

    public EventListAdapter() { this.eList = new ArrayList<>(); }

    public EventListAdapter(List<Event> eList) { this.eList = eList; }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new EventViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_building_search, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event e = eList.get(position);
        holder.eventNameView.setText(e.getTitle());
        if (e.getCategories().size() > 0) {
            holder.categoryView.setText(e.getCategories().toString());
            holder.categoryView.setTextColor(Color.parseColor("#" + e.getCategories().get(0).getColor()));
        }
        holder.setObjectId(e.getId());
    }

    @Override
    public int getItemCount() { return eList.size(); }

    public void setData(List<Event> eList) {
        this.eList = eList;
        notifyDataSetChanged();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView eventNameView;
        public TextView categoryView;
        public int objectId;

        public EventViewHolder(View view) {
            super(view);
            eventNameView = view.findViewById(R.id.tv_building_data);
            categoryView = view.findViewById(R.id.tv_category);
            objectId = -1;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent mapActivityIntent = new Intent(context, MapActivity.class);
            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.EVENT);
            mapActivityIntent.putExtra(Arguments.OBJECT_ID, objectId);
            context.startActivity(mapActivityIntent);
        }

        public void setObjectId(int objectId) {
            this.objectId = objectId;
        }
    }
}
