package com.example.cse110finalproject;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.lifecycle.ViewModelProvider;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.os.Bundle;

        import com.example.cse110finalproject.ZooData.VertexInfo;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;

public class PlanActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
//        List<SearchItem> searches = SearchItem.loadJSON(this,"demo.json");
//        Log.d("SearchActivity", searches.toString());

        PlanViewModel viewModel = new ViewModelProvider(this)
                .get(PlanViewModel.class);

        PlanListAdapter adapter = new PlanListAdapter();
        viewModel.getSearchItems().observe(this, adapter::setSearchItem);

        recyclerView = findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        VertexInfo[] example_array = {MainActivity.exhibitsList.get(1)};
        List<VertexInfo> example_arrlst = new ArrayList<VertexInfo>(Arrays.asList(example_array));
        adapter.setSearchItem(example_arrlst);
    }
}
