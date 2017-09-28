package com.example.vi_tu.gtinteractive.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.MapActivity;
import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Building;

import java.util.ArrayList;
import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {

    private List<Building> bList;

    public BuildingAdapter() {
        this.bList = new ArrayList<>();
    }

    public BuildingAdapter(List<Building> bList) {
        this.bList = bList;
    }

    @Override
    public BuildingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new BuildingViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(BuildingViewHolder holder, int position) {
        Building b = bList.get(position);
        holder.buildingNameView.setText(b.getName());
        holder.setObjectId(b.getId());
    }

    @Override
    public int getItemCount() {
        return bList.size();
    }

    public void setData(List<Building> bList) {
        this.bList = bList;
        notifyDataSetChanged();
    }

    public class BuildingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView buildingNameView;
        public int objectId;

        public BuildingViewHolder(View view) {
            super(view);
            buildingNameView = view.findViewById(R.id.tv_building_data);
            objectId = -1;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent mapActivityIntent = new Intent(context, MapActivity.class);
            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.BUILDING);
            mapActivityIntent.putExtra(Arguments.OBJECT_ID,objectId);
            context.startActivity(mapActivityIntent);
        }

        public void setObjectId(int objectId) {
            this.objectId = objectId;
        }
    }

}
