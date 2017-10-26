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

import java.util.ArrayList;
import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private List<Place> pList;

    public PlaceListAdapter() {
        this.pList = new ArrayList<>();
    }

    public PlaceListAdapter(List<Place> pList) {
        this.pList = pList;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new PlaceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place p = pList.get(position);
        holder.placeNameView.setText(p.getName());
        holder.categoryView.setText(p.getCategory().toString());
        holder.categoryView.setTextColor(Color.parseColor("#" + p.getCategory().getColor()));

        if (p.getCategory().toString().equals("FOOD")) {
            holder.openView.setText(" - Open");
            holder.openView.setTextColor(Color.parseColor("#22b21a"));
        }
        holder.setObjectId(p.getId());
    }

    @Override
    public int getItemCount() {
        return pList.size();
    }

    public void setData(List<Place> bList) {
        this.pList = bList;
        notifyDataSetChanged();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView placeNameView;
        public TextView categoryView;
        public TextView openView;
        public int objectId;

        public PlaceViewHolder(View view) {
            super(view);
            placeNameView = view.findViewById(R.id.tv_label);
            categoryView = view.findViewById(R.id.tv_category);
            openView = view.findViewById(R.id.tv_open);
            objectId = -1;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent mapActivityIntent = new Intent(context, MapActivity.class);
            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
            mapActivityIntent.putExtra(Arguments.OBJECT_ID, objectId);
            context.startActivity(mapActivityIntent);
        }

        public void setObjectId(int objectId) {
            this.objectId = objectId;
        }
    }

}
