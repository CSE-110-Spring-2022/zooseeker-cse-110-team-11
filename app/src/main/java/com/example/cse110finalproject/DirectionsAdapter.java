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
            String directionStr;
            directionStr = String.format("travel %s meters along %s",
                    searchItem.distance,
                    searchItem.streetName);
            this.textView.setText(directionStr);
        }

    }
}
