package com.example.cse110finalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class SearchFragment extends Fragment {
    public RecyclerView recyclerView;
    private SearchViewModel searchViewModel;
    public SearchViewModel viewModel;
    ViewGroup rootView;
    AnimalListAdapter adapter;
    EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =(ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        buildRecycleView();
        editText = rootView.findViewById(R.id.add_search_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String current = s.toString();
                if(current.length()==0){
                    viewModel.getSearchItems().observe(getViewLifecycleOwner(), adapter::setSearchItem);
                }
                //filter(current);
                viewModel.loadSearchResult(current).observe(getViewLifecycleOwner(),adapter::filterList);
                //adapter.filterList(viewModel.loadSearchResult(current));
            }
        });
        // Configure Button
        Button buttonSearch = (Button) rootView.findViewById(R.id.add_search_btn);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText)getView().findViewById(R.id.add_search_text);
                //filter(text.getText().toString());
                String content = text.getText().toString();
                //List<Places> searchResult = viewModel.loadSearchResult(content);
            }
        });

        // Inflate the layout for this fragment
    }
//    private void filter(String text) {
//        Log.d("Message", "Testing3");
//        List<Places> filteredList = new ArrayList<>();
//        List<Places> allExhibits = new ArrayList(adapter.getPlaces());
//        for (Places item : allExhibits) {
//            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
//                filteredList.add(item);
//            }
//        }
//
//        adapter.filterList(filteredList);
//    }

    private void buildRecycleView(){

        viewModel = new ViewModelProvider(this)
                .get(SearchViewModel.class);
        adapter = new AnimalListAdapter();
        viewModel.getSearchItems().observe(getViewLifecycleOwner(), adapter::setSearchItem);
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClicked(viewModel::updateCheckbox);
        recyclerView = rootView.findViewById(R.id.animal_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }


}