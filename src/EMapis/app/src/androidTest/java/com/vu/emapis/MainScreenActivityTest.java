package com.vu.emapis;

import static org.junit.Assert.*;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainScreenActivityTest {

    @Rule
    public ActivityTestRule<MainScreenActivity> mActivityTestRule = new ActivityTestRule<MainScreenActivity>(MainScreenActivity.class);

    private MainScreenActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View view = mActivity.findViewById(R.id.statText);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}