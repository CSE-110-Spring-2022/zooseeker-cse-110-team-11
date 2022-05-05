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
import java.util.function.BiConsumer;

public class AnimalListAdapter extends RecyclerView.Adapter<AnimalListAdapter.ViewHolder> {
    private List<Places> places = Collections.emptyList();
    private BiConsumer<Places, String> onTextEditedHandler;

    public void setSearchItem(List<Places> places){
        this.places.clear();
        this.places = places;
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
        holder.setSearchItem(places.get(position));
    }

    public void setOnTextEditedHandler(BiConsumer<Places, String> onTextEdited){
        this.onTextEditedHandler = onTextEdited;
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public long getItemId(int position){
        return places.get(position).id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private final CheckBox checkBox;
        private Places places;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.search_items);
            this.checkBox = itemView.findViewById(R.id.added);

            this.textView.setOnFocusChangeListener(((view, hasFocus) -> {
                if(!hasFocus){
                    onTextEditedHandler.accept(places, textView.getText().toString());
                }
            }));
        }

        public void setSearchItem(Places places){
            this.places = places;
            this.checkBox.setChecked(places.checked);
            this.textView.setText(places.name);
        }
    }
}
