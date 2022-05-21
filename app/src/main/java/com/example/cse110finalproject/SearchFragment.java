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

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return Returns the view of the fragment, not currently visible
     * not all components are initialized at this point
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =(ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        return rootView;
    }

    /**
     * @param view
     * @param savedInstanceState
     *
     * In this method, the view is created and we can access all components contained in fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        buildRecycleView();
        editText = rootView.findViewById(R.id.add_search_text);

        //We update the search items as the user types into the searchbar, everytime the search
        //Text changes, we update

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String current = s.toString();
                //If the search bar is empty, we show all items
                if(current.length()==0){
                    adapter.setSearchItem(viewModel.getSearchItems());
                    return;
                }
                //TODO: Rename filterList() method
                //We feed the recyclerview a query from the database
                adapter.filterList(viewModel.loadSearchResult(current));
            }
        });
        // Configure search Button
        // TODO: Do we really need a search button?
        Button buttonSearch = (Button) rootView.findViewById(R.id.add_search_btn);
        buttonSearch.setOnClickListener(v -> {
            EditText text = (EditText)getView().findViewById(R.id.add_search_text);
            String content = text.getText().toString();
        });

    }

    /**
     * This uses class members to create the recyclerview, also sets check listener
     */
    private void buildRecycleView(){

        viewModel = new ViewModelProvider(this)
                .get(SearchViewModel.class);
        adapter = new AnimalListAdapter();
        adapter.setSearchItem(viewModel.getSearchItems());
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClicked(viewModel::updateCheckbox);
        recyclerView = rootView.findViewById(R.id.animal_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }


}