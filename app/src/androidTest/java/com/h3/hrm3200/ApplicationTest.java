package com.h3.hrm3200;

import android.app.Application;
import android.content.Intent;
import android.test.ApplicationTestCase;
import android.test.ActivityUnitTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityUnitTestCase<MainActivity> {

    public ApplicationTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    public void testMain() throws Exception {
        Intent intent = new Intent();
        intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.MainActivity");
        startActivity(intent, null, null);
        jacoco.generateCoverageReport();
    }
}