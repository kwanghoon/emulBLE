package com.h3.hrm3200.tests;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

import com.h3.hrm3200.MainActivity;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityUnitTestCase<MainActivity> {

    public ApplicationTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    public void testMain()  {
        Intent intent = new Intent();
        intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.MainActivity");
        startActivity(intent, null, null);
    }
}