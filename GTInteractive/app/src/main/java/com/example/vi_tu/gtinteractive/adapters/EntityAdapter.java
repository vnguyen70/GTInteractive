package com.example.vi_tu.gtinteractive.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
    private List<? extends Entity> entityList;

    public EntityAdapter(List<? extends Entity> eList) { this.entityList = eList; }

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
            return new PlaceListAdapter.PlaceViewHolder(layoutInflater.inflate(R.layout.list_place_item, parent, false), true);
        } else if (viewType == ITEM_TYPE_EVENT){
            return new EventListAdapter.EventViewHolder(layoutInflater.inflate(R.layout.list_event_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof PlaceListAdapter.PlaceViewHolder) {
            PlaceListAdapter.PlaceViewHolder holder = (PlaceListAdapter.PlaceViewHolder) viewHolder;
            Place p = (Place) entityList.get(position);
            holder.placeNameView.setText(p.getName());
            holder.categoryView.setText(p.getCategory().toString());
            holder.categoryView.setTextColor(Color.parseColor("#" + p.getCategory().getColor()));

            if (p.getCategory().toString().equals("FOOD")) {
                if (p.isOpen()) {
                    holder.openView.setText("   Open");
                    holder.openView.setTextColor(Color.parseColor("#22b21a"));
                } else {
                    holder.openView.setText("  Closed");
                    holder.openView.setTextColor(Color.RED);
                }
            } else {
                // this statement is important because holders are reused
                // if the holder contained a food place and then contains another place
                // the open text will still be there
                holder.openView.setText("");
            }
            holder.setObjectId(p.getId());
        } else if (viewHolder instanceof EventListAdapter.EventViewHolder) {
            Event e = (Event) entityList.get(position);
            EventListAdapter.EventViewHolder holder = (EventListAdapter.EventViewHolder) viewHolder;
            holder.eventNameView.setText(e.getTitle());
            if (e.getCategories().size() > 0) {
                holder.categoryView.setText(e.getCategories().toString());
                holder.categoryView.setTextColor(Color.parseColor("#" + e.getCategories().get(0).getColor()));
            }
            holder.dateView.setText((e.getStartDate() != null ? e.getStartDate().toString("MMM dd, YYYY h:mm a") : ""));
            holder.locationView.setText(e.getLocation());
            holder.setObjectId(e.getId());
        }
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }

    public void setData(List<? extends Entity> eList) {
        this.entityList = eList;
        notifyDataSetChanged();
    }
}