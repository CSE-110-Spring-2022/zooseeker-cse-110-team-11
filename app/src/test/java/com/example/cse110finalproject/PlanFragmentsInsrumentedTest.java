package com.example.cse110finalproject;

import android.widget.EditText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PlanFragmentsInsrumentedTest {
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
//    @Test
//    public void testingFragmentSearch() {
//        //When
//        FragmentScenario<SearchFragment> scenario = FragmentScenario.launchInContainer(SearchFragment.class).onFragment(
//                planFragment -> {
//                    EditText searchBar = planFragment.getView().findViewById(R.id.add_search_text);
//                    AnimalListAdapter searchAdapter = planFragment.adapter;
//                    searchBar.setText("Gor");
//                    assert(searchAdapter.getPlaces().get(0).id_name.contains("gor"));
//                    testDb=planFragment.viewModel.db;
//                    planFragment.viewModel.db.releaseSingleton();
//                    synchronized (planFragment.viewModel.db) {
//                        planFragment.viewModel.db.close();
//                    }
//                }
//        );
//        scenario.close();
//
//
//    }
    @Test
    public void testingFragmentPlan() {
        //Given

        //When
        FragmentScenario<PlanFragment> fragmentScenario = FragmentScenario.launchInContainer(PlanFragment.class).onFragment(
                planFragment -> {
                    PlanListAdapter planListAdapter = planFragment.adapter;
                    assert(planListAdapter.searchItem!=null);
                    assert(planFragment.isVisible());
                    assert(planFragment.getView().findViewById(R.id.plan_items).isShown());
                    planFragment.viewModel.db.releaseSingleton();
                    planFragment.viewModel.db.close();
                }
        );
    }

    @After
    public void closeResources() {


    }

//    @Test //    public void testingFragmentPlan() {
//        //Given
//
//        //When
//        FragmentScenario.launchInContainer(PlanFragment.class).onFragment(
//                planFragment -> {
//                    PlanListAdapter planListAdapter = planFragment.adapter;
//                    assert(planListAdapter.searchItem!=null);
//                    assert(planFragment.isVisible());
//                    assert(planFragment.getView().findViewById(R.id.plan_items).isShown());
//                }
//        );
//    }
}