package com.example.cse110finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testingFragmentPlan() {
        //Given
//        ActivityScenario<MainActivity> activity = rule.getScenario().onActivity(
//                (activity1) -> {
//                    PlanFragment planFragment = new PlanFragment();
//                    ((MainActivity)activity1).supportFragmentManager.beginTransaction().replace(R.id.container,planFragment).commit();
//                    planFragment.getView();
//                }
//        );

        //When
        FragmentScenario.launchInContainer(PlanFragment.class).onFragment(
                planFragment -> {
                    planFragment.getView();
                    assert(planFragment.getView().findViewById(R.id.plan_items)!=null);
                }
        );


        assert(1==1);
        //Then

    }
}