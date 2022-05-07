package com.example.cse110finalproject;

import android.widget.EditText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PlanFragmentsInsrumentedTest {

    @Test
    public void testingFragmentPlan() {
        //Given

        //When
        FragmentScenario.launchInContainer(PlanFragment.class).onFragment(
                planFragment -> {
                    PlanListAdapter planListAdapter = planFragment.adapter;
                    assert(planListAdapter.searchItem!=null);
                    assert(planFragment.isVisible());
                    assert(planFragment.getView().findViewById(R.id.plan_items).isShown());
                }
        );
    }
}