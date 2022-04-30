package com.example.cse110finalproject;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.lifecycle.ViewModelProvider;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.os.Bundle;
        import android.util.Log;

        import java.util.List;

public class PlanActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        List<SearchItem> searches = SearchItem.loadJSON(this,"demo.json");
//        Log.d("SearchActivity", searches.toString());

        SearchViewModel viewModel = new ViewModelProvider(this)
                .get(SearchViewModel.class);

        AnimalListAdapter adapter = new AnimalListAdapter();
        viewModel.getSearchItems().observe(this, adapter::setSearchItem);

        recyclerView = findViewById(R.id.animal_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setSearchItem(SearchItem.loadJSON(this,"demo.json"));
    }
}
