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
import com.example.vi_tu.gtinteractive.domain.Building;
import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raynerkristanto on 9/19/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_BUILDING = 0;
    private static final int ITEM_TYPE_EVENT = 1;
    private List<Object> searchList;

    public SearchAdapter() {
        this.searchList = new ArrayList<>();
    }
    public SearchAdapter(List<Object> searchList) { this.searchList = searchList; }

    private static class BuildingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView buildingNameView;
        public int buildingId;

        public BuildingViewHolder (View view) {
            super(view);
            buildingNameView = view.findViewById(R.id.tv_building_data);
            buildingId = -1;
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            Context context = view.getContext();
            Intent mapActivityIntent = new Intent(context, MapActivity.class);
            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
            mapActivityIntent.putExtra(Arguments.OBJECT_ID, buildingId);
            context.startActivity(mapActivityIntent);
        }
        public void setBuildingId(Integer buildingId) {
            this.buildingId = buildingId;
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
        if (searchList.get(position) instanceof Building) {
            return ITEM_TYPE_BUILDING;
        } else if (searchList.get(position) instanceof Event) {
            return ITEM_TYPE_EVENT;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        Log.d("SearchAdapter", "onCreateViewHolder");
        if (viewType == ITEM_TYPE_BUILDING) {
            return new BuildingViewHolder(layoutInflater.inflate(R.layout.search_list_item, parent, false));
        } else if (viewType == ITEM_TYPE_EVENT){
            return new EventViewHolder(layoutInflater.inflate(R.layout.event_card, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Object item = searchList.get(position);
        if (viewHolder instanceof BuildingViewHolder) {
            Building building = (Building) item;
            ((BuildingViewHolder) viewHolder).buildingNameView.setText(building.getName());
            ((BuildingViewHolder) viewHolder).setBuildingId(building.getId());
        } else if (viewHolder instanceof EventViewHolder) {
            Event event = (Event) item;
            ((EventViewHolder) viewHolder).eventNameView.setText(event.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public void setData(List<Object> searchList) {
        this.searchList = searchList;
        Log.d("SearchAdapter", "setData");
        notifyDataSetChanged();
    }
}