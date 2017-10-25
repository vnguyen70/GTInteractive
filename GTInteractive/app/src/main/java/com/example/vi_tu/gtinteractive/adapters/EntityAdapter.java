package com.example.vi_tu.gtinteractive.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.MapActivity;
import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Place;
import com.example.vi_tu.gtinteractive.domain.Entity;
import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raynerkristanto on 9/19/17.
 */

public class EntityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_PLACE = 0;
    private static final int ITEM_TYPE_EVENT = 1;
    private List<Entity> entityList;

    public EntityAdapter() {
        this.entityList = new ArrayList<>();
    }
    public EntityAdapter(List<Entity> eList) { this.entityList = eList; }

    private static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView placeNameView;
        public int placeId;

        public PlaceViewHolder(View view) {
            super(view);
            placeNameView = view.findViewById(R.id.tv_label);
            placeId = -1;
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            Context context = view.getContext();
            Intent mapActivityIntent = new Intent(context, MapActivity.class);
            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
            mapActivityIntent.putExtra(Arguments.OBJECT_ID, placeId);
            context.startActivity(mapActivityIntent);
        }
        public void setPlaceId(Integer placeId) {
            this.placeId = placeId;
        }

    }

    private static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventNameView;
        public EventViewHolder (View view) {
            super(view);
            eventNameView = view.findViewById(R.id.nameText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (entityList.get(position) instanceof Place) {
            return ITEM_TYPE_PLACE;
        } else if (entityList.get(position) instanceof Event) {
            return ITEM_TYPE_EVENT;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        Log.d("EntityAdapter", "onCreateViewHolder");
        if (viewType == ITEM_TYPE_PLACE) {
            return new PlaceViewHolder(layoutInflater.inflate(R.layout.list_item, parent, false));
        } else if (viewType == ITEM_TYPE_EVENT){
            return new EventViewHolder(layoutInflater.inflate(R.layout.event_card, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Object item = entityList.get(position);
        if (viewHolder instanceof PlaceViewHolder) {
            Place place = (Place) item;
            ((PlaceViewHolder) viewHolder).placeNameView.setText(place.getName());
            ((PlaceViewHolder) viewHolder).setPlaceId(place.getId());
        } else if (viewHolder instanceof EventViewHolder) {
            Event event = (Event) item;
            ((EventViewHolder) viewHolder).eventNameView.setText(event.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }

    public void setData(List<Entity> eList) {
        this.entityList = eList;
        Log.d("EntityAdapter", "setData");
        notifyDataSetChanged();
    }
}