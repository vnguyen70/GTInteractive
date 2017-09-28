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
import com.example.vi_tu.gtinteractive.domain.Dining;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 9/28/17.
 */

    public class DiningListAdapter extends RecyclerView.Adapter<DiningListAdapter.DiningViewHolder> {

        private List<Dining> dList;

        public DiningListAdapter() {
            this.dList = new ArrayList<>();
        }

    @Override
    public DiningViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new DiningViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(DiningViewHolder holder, int position) {
        Dining d = dList.get(position);
        holder.diningNameView.setText(d.getName());
    }

    public DiningListAdapter(List<Dining> bList) {
            this.dList= bList;
        }


        @Override
        public int getItemCount() {
            return dList.size();
        }

        public void setData(List<Dining> bList) {
            this.dList = bList;
            notifyDataSetChanged();
        }

        public class DiningViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView diningNameView;
            public int objectId;

            public DiningViewHolder(View view) {
                super(view);
                diningNameView = view.findViewById(R.id.tv_building_data);
                objectId = -1;
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent mapActivityIntent = new Intent(context, MapActivity.class);
                mapActivityIntent.putExtra(Arguments.DEFAULT_VIEW, ViewType.DINING);
                mapActivityIntent.putExtra(Arguments.OBJECT_ID, objectId);
                context.startActivity(mapActivityIntent);
            }

            public void setObjectId(int objectId) {
                this.objectId = objectId;
            }
        }

    }



