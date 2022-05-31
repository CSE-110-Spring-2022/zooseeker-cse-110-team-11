package com.example.cse110finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


import org.jgrapht.Graph;
import org.w3c.dom.Text;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {
    public List<Places> searchItem = Collections.emptyList();
    private Consumer<Places> onClearClicked;

    public void setSearchItem(List<Places> searchItem){
        this.searchItem.clear();
        this.searchItem = searchItem;
        notifyDataSetChanged();
    }

    public void setDeletePlannedPlace(Consumer<Places> onClearClicked){
        this.onClearClicked = onClearClicked;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.planned_exhibit_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setSearchItem(searchItem.get(position));
        //holder.setSearchItem();
    }

    @Override
    public int getItemCount() {
        return searchItem.size();
    }

    @Override
    public long getItemId(int position){
        return searchItem.get(position).id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private Button clearButton;
        private Places places;
        private TextView street_name;
        private TextView distance;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.search_items);
            this.clearButton = itemView.findViewById(R.id.plan_clr_bttn);
            this.street_name = itemView.findViewById(R.id.exhibit_street_name);
            this.distance = itemView.findViewById(R.id.plan_exhibit_dist);

            this.clearButton.setOnClickListener(view -> {
                if(onClearClicked == null) return;
                onClearClicked.accept(places);
                notifyDataSetChanged();
            });
        }

        public void setSearchItem(Places searchItem){
            this.places = searchItem;
            this.textView.setText(searchItem.name);
        }



    }
}
