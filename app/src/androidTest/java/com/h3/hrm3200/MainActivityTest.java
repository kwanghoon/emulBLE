package com.h3.hrm3200;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


/**
 * Created by moonh on 2017-08-27.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Intent intent = new Intent();
        intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.MainActivity");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
        Thread.sleep(60000);
        jacoco.generateCoverageReport();


//        assertEquals("com.example.android.testjacoco", appContext.getPackageName());
    }
}

