package com.example.cse110finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import com.example.cse110finalproject.ZooData.VertexInfo;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {
    private List<VertexInfo> searchItem = Collections.emptyList();

    public void setSearchItem(List<VertexInfo> searchItem){
        this.searchItem.clear();
        this.searchItem = searchItem;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animal_list,parent,false);
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
        return searchItem.get(position).idLong;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private VertexInfo searchItem;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.search_items);
        }

        public void setSearchItem(VertexInfo searchItem){
            this.searchItem = searchItem;
            this.textView.setText(searchItem.name);
        }
    }
}
