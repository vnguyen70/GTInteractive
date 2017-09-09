package com.example.vi_tu.gtinteractive.temp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.buildingDetail_info;
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

    public class BuildingAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mBuildingTextView;
        public String buildingId;
        public BuildingAdapterViewHolder(View view) {
            super(view);
            mBuildingTextView = (TextView) view.findViewById(R.id.tv_building_data);
            buildingId = "";
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            Context context = view.getContext();
            Toast.makeText(view.getContext(), this.buildingId, Toast.LENGTH_LONG).show();
            Intent buildingDetailIntent = new Intent(context, buildingDetail_info.class);
            buildingDetailIntent.putExtra("buildingId", buildingId);
            context.startActivity(buildingDetailIntent);
        }

        public void setBuildingId(String buildingId) {
            this.buildingId = buildingId;
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
        String buildingId = bList.get(position).getBuildingId();
        buildingAdapterViewHolder.mBuildingTextView.setText(buildingName);
        buildingAdapterViewHolder.setBuildingId(buildingId);
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
