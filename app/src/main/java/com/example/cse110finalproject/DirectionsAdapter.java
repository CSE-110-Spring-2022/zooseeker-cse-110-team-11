package com.example.cse110finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsAdapter.ViewHolder> {
    private List<EdgeDispInfo> searchItem = Collections.emptyList();

    public void setDiretionsItems(List<EdgeDispInfo> searchItem){
        this.searchItem.clear();
        this.searchItem = searchItem;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.direction_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setSearchItem(searchItem.get(position));
    }

    @Override
    public int getItemCount() {
        return searchItem.size();
    }

    @Override
    public long getItemId(int position){
        return searchItem.get(position).id;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private EdgeDispInfo searchItem;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.direction_textView);
        }

        public void setSearchItem(EdgeDispInfo searchItem){
            this.searchItem = searchItem;
            String directionStr,directionStr2;

            directionStr = String.format("From %1$s, travel %2$s meters along %3$s to %4$s",
                    searchItem.start,
                    searchItem.distance,
                    searchItem.streetName,
                    searchItem.end
                    );

            String rStart;
            String rStart1;
            String rStart2;

            String rEnd;
            String rEnd1;
            String rEnd2;

            if(searchItem.start.contains("/")){
                rStart1 = searchItem.start.split("/")[0];
                rStart2 = searchItem.start.split("/")[1];
                rStart = "the corner of " + rStart1 + "and" + rStart2;
            }else {
                rStart = searchItem.start;
            }

            if(searchItem.end.contains("/")){
                rEnd1 = searchItem.end.split("/")[0];
                rEnd2 = searchItem.end.split("/")[1];
                rEnd = "the corner of " + rEnd1 + "and" + rEnd2;
            }else {
                rEnd = searchItem.end;
            }

            //arka version (following piazza and examples)
            directionStr2 = String.format("-- From %1$s, proceed on %3$s %2$s ft towards %4$s",
                    rStart,
                    searchItem.distance,
                    searchItem.streetName,
                    rEnd
            );

            this.textView.setText(directionStr2);
        }

    }
}
