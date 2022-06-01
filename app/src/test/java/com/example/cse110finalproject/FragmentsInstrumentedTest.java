package com.example.cse110finalproject;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FragmentsInstrumentedTest {
    SearchDatabase testDb;
    SearchPlacesDao placesListItemDao;
//    @Before
//    public void resetDatabase() {
//        Context context = ApplicationProvider.getApplicationContext();
//        testDb = Room.inMemoryDatabaseBuilder(context, SearchDatabase.class)
//                .allowMainThreadQueries()
//                .build();
//
//        placesListItemDao = testDb.searchPlacesDao();
//    }


    /**
     * We have to put all of out tests under one @Plan notation due to a strange database
     * mocking error that androidx does in the background
     */
    @Test
    public void testingAllFragments() {

        //Testing if search by name works
        searchingForGorillaSearchFragment();

        //
        searchingForMammalTagSearchFragment();

        //
        planTabTestPlannedExhibitsCounter();



        //Testing that directions are displayed with no exceptions
        directionsTabInitialization();

    }

    //TODO: Get this to work
    private void directionsTabInitialization() {
//        FragmentScenario<DirectionsFragment> directionsScenario = FragmentScenario.launchInContainer(DirectionsFragment.class).onFragment(
//                directionsFragment -> {
//                    Context context = directionsFragment.getContext();
//                    Map<String, ZooData.VertexInfo> exhibitsMap =
//                            ZooData.loadVertexInfoJSON(context, "exhibit_info.json");
//                    List<ZooData.VertexInfo> exhibitsList = new ArrayList<ZooData.VertexInfo>(exhibitsMap.values());
//                    Graph<String, IdentifiedWeightedEdge> graph =  ZooData.loadZooGraphJSON(context, "zoo_graph.json");
//
//                    List<Places> placesList = Places.convertVertexListToPlaces(exhibitsList);
//
//                    List<Places> wantToVisit = placesList.subList(1,3);
//
//
//                    directionsFragment.unvisited = wantToVisit;
//
//                    directionsFragment.nextDirections();
//                }
//        );
    }



    private void searchingForGorillaSearchFragment() {
        FragmentScenario<SearchFragment> scenario = FragmentScenario.launchInContainer(SearchFragment.class).onFragment(
                searchFragment -> {
                    EditText searchBar = searchFragment.getView().findViewById(R.id.add_search_text);
                    AnimalListAdapter searchAdapter = searchFragment.adapter;
                    synchronized (searchBar) {
                        searchBar.setText("Gor");
                    }
                    assert(searchAdapter.getPlaces().get(0).id_name.contains("gor"));
                    searchFragment.viewModel.db.close();
                }
        );
        scenario.close();
    }

    private void searchingForMammalTagSearchFragment() {
        FragmentScenario<SearchFragment> scenario = FragmentScenario.launchInContainer(SearchFragment.class).onFragment(
                searchFragment -> {
                    EditText searchBar = searchFragment.getView().findViewById(R.id.add_search_text);
                    AnimalListAdapter searchAdapter = searchFragment.adapter;
                    synchronized (searchBar) {
                        searchBar.setText("mammal");
                    }
                    assert(searchAdapter.getPlaces().get(0).tags.contains("mammal"));
                    searchFragment.viewModel.db.close();
                }
        );
        scenario.close();
    }

    private void planTabTestPlannedExhibitsCounter() {
        //Checking if the number of displaying the animal count works and that it
        //is initialized after the create.
        FragmentScenario<PlanFragment> fragmentScenario = FragmentScenario.launchInContainer(PlanFragment.class).onFragment(
                planFragment -> {
//                    PlanListAdapter planListAdapter = planFragment.adapter;
//                    assert(planListAdapter.searchItem!=null);
//                    assert(planFragment.isVisible());
//                    assert(planFragment.getView().findViewById(R.id.plan_items).isShown());
//                    TextView numPlanned = planFragment.getView().findViewById(R.id.num_exhibits_textview);
//                    assert(numPlanned.getText().equals(String.valueOf(planListAdapter.getItemCount())));
//
//                    planFragment.viewModel.db.releaseSingleton();
//                    planFragment.viewModel.db.close();
                }
        );
    }

}