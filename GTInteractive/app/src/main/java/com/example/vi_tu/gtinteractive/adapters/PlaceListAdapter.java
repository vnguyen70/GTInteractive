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
import com.example.vi_tu.gtinteractive.PlaceDetailsActivity;
import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.constants.ViewType;
import com.example.vi_tu.gtinteractive.domain.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private List<Place> pList;
    private boolean showMapNext;

    public PlaceListAdapter(List<Place> pList, boolean showMapNext) {
        this.pList = pList;
        this.showMapNext = showMapNext;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new PlaceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_place_item, viewGroup, false), showMapNext);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place p = pList.get(position);
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
    }

    @Override
    public int getItemCount() {
        return pList.size();
    }

    public void setData(List<Place> pList) {
        this.pList = pList;
        notifyDataSetChanged();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView placeNameView;
        public TextView categoryView;
        public TextView openView;
        public int objectId;
        public boolean showMapNext;

        public PlaceViewHolder(View view, boolean showMapNext) {
            super(view);
            placeNameView = view.findViewById(R.id.tv_label);
            categoryView = view.findViewById(R.id.tv_category);
            openView = view.findViewById(R.id.tv_open);
            objectId = -1;
            this.showMapNext = showMapNext;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            if (showMapNext) {
                Intent mapActivityIntent = new Intent(context, MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, objectId);
                context.startActivity(mapActivityIntent);
            } else {
                Intent detailsActivityIntent = new Intent(context, PlaceDetailsActivity.class);
                detailsActivityIntent.putExtra(Arguments.OBJECT_ID, objectId);
                context.startActivity(detailsActivityIntent);
            }

        }

        public void setObjectId(int objectId) {
            this.objectId = objectId;
        }
    }

}
