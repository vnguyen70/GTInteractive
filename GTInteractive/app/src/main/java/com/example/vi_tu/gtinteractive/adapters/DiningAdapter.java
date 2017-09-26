package com.example.vi_tu.gtinteractive.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.DiningDetailsActivity;
import com.example.vi_tu.gtinteractive.R;
import com.example.vi_tu.gtinteractive.constants.Arguments;
import com.example.vi_tu.gtinteractive.domain.Dining;

import java.util.ArrayList;
import java.util.List;

public class DiningAdapter extends RecyclerView.Adapter<DiningAdapter.DiningViewHolder> {

    private List<Dining> dList;

    public DiningAdapter() {
        dList = new ArrayList<>();
    }

    public DiningAdapter(List<Dining> dList) {
        this.dList = dList;
    }

    @Override
    public DiningViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new DiningViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dining_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(DiningViewHolder holder, int position) {
        Dining d = dList.get(position);
        holder.nameText.setText(d.getName());
        holder.descriptionText.setText(d.getDescription());
        holder.operationHrsText.setText("8:00 AM - 9:00 PM"); // TODO
        holder.setObjectId(d.getId());
    }

    @Override
    public int getItemCount() {
        return dList.size();
    }

    public void setData(List<Dining> dList) {
        this.dList = dList;
        notifyDataSetChanged();
    }

    public class DiningViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameText;
        public TextView operationHrsText;
        public TextView descriptionText;
        public int objectId;

        public DiningViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.nameText);
            operationHrsText = view.findViewById(R.id.operationHrsText);
            descriptionText = view.findViewById(R.id.descriptionText);
            objectId = -1;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent diningDetailsActivityIntent = new Intent(context, DiningDetailsActivity.class);
            diningDetailsActivityIntent.putExtra(Arguments.OBJECT_ID, objectId);
            context.startActivity(diningDetailsActivityIntent);
        }

        public void setObjectId(int objectId) {
            this.objectId = objectId;
        }
    }
}
