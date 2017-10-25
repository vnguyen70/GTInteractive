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
import com.example.vi_tu.gtinteractive.domain.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<Place> pList;

    public PlaceAdapter() {
        this.pList = new ArrayList<>();
    }

    public PlaceAdapter(List<Place> pList) {
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

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView placeNameView;
        public int objectId;

        public PlaceViewHolder(View view) {
            super(view);
            placeNameView = view.findViewById(R.id.tv_label);
            objectId = -1;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent mapActivityIntent = new Intent(context, MapActivity.class);
            mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.PLACE);
            mapActivityIntent.putExtra(Arguments.OBJECT_ID,objectId);
            context.startActivity(mapActivityIntent);
        }

        public void setObjectId(int objectId) {
            this.objectId = objectId;
        }
    }

}
