package com.example.vi_tu.gtinteractive.temp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vi_tu on 9/5/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<Event> placeData;

    public EventAdapter() {
        placeData = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Instantiate event_card elements
        public final TextView titleText;
        public final TextView durationText;
        public final TextView descriptionText;
        public final TextView dateText;

        public ViewHolder(View v) {
            super(v);
            titleText = (TextView) v.findViewById(R.id.nameText);
            durationText = (TextView) v.findViewById(R.id.operationHrsText);
            descriptionText = (TextView) v.findViewById(R.id.descriptionText);
            dateText = (TextView) v.findViewById(R.id.dateText);
        }
    }



    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIDEventCard = R.layout.event_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIDEventCard, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.mTextView.setText(placeData[position]);
        String building = placeData.get(position).getTitle();
        String description = placeData.get(position).getDescription();
        String dateText = placeData.get(position).getStartDate() + " to "
                + placeData.get(position).getEndDate();

        // Sets titleText TextView to building's title
        holder.titleText.setText(building);
        // Sets description text
        holder.descriptionText.setText(description);
        holder.dateText.setText(dateText);
    }

    @Override
    public int getItemCount() {
        return placeData.size();
    }

    public void setData(List<Event> data) {
        placeData = data;
        notifyDataSetChanged();
    }
}
