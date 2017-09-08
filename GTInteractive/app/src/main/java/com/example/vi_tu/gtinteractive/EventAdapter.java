package com.example.vi_tu.gtinteractive;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by vi_tu on 9/5/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private String[] placeData;

    public EventAdapter() {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.testText);
        }
    }



    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(
//                R.layout.event_card, parent, false);
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
        Context context = parent.getContext();
        int layoutIDEventCard = R.layout.event_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediatel = false;

        View view = inflater.inflate(layoutIDEventCard, parent, shouldAttachToParentImmediatel);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.mTextView.setText(placeData[position]);
        String building = placeData[position];
        holder.mTextView.setText(building);
    }

    @Override
    public int getItemCount() {
        if (null == placeData) return 0;
        return placeData.length;
    }

    public void setData(String[] data) {
        placeData = data;
        notifyDataSetChanged();
    }
}
