package com.example.cse110finalproject;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.junit.After;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
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
public class PlanFragmentsInstrumentedTest {
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


    @Test
    public void testingFragmentPlan() {

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
        //Checking if the number of displaying the animal count works and that it
        //is initialized after the create.
        FragmentScenario<PlanFragment> fragmentScenario = FragmentScenario.launchInContainer(PlanFragment.class).onFragment(
                planFragment -> {
                    PlanListAdapter planListAdapter = planFragment.adapter;
                    assert(planListAdapter.searchItem!=null);
                    assert(planFragment.isVisible());
                    assert(planFragment.getView().findViewById(R.id.plan_items).isShown());
                    TextView numPlanned = planFragment.getView().findViewById(R.id.num_exhibits_textview);
                    assert(numPlanned.getText().equals(String.valueOf(planListAdapter.getItemCount())));

                    planFragment.viewModel.db.releaseSingleton();
                    planFragment.viewModel.db.close();
                }
        );

        FragmentScenario<PlanFragment> fragmentScenario2 = FragmentScenario.launchInContainer(PlanFragment.class).onFragment(
                planFragment -> {
                    Places alligator = new Places("gators", ZooData.VertexInfo.Kind.EXHIBIT,true,"Alligators");
                    Places lions = new Places("lions", ZooData.VertexInfo.Kind.EXHIBIT,true,"Lions");
                    Places elephant = new Places("elephant_odyssey", ZooData.VertexInfo.Kind.EXHIBIT,true,"Elephant Odyssey");

                    List<Places> plannedPlacesList = new ArrayList<Places>();
                    plannedPlacesList.add(alligator);
                    plannedPlacesList.add(lions);
                    plannedPlacesList.add(elephant);

                    Button clearAll = planFragment.getView().findViewById(R.id.all_clr_bttn);
                    planFragment.adapter.searchItem = plannedPlacesList;
                    planFragment.adapter.notifyDataSetChanged();
                    planFragment.viewModel.setPlannedPlacesList(plannedPlacesList);
                    clearAll.callOnClick();
                    assert(planFragment.adapter.searchItem.size() == 0);
                }
        );

        FragmentScenario<DirectionsFragment> directionsscenario = FragmentScenario.launchInContainer(DirectionsFragment.class).onFragment(
                directionsFragment -> {
                    Context context = directionsFragment.getContext();
                    Map<String, ZooData.VertexInfo> exhibitsMap =
                            ZooData.loadVertexInfoJSON(context,"sample_node_info.json");
                    List<ZooData.VertexInfo> exhibitsList = new ArrayList<ZooData.VertexInfo>(exhibitsMap.values());
                    Graph<String, IdentifiedWeightedEdge> graph =  ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");

                    List<Places> placesList = Places.convertVertexListToPlaces(exhibitsList);

                    List<Places> wantToVisit = placesList.subList(1,3);


                    directionsFragment.unvisited = wantToVisit;

                    directionsFragment.nextDirections();
                }
        );

    }

}