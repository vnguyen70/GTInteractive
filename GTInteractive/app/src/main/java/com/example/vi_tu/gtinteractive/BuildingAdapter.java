package com.example.android.testproject;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Raykris on 8/31/17.
 */

//
public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingAdapterViewHolder>  {
    private String[] mBuildingData;
    public BuildingAdapter() {

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
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new BuildingAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BuildingAdapterViewHolder buildingAdapterViewHolder, int position) {
        String building = mBuildingData[position];
        buildingAdapterViewHolder.mBuildingTextView.setText(building);
    }

    @Override
    public int getItemCount() {
        if (null == mBuildingData) return 0;
        return mBuildingData.length;
    }

    public void setBuildingData(String[] buildingData) {
        mBuildingData = buildingData;
        notifyDataSetChanged();
    }
}
