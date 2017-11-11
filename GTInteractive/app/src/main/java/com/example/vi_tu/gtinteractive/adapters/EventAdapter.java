package com.example.vi_tu.gtinteractive.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.EventDetailsActivity;
import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.domain.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eList;

    public EventAdapter() {
        this.eList = new ArrayList<>();
    }

    public EventAdapter(List<Event> eList) {
        this.eList = eList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new EventViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event e = eList.get(position);
        holder.titleText.setText(e.getTitle());
        holder.descriptionText.setText(e.getDescription());
        holder.dateText.setText(e.getStartDate() != null ? e.getStartDate().toString("YYYY-MM-DD") : "");
        holder.setObjectId(e.getId());
    }

    @Override
    public int getItemCount() {
        return eList.size();
    }

    public void setData(List<Event> eList) {
        this.eList = eList;
        notifyDataSetChanged();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleText;
        public TextView dateText;
        public TextView durationText;
        public TextView descriptionText;
        public int objectId;

        public EventViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.nameText);
            dateText = view.findViewById(R.id.dateText);
            durationText = view.findViewById(R.id.operationHrsText);
            descriptionText = view.findViewById(R.id.descriptionText);
            objectId = -1;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent eventDetailsActivityIntent = new Intent(context, EventDetailsActivity.class);
            eventDetailsActivityIntent.putExtra(Arguments.OBJECT_ID, objectId);
            context.startActivity(eventDetailsActivityIntent);
        }

        public void setObjectId(int objectId) {
            this.objectId = objectId;
        }
    }
}
