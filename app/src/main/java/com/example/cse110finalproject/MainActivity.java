package com.example.cse110finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    SearchFragment searchFragment = new SearchFragment();
    PlanFragment planFragment = new PlanFragment();
    DirectionsFragment directionsFragment = new DirectionsFragment();
    static List<ZooData.VertexInfo> exhibitsList;
    static Map<String, ZooData.VertexInfo> exhibitsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         exhibitsMap =
            ZooData.loadVertexInfoJSON(this,"sample_node_info.json");
        exhibitsList = new ArrayList<ZooData.VertexInfo>(exhibitsMap.values());
        Log.d("json", exhibitsMap.toString());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,searchFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_menu:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,searchFragment).commit();
                        return true;
                    case R.id.plan_menu:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,planFragment).commit();
                        return true;
                    case R.id.directions_menu:_menu:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,directionsFragment).commit();
                        return true;
                }

                return false;
            }
        });


    }
}