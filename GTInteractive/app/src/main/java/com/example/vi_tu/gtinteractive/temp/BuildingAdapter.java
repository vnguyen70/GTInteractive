package com.example.vi_tu.gtinteractive.temp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.domain.Building;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 8/31/17.
 */

//
public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingAdapterViewHolder>  {

    private List<Building> bList;

    public BuildingAdapter() {
        bList = new ArrayList<>();
    }

    public class BuildingAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mBuildingTextView;

        public BuildingAdapterViewHolder(View view) {
            super(view);
            mBuildingTextView = (TextView) view.findViewById(R.id.tv_building_data);
        }
    }

    @Override
    public BuildingAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.search_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new BuildingAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BuildingAdapterViewHolder buildingAdapterViewHolder, int position) {
        String buildingName = bList.get(position).getName();
        buildingAdapterViewHolder.mBuildingTextView.setText(buildingName);
    }

    @Override
    public int getItemCount() {
        return bList.size();
    }

    public void setBuildingsData(List<Building> bList) {
        this.bList = bList;
        notifyDataSetChanged();
    }

}
