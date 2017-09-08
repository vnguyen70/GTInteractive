package com.example.vi_tu.gtinteractive.temp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vi_tu on 9/5/2017.
 */

public class DiningAdapter extends RecyclerView.Adapter<DiningAdapter.ViewHolder> {
    private List<Dining> placeData;

    public DiningAdapter() {
        placeData = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Instantiate event_card elements
        public final TextView nameText;
        public final TextView operationHrsText;
        public final TextView descriptionText;


        public ViewHolder(View v) {
            super(v);
            nameText = (TextView) v.findViewById(R.id.nameText);
            operationHrsText = (TextView) v.findViewById(R.id.operationHrsText);
            descriptionText = (TextView) v.findViewById(R.id.descriptionText);

        }
    }



    @Override
    public DiningAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIDEventCard = R.layout.dining_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIDEventCard, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.mTextView.setText(placeData[position]);
        String name = placeData.get(position).getName();
        String description = placeData.get(position).getDescription();
        String operationHrs = placeData.get(position).getOpenTimes() + " - "
                + placeData.get(position).getCloseTimes();

        // Sets titleText TextView to building's title
        holder.nameText.setText(name);
        // Sets description text
        holder.descriptionText.setText(description);

        /* Operation hours currently show up as something weird so I'm hardcoding until we can
        figure it out */
//        holder.operationHrsText.setText(operationHrs);
        holder.operationHrsText.setText("8:00 AM - 9:00 PM");

    }

    @Override
    public int getItemCount() {
        return placeData.size();
    }

    public void setData(List<Dining> data) {
        placeData = data;
        notifyDataSetChanged();
    }
}
